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
package org.apache.myfaces.trinidad.component;

import java.io.IOException;

import java.util.Collection;
import java.util.Iterator;

import javax.faces.component.UIComponent;

import javax.el.MethodExpression;

import javax.faces.component.NamingContainer;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;

import javax.faces.event.PhaseId;
import javax.faces.render.Renderer;

import org.apache.myfaces.trinidad.bean.FacesBean;
import org.apache.myfaces.trinidad.component.visit.VisitCallback;
import org.apache.myfaces.trinidad.component.visit.VisitContext;
import org.apache.myfaces.trinidad.component.visit.VisitHint;
import org.apache.myfaces.trinidad.component.visit.VisitResult;
import org.apache.myfaces.trinidad.context.PartialPageContext;
import org.apache.myfaces.trinidad.context.RenderingContext;
import org.apache.myfaces.trinidad.event.AttributeChangeListener;
import org.apache.myfaces.trinidad.render.CoreRenderer;

/**
 * Pure abstract base class for all UIX components.
 */
abstract public class UIXComponent extends UIComponent
{
  /**
   * Helper function called by Renderers to iterate over a flattened view of a group of
   * potentially FlattenedComponent instances rooted at a single child of the component that
   * the Renderer is encoding, invoking the <code>childProcessor</code> with its
   * <code>callbackContext</code> on each renderable instance.
   * <p>
   * If the child is a FlattenedComponent, the <code>childProcessor</code> will
   * be called on each of that FlattenedComponent's children, recursing if those children are
   * themselves FlattenedComponents, otherwise, the <code>childProcessor</code> will be called on
   * the child itself.
   * <p>
   * This method is typically used to flatten the contents of a facet on the FlattenedComponent to
   * be encoded.  If the Renderer accidentally passes in the component to be encoded instead of one
   * of its children, the result will almost certainly be an infinite recursion and stack overflow.
   * @see UIXComponent#processFlattenedChildren(FacesContext, ComponentProcessor, Iterable, Object)
   * @see UIXComponent#processFlattenedChildren(FacesContext, ComponentProcessingContext, ComponentProcessor, UIComponent, Object)
   * @see UIXComponent#processFlattenedChildren(FacesContext, ComponentProcessingContext, ComponentProcessor, Iterable, Object)
   * @see FlattenedComponent
   */
  public static <S> boolean processFlattenedChildren(
    FacesContext context,
    ComponentProcessor<S> childProcessor,
    UIComponent child,
    S callbackContext) throws IOException
  {
    return processFlattenedChildren(context,
                                    new ComponentProcessingContext(),
                                    childProcessor,
                                    child,
                                    callbackContext);
  }

  /**
   * Helper function called by FlattenedComponent to iterate over a flattened view of a group of
   * potentially FlattenedComponent instances rooted at a single child of the FlattenedComponent,
   * invoking the <code>childProcessor</code> with its
   * <code>callbackContext</code> on each renderable instance.
   * <p>
   * If the child is a FlattenedComponent, the <code>childProcessor</code> will
   * be called on each of that FlattenedComponent's children, recursing if those children are
   * themselves FlattenedComponents, otherwise, the <code>childProcessor</code> will be called on
   * the child itself.
   * <p>
   * This method is typically used to flatten the contents of a facet of the FlattenedComponent.
   * If the FlattenedComponent accidentally passes in itself instead of one
   * of its children, the result will almost certainly be an infinite recursion and stack overflow.
   * @see UIXComponent#processFlattenedChildren(FacesContext, ComponentProcessor, UIComponent, Object)
   * @see UIXComponent#processFlattenedChildren(FacesContext, ComponentProcessor, Iterable, Object)
   * @see UIXComponent#processFlattenedChildren(FacesContext, ComponentProcessingContext, ComponentProcessor, Iterable, Object)
   * @see FlattenedComponent
   */
    public static <S> boolean processFlattenedChildren(
    FacesContext context,
    ComponentProcessingContext cpContext,
    ComponentProcessor<S> childProcessor,
    UIComponent child,
    S callbackContext) throws IOException
  {
    if (child.isRendered())
    {      
       // component is an action FlattenedComponent.  Ask it to flatten its children
      if ((child instanceof FlattenedComponent) &&
          ((FlattenedComponent)child).isFlatteningChildren(context))
      {
        return ((FlattenedComponent)child).processFlattenedChildren(context,
                                                                    cpContext,
                                                                    childProcessor,
                                                                    callbackContext);
      }
      else
      {
        // not a FlattenedComponent, pass the component directly to the ComponentProcessor
        try
        {
          childProcessor.processComponent(context, cpContext, child, callbackContext);
        }
        finally
        {
          // if startDepth is > 0, only the first visible child will be marked as starting a group
          cpContext.resetStartDepth();
        }
        
        return true;
      }
    }
    else
    {
      // component not rendered
      return false;
    }
  }

