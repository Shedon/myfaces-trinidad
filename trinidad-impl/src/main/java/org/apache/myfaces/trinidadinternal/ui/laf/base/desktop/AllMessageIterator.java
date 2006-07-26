/*
 * Copyright  2005,2006 The Apache Software Foundation.
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
package org.apache.myfaces.trinidadinternal.ui.laf.base.desktop;

import java.util.Collections;
import java.util.Iterator;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

/**
 * Iterates through the Faces messages (either global messages only,
 * or global messages followed by all per-component messages).
 * <p>
 * @version $Name:  $ ($Revision: adfrt/faces/adf-faces-impl/src/main/java/oracle/adfinternal/view/faces/ui/laf/base/desktop/AllMessageIterator.java#0 $) $Date: 10-nov-2005.18:55:07 $
 * @author The Oracle ADF Faces Team
 * @todo Iterate through global messages then messages that don't have labels,
 *       finally messages with labels.
 */
class AllMessageIterator implements java.util.Iterator
{
  /**
   * Creates an {@Link Java.Util.Iterator} that will iterate through all
   * messages (global followed by per-component).
   *
   * @param fContext The FacesContext
   *
   */
  public AllMessageIterator(FacesContext fContext)
  {
    this(fContext, true, false);
  }

  /**
   * Creates an {@link Iterator}
   * @param fContext The FacesContext
   * @param allMessages If true, {@link Iterator} will automatically return
   *                    all global messages followed by all per-component
   *                    messages.
   *                    If false, only global messages will be fetched.
   * @param skipGlobals If true, {@link Iterator} will skip all global
   *                    messages. Note the obvious corollary that if
   *                    allMessages is false and skipGlobals is true,
   *                    no messages will ever be returned.
   */
  public AllMessageIterator(FacesContext fContext,
                            boolean allMessages,
                            boolean skipGlobals)
  {
    _allMsg = allMessages;
    _fContext = fContext;

    if (skipGlobals)
    {
      // Skip global messages, Start off iterating through client messages
      _nextItr();
    }
    else
    {
      // Start off iterating through all the global messages
      _itr = _fContext.getMessages(null);
    }
  }

  public boolean hasNext()
  {
    if (_itr.hasNext())
      return true;

    // If we're done with the current Iterator, check for others
    return _nextItr();
  }

  public Object next()
  {
    MessageWrapper msg = null;

    if (_itr.hasNext() || _nextItr())
    {
      FacesMessage fm = (FacesMessage) _itr.next();
      msg = new MessageWrapper(fm, _currentId);
    }
    return msg;
  }

  public void remove()
  {
    _itr.remove();
  }

  private boolean _nextItr()
  {
    if (_allMsg)
    {
      // Iterate through all the components that have messages
      if (_clients == null)
        _clients = _fContext.getClientIdsWithMessages();

      while (_clients.hasNext())
      {
        _currentId = (String) _clients.next();
        if (_currentId != null)
        {
          // Set the current Iterator to the component message Iterator
          _itr = _fContext.getMessages(_currentId);
        }
        else
          _itr = _EMPTY_ITERATOR;

        if (_itr.hasNext())
          return true;
      }
    }
    else
      _itr = _EMPTY_ITERATOR;

    return false;
  }

  private static final Iterator _EMPTY_ITERATOR =
    Collections.EMPTY_LIST.iterator();
  private FacesContext _fContext;
  private Iterator     _clients;
  private Iterator     _itr;
  private String       _currentId;
  private boolean      _allMsg = true;
}
