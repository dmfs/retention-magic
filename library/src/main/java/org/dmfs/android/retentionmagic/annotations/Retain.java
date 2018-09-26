/*
 * Copyright (C) 2013 Marten Gajda <marten@dmfs.org>
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
 * 
 */

package org.dmfs.android.retentionmagic.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.dmfs.android.retentionmagic.Activity;
import org.dmfs.android.retentionmagic.RetentionMagic;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;


/**
 * Retain the value of a field in an {@link Activity} or {@link Fragment} across configuration changes or restarts.
 * <p>
 * <strong>Note:</strong> not all field types are supported. See the documentation of {@link Bundle} and {@link SharedPreferences} for which types are
 * supported.
 * </p>
 * <p>
 * The key under which the values are stored in the Bundle or in the shard preferences can be customized. Here are a number of examples, see {@link #key()},
 * {@link #classNS()} and {@link #instanceNSField()} for details.
 * </p>
 * 
 * <pre>
 * package some.package
 * 
 * import org.dmfs.android.retentionmagic.Activity
 * 
 * public class SomeActivity extends Activity
 * {
 * 
 * 	{@literal @}Retain(permanent = true)
 * 	String value1;
 * 	{@literal /}*
 * 	 * stored under "value1" in bundle
 * 	 * stored under "some.package.SomeActivity.value1" in shared preferences.
 * 	 *{@literal /}
 * 
 * 	{@literal @}Retain(permanent = true, key = "someKey2")
 * 	String value2;
 * 	{@literal /}*
 * 	 * stored under "someKey2" in bundle
 * 	 * stored under "some.package.SomeActivity.someKey2" in shared preferences.
 * 	 *{@literal /}
 * 
 * 	{@literal @}Retain(permanent = true, classNS = "")
 * 	String value3;
 * 	{@literal /}*
 * 	 * stored under "value3" in bundle
 * 	 * stored under "value3" in shared preferences.
 * 	 *{@literal /}
 * 
 * 	{@literal @}Retain(permanent = true, classNS = "TAG")
 * 	String value4;
 * 	{@literal /}*
 * 	 * stored under "value4" in bundle
 * 	 * stored under "TAG.value4" in shared preferences.
 * 	 *{@literal /}
 * 
 * 	{@literal @}Retain(permanent = true, key="someKey5", classNS = "TAG")
 * 	String value5;
 * 	{@literal /}*
 * 	 * stored under "someKey5" in bundle
 * 	 * stored under "TAG.someKey5" in shared preferences.
 * 	 *{@literal /}
 * 
 * 	{@literal /}* A variable that holds an identifier for the current instance. You need this if you use several instances of the same class (like {@link Fragment}s in a {@link ViewPager}).
 * 	 * This value is inserted into the name space of fields that use instanceNSField = "instanceNameSpace".
 * 	 *{@literal /}
 * 	String instanceNameSpace = "myInstance";
 * 
 * 	{@literal @}Retain(permanent = true, key="someKey6", instanceNSField = "instanceNameSpace")
 * 	String value6;
 * 	{@literal /}*
 * 	 * stored under "someKey6" in bundle
 * 	 * stored under "some.package.SomeActivity.myInstance.someKey6" in shared preferences.
 * 	 *{@literal /}
 * 
 * 	{@literal @}Retain(permanent = true, key="someKey7", classNS = "SomeActivity", instanceNSField = "instanceNameSpace")
 * 	String value7;
 * 	{@literal /}*
 * 	 * stored under "someKey7" in bundle
 * 	 * stored under "SomeActivity.myInstance.someKey7" in shared preferences.
 * 	 *{@literal /}
 * 
 * 	{@literal @}Retain(permanent = true, classNS = "", instanceNSField = "instanceNameSpace")
 * 	String value8;
 * 	{@literal /}*
 * 	 * stored under "value8" in bundle
 * 	 * stored under "myInstance.value8" in shared preferences.
 * 	 *{@literal /}
 * }
 * </pre>
 * <p>
 * <code>classNS</code> and <code>instanceNSField</code> have no effect if <code>permanent = true</code> is not specified.
 * </p>
 * <p>
 * You can specify the initial values for permanent fields like so:
 * 
 * <pre>
 * 	{@literal @}Retain(permanent = true)
 * 	private String mFirstStart = true;
 * </pre>
 * 
 * In <code>onCreate(Bundle)</code> you can check if <code>mFirstStart</code> is <code>true</code>, show a welcome message and set it to <code>false</code>.
 * </p>
 * <p>
 * <strong>Caveats:</strong> You have to take special care if you use ProGuard or a similar tool to optimize and/or obfuscate the final package. Have a look at
 * <code>README.md</code> or the demo project for an example <code>proguard.cfg</code>.
 * </p>
 * 
 * @author Marten Gajda <marten@dmfs.org>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface Retain {
	/**
	 * The key under which the field value is stored in the instance state bundle. When persisting to shared preferences this key will be prefixed by a
	 * namespace. If this value is not specified or empty the field name will be used as key
	 * 
	 * @see #permanent()
	 */
	String key() default "";


	/**
	 * Make this field permanent, retaining the value across app restarts. The value is stored in the shared preferences using the specified {@link #key()} and
	 * a name space for the class and/or instance. If key is empty, the variable name will be used. The actual key is made up by the key, the instance tag and
	 * the class tag like so:
	 * 
	 * <pre>
	 * &lt;class name space>.&lt;instance name space>.&lt;key>
	 * </pre>
	 * 
	 * <strong>Warning:</strong> permantent persisting is not supported for all types. At present only <code>boolean</code>, <code>byte</code>,
	 * <code>short</code>, <code>char</code>, <code>char[]</code>,<code>int</code>, <code>long</code> , <code>float</code> and {@link String} fields are
	 * supported.
	 * 
	 * @see #key()
	 * @see #instanceNSField()
	 * @see #classNS()
	 */
	boolean permanent() default false;


	/**
	 * The name of the field that specifies the instance name space. The name space will become part of the key when persisting the value to the shared
	 * preferences. If this field is an empty string or the field does not exist, no instance specific name space is used. The tag is made up by the toString
	 * result of the value of the tag field.
	 * <p>
	 * Use this feature when you use multiple instances of the same {@link Fragment} at the same time (like in a {@link ViewPager}.
	 * </p>
	 * <p>
	 * The default is to use no instance specific name space.
	 * </p>
	 * 
	 * @see #permanent()
	 * @see #classNS()
	 */
	String instanceNSField() default "";


	/**
	 * The class name space will become part of the key when persisting the value to the shared preferences.
	 * <p>
	 * You need to set a class name space when using a tool like ProGuard that shortens class names, since the resulting class name may change with each build.
	 * </p>
	 * <p>
	 * The default is "." which instructs the {@link RetentionMagic} to use the string value of the TAG field or the canonical class name if no such field
	 * exists. Use an empty string (<code>""</code>) to instruct the {@link RetentionMagic} to not use any class specific name space.
	 * </p>
	 * 
	 * @see #permanent()
	 * @see #instanceNSField()
	 */
	String classNS() default ".";
}
