package com.technicalround.iskconsadhanareport;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class ActivityFriendsSadhana extends AppCompatActivity
        implements InterfaceTaskCompleted, FragmentFriendsList.OnFragmentInteractionListener {

    public static AppPreferences userPreferences;
    public DatabaseActions databaseSadhanaControl;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_friends_sadhana);

            userPreferences = new AppPreferences(this);
            //Database controller init
            databaseSadhanaControl = new DatabaseActions(this);
            DatabaseActions.open();

            /*
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            */
            // Create the adapter that will return a fragment for each of the three
            // primary sections of the activity.
            mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

            // Set up the ViewPager with the sections adapter.
            mViewPager = (ViewPager) findViewById(R.id.container);
            mViewPager.setAdapter(mSectionsPagerAdapter);

            TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(mViewPager);
/*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
*/
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_friends_sadhana, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Callback function handler
    @Override
    public void onFragmentInteraction(String response) {
    }

    //Callback function handler
    @Override
    public void onHttpResponse(String response) {
    }

    @Override
    public void onTimePickSelection(View callerView, int combinedTimeIdx, boolean delayIgnoreDialog) {
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_activity_friends_sadhana, container, false);
            //TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        public DevoteeEntry[] devoteeList = null;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            //Fetch existing friends list
            devoteeList = DatabaseActions.fetchFriends(userPreferences.user.id, (short) 1);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            if (position == 0) {
                //Log.d("ISS", "PlaceHolderFrag: " + position);
                // Return a PlaceholderFragment (defined as a static inner class below).
                //return PlaceholderFragment.newInstance(position);
                return FragmentFriendsList.newInstance(userPreferences.user.email, "");
            } else {
                //Log.d("ISS", "FragmentSadhanaHistory:friendId: " + devoteeList[position-1].id);
                return FragmentSadhanaHistory.newInstance(devoteeList[position - 1].id, userPreferences.user.id);
            }
        }

        @Override
        public int getCount() {
            // Show (1+numFriends) total pages.
            if (devoteeList != null) {
                return (1 + devoteeList.length);
            } else return 1;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position > 0) {
                //Log.d("ISS", "getPageTitle called " + devoteeList[position - 1].name);
                return devoteeList[position - 1].name;
            } else return "FRIENDS";
        }
    }

}
