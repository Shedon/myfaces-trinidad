/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.apache.myfaces.trinidad.change;

import java.util.Map;

import javax.el.ValueExpression;

import javax.faces.component.ContextCallback;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import org.apache.myfaces.trinidad.model.RowKeySet;

/**
 * Handles RowKeySetAttribute changes, which need to be handled specially because they are mutable
 * and programmers assume that the instances don't change
 */
public final class RowKeySetAttributeChange extends AttributeComponentChange
{
  public RowKeySetAttributeChange(String clientId,  String propertyName, Object value)
  {
    super(propertyName, value);
    
    if ((clientId == null) || (clientId.length() == 0))
      throw new IllegalArgumentException("No clientId specified");

    _clientId = clientId;
  }

  @Override
  @SuppressWarnings("deprecation")
  public void changeComponent(UIComponent uiComponent)
  {
    Map<String, Object> attributeMap = uiComponent.getAttributes();

    Object attributeValue = getAttributeValue();
    String attributeName  = getAttributeName();
    
    // if the attributevalue is a ValueExpression or ValueBinding, use the
    // appropriate setValueExpression/setValueBinding call and remove the
    // current attribute value, if any, so that the ValueExpression/ValueBinding
    // can take precedence
    if (attributeValue instanceof ValueExpression)
    {
      uiComponent.setValueExpression(attributeName, (ValueExpression)attributeValue);
      attributeMap.remove(attributeName);
    }
    else if (attributeValue instanceof ValueBinding)
    {
      uiComponent.setValueBinding(attributeName, (ValueBinding)attributeValue);
      attributeMap.remove(attributeName);
    }
    else
    {      
      // Specially handle RowKeySet case by replacing the contents of the RowKeySet in-place
      // rather than replacing the entire object.  This keeps the mutable object instance from
      // changing
      if (attributeValue instanceof RowKeySet)
      {
        ValueExpression expression = uiComponent.getValueExpression(attributeName);

        Object oldValue;
        
        if (expression != null)
        {
          //use EL to get the oldValue and then determine whether we need to update in place
          final FacesContext context = FacesContext.getCurrentInstance();
                    
          context.getViewRoot().invokeOnComponent(
            context,
            _clientId,
            new GetOldValueAndUpdate(expression, (RowKeySet)attributeValue));
        }
        else
        {
          oldValue = attributeMap.get(attributeName);

          if (oldValue instanceof RowKeySet)
          {
            _updateKeySet(_clientId, (RowKeySet)oldValue, (RowKeySet)attributeValue);
            
            // we updated in place, but we still need to set the attribute in order for partial
            // state saving to work
          }
        }      
      }
      
      
      attributeMap.put(attributeName, attributeValue);
    }
  }
  
  private static void _updateKeySet(String clientId, RowKeySet oldKeySet, RowKeySet newKeySet)
  {
    // check for equality because otherwise we would clear ourselves and end up empty
    if (oldKeySet != newKeySet)
    {
      // no client id, so we're in context
      if (clientId == null)
      {
        oldKeySet.clear();
        oldKeySet.addAll(newKeySet);        
      }
      else
      {
        final FacesContext context = FacesContext.getCurrentInstance();
        
        context.getViewRoot().invokeOnComponent(
           context,
           clientId,
           new RowKeySetUpdater(oldKeySet, newKeySet));
      }
    }    
  }
  
  /**
   * Get the oldValue in context and update it in context
   */
  private static final class GetOldValueAndUpdate implements ContextCallback
  {
    public GetOldValueAndUpdate(ValueExpression expression, RowKeySet newKeySet)
    {
      _expression = expression;
      _newKeySet  = newKeySet;
    }
    public void invokeContextCallback(FacesContext context,
                                      UIComponent target)
    {
      // update the KeySet with the old and new values
      RowKeySetAttributeChange._updateKeySet(null,
                                             (RowKeySet)_expression.getValue(context.getELContext()),
                                             _newKeySet);
    }
    
    private final ValueExpression _expression;
    private final RowKeySet _newKeySet;
  }

  /**
   * Makes sure that we clear and add the RowKeySet in context
   */
  private static final class RowKeySetUpdater implements ContextCallback
  {
    public RowKeySetUpdater(RowKeySet oldKeySet, RowKeySet newKeySet)
    {
      _oldKeySet = oldKeySet;
      _newKeySet = newKeySet;
    }

    public void invokeContextCallback(FacesContext context,
                                      UIComponent target)
    {
      _oldKeySet.clear();
      _oldKeySet.addAll(_newKeySet);
    }
    
    private final RowKeySet _oldKeySet;
    private final RowKeySet _newKeySet;
  }

  private static final long serialVersionUID = 1L;
  
  private final String _clientId;
}
