/*
 * Copyright  2003-2006 The Apache Software Foundation.
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

package org.apache.myfaces.adfinternal.ui.laf.base;

/**
 * This embarrassingly named interface is used to mark LookAndFeel
 * (or LookAndFeelExtension) implementations which provide access
 * to a LafIconProvider.  Previously, we were able to access
 * the LafIconProvider simply be casting to BaseLookAndFeel.  This
 * doesn't work now that we have LAFs which extend LookAndFeelExtension
 * instead of BaseLookAndFeel (such as OracleDesktopLookAndFeel).
 * We should remove this interface once we switch over to the new
 * LookAndFeel Icon API.
 *
 * @version $Name:  $ ($Revision: adfrt/faces/adf-faces-impl/src/main/java/oracle/adfinternal/view/faces/ui/laf/base/LafIconProviderProvider.java#0 $) $Date: 10-nov-2005.18:53:04 $
 * @author The Oracle ADF Faces Team
 */
public interface LafIconProviderProvider
{
  public LafIconProvider getLafIconProvider();
}
