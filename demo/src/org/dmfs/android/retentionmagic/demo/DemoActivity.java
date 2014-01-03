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

package org.dmfs.android.retentionmagic.demo;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import org.dmfs.android.retentionmagic.FragmentActivity;
import org.dmfs.android.retentionmagic.annotations.Retain;
import org.dmfs.android.retentionmagic.annotations.RetainArrayList;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;


public class DemoActivity extends FragmentActivity implements OnPageChangeListener
{

	@SuppressWarnings("unused")
	private final static String TAG = "DemoActivity";

	SectionsPagerAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;

	@Retain
	public boolean _boolean;

	@Retain
	public boolean[] _booleanArray;

	@Retain
	public byte _byte;

	@Retain
	public byte[] _byteArray;

	@Retain
	public short _short;

	@Retain
	public short[] _shortArray;

	@Retain
	public char _char;

	@Retain
	public char[] _charArray;

	@Retain
	public int _int;

	@Retain
	public int[] _intArray;

	@Retain
	public float _float;

	@Retain
	public float[] _floatArray;

	@Retain
	public double _double;

	@Retain
	public double[] _doubleArray;

	@Retain
	public long _long;

	@Retain
	public long[] _longArray;

	@Retain
	public String _string;

	@Retain
	public String[] _stringArray;

	@Retain
	public Bundle _bundle;

	@Retain
	public CharSequence _charSequence;

	@Retain
	public CharSequence[] _charSequenceArray;

	@Retain
	public Parcelable _parcelable;

	@Retain
	public Parcelable[] _parcelableSequenceArray;

	@Retain
	public Serializable _serializeable;

	// @Retain
	public IBinder _binder;

	@Retain
	public SparseArray<Parcelable> _sparseArrayParcelable;

	@RetainArrayList(genericType = Integer.class)
	public ArrayList<Integer> _integerArrayList;

	@RetainArrayList(genericType = String.class)
	public ArrayList<String> _stringArrayList;

//	@RetainArrayList(genericType = CharSequence.class)
//	public ArrayList<CharSequence> _charSequenceArrayList;

	@RetainArrayList(genericType = Parcelable.class)
	public ArrayList<Parcelable> _parcelableArrayList;

	@Retain(permanent = true, instanceNSField = "mInstance")
	private String textValue = "initial value";

	@Retain(permanent = true)
	private boolean firstStart = true;

	public int mInstance = 123;

	// always start with the last selected section
	@Retain(permanent = true)
	private int mSelectedSection = 0;


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		long start = System.currentTimeMillis();
		super.onCreate(savedInstanceState);
		Log.i("DemoActivity", "time to restore state " + (System.currentTimeMillis() - start));
		if (savedInstanceState == null)
		{
			_boolean = true;
			_booleanArray = new boolean[] { true, false };
			_byte = 1;
			_byteArray = new byte[] { 1, 2, 3 };
			_short = 2;
			_shortArray = new short[] { 4, 5, 6 };
			_char = '3';
			_charArray = new char[] { '7', '8', '9' };
			_int = 4;
			_intArray = new int[] { 10, 11, 12 };
			_float = 5f;
			_floatArray = new float[] { 13f, 14f, 15f };
			_double = 6d;
			_doubleArray = new double[] { 16d, 17d, 18d };
			_long = 7;
			_longArray = new long[] { 19, 20, 21 };

			_string = "8";
			_stringArray = new String[] { "22", "23", "24" };
			_charSequence = "9";
			_charSequenceArray = new CharSequence[] { "25", "26", "27" };
			_bundle = new Bundle();
			_bundle.putString("key", "value");

			_integerArrayList = new ArrayList<Integer>();
			_integerArrayList.add(28);
			_integerArrayList.add(29);

			_stringArrayList = new ArrayList<String>();
			_stringArrayList.add("30");
			_stringArrayList.add("31");

	/*		_charSequenceArrayList = new ArrayList<CharSequence>();
			_charSequenceArrayList.add("32");
			_charSequenceArrayList.add("33");
			*/
		}
		else
		{
			assertEquals(true, _boolean);
			assertTrue(Arrays.equals(_booleanArray, new boolean[] { true, false }));
			assertEquals(1, _byte);
			assertTrue(Arrays.equals(_byteArray, new byte[] { 1, 2, 3 }));
			assertEquals(2, _short);
			assertTrue(Arrays.equals(_shortArray, new short[] { 4, 5, 6 }));
			assertEquals('3', _char);
			assertTrue(Arrays.equals(_charArray, new char[] { '7', '8', '9' }));
			assertEquals(4, _int);
			assertTrue(Arrays.equals(_intArray, new int[] { 10, 11, 12 }));
			assertEquals(5f, _float);
			assertTrue(Arrays.equals(_floatArray, new float[] { 13f, 14f, 15f }));
			assertEquals(6d, _double);
			assertTrue(Arrays.equals(_doubleArray, new double[] { 16d, 17d, 18d }));
			assertEquals(7, _long);
			assertTrue(Arrays.equals(_longArray, new long[] { 19, 20, 21 }));

			assertEquals("8", _string);
			assertTrue(Arrays.equals(_stringArray, new String[] { "22", "23", "24" }));
			assertEquals("9", _charSequence);
			assertTrue(Arrays.equals(_charSequenceArray, new CharSequence[] { "25", "26", "27" }));
			assertEquals("value", _bundle.get("key"));
			assertEquals(1, _bundle.size());

			ArrayList<Integer> integerArrayList = new ArrayList<Integer>();
			integerArrayList.add(28);
			integerArrayList.add(29);
			assertEquals(integerArrayList, _integerArrayList);

			ArrayList<String> stringArrayList = new ArrayList<String>();
			stringArrayList.add("30");
			stringArrayList.add("31");
			assertEquals(stringArrayList, _stringArrayList);

		/*	ArrayList<CharSequence> charSequenceArrayList = new ArrayList<CharSequence>();
			charSequenceArrayList.add("32");
			charSequenceArrayList.add("33");
			assertEquals(charSequenceArrayList, _charSequenceArrayList);
			*/
		}