  /**
   * Helper function called by Renderers to iterate over a flattened view of the
   * children, potentially containing FlattenedComponents, of the component the Renderer is
   * encoding, invoking the <code>childProcessor</code> with its
   * <code>callbackContext</code> on each renderable instance.
   * <p>
   * For each FlattenedComponent child, the <code>childProcessor</code> will
   * be called on each of that FlattenedComponent's children, recursing if those children are
   * themselves FlattenedComponents, otherwise, the <code>childProcessor</code> will be called on
   * the child itself.
   * <p>
   * This method is typically used to flatten the children of the FlattenedComponent to
   * be encoded.
   * @see UIXComponent#processFlattenedChildren(FacesContext, ComponentProcessor, UIComponent, Object)
   * @see UIXComponent#processFlattenedChildren(FacesContext, ComponentProcessingContext, ComponentProcessor, UIComponent, Object)
   * @see UIXComponent#processFlattenedChildren(FacesContext, ComponentProcessingContext, ComponentProcessor, Iterable, Object)
   * @see FlattenedComponent
   */
  public static <S> boolean processFlattenedChildren(
    FacesContext context,
    ComponentProcessor<S> childProcessor,
    Iterable<UIComponent> children,
    S callbackContext) throws IOException
  {
    return processFlattenedChildren(context,
                                    new ComponentProcessingContext(),
                                    childProcessor,
                                    children,
                                    callbackContext);
  }

  /**
   * Helper function called by FlattenedComponents to iterate over a flattened view of their
   * children, potentially themselves FlattenedComponents, invoking the <code>childProcessor</code>
   * with its <code>callbackContext</code> on each renderable instance.
   * <p>
   * For each FlattenedComponent child, the <code>childProcessor</code> will
   * be called on each of that FlattenedComponent's children, recursing if those children are
   * themselves FlattenedComponents, otherwise, the <code>childProcessor</code> will be called on
   * the child itself.
   * @see UIXComponent#processFlattenedChildren(FacesContext, ComponentProcessor, UIComponent, Object)
   * @see UIXComponent#processFlattenedChildren(FacesContext, ComponentProcessor, Iterable, Object)
   * @see UIXComponent#processFlattenedChildren(FacesContext, ComponentProcessingContext, ComponentProcessor, UIComponent, Object)
   * @see FlattenedComponent
   */
  public static <S> boolean processFlattenedChildren(
    FacesContext context,
    ComponentProcessingContext cpContext,
    ComponentProcessor<S> childProcessor,
    Iterable<UIComponent> children,
    S callbackContext) throws IOException
  {
    // we haven't processed a child yet
    boolean processedChild = false;
    
    for (UIComponent currChild : children)
    {
      // latch processed child to the first child processed
      processedChild |= processFlattenedChildren(context,
                                                 cpContext, childProcessor,
                                                 currChild,
                                                 callbackContext);
    }
    
    return processedChild;
  }

  /**
  * <p>Perform a tree visit starting at
  * this node in the tree.</p>
  *
  * <p>UIXComponent.visitTree() implementations do not invoke the
  * {@code VisitCallback} directly, but instead call
  * {@code VisitContext.invokeVisitCallback()} to invoke the
  * callback.  This allows {@code VisitContext} implementations
  * to provide optimized tree traversals, for example by only
  * calling the {@code VisitCallback} for a subset of components.</p>
  *
  * @param visitContext the <code>VisitContext</code> for this visit
  * @param callback the <code>VisitCallback</code> instance
  * whose <code>visit</code> method will be called
  * for each node visited.
  * @return component implementations may return <code>true</code>
  *   to indicate that the tree visit is complete (eg. all components
  *   that need to be visited have been visited).  This results in
  *   the tree visit being short-circuited such that no more components
  *   are visited.
  *
  * @see VisitContext#invokeVisitCallback VisitContext.invokeVisitCallback()
  */
  public boolean visitTree(
    VisitContext visitContext,
    VisitCallback callback)
  {
    return visitTree(visitContext, this, callback);
  }
  
