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

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.dmfs.android.retentionmagic.annotations.Parameter;
import org.dmfs.android.retentionmagic.annotations.ParameterArrayList;
import org.dmfs.android.retentionmagic.annotations.Retain;
import org.dmfs.android.retentionmagic.annotations.RetainArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.util.SparseArray;


/**
 * Helper to store and restore instance values.
 * 
 * @author Marten Gajda <marten@dmfs.org>
 */
public final class RetentionMagic
{
	/**
	 * Map of final classes to their respective {@link PersistenceHelper}s. Since we don't have to expect subclasses of these classes we can get the helpers
	 * with a simple <code>get()</code>.
	 * <p>
	 * Since we're always called from the UI thread, there is no need to synchronize access to this map.
	 * </p>
	 */
	private final static Map<Class<?>, PersistenceHelper> FINAL_CLASS_HELPERS = new HashMap<Class<?>, PersistenceHelper>();

	/**
	 * Map of non-final classes and interfaces to their respective {@link PersistenceHelper}s. A simple <code>get()</code> won't match, so we have to check each
	 * key separately here.
	 * <p>
	 * Since we're always called from the UI thread, there is no need to synchronize access to this map.
	 * </p>
	 */
	private final static Map<Class<?>, PersistenceHelper> OTHER_CLASS_HELPERS = new HashMap<Class<?>, PersistenceHelper>();

	/**
	 * Map of final generic type classes to their respective {@link PersistenceHelper}s. Since we don't have to expect subclasses of these classes we can get
	 * the helpers with a simple <code>get()</code>.
	 * <p>
	 * Since we're always called from the UI thread, there is no need to synchronize access to this map.
	 * </p>
	 */
	private final static Map<Class<?>, PersistenceHelper> ARRAYLIST_FINAL_CLASS_HELPERS = new HashMap<Class<?>, PersistenceHelper>();

	/**
	 * Map of non-final generic type classes and interfaces to their respective {@link PersistenceHelper}s. A simple <code>get()</code> won't match, so we have
	 * to check each key
	 * <p>
	 * Since we're always called from the UI thread, there is no need to synchronize access to this map.
	 * </p>
	 */
	private final static Map<Class<?>, PersistenceHelper> ARRAYLIST_OTHER_CLASS_HELPERS = new HashMap<Class<?>, PersistenceHelper>();

	/**
	 * Maps Activity and Fragment classes to a maps of fields to their respective {@link PersistenceHelper}s.
	 */
	private final static Map<Class<?>, Map<Field, PersistenceHelper>> CLASS_CACHE = new HashMap<Class<?>, Map<Field, PersistenceHelper>>();

