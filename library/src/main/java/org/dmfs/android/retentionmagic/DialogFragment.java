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

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;


/**
 * A {@link android.app.DialogFragment} that can retain field values.
 * 
 * @author Marten Gajda <marten@dmfs.org>
 */
public class DialogFragment extends android.app.DialogFragment
{
	private SharedPreferences mPrefs;


	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		RetentionMagic.init(this, getArguments());
	}


	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		Activity activity = getActivity();
		mPrefs = activity.getSharedPreferences(activity.getPackageName() + ".sharedPrefences", 0);

		if (savedInstanceState == null)
		{
			RetentionMagic.init(this, mPrefs);
		}
		else
		{
			RetentionMagic.restore(this, savedInstanceState);
		}
	}


	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		RetentionMagic.store(this, outState);
	}


	@Override
	public void onPause()
	{
		super.onPause();
		/*
		 * On older SDK version we have to store permanent data in onPause(), because there is no guarantee that onStop() will be called.
		 */
		if (VERSION.SDK_INT < VERSION_CODES.HONEYCOMB)
		{
			RetentionMagic.persist(this, mPrefs);
		}
	}


	@Override
	public void onStop()
	{
		super.onStop();
		if (VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB)
		{
			RetentionMagic.persist(this, mPrefs);
		}
	}
}
