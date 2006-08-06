/*
* Copyright 2006 The Apache Software Foundation.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.apache.myfaces.trinidadinternal.facelets;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.el.MethodExpression;
import javax.faces.el.MethodBinding;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.el.LegacyMethodBinding;
import com.sun.facelets.tag.MetaRule;
import com.sun.facelets.tag.Metadata;
import com.sun.facelets.tag.MetadataTarget;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagAttributeException;

/**
 * 
 * @author Adam Winer
 * =-=AEW REWRITE USING BEANINFO
 */
final class TrinidadListenersTagRule extends MetaRule
{
  public static final MetaRule Instance = new TrinidadListenersTagRule();

  private static class ListenerPropertyMetadata extends Metadata
  {
    public ListenerPropertyMetadata(Method method, TagAttribute attribute, Class[] paramList)
    {
      _method = method;
      _attribute = attribute;
      _paramList = paramList;
    }
    
    @Override
    public void applyMetadata(FaceletContext ctx, Object instance)
    {
      MethodExpression expr =
        _attribute.getMethodExpression(ctx, null, _paramList);
      
      try
      {
        _method.invoke(instance,
                       new Object[]{new LegacyMethodBinding(expr)});
      }
      catch (InvocationTargetException e)
      {
        throw new TagAttributeException(_attribute, e.getCause());
      }
      catch (Exception e)
      {
        throw new TagAttributeException(_attribute, e);
      }
    }

    private final Method       _method;
    private final TagAttribute _attribute;
    private       Class[]      _paramList;
  }
   

  @Override
  public Metadata applyRule(
     String name,
     TagAttribute attribute,
     MetadataTarget meta)
  {
    if ((meta.getPropertyType(name) == MethodBinding.class) &&
        name.endsWith("Listener"))
    {
      // OK, we're trying to call setFooListener()
      Method m = meta.getWriteMethod(name);
      if (m != null)
      {
        // First, look for the getFooListeners() property
        PropertyDescriptor listeners = meta.getProperty(name + "s");
        if (listeners == null)
          return null;

        // It should return an array of FooListener objects
        Class<?> arrayType = listeners.getPropertyType();
        if (!arrayType.isArray())
          return null;
        
        // Ignore non-ADF types
        Class<?> listenerClass = arrayType.getComponentType();
        if (!listenerClass.getName().startsWith("org.apache.myfaces.trinidad."))
          return null;

        // Turn that into an Event to get the signature
        Class<?> eventClass = _getEventClass(listenerClass);
        if (eventClass == null)
          return null;

        // And go
        return new ListenerPropertyMetadata(m, attribute,
                                            new Class[]{eventClass});
      }
    }
    return null;
  }

  static private Class<?> _getEventClass(Class<?> listenerClass)
  {
    String listenerName = listenerClass.getName();
    if (!listenerName.endsWith("Listener"))
      return null;
    
    String eventName = (listenerName.substring(0,
                          listenerName.length() - "Listener".length()) +
                        "Event");

    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    
    try
    {
      return Class.forName(eventName, true, loader);
    }
    catch (ClassNotFoundException cnfe)
    {
      return null;
    }
  }
}
