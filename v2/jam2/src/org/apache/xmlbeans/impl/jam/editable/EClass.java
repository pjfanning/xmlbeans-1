/*   Copyright 2004 The Apache Software Foundation
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.apache.xmlbeans.impl.jam.editable;

import org.apache.xmlbeans.impl.jam.JClass;

/**
 * Editable representation of a java class or interface.
 *
 * @author Patrick Calahan <pcal@bea.com>
 */
public interface EClass extends EMember, JClass {

  public void setIsInterface(boolean b);

  public void setIsAnnotationType(boolean b);  

  public void setIsUnresolved(boolean b);

  /**
   * Sets the class which this class extends.  The class name must be fully-
   * qualified.  Pass null to make the class extend nothing.
   *
   * @throws IllegalArgumentException if the name is not a valid class name.
   */
  public void setSuperclass(String qualifiedClassName);

  /**
   * Sets the name of this class that this class extends.  The name
   * may or may nor be fully-qualified.  Pass null to make the class
   * extend nothing.
   *
   * @throws IllegalArgumentException if the name is not a valid class name.
   */
  public void setSuperclassUnqualified(String unqualifiedClassName);

  /**
   * Sets the class which this class extends.  Pass null to make the class
   * extend nothing.
   *
   * @throws IllegalArgumentException if the given class cannot be extended
   * (i.e. final classes, interfaces, void, primitives, arrays).
   */
  public void setSuperclass(JClass clazz);

  /**
   * Adds to the list of interfaces implemented by this class.
   *
   * @throws IllegalArgumentException if the parameter is null or is not
   * a valid class name.
   */
  public void addInterface(String className);

  /**
   * Adds to the list of interfaces implemented by this class.  The class name
   * may or may not be qualified.
   *
   * @throws IllegalArgumentException if the parameter is null or is not
   * a valid class name.
   */
  public void addInterfaceUnqualified(String unqualifiedClassName);

  /**
   * Adds to the list of interfaces implemented by this class.
   *
   * @throws IllegalArgumentException if the given class cannot be implemented
   * (i.e. is not an interface).
   */
  public void addInterface(JClass interf);

  /**
   * Removes a named interface from the list of interfaces implemented by
   * this class.  Does nothing if the class does not implement the named
   * interface.
   *
   * @throws IllegalArgumentException if the parameter is null or is not
   * a valid class name.
   */
  public void removeInterface(String className);

  /**
   * Removes an interface from the list of interfaces implemented by
   * this class.  Does nothing if the class does not implement the named
   * interface.
   *
   * @throws IllegalArgumentException if the parameter is null or is not
   * an interface.
   */
  public void removeInterface(JClass interf);

  /**
   * Creates a new constructor, adds it to this class, and returns it.
   */
  public EConstructor addNewConstructor();

  /**
   * Removes a constructor from this class.  Does nothing if the given
   * constructor is not on this class.
   */
  public void removeConstructor(EConstructor constr);

  /**
   * Returns the constructors declared on this class.  This does not include
   * constructors from any base class or interface.  This is simply a more
   * strongly-typed version of getDeclaredConstructors().
   */
  public EConstructor[] getEditableConstructors();

  /**
   * Creates a new field, adds it to this class, and returns it.
   * The type of the field must be qualified
   */
  public EField addNewField();

  /**
   * Removes the given field from this class.  Does nothing if this class
   * does not contain the field.
   */
  public void removeField(EField field);

  /**
   * Returns the fields declared on this class.  This does not include
   * fields from any base class or interface.  This is simply a more
   * strongly-typed version of getDeclaredFields().
   */
  public EField[] getEditableFields();

  /**
   * Creates a new method, adds it to this class, and returns it.
   */
  public EMethod addNewMethod();

  /**
   * Removes the given method from this class.  Does nothing if this class
   * does not contain the method.
   */
  public void removeMethod(EMethod method);

  /**
   * Returns the EditableMethods declared on this class.  This does not
   * include methods inherited from any base class or interface.  This is
   * simply a more strongly-typed version of getDeclaredMethods().
   */
  public EMethod[] getEditableMethods();



  // not sure that these are something we want to do.  is a property really
  // an inherent part of a java type?

//  public EProperty createProperty(EMethod getter, EMethod setter);

//  public void removeProperty(EProperty prop);
}