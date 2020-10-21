package com.technicalround.iskconsadhanareport;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.rampo.updatechecker.UpdateChecker;
import com.rampo.updatechecker.notice.Notice;
import com.splunk.mint.Mint;

//import android.app.Fragment;
//import hotchemi.android.rate.AppRate;

public class ActivityMainDevotee extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, InterfaceTaskCompleted,
        FragmentInboxMessage.OnFragmentInteractionListener,
        View.OnClickListener{

    public static final String[] string_MONTH_NAMES = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    public static AppPreferences userPreferences;
    public int defaultUserId = 0;
    public DatabaseActions databaseSadhanaControl;

    /* This function prints Calender Date from dateIndex from 1Jan2016
    * */
    public static String printDateFromDateIndex(int dateIndex) {
        // date INdex is from 01/Jan/2016 considering 31 Days in each month.
        int year = dateIndex / (31 * 12);
        int dateIndexInaYear = dateIndex - (year * 31 * 12);
        year += 2016;
        int month = dateIndexInaYear / 31;
        int day = dateIndexInaYear - (month * 31) + 1;//0 index is date:1
        return (string_MONTH_NAMES[month] + " " + day + ", " + year);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main_devotee);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            if (!(BuildConfig.DEBUG)) {
                Mint.initAndStartSession(this.getApplication(), "123456789");
                Log.d("ISS", "Mint session started");
            } else Log.d("ISS", "Debug Mode: Mint NOT started");

            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //AppRate.with(this).showRateDialog(this);
                    //AppRate.with(getBaseContext()).showRateDialog(com.technicalround.iskconsadhanareport.ActivityAboutUs.this);

                    Snackbar.make(view, "visit 'About App' page to report any issue.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
            //user defined params
            //navigationView.removeHeaderView();
            //Add customized header
            View header = navigationView.inflateHeaderView(R.layout.nav_header_main_devotee);
            TextView drawerUserName = (TextView) header.findViewById(R.id.textViewUserName);
            TextView drawerEmail = (TextView) header.findViewById(R.id.textViewEmail);
            ImageView imageViewDrawer = (ImageView) header.findViewById(R.id.imageView);

            userPreferences = new AppPreferences(this);
            defaultUserId = userPreferences.user.id;
            drawerUserName.setText(userPreferences.user.name);
            drawerEmail.setText(userPreferences.user.email);
            imageViewDrawer.setOnClickListener(this);

            //Database controller init
            databaseSadhanaControl = new DatabaseActions(this);
            DatabaseActions.open();
            ClassImageLoader.initImageLoader(getApplicationContext());
            //Set user image
            if(userPreferences.user.picVer !=0) {
                imageViewDrawer = (ImageView) header.findViewById(R.id.imageView);
                ClassImageLoader.displayUserImage(getApplicationContext().getString(R.string.WEB_ROOT),
                        imageViewDrawer, defaultUserId, userPreferences.user.picVer);
            }
            //App version update checker
            UpdateChecker checker = new UpdateChecker(this);
            checker.setNotice(Notice.NOTIFICATION);
            checker.start();

            //Bring Overview fragment
            fragmentChange(R.id.nav_home);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.imageView:
                Log.d("ISS","Profile Pic clicked");
                intent = new Intent(getApplicationContext(), ActivityProfilePic.class);
                startActivity(intent);
                finish();
                break;
        }

    }
    @Override
    protected void onResume() {
        super.onResume();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        /*
        try {
            //Set user image
            if(userPreferences.user.picVer !=0) {
                ImageView imageViewDrawer = (ImageView) header.findViewById(R.id.imageView);
                ClassImageLoader.displayUserImage(getApplicationContext().getString(R.string.WEB_ROOT),
                        imageViewDrawer, defaultUserId, userPreferences.user.picVer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        */
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            AlertDialog.Builder b = new AlertDialog.Builder(this);
            b.setTitle("Do you want to Exit?");
            String[] types = {"No", "Go to Home", "Exit Program"};

            b.setItems(types, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    switch (which) {
                        case 0:
                            break;
                        case 1:
                            fragmentChange(R.id.nav_home);
                            break;
                        case 2:
                            //super.onBackPressed();
                            if (Build.VERSION.SDK_INT >= 16)
                                finishAffinity();//Exit all Activities
                            else
                                finish();
                            break;
                    }
                }
            });
            b.show();
        }
    }

    @Override
    public void onDestroy() {
        DatabaseActions.close();
        super.onDestroy();
    }

    public void openDrawer(View view) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.openDrawer(GravityCompat.START);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // you can Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.main_devotee, menu);
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
            Intent intent = new Intent(this, ActivitySettings.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        fragmentChange(id);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void fragmentChange(int id) {
        // Handle navigation view item clicks here.
        android.support.v4.app.Fragment fragment = null;
        if (id == R.id.nav_home) {
            // Handle the HOME action
            fragment = FragmentOverview.newInstance(userPreferences.user.name, "");
        } else if (id == R.id.nav_fillSadhana) {
            // Handle the FILL Sadhana action
            fragment = FragmentFillSadhana.newInstance("", "");
        } else if (id == R.id.nav_fillSadhanaQuick) {
            // Handle the FILL Quick Sadhana action
            fragment = FragmentFillSadhanaQuick.newInstance("", "");
        } else if (id == R.id.nav_history) {
            // Handle the History action
            fragment = FragmentSadhanaHistory.newInstance(defaultUserId, defaultUserId);
        } else if (id == R.id.nav_devotees) {
            //Start Friends activity
            Intent intent = new Intent(this, ActivityFriendsSadhana.class);
            startActivity(intent);
            //finish();
        } else if (id == R.id.nav_inbox) {
            // Handle the Inbox action
            //Toast.makeText(this,"Feature Coming Soon...", Toast.LENGTH_SHORT).show();
            fragment = FragmentInboxMessage.newInstance(userPreferences.user.name, ":INBOX");
        } else if (id == R.id.nav_profile) {
            // Handle the Profile action
            //fragment = FragmentOverview.newInstance(userPreferences.user.name,":Profile");
            //Start Profile activity
            Intent intent = new Intent(this, ActivityAddDevotee.class);
            intent.putExtra("isProfilePage", true);
            startActivity(intent);
        } else if (id == R.id.nav_export) {
            // Handle the Profile action
            //Start CSV Data Backup
            DatabaseActions.exportSadhanaEntriesToCsv(this, userPreferences.user.id,
                    userPreferences.user.name);
            DevoteeEntry[] friendList = DatabaseActions.fetchFriends(userPreferences.user.id, (short) 0);
            for (int i = 0; i < friendList.length; i++) {
                DatabaseActions.exportSadhanaEntriesToCsv(this, friendList[i].id,
                        friendList[i].name);
            }
            Log.d("ISS", "Database backup done");
        } else if (id == R.id.nav_settings) {
            // Handle the Settings action
            Intent intent = new Intent(this, ActivityAboutUs.class);
            startActivity(intent);
        }

        // Insert the fragment by replacing any existing fragment
        if (fragment != null) {
            // get fragment manager
            FragmentManager fm = getSupportFragmentManager();
            // add
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.content_frame, fragment).commit();
        }
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
}
