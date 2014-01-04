# android-retention-magic

__Helper to retain instance states in Android Activities and Fragments__

This is a small library to make it easier to retain the instance state of an Android Fragment or Activity. In addition it can persist fields to ```SharedPreferences``` and initialize fields from the given extras or arguments.

## Requirements

It has been tested on Android SDK Level 7 and above, but it might work on lower levels as well. Some class types are not supported on older SDK levels.

## Example code

When writing an Activity or Fragment you often have to write code like this to initialize, save and restore the state of the instance:

		import android.app.Activity;
		import android.content.SharedPreferences;
		...

		public class DemoActivity extends Activity
		{
			/** key for the value in the extras Bundle */
			public static final String EXTRA_VALUE = "com.example.EXTRA_VALUE";

			private static final String KEY_INT1 = "int1";
			private static final String KEY_STRING1 = "string1";
			private static final String KEY_BUNDLE1 = "bundle1";

			private static final String PREF_KEY_STRING1 = "string1pref";

			private int mInt1;
			private String mString1 = "initial value";
			private Bundle mBundle1;
			private String mValue;


			@Override
			protected void onCreate(Bundle savedInstanceState)
			{
				super.onCreate(savedInstanceState);

				mValue = getIntent().getStringExtra(EXTRA_VALUE);

				if (savedInstanceState != null)
				{
					mInt1 = savedInstanceState.getInt(KEY_INT1);
					mString1 = savedInstanceState.getString(KEY_STRING1);
					mBundle1 = savedInstanceState.getBundle(KEY_BUNDLE1);
				}
				else
				{
					SharedPreferences prefs = getSharedPreferences(getPackageName() + ".sharedPrefences", 0);
					mString1 = prefs.getString(PREF_KEY_STRING1, mString1);
				}

				...
			}

			@Override
			protected void onSaveInstanceState(Bundle outState)
			{
				super.onSaveInstanceState(Bundle outState)
				outState.putInt(KEY_INT1, mInt1);
				outState.putString(KEY_STRING1, mString1);
				outState.putBundle(KEY_BUNDLE1, mBundle1);
			}

		
			@Override
			protected void onPause()
			{
				super.onPause();
				if (VERSION.SDK_INT < VERSION_CODES.HONEYCOMB)
				{
					SharedPreferences.Editor editor = mPrefs.edit();
					editor.putString(PREF_KEY_STRING1, string1);
					if (VERSION.SDK_INT > VERSION_CODES.FROYO)
					{
						editor.apply();
					}
					else
					{
						editor.commit();
					}
				}
			}

			@Override
			protected void onStop()
			{
				super.onStop();
				if (VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB)
				{
					SharedPreferences.Editor editor = mPrefs.edit();
					editor.putString(PREF_KEY_STRING1, string1);
					editor.apply();
				}
			}


			...
		}

Using this library the same is achieved by


		import android.app.Activity;
		...

		public class DemoActivity extends org.dmfs.android.retentionmagic.Activity
		{
			/** key for the value in the extras Bundle */
			public static final String EXTRA_VALUE = "com.example.EXTRA_VALUE";

			@Retain
			private int mInt1;

			@Retain(permanent = true)
			private String mString1 = "initial Value";

			@Retain
			private Bundle mBundle1;

			@Parameter(key = EXTRA_VALUE) // initialize mValue with the value EXTRA_VALUE in the extras Bundle
			private String mValue;

			...
		}


If for some reason you can't inherit from the Activity and Fragment classes in ```org.dmfs.android.retentionmagic``` you still can do this:


		import android.app.Activity;
		import org.dmfs.android.retentionmagic.RetentionMagic;
		...

		public class DemoActivity extends Activity
		{
			/** key for the value in the extras Bundle */
			public static final String EXTRA_VALUE = "com.example.EXTRA_VALUE";

			@Retain
			private int mInt1;

			@Retain(permanent = true)
			private String mString1 = "initial Value";

			@Retain
			private Bundle mBundle1;

			@Parameter(key = EXTRA_VALUE)
			private String mValue;

			private SharedPreferences mPrefs;

			@Override
			protected void onCreate(Bundle savedInstanceState)
			{
				super.onCreate(savedInstanceState);
				mPrefs = getSharedPreferences(getPackageName() + ".sharedPrefences", 0);

				RetentionMagic.init(this, getIntent().getExtras());

				if (savedInstanceState == null)
				{
					RetentionMagic.init(this, mPrefs);
				}
				else
				{
					RetentionMagic.restore(this, savedInstanceState);
				}
				...
			}

			@Override
			protected void onSaveInstanceState(Bundle outState)
			{
				super.onSaveInstanceState(Bundle outState)
				RetentionMagic.store(this, outState);
			}

			@Override
			protected void onPause()
			{
				super.onPause();
				if (VERSION.SDK_INT < VERSION_CODES.HONEYCOMB)
				{
					RetentionMagic.persist(this, mPrefs);
				}
			}

			@Override
			protected void onStop()
			{
				super.onStop();
				if (VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB)
				{
					RetentionMagic.persist(this, mPrefs);
				}
			}

			...
		}

## CAVEATS

When using a tool like ProGuard you'll have to take special care, since it may remove or rename fields and annotations.

It's recommended to add the following lines to your ```proguard.cfg``` (adjust to your needs):

		# this is required to keep the Annotations
		-keepattributes *Annotation*
		
		# keep relevant members in Activities
		-keepclassmembers class * extends android.app.Activity 
		{
			# optional, keep TAG fields if you use them for automatic namespacing
			# you don't need this line if don't use the "permanent" feature or
			# if you set the namespace like so:
			# @Retain(permanent = true, classNS = TAG)
			# or
			# @Retain(permanent = true, classNS = "someNameSpace")
			java.lang.String TAG;
	
			# optional, keep names of retained fields
			# you don't need this line if don't use the "permanent" feature or
			# if you set the key manually like in @Retain(key = "someKey");
			@org.dmfs.android.retentionmagic.annotations.* <fields>;

			# optional, keep names of fields considered for the instance names space, adjust to your needs
			# you don't need this line if don't use the "permanent" feature or
			# if you don't use per instance fields 
			private java.lang.String instanceTag;
		}

		# same for Fragments
		-keepclassmembers class * extends android.app.Fragment 
		{
			java.lang.String TAG;
			@org.dmfs.android.retentionmagic.annotations.* <fields>;
			private java.lang.String instanceTag;
		}

		# same for support library Fragments
		-keepclassmembers class * extends android.support.v4.app.Fragment 
		{
			java.lang.String TAG;
			@org.dmfs.android.retentionmagic.annotations.* <fields>;
			private java.lang.String instanceTag;
		}

## TODO

* support more field types
* add support for fields in parent classes

## License

Copyright (c) Marten Gajda 2013, licensed under Apache 2 (see `LICENSE`).