	static
	{
		FINAL_CLASS_HELPERS.put(boolean.class, new PersistenceHelper()
		{

			@Override
			public void restoreFromBundle(Field field, Object instance, String key, Bundle bundle) throws IllegalAccessException
			{
				field.setBoolean(instance, bundle.getBoolean(key));
			}


			@Override
			public void storeInBundle(Field field, Object instance, String key, Bundle bundle) throws IllegalAccessException
			{
				bundle.putBoolean(key, field.getBoolean(instance));
			}


			@Override
			public void restoreFromPreferences(Field field, Object instance, String key, SharedPreferences prefs) throws IllegalAccessException
			{
				field.setBoolean(instance, prefs.getBoolean(key, field.getBoolean(instance)));
			}


			@Override
			public void storeInPreferences(Field field, Object instance, String key, SharedPreferences.Editor editor) throws IllegalAccessException
			{
				editor.putBoolean(key, field.getBoolean(instance));
			}
		});

		// TODO: support storing of boolean arrays as base64 encoded bit fields in SharedPreferences
		FINAL_CLASS_HELPERS.put(boolean[].class, new PersistenceHelper()
		{

			@Override
			public void restoreFromBundle(Field field, Object instance, String key, Bundle bundle) throws IllegalAccessException
			{
				field.set(instance, bundle.getBooleanArray(key));
			}


			@Override
			public void storeInBundle(Field field, Object instance, String key, Bundle bundle) throws IllegalAccessException
			{
				bundle.putBooleanArray(key, (boolean[]) field.get(instance));
			}
		});

		FINAL_CLASS_HELPERS.put(byte.class, new PersistenceHelper()
		{

			@Override
			public void restoreFromBundle(Field field, Object instance, String key, Bundle bundle) throws IllegalAccessException
			{
				field.setByte(instance, bundle.getByte(key));
			}


			@Override
			public void storeInBundle(Field field, Object instance, String key, Bundle bundle) throws IllegalAccessException
			{
				bundle.putByte(key, field.getByte(instance));
			}


			@Override
			public void restoreFromPreferences(Field field, Object instance, String key, SharedPreferences prefs) throws IllegalAccessException
			{
				field.setByte(instance, (byte) (prefs.getInt(key, field.getByte(instance)) & 0xff));
			}


			@Override
			public void storeInPreferences(Field field, Object instance, String key, SharedPreferences.Editor editor) throws IllegalAccessException
			{
				editor.putInt(key, field.getByte(instance));
			}
		});

		// TODO: support storing byte arrays as Base64 encoded arrays in SharedPreferences
		FINAL_CLASS_HELPERS.put(byte[].class, new PersistenceHelper()
		{

			@Override
			public void restoreFromBundle(Field field, Object instance, String key, Bundle bundle) throws IllegalAccessException
			{
				field.set(instance, bundle.getByteArray(key));
			}


			@Override
			public void storeInBundle(Field field, Object instance, String key, Bundle bundle) throws IllegalAccessException
			{
				bundle.putByteArray(key, (byte[]) field.get(instance));
			}
		});

		FINAL_CLASS_HELPERS.put(short.class, new PersistenceHelper()
		{

			@Override
			public void restoreFromBundle(Field field, Object instance, String key, Bundle bundle) throws IllegalAccessException
			{
				field.setShort(instance, bundle.getShort(key));
			}


			@Override
			public void storeInBundle(Field field, Object instance, String key, Bundle bundle) throws IllegalAccessException
			{
				bundle.putShort(key, field.getShort(instance));
			}


			@Override
			public void restoreFromPreferences(Field field, Object instance, String key, SharedPreferences prefs) throws IllegalAccessException
			{
				field.setShort(instance, (short) prefs.getInt(key, field.getShort(instance)));
			}


			@Override
			public void storeInPreferences(Field field, Object instance, String key, SharedPreferences.Editor editor) throws IllegalAccessException
			{
				editor.putInt(key, field.getShort(instance));
			}
		});

		// TODO: support storing short arrays as Base64 encoded arrays in SharedPreferences
		FINAL_CLASS_HELPERS.put(short[].class, new PersistenceHelper()
		{

			@Override
			public void restoreFromBundle(Field field, Object instance, String key, Bundle bundle) throws IllegalAccessException
			{
				field.set(instance, bundle.getShortArray(key));
			}


			@Override
			public void storeInBundle(Field field, Object instance, String key, Bundle bundle) throws IllegalAccessException
			{
				bundle.putShortArray(key, (short[]) field.get(instance));
			}
		});

		FINAL_CLASS_HELPERS.put(char.class, new PersistenceHelper()
		{

			@Override
			public void restoreFromBundle(Field field, Object instance, String key, Bundle bundle) throws IllegalAccessException
			{
				field.setChar(instance, bundle.getChar(key));
			}


			@Override
			public void storeInBundle(Field field, Object instance, String key, Bundle bundle) throws IllegalAccessException
			{
				bundle.putChar(key, field.getChar(instance));
			}


			@Override
			public void restoreFromPreferences(Field field, Object instance, String key, SharedPreferences prefs) throws IllegalAccessException
			{
				field.setChar(instance, (char) prefs.getInt(key, field.getChar(instance)));
			}


			@Override
			public void storeInPreferences(Field field, Object instance, String key, SharedPreferences.Editor editor) throws IllegalAccessException
			{
				editor.putInt(key, field.getChar(instance));
			}
		});

		FINAL_CLASS_HELPERS.put(char[].class, new PersistenceHelper()
		{

			@Override
			public void restoreFromBundle(Field field, Object instance, String key, Bundle bundle) throws IllegalAccessException
			{
				field.set(instance, bundle.getCharArray(key));
			}


			@Override
			public void storeInBundle(Field field, Object instance, String key, Bundle bundle) throws IllegalAccessException
			{
				bundle.putCharArray(key, (char[]) field.get(instance));
			}


			@Override
			public void restoreFromPreferences(Field field, Object instance, String key, SharedPreferences prefs) throws IllegalAccessException
			{
				field.set(instance, prefs.getString(key, new String((char[]) field.get(instance))).toCharArray());
			}


			@Override
			public void storeInPreferences(Field field, Object instance, String key, SharedPreferences.Editor editor) throws IllegalAccessException
			{
				editor.putString(key, new String((char[]) field.get(instance)));
			}

		});

		FINAL_CLASS_HELPERS.put(int.class, new PersistenceHelper()
		{

			@Override
			public void restoreFromBundle(Field field, Object instance, String key, Bundle bundle) throws IllegalAccessException
			{
				field.setInt(instance, bundle.getInt(key));
			}


			@Override
			public void storeInBundle(Field field, Object instance, String key, Bundle bundle) throws IllegalAccessException
			{
				bundle.putInt(key, field.getInt(instance));
			}


			@Override
			public void restoreFromPreferences(Field field, Object instance, String key, SharedPreferences prefs) throws IllegalAccessException
			{
				field.setInt(instance, prefs.getInt(key, field.getInt(instance)));
			}


			@Override
			public void storeInPreferences(Field field, Object instance, String key, SharedPreferences.Editor editor) throws IllegalAccessException
			{
				editor.putInt(key, field.getInt(instance));
			}

		});

		// TODO: support storing integer arrays as Base64 encoded arrays in SharedPreferences
		FINAL_CLASS_HELPERS.put(int[].class, new PersistenceHelper()
		{

			@Override
			public void restoreFromBundle(Field field, Object instance, String key, Bundle bundle) throws IllegalAccessException
			{
				field.set(instance, bundle.getIntArray(key));
			}


			@Override
			public void storeInBundle(Field field, Object instance, String key, Bundle bundle) throws IllegalAccessException
			{
				bundle.putIntArray(key, (int[]) field.get(instance));
			}
		});

		FINAL_CLASS_HELPERS.put(long.class, new PersistenceHelper()
		{

			@Override
			public void restoreFromBundle(Field field, Object instance, String key, Bundle bundle) throws IllegalAccessException
			{
				field.setLong(instance, bundle.getLong(key));
			}


			@Override
			public void storeInBundle(Field field, Object instance, String key, Bundle bundle) throws IllegalAccessException
			{
				bundle.putLong(key, field.getLong(instance));
			}


			@Override
			public void restoreFromPreferences(Field field, Object instance, String key, SharedPreferences prefs) throws IllegalAccessException
			{
				field.setLong(instance, prefs.getLong(key, field.getLong(instance)));
			}


			@Override
			public void storeInPreferences(Field field, Object instance, String key, SharedPreferences.Editor editor) throws IllegalAccessException
			{
				editor.putLong(key, field.getLong(instance));
			}
		});

		// TODO: support storing long arrays as Base64 encoded arrays in SharedPreferences
		FINAL_CLASS_HELPERS.put(long[].class, new PersistenceHelper()
		{

			@Override
			public void restoreFromBundle(Field field, Object instance, String key, Bundle bundle) throws IllegalAccessException
			{
				field.set(instance, bundle.getLongArray(key));
			}


			@Override
			public void storeInBundle(Field field, Object instance, String key, Bundle bundle) throws IllegalAccessException
			{
				bundle.putLongArray(key, (long[]) field.get(instance));
			}
		});

		FINAL_CLASS_HELPERS.put(float.class, new PersistenceHelper()
		{

			@Override
			public void restoreFromBundle(Field field, Object instance, String key, Bundle bundle) throws IllegalAccessException
			{
				field.setFloat(instance, bundle.getFloat(key));
			}


			@Override
			public void storeInBundle(Field field, Object instance, String key, Bundle bundle) throws IllegalAccessException
			{
				bundle.putFloat(key, field.getFloat(instance));
			}


			@Override
			public void restoreFromPreferences(Field field, Object instance, String key, SharedPreferences prefs) throws IllegalAccessException
			{
				field.setFloat(instance, prefs.getFloat(key, field.getFloat(instance)));
			}


			@Override
			public void storeInPreferences(Field field, Object instance, String key, SharedPreferences.Editor editor) throws IllegalAccessException
			{
				editor.putFloat(key, field.getFloat(instance));
			}
		});

		// TODO: support storing float arrays as Base64 encoded arrays in SharedPreferences
		FINAL_CLASS_HELPERS.put(float[].class, new PersistenceHelper()
		{

			@Override
			public void restoreFromBundle(Field field, Object instance, String key, Bundle bundle) throws IllegalAccessException
			{
				field.set(instance, bundle.getFloatArray(key));
			}


			@Override
			public void storeInBundle(Field field, Object instance, String key, Bundle bundle) throws IllegalAccessException
			{
				bundle.putFloatArray(key, (float[]) field.get(instance));
			}
		});

		// TODO: support douple in SharedPreferences
		FINAL_CLASS_HELPERS.put(double.class, new PersistenceHelper()
		{

			@Override
			public void restoreFromBundle(Field field, Object instance, String key, Bundle bundle) throws IllegalAccessException
			{
				field.setDouble(instance, bundle.getDouble(key));
			}


			@Override
			public void storeInBundle(Field field, Object instance, String key, Bundle bundle) throws IllegalAccessException
			{
				bundle.putDouble(key, field.getDouble(instance));
			}
		});

		// TODO: support storing double arrays as Base64 encoded arrays in SharedPreferences
		FINAL_CLASS_HELPERS.put(double[].class, new PersistenceHelper()
		{

			@Override
			public void restoreFromBundle(Field field, Object instance, String key, Bundle bundle) throws IllegalAccessException
			{
				field.set(instance, bundle.getDoubleArray(key));
			}


			@Override
			public void storeInBundle(Field field, Object instance, String key, Bundle bundle) throws IllegalAccessException
			{
				bundle.putDoubleArray(key, (double[]) field.get(instance));
			}
		});

		FINAL_CLASS_HELPERS.put(String.class, new PersistenceHelper()
		{

			@Override
			public void restoreFromBundle(Field field, Object instance, String key, Bundle bundle) throws IllegalAccessException
			{
				field.set(instance, bundle.getString(key));
			}


			@Override
			public void storeInBundle(Field field, Object instance, String key, Bundle bundle) throws IllegalAccessException
			{
				bundle.putString(key, (String) field.get(instance));
			}


			@Override
			public void restoreFromPreferences(Field field, Object instance, String key, SharedPreferences prefs) throws IllegalAccessException
			{
				field.set(instance, prefs.getString(key, (String) field.get(instance)));
			}


			@Override
			public void storeInPreferences(Field field, Object instance, String key, SharedPreferences.Editor editor) throws IllegalAccessException
			{
				editor.putString(key, (String) field.get(instance));
			}

		});

		// TODO: support storing string arrays in SharedPreferences
		FINAL_CLASS_HELPERS.put(String[].class, new PersistenceHelper()
		{

			@Override
			public void restoreFromBundle(Field field, Object instance, String key, Bundle bundle) throws IllegalAccessException
			{
				field.set(instance, bundle.getStringArray(key));
			}


			@Override
			public void storeInBundle(Field field, Object instance, String key, Bundle bundle) throws IllegalAccessException
			{
				bundle.putStringArray(key, (String[]) field.get(instance));
			}
		});

		FINAL_CLASS_HELPERS.put(Bundle.class, new PersistenceHelper()
		{

			@Override
			public void restoreFromBundle(Field field, Object instance, String key, Bundle bundle) throws IllegalAccessException
			{
				field.set(instance, bundle.getBundle(key));
			}


			@Override
			public void storeInBundle(Field field, Object instance, String key, Bundle bundle) throws IllegalAccessException
			{
				bundle.putBundle(key, (Bundle) field.get(instance));
			}
		});

		FINAL_CLASS_HELPERS.put(SparseArray.class, new PersistenceHelper()
		{

			@Override
			public void restoreFromBundle(Field field, Object instance, String key, Bundle bundle) throws IllegalAccessException
			{
				field.set(instance, bundle.getSparseParcelableArray(key));
			}


			@SuppressWarnings("unchecked")
			@Override
			public void storeInBundle(Field field, Object instance, String key, Bundle bundle) throws IllegalAccessException
			{
				bundle.putSparseParcelableArray(key, (SparseArray<Parcelable>) field.get(instance));
			}
		});

		ARRAYLIST_FINAL_CLASS_HELPERS.put(Integer.class, new PersistenceHelper()
		{

			@Override
			public void restoreFromBundle(Field field, Object instance, String key, Bundle bundle) throws IllegalAccessException
			{
				field.set(instance, bundle.getIntegerArrayList(key));
			}


			@SuppressWarnings("unchecked")
			@Override
			public void storeInBundle(Field field, Object instance, String key, Bundle bundle) throws IllegalAccessException
			{
				bundle.putIntegerArrayList(key, (ArrayList<Integer>) field.get(instance));
			}
		});

		ARRAYLIST_FINAL_CLASS_HELPERS.put(String.class, new PersistenceHelper()
		{

			@Override
			public void restoreFromBundle(Field field, Object instance, String key, Bundle bundle) throws IllegalAccessException
			{
				field.set(instance, bundle.getStringArrayList(key));
			}


			@SuppressWarnings("unchecked")
			@Override
			public void storeInBundle(Field field, Object instance, String key, Bundle bundle) throws IllegalAccessException
			{
				bundle.putStringArrayList(key, (ArrayList<String>) field.get(instance));
			}
		});

		if (VERSION.SDK_INT >= VERSION_CODES.FROYO)
		{
			// Bundle doesn't support CharSequence ArrayLists prior to SDK version 8
			ARRAYLIST_OTHER_CLASS_HELPERS.put(CharSequence.class, new PersistenceHelper()
			{

				@Override
				public void restoreFromBundle(Field field, Object instance, String key, Bundle bundle) throws IllegalAccessException
				{
					field.set(instance, bundle.getCharSequenceArrayList(key));
				}


				@SuppressWarnings("unchecked")
				@Override
				public void storeInBundle(Field field, Object instance, String key, Bundle bundle) throws IllegalAccessException
				{
					bundle.putCharSequenceArrayList(key, (ArrayList<CharSequence>) field.get(instance));
				}
			});
		}

		ARRAYLIST_OTHER_CLASS_HELPERS.put(Parcelable.class, new PersistenceHelper()
		{

			@Override
			public void restoreFromBundle(Field field, Object instance, String key, Bundle bundle) throws IllegalAccessException
			{
				field.set(instance, bundle.getParcelableArrayList(key));
			}


			@SuppressWarnings("unchecked")
			@Override
			public void storeInBundle(Field field, Object instance, String key, Bundle bundle) throws IllegalAccessException
			{
				bundle.putParcelableArrayList(key, (ArrayList<Parcelable>) field.get(instance));
			}
		});

		OTHER_CLASS_HELPERS.put(CharSequence.class, new PersistenceHelper()
		{

			@Override
			public void restoreFromBundle(Field field, Object instance, String key, Bundle bundle) throws IllegalAccessException
			{
				field.set(instance, bundle.getCharSequence(key));
			}


			@Override
			public void storeInBundle(Field field, Object instance, String key, Bundle bundle) throws IllegalAccessException
			{
				bundle.putCharSequence(key, (CharSequence) field.get(instance));
			}
		});

		if (VERSION.SDK_INT >= VERSION_CODES.FROYO)
		{
			// Bundle doesn't support CharSequence arrays prior to SDK version 8
			OTHER_CLASS_HELPERS.put(CharSequence[].class, new PersistenceHelper()
			{

				@Override
				public void restoreFromBundle(Field field, Object instance, String key, Bundle bundle) throws IllegalAccessException
				{
					field.set(instance, bundle.getCharSequenceArray(key));
				}


				@Override
				public void storeInBundle(Field field, Object instance, String key, Bundle bundle) throws IllegalAccessException
				{
					bundle.putCharSequenceArray(key, (CharSequence[]) field.get(instance));
				}
			});
		}

		OTHER_CLASS_HELPERS.put(Parcelable.class, new PersistenceHelper()
		{

			@Override
			public void restoreFromBundle(Field field, Object instance, String key, Bundle bundle) throws IllegalAccessException
			{
				field.set(instance, bundle.getParcelable(key));
			}


			@Override
			public void storeInBundle(Field field, Object instance, String key, Bundle bundle) throws IllegalAccessException
			{
				bundle.putParcelable(key, (Parcelable) field.get(instance));
			}
		});

		OTHER_CLASS_HELPERS.put(Parcelable[].class, new PersistenceHelper()
		{

			@Override
			public void restoreFromBundle(Field field, Object instance, String key, Bundle bundle) throws IllegalAccessException
			{
				field.set(instance, bundle.getParcelableArray(key));
			}


			@Override
			public void storeInBundle(Field field, Object instance, String key, Bundle bundle) throws IllegalAccessException
			{
				bundle.putParcelableArray(key, (Parcelable[]) field.get(instance));
			}
		});

		OTHER_CLASS_HELPERS.put(Serializable.class, new PersistenceHelper()
		{

			@Override
			public void restoreFromBundle(Field field, Object instance, String key, Bundle bundle) throws IllegalAccessException
			{
				field.set(instance, bundle.getSerializable(key));
			}


			@Override
			public void storeInBundle(Field field, Object instance, String key, Bundle bundle) throws IllegalAccessException
			{
				bundle.putSerializable(key, (Serializable) field.get(instance));
			}
		});

		if (Build.VERSION.SDK_INT >= 18)
		{
			// Bundle doesn't support IBinders prior to SDK version 18

			OTHER_CLASS_HELPERS.put(IBinder.class, new PersistenceHelper()
			{

				@Override
				public void restoreFromBundle(Field field, Object instance, String key, Bundle bundle) throws IllegalAccessException
				{
					field.set(instance, bundle.getBinder(key));
				}


				@Override
				public void storeInBundle(Field field, Object instance, String key, Bundle bundle) throws IllegalAccessException
				{
					bundle.putBinder(key, (IBinder) field.get(instance));
				}
			});
		}
	}


