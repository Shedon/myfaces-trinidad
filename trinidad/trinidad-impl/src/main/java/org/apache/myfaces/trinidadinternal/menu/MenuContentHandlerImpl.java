/* @(#)MenuContentHandlerImpl.java
 *
 *
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
package org.apache.myfaces.trinidadinternal.menu;

import java.io.InputStream;
import java.io.IOException;

import java.net.URL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.util.Map;

import java.util.Stack;

import javax.faces.context.FacesContext;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.myfaces.trinidad.logging.TrinidadLogger;
import org.apache.myfaces.trinidad.model.ChildPropertyTreeModel;
import org.apache.myfaces.trinidad.model.TreeModel;
import org.apache.myfaces.trinidad.model.XMLMenuModel;
import org.apache.myfaces.trinidad.model.XMLMenuModel.MenuContentHandler;

import org.xml.sax.helpers.DefaultHandler;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Handler called by the SAXParser when parsing menu metadata
 * as part of the XML Menu Model of Trinidad Faces.
 * <p>
 * This is called through the Services API (See XMLMenuModel.java) to 
 * keep the separation between API's and internal modules.
 * <p>
 * startElement() and endElement() are called as one would expect,
 * at the start of parsing an element in the menu metadata file and
 * at the end of parsing an element in the menu metadata file.
 * 
 * The menu model is created as a List of itemNodes and groupNodes
 * which is available to and used by the XMLMenuModel to create the
 * TreeModel and internal Maps.
 * 
 * @author Kris McQueen and Gary Kind
 */
 /* 
  * IMPORTANT NOTE: Much of the work and data structures used by the 
  * XMLMenuModel are created (and kept) in this class.  This is necessarily the 
  * case because the scope of the XMLMenuModel is request.  The 
  * MenuContentHandlerImpl is shared so it does not get rebuilt upon each 
  * request as the XMLMenuModel does. So the XMLMenuModel can get its data
  * each time it is rebuilt (on each request) without having to reparse and
  * recreate all of its data structures.  It simply gets them from here.
  * 
  * As well as the tree, three hashmaps are created in order to be able to 
  * resolve cases where multiple menu items cause navigation to the same viewId.  
  * All 3 of these maps are created after the metadata is parsed and the tree is
  * built, in the _addToMaps method. 
  * 
  * o The first hashMap is called the viewIdFocusPathMap and is built by 
  * traversing the tree after it is built (see endDocument()).  
  * Each node's focusViewId is 
  * obtained and used as the key to an entry in the viewIdHashMap.  An ArrayList 
  * is used as the entry's value and each item in the ArrayList is a node's 
  * rowkey from the tree. This allows us to have duplicate rowkeys for a single 
  * focusViewId which translates to a menu that contains multiple items pointing 
  * to the same page. In general, each entry will have an ArrayList of rowkeys 
  * with only 1 rowkey, AKA focus path.
  * o The second hashMap is called the nodeFocusPathMap and is built at the 
  * same time the viewIdHashMap is built. Each entry's key is the actual node 
  * and the value is the row key.  Since the model keeps track of the currently
  * selected menu node, this hashmap can be used to resolve viewId's with 
  * multiple focus paths.  Since we have the currently selected node, we just
  * use this hashMap to get its focus path.
  * o The third hashMap is called idNodeMap and is built at the same time as the
  * previous maps.  This map is populated by having each entry contain the node's
  * id as the key and the actual node as the value.  In order to keep track of 
  * the currently selected node in the case of a GET, the node's id is appended 
  * to the request URL as a parameter.  The currently selected node's id is 
  * picked up and this map is used to get the actual node that is currently 
  * selected.
  */ 
