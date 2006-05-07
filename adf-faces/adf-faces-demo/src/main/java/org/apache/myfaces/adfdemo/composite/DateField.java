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
package org.apache.myfaces.adfdemo.composite;

import java.io.IOException;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.NumberConverter;
import javax.faces.event.ValueChangeEvent;
import javax.faces.validator.LongRangeValidator;

import org.apache.myfaces.adf.component.UIXEditableValue;
import org.apache.myfaces.adf.component.core.output.CoreOutputText;
import org.apache.myfaces.adf.component.core.input.CoreInputText;

/**
 * An experiment in building a composite component.  Some basic
 * principles:
 * <ul>
 * <li> We're a NamingContainer, so our children won't show up in
 *   findComponent() calls.
 * <li> The child components get re-created on each pass through
 *    the system;  this means seeing if they exist in both Apply Request
 *    Values (<code>processDecodes</code>) and Render Response
 *    (<code>encodeBegin()</code>), and marking the components
 *    transient so they don't get saved.
 * <li> The model is the tricky part:  instead of using real
 *   <code>ValueBindings</code> on the children, I let them
 *   use local values, and then manully transfer over their local values
 *   into an overall "local value" during validate().  Unfortunately,
 *   using ValueBindings to automate the transfer wouldn't quite work,
 *   since the transfer wouldn't happen 'til Update Model, which is
 *   too late to preserve the semantics of an editable value component in JSF.
 * <li>Apply Request Values and Update Model don't need to do anything special
 *  for the children;  they just run as needed.
 * </ul>
 * @author Adam Winer
 */
public class DateField extends UIXEditableValue implements NamingContainer
{
  public DateField()
  {
    super(null);
  }


  public void processDecodes(FacesContext context)
  {
    _addChildren(context);
    super.processDecodes(context);
  }

  public void validate(FacesContext context)
  {
    if (!_month.isValid() ||
        !_year.isValid() ||
        !_day.isValid())
    {
      setValid(false);
      return;
    }

    int year = ((Number) _year.getValue()).intValue();
    // We'll be 1970 - 2069.  Good enough for a demo.
    if (year < 70)
      year += 100;

    int month = ((Number) _month.getValue()).intValue() - 1;
    int day = ((Number) _day.getValue()).intValue();

    Date oldValue = (Date) getValue();
    Date newValue = (Date) oldValue.clone();
    newValue.setYear(year);
    newValue.setMonth(month);
    newValue.setDate(day);

    //=-=AEW RUN VALIDATORS!

    // Invalid day given the month
    if (day != newValue.getDate())
    {
      int numberOfDaysInMonth = day - newValue.getDate();
      FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Invalid date.",
                    "This month only has " + numberOfDaysInMonth + " days!");
      setValid(false);
      context.addMessage(getClientId(context), message);
    }
    // Looks good
    else
    {
      setValid(true);

      // And if the value actually changed, store it and send a value change
      // event.
      if (!newValue.equals(oldValue))
      {
        setValue(newValue);
        queueEvent(new ValueChangeEvent(this, oldValue, newValue));
      }
    }
  }

  public void encodeBegin(FacesContext context) throws IOException
  {
    _addChildren(context);
    super.encodeBegin(context);
  }

  public void encodeChildren(FacesContext context) throws IOException
  {
    Iterator children = getChildren().iterator();
    while (children.hasNext())
    {
      UIComponent child = (UIComponent) children.next();
      assert(child.getChildCount() == 0);
      assert(child.getFacets().isEmpty());
      child.encodeBegin(context);
      child.encodeChildren(context);
      child.encodeEnd(context);
    }
  }

  public boolean getRendersChildren()
  {
    return true;
  }

  private void _addChildren(FacesContext context)
  {
    if (_month != null)
      return;

    List children = getChildren();
    children.clear();

    Date value = (Date) getValue();

    // A proper implementation would add children in the correct
    // order for the current locale
    _month = _createTwoDigitInput(context);
    _month.setId("month");
    _month.setShortDesc("Month");
    LongRangeValidator monthRange = _createLongRangeValidator(context);
    monthRange.setMinimum(1);
    monthRange.setMaximum(12);
    _month.addValidator(monthRange);
    if (value != null)
      _month.setValue(new Integer(value.getMonth() + 1));

    _day = _createTwoDigitInput(context);
    _day.setId("day");
    _day.setShortDesc("Day");
    LongRangeValidator dayRange = _createLongRangeValidator(context);
    dayRange.setMinimum(1);
    dayRange.setMaximum(31);
    _day.addValidator(dayRange);
    if (value != null)
      _day.setValue(new Integer(value.getDate()));

    _year = _createTwoDigitInput(context);
    _year.setId("year");
    _year.setShortDesc("Year");
    if (value != null)
    {
      int yearValue = value.getYear();
      if (yearValue >= 100)
        yearValue -= 100;
      _year.setValue(new Integer(yearValue));
    }

    children.add(_month);
    children.add(_createSeparator(context));
    children.add(_day);
    children.add(_createSeparator(context));
    children.add(_year);
  }

  private LongRangeValidator _createLongRangeValidator(FacesContext context)
  {
    return (LongRangeValidator)
      context.getApplication().createValidator(LongRangeValidator.VALIDATOR_ID);
  }

  private CoreInputText _createTwoDigitInput(FacesContext context)
  {
    CoreInputText input = new CoreInputText();
    input.setColumns(2);
    input.setMaximumLength(2);
    input.setTransient(true);
    input.setRequired(true);
    input.setSimple(true);

    NumberConverter converter = (NumberConverter)
      context.getApplication().createConverter(NumberConverter.CONVERTER_ID);
    converter.setIntegerOnly(true);
    converter.setMaxIntegerDigits(2);
    converter.setMinIntegerDigits(2);
    input.setConverter(converter);

    return input;
  }

  // A proper implementation would create a separator appropriate
  // to the current locale
  private CoreOutputText _createSeparator(FacesContext context)
  {
    CoreOutputText output = new CoreOutputText();
    output.setValue("/");
    output.setTransient(true);
    return output;
  }

  private transient CoreInputText _month;
  private transient CoreInputText _year;
  private transient CoreInputText _day;
}