	/**
	 * Don't allow instances.
	 */
	private RetentionMagic()
	{
	}


	/**
	 * Store all retainable fields of an Activity in a {@link Bundle}.
	 * 
	 * @param activity
	 *            The {@link Activity}.
	 * @param instanceState
	 *            The {@link Bundle} to store the state in.
	 */
	public static void store(final Activity activity, final Bundle instanceState)
	{
		try
		{
			storeAndRestore(activity.getClass(), activity, instanceState, true /* store */);
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
	}


	/**
	 * Store all retainable fields of a {@link Fragment} in a {@link Bundle}.
	 * 
	 * @param fragment
	 *            The {@link Fragment}.
	 * @param instanceState
	 *            The {@link Bundle} to store the state in.
	 */
	public static void store(final Fragment fragment, final Bundle instanceState)
	{
		try
		{
			storeAndRestore(fragment.getClass(), fragment, instanceState, true /* store */);
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
	}


	/**
	 * Store all retainable fields of a {@link android.support.v4.app.Fragment} in a {@link Bundle}.
	 * 
	 * @param fragment
	 *            The {@link android.support.v4.app.Fragment}.
	 * @param instanceState
	 *            The {@link Bundle} to store the state in.
	 */
	public static void store(final android.support.v4.app.Fragment fragment, final Bundle instanceState)
	{
		try
		{
			storeAndRestore(fragment.getClass(), fragment, instanceState, true /* store */);
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
	}


	/**
	 * Restore all retainable fields of an Activity from a {@link Bundle}.
	 * 
	 * @param activity
	 *            The {@link Activity}.
	 * @param instanceState
	 *            The {@link Bundle} to store the state in.
	 */
	public static void restore(final Activity activity, final Bundle instanceState)
	{
		try
		{
			storeAndRestore(activity.getClass(), activity, instanceState, false /* restore */);
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
	}


	/**
	 * Restore all retainable fields of a {@link Fragment} from a {@link Bundle}.
	 * 
	 * @param fragment
	 *            The {@link Fragment}.
	 * @param instanceState
	 *            The {@link Bundle} to store the state in.
	 */
	public static void restore(final Fragment fragment, final Bundle instanceState)
	{
		try
		{
			storeAndRestore(fragment.getClass(), fragment, instanceState, false /* restore */);
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
	}


	/**
	 * Restore all retainable fields of a {@link android.support.v4.app.Fragment} from a {@link Bundle}.
	 * 
	 * @param fragment
	 *            The {@link android.support.v4.app.Fragment}.
	 * @param instanceState
	 *            The {@link Bundle} to store the state in.
	 */
	public static void restore(final android.support.v4.app.Fragment fragment, final Bundle instanceState)
	{
		try
		{
			storeAndRestore(fragment.getClass(), fragment, instanceState, false /* restore */);
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
	}


	private static void storeAndRestore(final Class<?> classInstance, final Object instance, final Bundle instanceState, final boolean store)
		throws IllegalAccessException
	{
		if (instanceState == null)
		{
			// nothing to do
			return;
		}

		Map<Field, PersistenceHelper> helperCache = CLASS_CACHE.get(classInstance);

		if (helperCache == null)
		{
			helperCache = new HashMap<Field, PersistenceHelper>();
			for (Field field : classInstance.getDeclaredFields())
			{
				Retain retain = field.getAnnotation(Retain.class);
				if (retain != null && !ArrayList.class.isAssignableFrom(field.getType()))
				{
					field.setAccessible(true);

					String key = retain.key();
					if (key == null || key.length() == 0)
					{
						key = field.getName();
					}

					PersistenceHelper helper = getHelper(field.getType());
					if (helper != null)
					{
						if (store)
						{
							helper.storeInBundle(field, instance, key, instanceState);
						}
						else
						{
							helper.restoreFromBundle(field, instance, key, instanceState);
						}
						helperCache.put(field, helper);
					}
					else
					{
						throw new UnsupportedOperationException("field of class " + field.getType().getCanonicalName() + " not supported");
					}
				}
				else if (retain != null)
				{
					throw new UnsupportedOperationException("@Retain does not support ArrayLists, use @RetainArrayList instead");
				}
				else
				{
					RetainArrayList retainList = field.getAnnotation(RetainArrayList.class);
					if (retainList != null && ArrayList.class.isAssignableFrom(field.getType()))
					{
						field.setAccessible(true);
						String key = retainList.key();
						if (key == null || key.length() == 0)
						{
							key = field.getName();
						}

						PersistenceHelper helper = getArrayListHelper(retainList.genericType());
						if (helper != null)
						{
							if (store)
							{
								helper.storeInBundle(field, instance, key, instanceState);
							}
							else
							{
								helper.restoreFromBundle(field, instance, key, instanceState);
							}
							helperCache.put(field, helper);
						}
						else
						{
							throw new UnsupportedOperationException("list with generic type of " + field.getType().getCanonicalName() + " not supported");
						}
					}
					else if (retainList != null)
					{
						throw new UnsupportedOperationException("@RetainArrayList supports only ArrayList fields, use @Retain instead");
					}
				}
			}
			CLASS_CACHE.put(classInstance, helperCache);
		}
		else
		{
			for (Entry<Field, PersistenceHelper> entry : helperCache.entrySet())
			{
				PersistenceHelper helper = entry.getValue();
				Field field = entry.getKey();
				if (helper != null)
				{
					Retain retain = field.getAnnotation(Retain.class);
					String key;
					if (retain != null)
					{
						key = retain.key();
					}
					else
					{
						key = field.getAnnotation(RetainArrayList.class).key();
					}

					if (key == null || key.length() == 0)
					{
						key = field.getName();
					}

					if (store)
					{
						helper.storeInBundle(field, instance, key, instanceState);
					}
					else
					{
						helper.restoreFromBundle(field, instance, key, instanceState);
					}
				}
			}
		}
	}


	public static void init(final Activity activity, final SharedPreferences prefs)
	{
		try
		{
			init(activity.getClass(), activity, prefs);
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
	}


	public static void init(final Fragment fragment, final SharedPreferences prefs)
	{
		try
		{
			init(fragment.getClass(), fragment, prefs);
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
	}


	public static void init(final android.support.v4.app.Fragment fragment, final SharedPreferences prefs)
	{
		try
		{
			init(fragment.getClass(), fragment, prefs);
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
	}


	public static void init(final Activity activity, final Bundle extras)
	{
		try
		{
			init(activity.getClass(), activity, extras);
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
	}


	public static void init(final Fragment fragment, final Bundle arguments)
	{
		try
		{
			init(fragment.getClass(), fragment, arguments);
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
	}


	public static void init(final android.support.v4.app.Fragment fragment, final Bundle arguments)
	{
		try
		{
			init(fragment.getClass(), fragment, arguments);
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
	}


	public static void persist(final Activity activity, final SharedPreferences prefs)
	{
		SharedPreferences.Editor editor = prefs.edit();

		persist(activity, editor);
		if (VERSION.SDK_INT >= VERSION_CODES.GINGERBREAD)
		{
			// write out asynchronously on newer platforms
			editor.apply();
		}
		else
		{
			// use the synchronous call on older platforms
			editor.commit();
		}
	}


	public static void persist(final Fragment fragment, final SharedPreferences prefs)
	{
		SharedPreferences.Editor editor = prefs.edit();

		persist(fragment, editor);
		if (VERSION.SDK_INT >= VERSION_CODES.GINGERBREAD)
		{
			// write out asynchronously on newer platforms
			editor.apply();
		}
		else
		{
			// use the synchronous call on older platforms
			editor.commit();
		}
	}


	public static void persist(final android.support.v4.app.Fragment fragment, final SharedPreferences prefs)
	{
		SharedPreferences.Editor editor = prefs.edit();

		persist(fragment, editor);
		if (VERSION.SDK_INT >= VERSION_CODES.GINGERBREAD)
		{
			// write out asynchronously on newer platforms
			editor.apply();
		}
		else
		{
			// use the synchronous call on older platforms
			editor.commit();
		}
	}


	public static void persist(final Activity activity, final SharedPreferences.Editor editor)
	{
		try
		{
			persist(activity.getClass(), activity, editor);
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
	}


	public static void persist(final Fragment fragment, final SharedPreferences.Editor editor)
	{
		try
		{
			persist(fragment.getClass(), fragment, editor);
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
	}


	public static void persist(final android.support.v4.app.Fragment fragment, final SharedPreferences.Editor editor)
	{
		try
		{
			persist(fragment.getClass(), fragment, editor);
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
	}


	private static void init(final Class<?> classInstance, final Object instance, final SharedPreferences prefs) throws IllegalAccessException
	{
		Map<Field, PersistenceHelper> helperCache = CLASS_CACHE.get(classInstance);

		if (helperCache == null)
		{
			for (Field field : classInstance.getDeclaredFields())
			{
				Retain retain = field.getAnnotation(Retain.class);
				if (retain != null && !ArrayList.class.isAssignableFrom(field.getType()))
				{
					if (!retain.permanent())
					{
						continue;
					}

					field.setAccessible(true);

					String key = retain.key();
					key = getTag(classInstance, retain.instanceNSField(), retain.classNS(), instance).append(
						key == null || key.length() == 0 ? field.getName() : key).toString();

					PersistenceHelper helper = getHelper(field.getType());
					if (helper != null)
					{
						helper.restoreFromPreferences(field, instance, key, prefs);
					}
					else
					{
						throw new UnsupportedOperationException("field of class " + field.getType().getCanonicalName() + " not supported for permanent storage");
					}
				}
				else if (retain != null)
				{
					throw new UnsupportedOperationException("@Retain does not support ArrayLists, use @RetainArrayList instead");
				}
			}
		}
		else
		{
			for (Entry<Field, PersistenceHelper> entry : helperCache.entrySet())
			{
				PersistenceHelper helper = entry.getValue();
				Field field = entry.getKey();
				if (helper != null)
				{
					Retain retain = field.getAnnotation(Retain.class);
					if (retain == null || !retain.permanent())
					{
						continue;
					}
					String key = retain.key();
					key = getTag(classInstance, retain.instanceNSField(), retain.classNS(), instance).append(
						key == null || key.length() == 0 ? field.getName() : key).toString();

					helper.restoreFromPreferences(field, instance, key, prefs);
				}
			}
		}
	}


	private static void persist(final Class<?> classInstance, final Object instance, final SharedPreferences.Editor editor) throws IllegalAccessException
	{
		Map<Field, PersistenceHelper> helperCache = CLASS_CACHE.get(classInstance);

		if (helperCache == null)
		{
			for (Field field : classInstance.getDeclaredFields())
			{
				Retain retain = field.getAnnotation(Retain.class);
				if (retain != null && !ArrayList.class.isAssignableFrom(field.getType()))
				{
					if (!retain.permanent())
					{
						continue;
					}

					field.setAccessible(true);

					String key = retain.key();
					key = getTag(classInstance, retain.instanceNSField(), retain.classNS(), instance).append(
						key == null || key.length() == 0 ? field.getName() : key).toString();

					PersistenceHelper helper = getHelper(field.getType());
					if (helper != null)
					{
						helper.storeInPreferences(field, instance, key, editor);
					}
					else
					{
						throw new UnsupportedOperationException("field of class " + field.getType().getCanonicalName() + " not supported for permanent storage");
					}
				}
				else if (retain != null)
				{
					throw new UnsupportedOperationException("@Retain does not support ArrayLists, use @RetainArrayList instead");
				}
			}
		}
		else
		{
			for (Entry<Field, PersistenceHelper> entry : helperCache.entrySet())
			{
				PersistenceHelper helper = entry.getValue();
				Field field = entry.getKey();
				if (helper != null)
				{
					Retain retain = field.getAnnotation(Retain.class);
					if (retain == null || !retain.permanent())
					{
						continue;
					}

					String key = retain.key();
					key = getTag(classInstance, retain.instanceNSField(), retain.classNS(), instance).append(
						key == null || key.length() == 0 ? field.getName() : key).toString();

					helper.storeInPreferences(field, instance, key, editor);
				}
			}
		}
	}


	private static void init(final Class<?> classInstance, final Object instance, final Bundle bundle) throws IllegalAccessException
	{
		if (bundle == null || bundle.size() == 0)
		{
			return;
		}

		for (Field field : classInstance.getDeclaredFields())
		{
			Parameter param = field.getAnnotation(Parameter.class);
			if (param != null && !ArrayList.class.isAssignableFrom(field.getType()))
			{
				field.setAccessible(true);

				String key = param.key();
				if (key == null || key.length() == 0)
				{
					key = field.getName();
				}

				PersistenceHelper helper = getHelper(field.getType());
				if (helper != null)
				{
					helper.restoreFromBundle(field, instance, key, bundle);
				}
				else
				{
					throw new UnsupportedOperationException("field of class " + field.getType().getCanonicalName()
						+ " not supported for initialization from a Bundle");
				}
			}
			else if (param != null)
			{
				throw new UnsupportedOperationException("@Parameter does not support ArrayLists, use @ParameterArrayList instead");
			}
			else
			{
				ParameterArrayList paramList = field.getAnnotation(ParameterArrayList.class);
				if (paramList != null && ArrayList.class.isAssignableFrom(field.getType()))
				{
					field.setAccessible(true);
					String key = paramList.value();
					if (key == null || key.length() == 0)
					{
						key = field.getName();
					}

					PersistenceHelper helper = getArrayListHelper(paramList.genericType());
					if (helper != null)
					{
						helper.restoreFromBundle(field, instance, key, bundle);
					}
					else
					{
						throw new UnsupportedOperationException("list with generic type of " + field.getType().getCanonicalName() + " not supported");
					}
				}
				else if (paramList != null)
				{
					throw new UnsupportedOperationException("@ParameterArrayList supports only ArrayList fields, use @Parameter instead");
				}
			}

		}
	}


	private static PersistenceHelper getHelper(final Class<?> fieldType)
	{
		return getHelper(fieldType, FINAL_CLASS_HELPERS, OTHER_CLASS_HELPERS);
	}


	private static PersistenceHelper getArrayListHelper(final Class<?> genericArrayListType)
	{
		return getHelper(genericArrayListType, ARRAYLIST_FINAL_CLASS_HELPERS, ARRAYLIST_OTHER_CLASS_HELPERS);
	}


	private static PersistenceHelper getHelper(final Class<?> genericType, final Map<Class<?>, PersistenceHelper> finalClassHelper,
		final Map<Class<?>, PersistenceHelper> otherClassHelper)
	{
		PersistenceHelper result = finalClassHelper.get(genericType);
		if (result != null)
		{
			return result;
		}

		for (Class<?> classClass : otherClassHelper.keySet())
		{
			if (classClass.isAssignableFrom(genericType))
			{
				return otherClassHelper.get(classClass);
			}
		}
		return null;
	}


	private static StringBuilder getTag(final Class<?> classType, String instanceTag, String classTag, Object instance) throws IllegalAccessException
	{
		StringBuilder result = new StringBuilder(256);

		if (classTag != null && classTag.length() > 0)
		{
			if (classTag.length() == 1 && classTag.charAt(0) == '.')
			{
				try
				{
					Field tagField = classType.getDeclaredField("TAG");
					tagField.setAccessible(true);
					result.append(tagField.get(instance).toString());
				}
				catch (Exception e)
				{
					result.append(classType.getCanonicalName());
				}
			}
			else
			{
				result.append(classTag);
			}
			result.append('.');
		}

		if (instanceTag != null && instanceTag.length() > 0)
		{
			try
			{
				Field tagField = classType.getDeclaredField(instanceTag);
				tagField.setAccessible(true);
				Object value = tagField.get(instance);
				if (value != null)
				{
					result.append(value.toString());
					result.append('.');
				}
			}
			catch (NoSuchFieldException e)
			{
				// ignore
			}
			catch (SecurityException e)
			{
				// ignore
			}
		}

		return result;
	}
}
