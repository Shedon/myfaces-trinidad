/*
 * Copyright  2004-2006 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.myfaces.adf.component;
import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.FacesEvent;
import javax.faces.event.PhaseId;

import org.apache.myfaces.adf.model.CollectionModel;
import org.apache.myfaces.adf.model.RowKeySet;
import org.apache.myfaces.adf.model.RowKeySetTreeImpl;
import org.apache.myfaces.adf.model.TreeModel;


/**
 * Base class for the NavigationTree component.
 *
 * @version $Name:  $ ($Revision: adfrt/faces/adf-faces-api/src/main/java-templates/oracle/adf/view/faces/component/UIXNavigationTreeTemplate.java#0 $) $Date: 10-nov-2005.19:07:38 $
 * @author The Oracle ADF Faces Team
 */
abstract public class UIXNavigationTreeTemplate extends UIXNavigationHierarchy
{
/**/ // Abstract methods implemented by code gen
/**/  abstract public int getStartLevel();
/**/  public abstract RowKeySet getDisclosedRowKeys();
/**/  public abstract void setDisclosedRowKeys(RowKeySet state);
/**/  public abstract MethodBinding getRowDisclosureListener();

  /**
   * Sets the phaseID of UI events depending on the "immediate" property.
   */
  public void queueEvent(FacesEvent event)
  {
    TableUtils.__handleQueueEvent(this, event);
    super.queueEvent(event);
  }

  /**
   * Delivers an event.
   * @param event
   * @throws javax.faces.event.AbortProcessingException
   */
  public void broadcast(FacesEvent event) throws AbortProcessingException
  { 
    HierarchyUtils.__handleBroadcast(this, 
                                      event, 
                                      getDisclosedRowKeys(), 
                                      getRowDisclosureListener());
    super.broadcast(event);
  }
 
 
    
 public CollectionModel createCollectionModel(CollectionModel current, Object value)
  {
    TreeModel model = (TreeModel)super.createCollectionModel(current, value);    
    RowKeySet treeState = getDisclosedRowKeys();
    treeState.setCollectionModel(model);    
    return model;
  }




 protected void processFacetsAndChildren(
    FacesContext context,
    PhaseId phaseId)
  {
    // this component has no facets that need to be processed once.
    // instead process the "nodeStamp" facet as many times as necessary:
    Object oldPath = getRowKey();
    HierarchyUtils.__setStartLevelPath(this, getStartLevel());
    HierarchyUtils.__iterateOverTree(context, 
                                      phaseId, 
                                      this, 
                                      getDisclosedRowKeys(),
                                      true);
    setRowKey(oldPath);
  } 
  
 void __encodeBegin(FacesContext context) throws IOException
  {
    HierarchyUtils.__handleEncodeBegin(this, getDisclosedRowKeys());
    super.__encodeBegin(context);
  }

  void __init()
  {
    super.__init();
    if (getDisclosedRowKeys() == null)
      setDisclosedRowKeys(new RowKeySetTreeImpl());
  }  

}
