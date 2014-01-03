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

package org.dmfs.android.retentionmagic;

import java.lang.reflect.Field;

import android.content.SharedPreferences;
import android.os.Bundle;


/**
 * A helper to store values of certain types in a Bundle or the SharedPreferences or to load them.
 * 
 * @author Marten Gajda <marten@dmfs.org>
 */
abstract class PersistenceHelper
{
	/**
	 * Store a field in the given {@link Bundle}.
	 * 
	 * @param field
	 *            The Field definition.
	 * @param instance
	 *            The class instance.
	 * @param key
	 *            The key of the value to restore.
	 * @param bundle
	 *            The {@link Bundle}.
	 * @throws IllegalAccessException
	 */
	public abstract void storeInBundle(Field field, Object instance, String key, Bundle bundle) throws IllegalAccessException;


	/**
	 * Restore a field from the given {@link Bundle}.
	 * 
	 * @param field
	 *            The Field definition.
	 * @param instance
	 *            The class instance.
	 * @param key
	 *            The key of the value to restore.
	 * @param bundle
	 *            The {@link Bundle}.
	 * @throws IllegalAccessException
	 */
	public abstract void restoreFromBundle(Field field, Object instance, String key, Bundle bundle) throws IllegalAccessException;


	/**
	 * Store a field in the given {@link SharedPreferences.Editor}.
	 * 
	 * @param field
	 *            The Field definition.
	 * @param instance
	 *            The class instance.
	 * @param key
	 *            The key of the value to restore.
	 * @param editor
	 *            The {@link SharedPreferences.Editor}.
	 * @throws IllegalAccessException
	 */
	public void storeInPreferences(Field field, Object instance, String key, SharedPreferences.Editor editor) throws IllegalAccessException
	{
		// most field don't support that
		throw new UnsupportedOperationException("saving of type " + field.getClass().getCanonicalName() + " in preferences is not supported");
	}


	/**
	 * Restore a field from the given {@link SharedPreferences}.
	 * 
	 * @param field
	 *            The Field definition.
	 * @param instance
	 *            The class instance.
	 * @param key
	 *            The key of the value to restore.
	 * @param prefs
	 *            The {@link SharedPreferences}.
	 * @throws IllegalAccessException
	 */
	public void restoreFromPreferences(Field field, Object instance, String key, SharedPreferences prefs) throws IllegalAccessException
	{
		// most field don't support that
		throw new UnsupportedOperationException("loading of type " + field.getClass().getCanonicalName() + " from preferences not supported");
	}
}