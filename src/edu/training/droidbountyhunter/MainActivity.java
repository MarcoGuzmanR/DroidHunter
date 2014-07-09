package edu.training.droidbountyhunter;

import java.util.Locale;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements
		ActionBar.TabListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
	 * derivative, which will keep every loaded fragment in memory. If this
	 * becomes too memory intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	public static String UDID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		int iNCf = oDB.ContarFugitivos();
		if (iNCf <= 0) {
			NetServices oNS = new NetServices(new OnTaskCompleted() {
				@Override
				public void onTaskCompleted(Object feed) {
					UpdateLists();
				}
				@Override
				public void onTaskError(Object feed) {
					Toast.makeText(getApplicationContext(), "Ocurrio un problema con el Servicio Web", Toast.LENGTH_LONG).show();
				}
			});
			oNS.oActRef = this;
			oNS.execute("Fugitivos", "");
		}

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		
		// Set up the action bar.
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}

		UDID = Secure.getString(this.getContentResolver(), Secure.ANDROID_ID);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_home, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
		case R.id.menu_agregar:
			Intent oW = new Intent();
			oW.setClass(this, AgregarActivity.class);
			startActivityForResult(oW, 0);
			break;
		}
		return true;
	}

	private Fragment[] mFragments;

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
			mFragments = new Fragment[3];
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a PlaceholderFragment (defined as a static inner class
			// below).
			if (mFragments[position] == null) {
				if (position < 2) {
					mFragments[position] = new ListFragment();
					Bundle args = new Bundle();
					args.putInt(ListFragment.ARG_SECTION_NUMBER, position);
					mFragments[position].setArguments(args);
				}
				else {
					mFragments[position] = new AboutFragment();
				}
			}
			return mFragments[position];
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_fugitivos).toUpperCase(l);
			case 1:
				return getString(R.string.title_capturados).toUpperCase(l);
			case 2:
				return getString(R.string.title_acercade).toUpperCase(l);
			}
			return null;
		}
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class ListFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_TEXT = "section_text";
		public static final String ARG_SECTION_NUMBER = "section_number";
		View iView;
		int iMode;

		public ListFragment() {
		}

		public void UpdateList() {
			String[][] aRef = oDB.ObtenerFugitivos(iMode == 1);
			if (aRef != null) {
				String[] aData = new String[aRef.length];
				for (int iCnt = 0; iCnt < aRef.length; iCnt++) {
					aData[iCnt] = aRef[iCnt][1];
				}
				ListView tlList = (ListView)iView.findViewById(R.id.bhlist);
				ArrayAdapter<String> aList = new ArrayAdapter<String>(getActivity(), R.layout.row_list, aData);
				tlList.setTag(aRef);
				tlList.setAdapter(aList);
			}
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			iView = inflater.inflate(R.layout.list_fragment, container, false);
			Bundle aArgs = this.getArguments();
			iMode = aArgs.getInt(ListFragment.ARG_SECTION_NUMBER);

			ListView tlList = ((ListView) iView.findViewById(R.id.bhlist));
			UpdateList();

			tlList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				public void onItemClick(AdapterView<?> aList, View vItem, int iPosition, long l) {
					Intent oW = new Intent();
					String[][] aData = (String[][])aList.getTag();
					oW.setClass(getActivity(), DetalleActivity.class);
					oW.putExtra("title", ((TextView) vItem).getText());
					oW.putExtra("mode", iMode);
					oW.putExtra("id", aData[iPosition][0]);
					startActivity(oW);
				}
			});

			return iView;
		}
	}

	public static class AboutFragment extends Fragment {
		public AboutFragment() {
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View iView = inflater.inflate(R.layout.about_fragment, container, false);
			RatingBar rbApp = ((RatingBar) iView.findViewById(R.id.ratingApp));
			String sRating = "0.0";

			try {
				if (System.getProperty("rating") != null) {
					sRating = System.getProperty("raiting");
				}
			} catch (Exception ex) {
			}
			
			if (sRating.isEmpty())
				sRating = "0.0";

			rbApp.setRating(Float.parseFloat(sRating));
			rbApp.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
				@Override
				public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
					System.setProperty("rating", String.valueOf(rating));
					ratingBar.setRating(rating);
					
				}
			});
			return iView;
		}
	}

	public static DBProvider oDB;

	public MainActivity() {
		oDB = new DBProvider(this);
	}

	public void UpdateLists() {
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.setCurrentItem(getActionBar().getSelectedNavigationIndex());
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		UpdateLists();
	}

}