  /**
  * <p>Perform a tree visit starting at the specified node in the tree.</p>
  *
  * <p>UIXComponent.visitTree() implementations do not invoke the
  * {@code VisitCallback} directly, but instead call
  * {@code VisitContext.invokeVisitCallback()} to invoke the
  * callback.  This allows {@code VisitContext} implementations
  * to provide optimized tree traversals, for example by only
  * calling the {@code VisitCallback} for a subset of components.</p>
  *
  * @param visitContext the <code>VisitContext</code> for this visit
  * @param component the <code>UIComponent</code> to start the visit from
  * @param callback the <code>VisitCallback</code> instance
  * whose <code>visit</code> method will be called
  * for each node visited.
  * @return component implementations may return <code>true</code>
  *   to indicate that the tree visit is complete (eg. all components
  *   that need to be visited have been visited).  This results in
  *   the tree visit being short-circuited such that no more components
  *   are visited.
  *
  * @see VisitContext#invokeVisitCallback VisitContext.invokeVisitCallback()
  */
  public static boolean visitTree(
    VisitContext visitContext,
    UIComponent   component,
    VisitCallback callback)
  {
    UIXComponent uixComponent;

    // determine if we should even visit this component subtree
    if (component instanceof UIXComponent)
    {
      uixComponent = (UIXComponent)component;
      
      // delegate to the UIXComponent
      if (!uixComponent.isVisitable(visitContext))
        return false;
    }
    else
    {
      // use generic isVisitable implemetation
      if (!_isVisitable(visitContext, component))
        return false;
      
      uixComponent = null;
    }
    
    // invoke the callback for this component
    VisitResult result = visitContext.invokeVisitCallback(component, callback);

    if (result == VisitResult.COMPLETE)
      return true;
    else if (result == VisitResult.ACCEPT)
    {
      // now visit the children
      FacesContext context = visitContext.getFacesContext();
      PhaseId phaseId = visitContext.getPhaseId();
      RenderingContext rc = (PhaseId.RENDER_RESPONSE == phaseId)
                              ? RenderingContext.getCurrentInstance()
                              : null;
      
      if (uixComponent != null)
      {
        // assume that all UIXComponent NamingContainers always act as NamingContainers,
        // (unlike <h:form>) and this it is OK to put the optimization where we
        // don't visit the children if we know that we don't have any ids in this
        // subtree to visit
        if (uixComponent instanceof NamingContainer)
        {
          if (visitContext.getSubtreeIdsToVisit(uixComponent).isEmpty())
            return false;
        }
        
        // UIXComponents are allowed to set up their context differently for encoding
        // than normal processing, so behave differently if this is the RenderResponse
        // phase
        if (PhaseId.RENDER_RESPONSE == phaseId)
        {
          uixComponent.setUpEncodingContext(context, rc);
        }
        else
        {
          uixComponent.setupVisitingContext(context);
        }
      }
      else
      {
        // we only optimize walking into non-UIXComponent NamingContainers
        // if they are UINamingConainer (which is used by <f:subview>
        if (UINamingContainer.class == component.getClass())
        {
          if (visitContext.getSubtreeIdsToVisit(component).isEmpty())
            return false;
        }
      }
      
      // visit the children of the component
      try
      {
        Iterator<UIComponent> kids = component.getFacetsAndChildren();
              
        while(kids.hasNext())
        {
          boolean done;
          
          UIComponent currChild = kids.next();
          
          if (currChild instanceof UIXComponent)
          {
            UIXComponent uixChild = (UIXComponent)currChild;
            
            // delegate to UIXComponent's visitTree implementation to allow
            // subclassses to modify the behavior
            done = uixChild.visitTree(visitContext, callback);
          }
          else
          {
            // use generic visit implementation
            done = visitTree(visitContext, currChild, callback);
          }
          
          // If any kid visit returns true, we are done.
          if (done)
          {
            return true;
          }
        }
      }
      finally
      {
        // tear down the context we set up in order to visit our children
        if (uixComponent != null)
        {
          if (PhaseId.RENDER_RESPONSE == phaseId)
          {
            uixComponent.tearDownEncodingContext(context, rc);
          }
          else
          {
            uixComponent.tearDownVisitingContext(context);
          }
        }
      }
    }
    else
    {
      assert(result == VisitResult.REJECT);      
    }

    // if we got this far, we're not done
    return false;
  }

  
  
