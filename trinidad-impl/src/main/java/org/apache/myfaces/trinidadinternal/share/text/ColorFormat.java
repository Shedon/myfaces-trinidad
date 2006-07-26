/*
 * Copyright  2000-2006 The Apache Software Foundation.
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
package org.apache.myfaces.trinidadinternal.share.text;

import java.awt.Color;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParseException;
import java.text.ParsePosition;

/**
 * Format for colors.
 * <p>
 * @version $Name:  $ ($Revision: adfrt/faces/adf-faces-impl/src/main/java/oracle/adfinternal/view/faces/share/text/ColorFormat.java#0 $) $Date: 10-nov-2005.18:59:28 $
 * @author The Oracle ADF Faces Team
 */
public abstract class ColorFormat extends Format
{
  /**
   * Returns the value as a Color.
   */
  public final Color parse(
    String        source, 
    ParsePosition status)
  {
    return (Color)parseObject(source, status);
  }
  
  /**
   * Returns the value as a Color.
   */
  public final Color parse(
    String source) throws ParseException
  {
    return (Color)parseObject(source);
  }
  
  /**
   * Parses a string to produce an object.
   *
   * @exception ParseException if the specified string is invalid.
   */
  public Object parseObject(
    String source) throws ParseException 
  {
    ParsePosition status = new ParsePosition(0);
    Object result = parseObject(source, status);
    int index = status.getIndex();
    if (index == 0 ||
        (source != null && index < source.length())) 
    {
        throw new ParseException("Format.parseObject(String) failed",
            status.getErrorIndex());
    }
    return result;
  }

  /**
   * Returns the value as a Color.
   */
  abstract public Object parseObject(
    String        source, 
    ParsePosition status);
    
  /**
   * Formats a Color into a color string.
   * 
   * @param color  the color value to be formatted into a color string
   * 
   * @return the formatted color string
   */
  public final String format(
    Color color)
  {
    return format(color, new StringBuffer(),new FieldPosition(0)).toString();
  }

  public final StringBuffer format(
    Object obj, 
    StringBuffer toAppendTo,
    FieldPosition fieldPosition)
  {
    if (obj instanceof Color)
    {
      return format((Color)obj, toAppendTo, fieldPosition);
    }
    else if (obj instanceof Number)
    {
      return format(new Color(((Number)obj).intValue()),
                    toAppendTo, fieldPosition);
    }
    else 
    {
      throw 
        new IllegalArgumentException("Cannot format given Object as a Color");
    }
  }
  
  abstract public StringBuffer format(
    Color color,
    StringBuffer toAppendTo,
    FieldPosition pos);
}