public class MenuContentHandlerImpl extends DefaultHandler
                                    implements MenuContentHandler
{
  /**
    * Constructs a Menu Content Handler.
    */
  public MenuContentHandlerImpl()
  {
    super();
    
    // Init the menu list map
    if (_treeModelMap == null)
      _treeModelMap = new HashMap<String, TreeModel>();
  }
  
  /**
   * Called by the SAX Parser at the start of parsing a document.
   */
  public void startDocument()
  {
    _nodeDepth = 0;
    _menuNodes = new ArrayList();
    _menuList  = null;
   
    // Handler Id will have to change also to be unique 
    _handlerId = Integer.toString(System.identityHashCode((Object) _menuNodes));
  }
  
  /**
    * Start the parsing of an node element entry in the menu metadata file.
    * <p>
    * If the entry is for an itemNode or a destinationNode, create the node
    * and it to the List.  If the entry is for a sharedNode, a new submenu
    * model is created.
    * 
    * @param nameSpaceUri - only used when passed to super class.
    * @param localElemName - only used when passed to super class.
    * @param qualifiedElemName - String designating the node type of the entry.
    * @param attrList - List of attributes in the menudata entry.
    * @throws SAXException
    */
  public void startElement(String nameSpaceUri, String localElemName, 
                           String qualifiedElemName, Attributes attrList)
    throws SAXException
  {
    super.startElement(nameSpaceUri, localElemName, qualifiedElemName, 
                       attrList);
                       
    if (_ROOT_NODE.equals(qualifiedElemName))
    {
      // Unless both of these are specified, don't attempt to load
      // the resource bundle.
      String resBundle    = attrList.getValue(_RES_BUNDLE_ATTR);
      String resBundleKey = attrList.getValue(_VAR_ATTR);
      
      if (   (resBundle != null    && !"".equals(resBundle))
          && (resBundleKey != null && !"".equals(resBundleKey))
         )
      {        
        // Load the resource Bundle.
        // Ensure the bundle key is unique by appending the 
        // handler Id.
        MenuUtils.loadBundle(resBundle, resBundleKey + getHandlerId());
        _resBundleKey  = resBundleKey;
        _resBundleName = resBundle;
      }
    }
    else
    {
      // Either itemNode, destinationNode, or groupNode
      boolean isNonSharedNode = (   _ITEM_NODE.equals(qualifiedElemName)
                                 || _GROUP_NODE.equals(qualifiedElemName)
                                );
               
      if (isNonSharedNode) 
      {
        _currentNodeStyle = (  _ITEM_NODE.equals(qualifiedElemName) 
                             ? MenuConstants.NODE_STYLE_ITEM
                             : MenuConstants.NODE_STYLE_GROUP
                            );
        _nodeDepth++;
        
        if ((_skipDepth >= 0) && (_nodeDepth > _skipDepth))
        {
          // This sub-tree is being skipped, so just return
          return;
        }
        
        if (_menuNodes.size() < _nodeDepth) 
        {
          ArrayList list = new ArrayList();
          _menuNodes.add(list);
        }

        _attrList = new AttributesImpl(attrList);
          
        // Create either an itemNode or groupNode.
        MenuNode menuNode = _createMenuNode();
        
        if (menuNode == null)
        {
          // No menu item is created, so note that we are 
          // now skipping the subtree
          _skipDepth = _nodeDepth;
        }
        else
        {
          if (   (_resBundleName != null && !"".equals(_resBundleName))
              && (_resBundleKey  != null && !"".equals(_resBundleKey))
             )
          {
            menuNode.setResBundleKey(_resBundleKey);
            menuNode.setResBundleName(_resBundleName);
          }
          
          // Set the node's MenuContentHandlerImpl id so that when
          // the node's getLabel() method is called, we can
          // use the handlerId to insert into the label
          // if it is an EL expression.
          menuNode.setHandlerId(getHandlerId());
          
          // Set the root model on the node so we can call into
          // the root model from the node to populate its
          // idNodeMap (See CombinationMenuModel.java)
          menuNode.setRootModelUri(getRootModelUri());
          
          // Set the local model (created when parsing a sharedNode)
          // on the node in case the node needs to get back to its
          // local model.
          // menuNode.setModel(getModel());
          
          List list = (List)_menuNodes.get(_nodeDepth-1);
          list.add(menuNode);
        }
      }
      else if (_SHARED_NODE.equals(qualifiedElemName))
      {
        _nodeDepth++;
  
        // SharedNode's "ref" property points to another submenu's metadata,
        // and thus a new model, which we build here.  Note: this will
        // recursively call into this MenuContentHandlerImpl when parsing the
        // submenu's metadata.
        String expr            = attrList.getValue(_REF_ATTR);
        
        // Need to push several items onto the stack now as we recurse
        // into another menu model.
        _saveModelData();        

        XMLMenuModel menuModel = (XMLMenuModel)MenuUtils.getBoundValue(expr);
        
        // Now must pop the values cause we are back to the parent
        // model.
        _restoreModelData();
        
        Object subMenuObj      = menuModel.getWrappedData();
        List subMenuList       = null;
        
        if (subMenuObj instanceof ChildPropertyTreeModel)
        {
          subMenuList =  
            (List)((ChildPropertyTreeModel)subMenuObj).getWrappedData();
        }
  
        // SharedNode could be the first child
        // So we need a new list for the children
        if (_menuNodes.size() < _nodeDepth) 
        {
          ArrayList list = new ArrayList();
          _menuNodes.add(list);
        }
        
        List list = (List)_menuNodes.get(_nodeDepth-1);
        list.addAll(subMenuList);
      }
    }
  }
  
  /**
   * Processing done at the end of parsing a node enty element from the 
   * menu metadata file.  This manages the node depth properly so that
   * method startElement works correctly to build the List.
   * 
   * @param nameSpaceUri - not used.
   * @param localElemName - not used.
   * @param qualifiedElemName - String designating the node type of the entry.
   */
  public void endElement(String nameSpaceUri, String localElemName, String qualifiedElemName)
  {
    if (   _ITEM_NODE.equals(qualifiedElemName) 
        || _GROUP_NODE.equals(qualifiedElemName)
       ) 
    {
      _nodeDepth--;

      if (_skipDepth >= 0)
      {
        if (_nodeDepth < _skipDepth)
        {
          _skipDepth = -1;
        }
      }
      else
      {
        if (_nodeDepth > 0)
        {
          // The parent menu item is the last menu item at the previous depth
          List      parentList = (List)_menuNodes.get(_nodeDepth-1);
          MenuNode  parentNode = (MenuNode)parentList.get(parentList.size()-1);
          
          parentNode.setChildren((List)_menuNodes.get(_nodeDepth));
        }

        // If we have dropped back two levels, then we are done adding
        // the parent's sub tree, remove the list of menu items at that level
        // so they don't get added twice.
        if (_nodeDepth == (_menuNodes.size() - 2)) 
        {
          _menuNodes.remove(_nodeDepth+1);
        }
      }
    }
    else if (_SHARED_NODE.equals(qualifiedElemName))
    {
      _nodeDepth--;

      if (_nodeDepth > 0)
      {
        // The parent menu item is the last menu item at the previous depth
        List      parentList = (List)_menuNodes.get(_nodeDepth-1);
        MenuNode  parentNode = (MenuNode)parentList.get(parentList.size()-1);
        
        parentNode.setChildren((List)_menuNodes.get(_nodeDepth));
      }
    }
  }

  /**
   * Called by the SAX Parser at the end of parsing a document. 
   * Here, the menuList is put on the menuList map.
   */
  public void endDocument()
  {
    _menuList = (List)_menuNodes.get(0);
    
    // Create the treeModel
    ChildPropertyTreeModel treeModel = 
                  new ChildPropertyTreeModel((Object)_menuList, "children");
                  
    // Put it in the map
    _treeModelMap.put(_currentTreeModelMapKey, treeModel);
    
    // If Model is the Root, then build Model's hashmaps 
    // and set them on the Root Model.
    if (getRootModelUri().equals(getModelUri()))
    {
      _viewIdFocusPathMap = new HashMap<String, ArrayList>();
      _nodeFocusPathMap   = new HashMap<Object, ArrayList>();
      _idNodeMap          = new HashMap<String, Object>();
      Object oldPath      = treeModel.getRowKey(); 

      treeModel.setRowKey(null);

      // Populate the maps
      _addToMaps(treeModel, _viewIdFocusPathMap, _nodeFocusPathMap, _idNodeMap);

      treeModel.setRowKey(oldPath);
    }
  }
  
  /**
   * Get the Model's viewIdFocusPathMap
   * 
   * @return the Model's viewIdFocusPathMap
   */
  public Map getViewIdFocusPathMap()
  {
    return _viewIdFocusPathMap;
  }
  
  /**
   * Get the Model's nodeFocusPathMap
   * 
   * @return the Model's nodeFocusPathMap
   */
  public Map getNodeFocusPathMap()
  {
    return _nodeFocusPathMap;
  }

  /**
   * Get the Model's idNodeMap
   * 
   * @return the Model's idNodeMap
   */
  public Map getIdNodeMap()
  {
    return _idNodeMap;
  }

  /**
    * Get the treeModel built during parsing
    * 
    * @return List of menu nodes.
    */
  public TreeModel getTreeModel(String uri)
  {
    TreeModel model = _treeModelMap.get(uri);
    if (model == null)
    {
      _currentTreeModelMapKey = uri;

      try
      {
        // Get a parser.  NOTE: we are using the jdk's 1.5 SAXParserFactory
        // and SAXParser here.
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        
        
        // Parse the metadata
        InputStream inStream = _getStream(uri);
        parser.parse(inStream, this);
      }
      catch (SAXException saxex)
      {
        _LOG.severe ( "Exception creating model " + uri, saxex);
      }
      catch (IOException ioe)
      {
        _LOG.severe ( "Exception creating model " + uri, ioe);
      }
      catch (ParserConfigurationException pce)
      {
        _LOG.severe ( "Exception creating model " + uri, pce);
      }
    }

    return _treeModelMap.get(uri);
  }

  /**
   * Get the top-level, root menu model, which contains
   * the entire menu tree.
   * 
   * @return root, top-level XMLMenuModel
   */
  public XMLMenuModel getRootModel()
  {
    FacesContext facesContext = FacesContext.getCurrentInstance();
    Map requestMap = facesContext.getExternalContext().getRequestMap();
    
    return (XMLMenuModel) requestMap.get(getRootModelUri());
  }
  
  /**
   * Get the top-level, root menu model's Uri.
   * 
   * @return root, top-level XMLMenuModel's Uri
   */
  public String getRootModelUri()
  {
    return _rootModelUri;
  }
  
  /**
   * Sets the root menu Model's Uri.
   * <p>
   * This is always only the top-level, root model's Uri.
   * We do this because the MenuContentHandlerImpl and nodes need to be able 
   * to call into the root model to:
   * <ul>
   * <li>notify them root menu model of the currently selected node on a POST
   * </ul>
   * 
   * @param rootModelUri - String the root, top-level menu model's Uri.
   */
  public void setRootModelUri(String rootModelUri)
  {
    _rootModelUri = rootModelUri;
  }

  /**
   * Get the local (sharedNode) menu model.
   * 
   * @return sharedNode's XMLMenuModel
   */
  public XMLMenuModel getModel()
  {
    FacesContext facesContext = FacesContext.getCurrentInstance();
    Map requestMap = facesContext.getExternalContext().getRequestMap();
    
    return (XMLMenuModel) requestMap.get(getModelUri());
  }
  
  /**
   * Get the local (sharedNode) menu model's Uri.
   * 
   * @return sharedNode's XMLMenuModel Uri
   */
  public String getModelUri()
  {
    return _localModelUri;
  }
  
  /**
   * Sets the local (sharedNode's) menu Model's Uri.
   * 
   * @param localModelUri - String the root, top-level menu model's Uri.
   */
  public void setModelUri(String localModelUri)
  {
    _localModelUri = localModelUri;
  }

  /**
   * Sets the treeModel map key used to get a cached treeModel 
   * from the MenuContentHandlerImpl.
   * 
   * Note: this is set from the XMLMenuModel BEFORE parsing begins
   * 
   * @param uri String path to the menu model's metadata
   */
  public void setTreeModelKey(String uri)
  {
    _currentTreeModelMapKey = uri;
  }
  
  //=======================================================================
  // Package Private Methods
  //=======================================================================
  /**
   * Gets the MenuContentHandlerImpl's id.
   * 
   * This is set in the MenuContentHandlerImpl's Constructor
   * and is used to ensure that the all resource bundle keys
   * and node ids are unique.
   * 
   * @return String handler id.
   */
  String getHandlerId()
  {
    return _handlerId;
  }
    
  /**
   * Returns the hashmap key for a resource bundle.
   * 
   * This the value of the "var" attribute for the menu root node
   * from the menu's metadata
   * 
   * @return String hashmap key.
   */
  String getBundleKey()
  {
    return _resBundleKey;
  }
  
  //=======================================================================
  // Private Methods
  //=======================================================================
  
  /**
   * Creates a MenuNode from attribute list.
   *
   * @return MenuNode used in the Menu List.
   */
  private MenuNode _createMenuNode ()
  {
    // Get generic attributes
    
    // If the node has rendered = false, do not create it.
    // This is a security risk and cannot be allowed
    String renderedStr = _getAndRemoveAttrValue(_RENDERED_ATTR);
    
    // We do not create nodes whose rendered attr is false
    // and if the Root model or the local model's (sharedNode 
    // model) says that nodes whose rendered attribute is false
    // should not be created, then we don't either.
    //
    // This default value of false (don't create nodes whose
    // rendered attr is false) can be overridden by the 
    // XMLMenuModel's managed property, createHiddenNodes.  
    // Typically this is done in faces-config.xml
    //
    if (   "false".equals(renderedStr)
        && (   !getRootModel().getCreateHiddenNodes()
            || !getModel().getCreateHiddenNodes()
           )
       )
    {
      return null;
    }
      
    String label       = _getAndRemoveAttrValue(_LABEL_ATTR);
    String icon        = _getAndRemoveAttrValue(_ICON_ATTR);
    String disabledStr = _getAndRemoveAttrValue(_DISABLED_ATTR);
    String readOnlyStr = _getAndRemoveAttrValue(_READONLY_ATTR);
    String accessKey   = _getAndRemoveAttrValue(_ACCESSKEY_ATTR);
    String labelAndAccessKey   = _getAndRemoveAttrValue(_LABEL_AND_ACCESSKEY_ATTR);
    String id          = _getAndRemoveAttrValue(_ID_ATTR);
    String visibleStr  = _getAndRemoveAttrValue(_VISIBLE_ATTR);
    
    MenuNode menuNode = 
        (  _currentNodeStyle == MenuConstants.NODE_STYLE_ITEM
         ? _createItemNode()
         : _createGroupNode()
        );
  
    // Set the generic attributes
    menuNode.setLabel(label);
    menuNode.setIcon(icon);
    menuNode.setDisabled(disabledStr);
    menuNode.setRendered(renderedStr);
    menuNode.setReadOnly(readOnlyStr);
    menuNode.setAccessKey(accessKey);
    menuNode.setId(id);
    menuNode.setVisible(visibleStr);
    
    if (labelAndAccessKey != null)
      menuNode.setLabelAndAccessKey(labelAndAccessKey);

    // Set the Any Attributes Attrlist
    if (_attrList.getLength() > 0)
    {
      menuNode.setCustomPropList(_attrList);
    }
    
    return menuNode;
  }
  
  /**
    * Creates an itemNode from attribute list obtained by parsing an 
    * itemNode menu metadata entry.
    * 
    * @return Node of type ItemNode.
    */
  private ItemNode _createItemNode()
  {
    // Create the itemNode  
    ItemNode itemNode = new ItemNode();

    String action         = _getAndRemoveAttrValue(_ACTION_ATTR);
    String actionListener = _getAndRemoveAttrValue(_ACTIONLISTENER_ATTR);
    String launchListener = _getAndRemoveAttrValue(_LAUNCHLISTENER_ATTR);
    String returnListener = _getAndRemoveAttrValue(_RETURNLISTENER_ATTR);
    String immediate      = _getAndRemoveAttrValue(_IMMEDIATE_ATTR);
    String useWindow      = _getAndRemoveAttrValue(_USEWINDOW_ATTR);
    String windowHeight   = _getAndRemoveAttrValue(_WINDOWHEIGHT_ATTR);
    String windowWidth    = _getAndRemoveAttrValue(_WINDOWWIDTH_ATTR);
    String defaultFocusPathStr = _getAndRemoveAttrValue(_DEFAULT_FOCUS_PATH_ATTR);
    String focusViewId    = _getAndRemoveAttrValue(_FOCUS_VIEWID_ATTR);

    // Former Destination node attrs
    String destination = _getAndRemoveAttrValue(_DESTINATION_ATTR);
    String targetFrame = _getAndRemoveAttrValue(_TARGETFRAME_ATTR);
    
    // An item node with one of two(2) possible values:
    // 1) outcome
    // 2) EL method binding  (which can return either a URI or 
    //    an outcome

    // Set its properties - null is ok.
    itemNode.setAction(action);
    itemNode.setActionListener(actionListener);
    itemNode.setLaunchListener(launchListener);
    itemNode.setReturnListener(returnListener);
    itemNode.setImmediate(immediate);
    itemNode.setUseWindow(useWindow);
    itemNode.setWindowHeight(windowHeight);
    itemNode.setWindowWidth(windowWidth);
    itemNode.setFocusViewId(focusViewId);
    itemNode.setDefaultFocusPath(defaultFocusPathStr);

    // Former destination node attrs
    itemNode.setDestination(destination);
    itemNode.setTargetFrame(targetFrame);
    
    return itemNode;
  }

  /**
    * Creates a GroupNode from attribute list passed obtained by parsing
    * a GroupNode menu metadata entry.
    * 
    * @return Node of type GroupNode
    */
  private GroupNode _createGroupNode()
  {
    // Create the GroupNode    
    GroupNode groupNode = new GroupNode();
    String idRef = _getAndRemoveAttrValue(_IDREF_ATTR);
      
    // Set its attributes - null is ok
    groupNode.setIdRef(idRef);
    
    return groupNode;
  }

  /**
   * Saves all information needed for parsing and building model data 
   * before recursing into the new model of a sharedNode.
   * 
   * Note: if you add a new push in this method, you must also add
   * a corresponding pop in _restoreModelData() below in the correct order.
   */
  private void _saveModelData()
  {
    if (_saveDataStack == null)
    {
      _saveDataStack = new Stack();
    }

    // DO NOT CHANGE THE ORDER HERE.  IT MUST MATCH
    // "pushes" DONE BELOW in _restoreModelData.
    int nodeDepthSave       = _nodeDepth;
    ArrayList menuNodesSave = new ArrayList(_menuNodes);
    
    
    ArrayList menuListSave  = (  _menuList != null
                               ? new ArrayList(_menuList)
                               : null
                              );
                              
    String mapTreeKeySave   = _currentTreeModelMapKey;
    String localModelUriSave   = _localModelUri;
    String handlerId = _handlerId;
    String resBundleName = _resBundleName;
    String resBundleKey = _resBundleKey;
    _saveDataStack.push(nodeDepthSave);
    _saveDataStack.push(menuNodesSave);
    _saveDataStack.push(menuListSave);
    _saveDataStack.push(mapTreeKeySave);        
    _saveDataStack.push(localModelUriSave);        
    _saveDataStack.push(handlerId);        
    _saveDataStack.push(resBundleName);        
    _saveDataStack.push(resBundleKey);            
  }

  /**
   * Restores data needed for parsing and building model data
   * as execution returns from creating a sharedNode child menu model.
   *
   * Note: if you add a new pop in this method, you must also add
   * a corresponding push in _saveModelData() above in the correct order.
   */
  private void _restoreModelData()
  {
    // DO NOT CHANGE THE ORDER HERE.  IT MUST MATCH
    // "pushes" DONE ABOVE in _saveModelData.
    _resBundleKey           = (String) _saveDataStack.pop();
    _resBundleName          = (String) _saveDataStack.pop();
    _handlerId              = (String) _saveDataStack.pop();
    _localModelUri          = (String) _saveDataStack.pop();
    _currentTreeModelMapKey = (String) _saveDataStack.pop();
    _menuList               = (ArrayList) _saveDataStack.pop();
    _menuNodes              = (ArrayList) _saveDataStack.pop();
    _nodeDepth              = ((Integer)_saveDataStack.pop()).intValue();    
  }
  
  /**
   * Gets the specified attribute's value from the Attributes List
   * passed in by the parser.  Also removes this attribute so that 
   * once we are finished processing and removing all the known 
   * attributes, those left are custom attributes.
   * 
   * @param attrName
   * @return String value of the attribute in the Attributes List.
   */
  private String _getAndRemoveAttrValue(String attrName)  
  {
    int idx = _attrList.getIndex(attrName);
    
    if (idx == -1)
      return null;
      
    String attrValue = _attrList.getValue(idx);
    _attrList.removeAttribute(idx);
    return attrValue;
  }

  /*=========================================================================
   * Menu Model Data Structure section.
   * ======================================================================*/
  /**
   * Traverses the tree and builds the model's viewIdFocusPathMap, 
   * nodeFocusPathMap, and _idNodeMap
   * 
   * @param tree
   */
  private void _addToMaps(
    TreeModel tree,
    Map viewIdFocusPathMap,
    Map nodeFocusPathMap,
    Map idNodeMap)
  {
    for ( int i = 0; i < tree.getRowCount(); i++)
    {
      tree.setRowIndex(i);

      // Get the node
      MenuNode node = (MenuNode) tree.getRowData();

      // Get its focus path
      ArrayList focusPath = (ArrayList)tree.getRowKey();
      
      // Get the focusViewId of the node
      Object viewIdObject = node.getFocusViewId(); 
      
      if (viewIdObject != null)
      {          
        // Put this entry in the nodeFocusPathMap
        nodeFocusPathMap.put(node, (Object)focusPath);

        // Does this viewId already exist in the _viewIdFocusPathMap?
        ArrayList existingFpArrayList = 
            (ArrayList) viewIdFocusPathMap.get(viewIdObject);
        
        if (existingFpArrayList == null)
        {
          // This is a node with a unique focusViewId.  Simply create
          // and Arraylist and add the focusPath as the single entry
          // to the focus path ArrayList.  Then put the focusPath 
          // ArrayList in the focusPath HashMap.
          ArrayList<ArrayList> fpArrayList = new ArrayList<ArrayList>();
          fpArrayList.add(focusPath);
          viewIdFocusPathMap.put(viewIdObject, (Object)fpArrayList);
        }
        else
        {
          // This is a node that points to the same viewId as at least one 
          // other node.
          
          // If the node's defaultFocusPath is set to true, we move it to
          // the head of the ArrayList. The 0th element of the list is 
          // always returned when navigation to a viewId occurs from outside 
          // the menu model (that is _currentNode is null)
          boolean defFocusPath = node.getDefaultFocusPath();
          
          if (defFocusPath)
          {
            existingFpArrayList.add(0, (Object)focusPath);
          }
          else
          {
            existingFpArrayList.add((Object)focusPath);
          }              
        }
      }
      
      // Get the Id of the node
      String idProp = node.getId(); 
      
      if (idProp != null)
      {
        idNodeMap.put(idProp, node);
      }
      
      if (tree.isContainer() && !tree.isContainerEmpty())
      {
        tree.enterContainer();
        _addToMaps(tree, viewIdFocusPathMap, nodeFocusPathMap, idNodeMap);
        tree.exitContainer();
      }
    }
  }  
  

  /**
   * getStream - Opens an InputStream to the provided URI.
   * 
   * @param uri - String uri to a data source.
   * @return InputStream to the data source.
   */
  private InputStream _getStream(String uri)
  {
    try
    {
      // Open the metadata  
      FacesContext context = FacesContext.getCurrentInstance();
      URL url = context.getExternalContext().getResource(uri);
      return url.openStream();
    }
    catch (Exception ex)
    {
      _LOG.severe("Exception opening URI " + uri, ex);
      return null;
    }    
  }


  //========================================================================
  // Private variables
  //========================================================================
  
  private List   _menuNodes;
  private List   _menuList;
  private String _currentTreeModelMapKey;
  private int    _nodeDepth;
  private int    _skipDepth = -1;
  private String _currentNodeStyle;
  private String _handlerId;
  private String _resBundleKey;
  private String _resBundleName;
  private AttributesImpl _attrList;
  private Map<String, TreeModel> _treeModelMap;
  private Stack  _saveDataStack;
  private Map    _viewIdFocusPathMap;
  private Map    _nodeFocusPathMap;
  private Map    _idNodeMap;

  
  // Menu model Uri's
  private String _rootModelUri  = null;
  private String _localModelUri = null;

  // Nodes
  private final static String _GROUP_NODE        = "groupNode";
  private final static String _ITEM_NODE         = "itemNode";
  private final static String _SHARED_NODE       = "sharedNode";
  private final static String _ROOT_NODE         = "menu";
  
  // Attributes    
  private final static String _LABEL_ATTR        = "label";
  private final static String _RENDERED_ATTR     = "rendered";
  private final static String _ID_ATTR           = "id";
  private final static String _IDREF_ATTR        = "idref";
  private final static String _ICON_ATTR         = "icon";
  private final static String _DISABLED_ATTR     = "disabled";
  private final static String _DESTINATION_ATTR  = "destination";
  private final static String _ACTION_ATTR       = "action";
  private final static String _REF_ATTR          = "ref";    
  private final static String _READONLY_ATTR     = "readOnly";
  private final static String _VAR_ATTR          = "var";
  private final static String _RES_BUNDLE_ATTR   = "resourceBundle";
  private final static String _FOCUS_VIEWID_ATTR = "focusViewId";
  private final static String _ACCESSKEY_ATTR    = "accessKey";
  private final static String _LABEL_AND_ACCESSKEY_ATTR = "labelAndAccessKey";
  private final static String _TARGETFRAME_ATTR  = "targetframe";
  private final static String _ACTIONLISTENER_ATTR = "actionListener";
  private final static String _LAUNCHLISTENER_ATTR = "launchListener";
  private final static String _RETURNLISTENER_ATTR = "returnListener";
  private final static String _IMMEDIATE_ATTR      = "immediate";
  private final static String _USEWINDOW_ATTR      = "useWindow";
  private final static String _WINDOWHEIGHT_ATTR   = "windowHeight";
  private final static String _WINDOWWIDTH_ATTR    = "windowWidth";
  private final static String _DEFAULT_FOCUS_PATH_ATTR  = "defaultFocusPath";
  private final static String _VISIBLE_ATTR        = "visible";
    
  private final static TrinidadLogger _LOG = 
                        TrinidadLogger.createTrinidadLogger(MenuContentHandlerImpl.class);
  
} // endclass MenuContentHandlerImpl