  /**
   * <p>Called by
   * {@link UIXComponent#visitTree UIXComponent.visitTree()} to determine
   * whether this component is "visitable" - ie. whether this component
   * satisfies the {@link org.apache.myfaces.trinidad.component.visit.VisitHints} returned by
   * {@link VisitContext#getHints VisitContext.getHints()}.</p>
   * <p>If this component is not visitable (ie. if this method returns
   * false), the tree visited is short-circuited such that neither the
   * component nor any of its descendents will be visited></p>
   * <p>Custom {@code treeVisit()} implementations may call this method
   * to determine whether the component is visitable before performing
   * any visit-related processing.</p>
   *
   * @return true if this component should be visited, false otherwise.
   */
  protected boolean isVisitable(VisitContext visitContext)
  {
    return _isVisitable(visitContext, this);
  }
  
  /**
   * default implementation checking the <code>VisitHint.SKIP_TRANSIENT</code> and
   * <code>VisitHint.SKIP_UNRENDERED</code> hints.
   */
  private static boolean _isVisitable(VisitContext visitContext, UIComponent component)
  {
    Collection<VisitHint> hints = visitContext.getHints();
    
    if (hints.contains(VisitHint.SKIP_TRANSIENT) && component.isTransient())
      return false;
    
    if (hints.contains(VisitHint.SKIP_UNRENDERED) && !component.isRendered())
      return false;
    
    return true;
  }
  
  /**
   * <p>
   * Called when visiting the component during optimized partial page encoding so that the
   * component can modify what is actually encoded.  For example tab controls often
   * render the tabs for the ShowDetailItems in the tab bar before delegating to the
   * disclosed ShowDetailItem to render the tab content.  As a result, the tab control
   * needs to encode its tab bar if any of its ShowDetailItems are partial targets so that
   * the tab labels, for example, are up-to-date.
   * </p>
   * <p>
   * The default implementation delegates to the CoreRenderer if this component has one, otherwise
   * it calls the VisitCallback and returns its result if this UIXComponent is a partial
   * target of the current encoding.
   * </p>
   * @param visitContext VisitContext to pass to the VisitCallback
   * @param partialContext PartialPageContext for the current partial encoding
   * @param callback VisitCallback to call if this component is a partial target
   * @return The VisitResult controlling continued iteration of the visit.
   */
  public VisitResult partialEncodeVisit(
    VisitContext visitContext,
    PartialPageContext partialContext,
    VisitCallback callback)
  {
    FacesContext context  = visitContext.getFacesContext();
    Renderer     renderer = getRenderer(context);
    
    if (renderer instanceof CoreRenderer)
    {
      // delegate to the CoreRenderer
      return ((CoreRenderer)renderer).partialEncodeVisit(visitContext,
                                                         partialContext,
                                                         this,
                                                         callback);
    }
    else
    {
      // check that this is a component instance that should be encoded
      if (partialContext.isPossiblePartialTarget(getId()) &&
          partialContext.isPartialTarget(getClientId(context)))
      {
        // visit the component instance
        return callback.visit(visitContext, this);
      }
      else
      {
        // Not visiting this component, but allow visit to
        // continue into this subtree in case we've got
        // visit targets there.
        return VisitResult.ACCEPT;
      }
    }
  }

  /**
   * <p>Sets up the context necessary to visit or invoke the component for all phases.</p>
   * <p>The default implementation does nothing.</p>
   * <p>If a subclass overrides this method, it should override
   * <code>tearDownVisitingContext</code> as well.</p>
   * <p>It is guaranteed that if <code>setupVisitingContext</code> completes
   * <code>tearDownVisitingContext</code> will be called for this component</p>
   * @see #tearDownVisitingContext
   * @see #setUpEncodingContext
   * @see #tearDownEncodingContext
   */
  protected void setupVisitingContext(FacesContext context)
  {
    // do nothing
  }
  
  /**
   * <p>Tears down context created in order to visit or invoke the component
   * for all phases.</p>
   * <p>The default implementation does nothing.</p>
   * <p>A subclass should only override this method if it overrode
   * <code>setupVisitingContext</code> as well</p>
   * <p>It is guaranteed that <code>tearDownVisitingContext</code> will be called only after
   * <code>setupVisitingContext</code> has been called for this component</p>
   * @see #setupVisitingContext
   * @see #setUpEncodingContext
   * @see #tearDownEncodingContext
   */
  protected void tearDownVisitingContext(FacesContext context) 
  {
    // do nothing
  }
  
