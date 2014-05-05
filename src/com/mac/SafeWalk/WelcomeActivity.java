package com.mac.SafeWalk;

/**
 * Created by Rhyan on 4/10/14.
 */

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Copyright 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Further acknowledgment
 * http://www.lucazanini.eu/2012/android/tabs-and-swipe-views/?lang=en
 * http://developer.android.com/training/implementing-navigation/lateral.html
 *
 */
public class WelcomeActivity extends FragmentActivity implements ActionBar.TabListener{

    /**
     * The PagerAdapter will provide fragments representing each object in welcome_screen collection.
     * We use welcome_screen {@link android.support.v4.app.FragmentStatePagerAdapter}
     * derivative, which will destroy and re-create fragments as needed, saving and restoring their
     * state in the process. This is important to conserve memory.
     */
    private CollectionPagerAdapter collectionPagerAdapter;

    /**
     * The ViewPager will display the object collection.
     */
    private ViewPager viewPager;

    // number of tabs needed
    private final int NUM_ITEMS = 3;


    /**
     * Called when the activity is first created.
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_screen);

        // Create adapter
        collectionPagerAdapter = new CollectionPagerAdapter(getSupportFragmentManager());

        // Set up action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.hide();

        // Specify that the Home/Up button should not be enabled, since there is no hierarchical parent.
        actionBar.setHomeButtonEnabled(false);

        // Specify that we will be displaying tabs in the action bar.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Set up the ViewPager
        // Attache the adapter and implement listener in order to respond when user swipes between sections.
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(collectionPagerAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // Select the corresponding tab when user swipes
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add tab to the action bar.
        for (int i = 0; i < collectionPagerAdapter.getCount(); i++) {
            // Create tabs
            actionBar.addTab(actionBar.newTab()
                    .setTabListener(this));
        }
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        // When tab is selected, switch to the corresponding page in the ViewPager.
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }


    public class CollectionPagerAdapter extends FragmentPagerAdapter {

        public CollectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 2:
                    // Allows us to define a new fragment (GettingStarted) and open it
                    GettingStarted gettingStarted = new GettingStarted();
                    return gettingStarted;

                default:
                    // Gets the other fragments (tabs).
                    Fragment fragment = new TabFragment();
                    Bundle args = new Bundle();
                    args.putInt(TabFragment.ARG_SECTION_NUMBER, i);
                    fragment.setArguments(args);
                    return fragment;
            }
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public String getPageTitle(int position) {
            String tabLabel = null;
            switch (position) {
                case 0:
                    tabLabel = "Logo";
                    break;
                case 1:
                    tabLabel = getString(R.string.label_tab2);
                    break;
                case 2:
                    tabLabel = getString(R.string.label_tab3);
                    break;
            }
            return tabLabel;
        }
    }

    /**
     * A fragment that launches settings_Activity when button is pressed.
     */
    public static class GettingStarted extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.getting_started, container, false);

            // Open the settings on click.
            rootView.findViewById(R.id.settings_Activity)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent settings = new Intent(getActivity(), SettingsActivity.class);
                            startActivity(settings);
                        }
                    });

            return rootView;
        }
    }

    /*
     * Display the other fragment tabs
     */
    public static class TabFragment extends Fragment {

        public static final String ARG_SECTION_NUMBER = "section_number";

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            Bundle args = getArguments();
            int position = args.getInt(ARG_SECTION_NUMBER);

            int tabLayout = 0;

            switch (position){
                case 0:
                    tabLayout = R.layout.logo_display;
                    break;
                case 1:
                    tabLayout = R.layout.how_to_use;
                    break;
            }

            View rootView = inflater.inflate(tabLayout, container, false);
            return rootView;
        }
    }
}
