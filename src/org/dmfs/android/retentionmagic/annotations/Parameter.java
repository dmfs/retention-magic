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

import android.app.Activity;
import android.app.Fragment;


/**
 * Initialize the annotated field from the Extras/Arguments bundle of the {@link Activity} or {@link Fragment}.
 * <p>
 * <strong>Note:</strong> When using ProGuard or a similar tool you should set the key or exclude the field from code obfuscation, otherwise the field name may
 * me changed.
 * </p>
 * 
 * @see #key()
 * 
 * @author Marten Gajda <marten@dmfs.org>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface Parameter {
	/**
	 * The key under which the field value is stored in the argument/extras bundle. By default the field name is used.
	 */
	String key() default "";
}
