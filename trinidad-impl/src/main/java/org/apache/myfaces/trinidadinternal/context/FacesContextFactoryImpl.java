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
package org.apache.myfaces.trinidadinternal.context;

import java.io.IOException;

import java.util.Iterator;

import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.context.ResponseStream;
import javax.faces.context.ResponseWriter;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.render.RenderKit;

import org.apache.myfaces.trinidad.logging.ADFLogger;
import org.apache.myfaces.trinidad.context.AdfFacesContext;

/**
 * Internal class that optimizes retrieval of the RenderKit by caching it
 * on the FacesContext, and hooks ExternalContext.dispatch()
 * to use the PageResolver.
 * <p>
 * @author The Oracle ADF Faces Team
 */
public class FacesContextFactoryImpl
  extends FacesContextFactory
{
  public FacesContextFactoryImpl(FacesContextFactory factory)
  {
    _factory = factory;
  }

  public FacesContext getFacesContext(Object context, Object request,
                                      Object response, Lifecycle lifecycle)
  {
    return new CacheRenderKit(_factory.getFacesContext(context,
                                                       request,
                                                       response,
                                                       lifecycle));
  }

  static public class CacheRenderKit extends FacesContext
  {
    public CacheRenderKit(FacesContext base)
    {
      _base = base;
      _external = new OverrideDispatch(base.getExternalContext());
      setCurrentInstance(this);
    }

    public Application getApplication()
    {
      return _base.getApplication();
    }

    public Iterator getClientIdsWithMessages()
    {
      return _base.getClientIdsWithMessages();
    }

    public ExternalContext getExternalContext()
    {
      return _external;
    }

    public FacesMessage.Severity getMaximumSeverity()
    {
      return _base.getMaximumSeverity();
    }

    public Iterator getMessages()
    {
      return _base.getMessages();
    }

    public Iterator getMessages(String clientId)
    {
      return _base.getMessages(clientId);
    }

    public RenderKit getRenderKit()
    {
      if (_kit == null)
      {
        _kit = _base.getRenderKit();
      }
      else
      {
        UIViewRoot root = getViewRoot();
        if (root != null)
        {
          String renderKitId = root.getRenderKitId();
          // Yes, instance equality, not .equals();  within a single
          // request and single thread, instance equality should always
          // be sufficient, and behavior will still be correct even
          // if it was somehow not (we'll just spend more time re-getting the
          // RenderKit)
          if (renderKitId != _renderKitId)
          {
            _renderKitId = renderKitId;
            _kit = _base.getRenderKit();
          }
        }
      }

      return _kit;
    }

    public boolean getRenderResponse()
    {
      return _base.getRenderResponse();
    }

    public boolean getResponseComplete()
    {
      return _base.getResponseComplete();
    }

    public ResponseStream getResponseStream()
    {
      return _base.getResponseStream();
    }

    public void setResponseStream(ResponseStream responseStream)
    {
      _base.setResponseStream(responseStream);
    }

    public ResponseWriter getResponseWriter()
    {
      return _base.getResponseWriter();
    }

    public void setResponseWriter(ResponseWriter responseWriter)
    {
      _base.setResponseWriter(responseWriter);
    }

    public UIViewRoot getViewRoot()
    {
      return _base.getViewRoot();
    }

    public void setViewRoot(UIViewRoot viewRoot)
    {
      _base.setViewRoot(viewRoot);
    }

    public void addMessage(String clientId, FacesMessage facesMessage)
    {
      _base.addMessage(clientId, facesMessage);
    }

    public void release()
    {
      _base.release();
    }

    public void renderResponse()
    {
      _base.renderResponse();
    }

    public void responseComplete()
    {
      _base.responseComplete();
    }

    private final FacesContext    _base;
    private final ExternalContext _external;
    private String    _renderKitId;
    private RenderKit _kit;
  }

  static public class OverrideDispatch extends ExternalContextDecorator
  {
    public OverrideDispatch(ExternalContext decorated)
    {
      _decorated = decorated;
    }

    public void dispatch(String path) throws IOException
    {
      AdfFacesContext afc = AdfFacesContext.getCurrentInstance();
      if (afc != null)
      {
        path = afc.getPageResolver().getPhysicalURI(path);
      }

      super.dispatch(path);
    }


    protected ExternalContext getExternalContext()
    {
      return _decorated;
    }

    private final ExternalContext _decorated;
  }

  private final FacesContextFactory _factory;

  static private final ADFLogger _LOG =
    ADFLogger.createADFLogger(FacesContextFactoryImpl.class);
}