  /**
   * <p>Sets up the context necessary to encode the component.</p>
   * <p>The default implementation delegates to
   * <code>CoreRenderer.setupEncodingContext</code> and then calls
   * <code>setupVisitingContext</code></p>
   * <p>If a subclass overrides this method, it should override
   * <code>tearDownEncodingContext</code> as well.</p>
   * <p>It is guaranteed that if <code>setUpEncodingContext</code> completes
   * <code>tearDownEncodingContext</code> will be called for this component</p>
   * @see #setupVisitingContext
   * @see #tearDownVisitingContext
   * @see #tearDownEncodingContext
   * @see CoreRenderer#setupEncodingContext
   */
  protected void setUpEncodingContext(FacesContext context, RenderingContext rc)
  {
    setupVisitingContext(context);
    
    Renderer renderer = getRenderer(context);
    
    if (renderer instanceof CoreRenderer)
    {
      CoreRenderer coreRenderer = (CoreRenderer)renderer;
      
      coreRenderer.setupEncodingContext(context, rc, this);
    }
  }

  /**
   * <p>Tears down the context created in order to encode the component</p>
   * <p>The default implementation delegates to
   * <code>CoreRenderer.tearDownEncodingContext</code> and then calls
   * <code>tearDownVisitingContext</code></p>
   * <p>A subclass should only override this method if it overrode
   * <code>setUpEncodingContext</code> as well</p>
   * <p>It is guaranteed that <code>tearDownEncodingContext</code> will be called only after
   * <code>setUpEncodingContext</code> has been called for this component</p>
   * @see #setUpEncodingContext
   * @see #tearDownVisitingContext
   * @see #setUpEncodingContext
   * @see CoreRenderer#tearDownEncodingContext
   */
  protected void tearDownEncodingContext(
    FacesContext context,
    RenderingContext rc)
  {
    Renderer renderer = getRenderer(context);
    
    try
    {
      if (renderer instanceof CoreRenderer)
      {
        CoreRenderer coreRenderer = (CoreRenderer)renderer;
      
        coreRenderer.tearDownEncodingContext(context, rc, this);
      } 
    }
    finally
    {
      tearDownVisitingContext(context);
    }
  }

  /**
   * Returns the FacesBean used for storing the component's state.
   */
  abstract public FacesBean getFacesBean();

  /**
   * Adds an AttributeChangeListener.  Attribute change events are not
   * delivered for any programmatic change to a property.  They are only
   * delivered when a renderer changes a property without the application's
   * specific request.  An example of an attribute change events might
   * include the width of a column that supported client-side resizing.
   */
  abstract public void addAttributeChangeListener(AttributeChangeListener acl);

  /**
   * Removes an AttributeChangeListener.  Attribute change events are not
   * delivered for any programmatic change to a property.  They are only
   * delivered when a renderer changes a property without the application's
   * specific request.  An example of an attribute change events might
   * include the width of a column that supported client-side resizing.
   */
  abstract public void removeAttributeChangeListener(AttributeChangeListener acl);

  /**
   * Gets the registered AttributeChangeListeners.
   */ 
  abstract public AttributeChangeListener[] getAttributeChangeListeners();

  /**
   * Sets a method binding to an AttributeChangeListener.  Attribute
   * change events are not
   * delivered for any programmatic change to a property.  They are only
   * delivered when a renderer changes a property without the application's
   * specific request.  An example of an attribute change events might
   * include the width of a column that supported client-side resizing.
   */
  abstract public void setAttributeChangeListener(MethodExpression me);

  /**
   * Gets the method binding to an AttributeChangeListener.  Attribute
   * change events are not
   * delivered for any programmatic change to a property.  They are only
   * delivered when a renderer changes a property without the application's
   * specific request.  An example of an attribute change events might
   * include the width of a column that supported client-side resizing.
   */
  abstract public MethodExpression getAttributeChangeListener();

  abstract public void markInitialState();
  
  /**
   * Provides additional context (the target child component for which the container 
   * client ID is requested) to a naming container for constructing a client ID.
   * This is useful for components such as @link UIXTable and @link UIXTreeTable which need 
   * to return different container client IDs for stamped and non-stamped child components.
   * @see UIXComponentBase#getClientId(FacesContext context)
   */
  abstract public String getContainerClientId(FacesContext context, UIComponent child);
}
