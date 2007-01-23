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
package org.apache.myfaces.trinidadinternal.share.expl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * adapts a java Method object into an EL Function
 * <p>
 * @version $Name:  $ ($Revision: adfrt/faces/adf-faces-impl/src/main/java/oracle/adfinternal/view/faces/share/expl/JavaMethod.java#0 $) $Date: 10-nov-2005.19:00:14 $
 * @author The Oracle ADF Faces Team
 */
public final class JavaMethod extends Function
{

  public JavaMethod(Method method)
  {
    _met = method;
  }

  @Override
  public Object invoke(Object instance, Object[] args)
    throws IllegalAccessException, IllegalArgumentException,
           InvocationTargetException
  {
    return _met.invoke(instance, args);
  }

  @Override
  public Class[] getParameterTypes()
  {
    return _met.getParameterTypes();
  }

  @Override
  public Class<?> getReturnType()
  {
    return _met.getReturnType();
  }

  public Method getMethod()
  {
    return _met;
  }

  private final Method _met;
}
