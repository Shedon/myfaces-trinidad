/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.myfaces.trinidadinternal.convert;

import java.util.List;

/**
 * This class is capable of converting
 * one Object into another.
 * Users of this class may discover source types supported by this TypeConverter
 * by specifying a target type. Regular GenericConverter allows discovery of supported
 * target types by specifying a source type
 */

public abstract class ReverseDiscoveryGenericConverter implements TypeConverter
{
  /**
   * converts the given Object into an instance of the 
   * targetType.
   * @return an instance of the targetType.
   */
  public abstract Object convert(Object source, Class<?> targetType);
    
  /**
   * Gets all the supported source types for the given targetType.
   * This converter must be able to convert each source type into the
   * targetType.
   * @return Each item is of type {@link Class}. An empty list must be
   * returned if the given targetType cannot be converted from anything.
   */
  public abstract List<Class<?>> getSourceTypes(Class<?> targetType);
}
