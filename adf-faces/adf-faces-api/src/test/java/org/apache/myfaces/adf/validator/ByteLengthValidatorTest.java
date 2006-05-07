/*
 * Copyright 2004,2006 The Apache Software Foundation.
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
package org.apache.myfaces.adf.validator;

import java.util.Locale;

import javax.faces.component.UIViewRoot;
import javax.faces.validator.ValidatorException;

import org.apache.myfaces.adfbuild.test.MockUtils;

import javax.faces.component.MockUIComponent;
import javax.faces.context.MockFacesContext;


/**
 * Unit tests for ByteLengthValidator
 *
 * @author vijay venkataraman
 */
public class ByteLengthValidatorTest extends ValidatorTestCase
{
  public ByteLengthValidatorTest(String testName)
  {
    super(testName);
  }

  /**
   * Tests that null returns immediately.
   *
   * @throws ValidatorException  when test fails
   */
  public void testNull() throws ValidatorException
  {
    MockFacesContext context = new MockFacesContext();
    MockUIComponent component = MockUtils.buildMockUIComponent();
    ByteLengthValidator validator = new ByteLengthValidator();
    doTestNull(context, component, validator);
  }

  /**
   * Test when context is set to null
   */
  public void testNullContext()
  {

    MockUIComponent component = MockUtils.buildMockUIComponent();
    ByteLengthValidator validator = new ByteLengthValidator();
    doTestNullContext(component, validator);
  }
 /**
  * Check when component whose value is null is passed to the validator.
  * Should result in exception.
  */
  public void testNullComponent()
  {
    MockFacesContext context      = new MockFacesContext();
    ByteLengthValidator validator = new ByteLengthValidator();
    doTestNullComponent(context, validator);
  }

  /**
   * Tests that non String objects throw a ValidationException.
   */
  public void testNotString()
  {
    doTestIsNotString(new ByteLengthValidator());
  }

  /**
   * Test that basic test passes
   */
  public void testSanitySuccess()
  {

    MockFacesContext context      = new MockFacesContext();
    ByteLengthValidator validator = new ByteLengthValidator();
    //some very basic sanity test
    String values[]    = {"four"};
    String encodings[] = {"ISO-8859-1"};
    int maxBytes[]     = {4};

    MockUIComponent component = MockUtils.buildMockUIComponent();

    for (int i = 0; i < values.length ; i++)
    {
      validator.setEncoding(encodings[i]);
      validator.setMaximum(maxBytes[i]);
      doTestValidate(validator, context, component, values[i]);
    }
  }

  public void testDefaultEncodingWorks()
  {
    MockFacesContext context      = new MockFacesContext();
    MockUIComponent component = MockUtils.buildMockUIComponent();
    ByteLengthValidator validator = new ByteLengthValidator();
    String value = "four";
    validator.setMaximum(4);
    doTestValidate(validator, context, component, value);
  }

  /**
   * Test that StateHolder implementation for the validator works fine.
   */
  public void testStateHolderSaveRestore()
  {
    MockFacesContext context = new MockFacesContext();
    MockUIComponent component = MockUtils.buildMockUIComponent();
    ByteLengthValidator validator = new ByteLengthValidator();
    validator.setEncoding("ISO-8859-1");
    validator.setMaximum(4);
    validator.setMaximumMessageDetail("Testing state holder?");
    ByteLengthValidator restoreValidator = new  ByteLengthValidator();

    doTestStateHolderSaveRestore(validator, restoreValidator,
                                 context, component);
  }

  /**
   * Test for equals function of Validator
   */
  public void testEquals()
  {
    ByteLengthValidator validator = new ByteLengthValidator();
    //1
    validator.setEncoding("ISO-8859-1");
    validator.setMaximum(100);
    validator.setMaximumMessageDetail("MaxMessage");

    ByteLengthValidator otherValidator = new ByteLengthValidator();
    otherValidator.setEncoding("ISO-8859-1");
    otherValidator.setMaximum(100);
    otherValidator.setMaximumMessageDetail("MaxMessage");
    doTestEquals(validator, otherValidator, true);
    assertEquals(validator.hashCode(), otherValidator.hashCode());

    //2
    otherValidator.setMaximum(150);
    validator.setMaximum(150);
    validator.setMaximumMessageDetail("MaxMessage1");
    otherValidator.setMaximumMessageDetail("MaxMessage1");
    doTestEquals(validator, otherValidator, true);
    assertEquals(validator.hashCode(), otherValidator.hashCode());

    //3
    otherValidator.setMaximum(200);
    otherValidator.setMaximumMessageDetail("MaxMessage");
    doTestEquals(validator, otherValidator, false);
    assertEquals(false, (validator.hashCode() == otherValidator.hashCode()));
  }

  public void testCustomMessageIsSet()
  {
    MockFacesContext context = new MockFacesContext();
    MockUIComponent component = MockUtils.buildMockUIComponent();
    UIViewRoot root = new UIViewRoot();
    Locale usLoc =  Locale.US;
    root.setLocale(usLoc);
    context.setupGetViewRoot(root);
    context.setupGetViewRoot(root);
    setMockLabelForComponent(component);
    ByteLengthValidator validator = new ByteLengthValidator();
    int maxBytes[]     = {3};
    validator.setMaximumMessageDetail("\"{1}\"" + _IS_GREATER
                                      + maxBytes[0]);

    //some very basic sanity test
    String values[]    = {"four"};
    String encodings[] = {"ISO-8859-1"};
    String expected = "\"" + values[0] + "\"" + _IS_GREATER + maxBytes[0];
    try
    {
      validator.setMaximum(maxBytes[0]);
      validator.setEncoding(encodings[0]);
      validator.validate(context, component, values[0]);
    }
    catch (ValidatorException ve)
    {
      String msg = ve.getFacesMessage().getDetail();
      assertEquals(msg, expected);
    }
  }
  private static final String _IS_GREATER
    = " exceeds maximum allowed length of ";
}