		setContentView(R.layout.activity_demo);

		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.setCurrentItem(mSelectedSection);
		mViewPager.setOnPageChangeListener(this);

		if (firstStart)
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			TextView textView = new TextView(this);
			textView.setText("this is the first start of this activity");
			builder.setView(textView);
			builder.setTitle("Info");
			builder.create().show();
		}

		firstStart = false;
	}


	@Override
	public void onPageScrollStateChanged(int arg0)
	{
		// TODO Automatisch generierter Methodenstub

	}


	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2)
	{
		// TODO Automatisch generierter Methodenstub

	}


	@Override
	public void onPageSelected(int arg0)
	{
		mSelectedSection = arg0;
	}

	public class SectionsPagerAdapter extends FragmentPagerAdapter
	{

		public SectionsPagerAdapter(FragmentManager fm)
		{
			super(fm);
		}


		@Override
		public Fragment getItem(int position)
		{
			Fragment fragment = new DummySectionFragment();
			Bundle args = new Bundle();
			args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
			fragment.setArguments(args);
			return fragment;
		}


		@Override
		public int getCount()
		{
			// Show 3 total pages.
			return 3;
		}


		@Override
		public CharSequence getPageTitle(int position)
		{
			Locale l = Locale.getDefault();
			switch (position)
			{
				case 0:
					return getString(R.string.title_section1).toUpperCase(l);
				case 1:
					return getString(R.string.title_section2).toUpperCase(l);
				case 2:
					return getString(R.string.title_section3).toUpperCase(l);
			}
			return null;
		}
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply displays dummy text.
	 */
	public static class DummySectionFragment extends org.dmfs.android.retentionmagic.SupportFragment implements TextWatcher
	{
		@SuppressWarnings("unused")
		private final static String TAG = "DummySectionFragment";

		/**
		 * The fragment argument representing the section number for this fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		@Retain
		public boolean _boolean;

		@Retain
		public boolean[] _booleanArray;

		@Retain
		public byte _byte;

		@Retain
		public byte[] _byteArray;

		@Retain
		public short _short;

		@Retain
		public short[] _shortArray;

		@Retain
		public char _char;

		@Retain
		public char[] _charArray;

		@Retain
		public int _int;

		@Retain
		public int[] _intArray;

		@Retain
		public float _float;

		@Retain
		public float[] _floatArray;

		@Retain
		public double _double;

		@Retain
		public double[] _doubleArray;

		@Retain
		public long _long;

		@Retain
		public long[] _longArray;

		@Retain
		public String _string;

		@Retain
		public String[] _stringArray;

		@Retain
		public Bundle _bundle;

		@Retain
		public CharSequence _charSequence;

		@Retain
		public CharSequence[] _charSequenceArray;

		@Retain
		public Parcelable _parcelable;

		@Retain
		public Parcelable[] _parcelableSequenceArray;

		@Retain
		public Serializable _serializeable;

		// @Retain
		public IBinder _binder;

		@Retain
		public SparseArray<Parcelable> _sparseArrayParcelable;

		@RetainArrayList(genericType = Integer.class)
		public ArrayList<Integer> _integerArrayList;

		@RetainArrayList(genericType = String.class)
		public ArrayList<String> _stringArrayList;

	/*	@RetainArrayList(genericType = CharSequence.class)
		public ArrayList<CharSequence> _charSequenceArrayList;
*/
		@RetainArrayList(genericType = Parcelable.class)
		public ArrayList<Parcelable> _parcelableArrayList;

		private EditText editText;

		@Retain(permanent = true, instanceNSField = "instanceTag")
		private String text = "initial text";

		@SuppressWarnings("unused")
		private String instanceTag;


		public DummySectionFragment()
		{
		}


		@Override
		public void onCreate(Bundle savedInstanceState)
		{
			// set instance tag first
			instanceTag = "fragment" + getArguments().getInt(ARG_SECTION_NUMBER);
			super.onCreate(savedInstanceState);
			if (savedInstanceState == null)
			{
				_boolean = false;
				_booleanArray = new boolean[] { false, true };
				_byte = 2;
				_byteArray = new byte[] { 2, 3, 4 };
				_short = 3;
				_shortArray = new short[] { 5, 6, 7 };
				_char = '4';
				_charArray = new char[] { '8', '9', 'A' };
				_int = 5;
				_intArray = new int[] { 11, 12, 13 };
				_float = 6f;
				_floatArray = new float[] { 14f, 15f, 16f };
				_double = 7d;
				_doubleArray = new double[] { 17d, 18d, 19d };
				_long = 8;
				_longArray = new long[] { 20, 21, 22 };

				_string = "9";
				_stringArray = new String[] { "23", "24", "25" };
				_charSequence = "10";
				_charSequenceArray = new CharSequence[] { "26", "27", "28" };
				_bundle = new Bundle();
				_bundle.putString("key", "value");

				_integerArrayList = new ArrayList<Integer>();
				_integerArrayList.add(29);
				_integerArrayList.add(30);

				_stringArrayList = new ArrayList<String>();
				_stringArrayList.add("31");
				_stringArrayList.add("32");

				/*
				_charSequenceArrayList = new ArrayList<CharSequence>();
				_charSequenceArrayList.add("33");
				_charSequenceArrayList.add("34");
				*/
			}
			else
			{
				assertEquals(false, _boolean);
				assertTrue(Arrays.equals(_booleanArray, new boolean[] { false, true }));
				assertEquals(2, _byte);
				assertTrue(Arrays.equals(_byteArray, new byte[] { 2, 3, 4 }));
				assertEquals(3, _short);
				assertTrue(Arrays.equals(_shortArray, new short[] { 5, 6, 7 }));
				assertEquals('4', _char);
				assertTrue(Arrays.equals(_charArray, new char[] { '8', '9', 'A' }));
				assertEquals(5, _int);
				assertTrue(Arrays.equals(_intArray, new int[] { 11, 12, 13 }));
				assertEquals(6f, _float);
				assertTrue(Arrays.equals(_floatArray, new float[] { 14f, 15f, 16f }));
				assertEquals(7d, _double);
				assertTrue(Arrays.equals(_doubleArray, new double[] { 17d, 18d, 19d }));
				assertEquals(8, _long);
				assertTrue(Arrays.equals(_longArray, new long[] { 20, 21, 22 }));

				assertEquals("9", _string);
				assertTrue(Arrays.equals(_stringArray, new String[] { "23", "24", "25" }));
				assertEquals("10", _charSequence);
				assertTrue(Arrays.equals(_charSequenceArray, new CharSequence[] { "26", "27", "28" }));
				assertEquals("value", _bundle.get("key"));
				assertEquals(1, _bundle.size());

				ArrayList<Integer> integerArrayList = new ArrayList<Integer>();
				integerArrayList.add(29);
				integerArrayList.add(30);
				assertEquals(integerArrayList, _integerArrayList);

				ArrayList<String> stringArrayList = new ArrayList<String>();
				stringArrayList.add("31");
				stringArrayList.add("32");
				assertEquals(stringArrayList, _stringArrayList);

			/*	ArrayList<CharSequence> charSequenceArrayList = new ArrayList<CharSequence>();
				charSequenceArrayList.add("33");
				charSequenceArrayList.add("34");
				assertEquals(charSequenceArrayList, _charSequenceArrayList);
				*/
			}

		}


		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
			View rootView = inflater.inflate(R.layout.fragment_demo_dummy, container, false);
			editText = (EditText) rootView.findViewById(R.id.editText1);
			editText.setText(text);
			editText.addTextChangedListener(this);
			return rootView;
		}


		@Override
		public void afterTextChanged(Editable s)
		{
			text = s.toString();
		}


		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after)
		{
		}


		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count)
		{
		}
	}

}
