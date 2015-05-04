package de.ifgi.iobapp;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends Activity {
    private ArrayList<String> mIconNames;
    private ArrayList<String> mNavigationItems;
    private DrawerLayout mDrawerLayout;
    private LinearLayout mLeftDrawerLayout;
    private ListView mDrawerList;
    private ImageButton mMenuButton;
    private LinearLayout mMenuCloseButton;
    private int mDefaultItemPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // file name list of the navigation drawer icons
        mIconNames = new ArrayList<String>(Arrays.asList(
                getResources().getStringArray(R.array.navigation_icons)));

        // list of the navigation drawer item texts
        mNavigationItems = new ArrayList<String>(Arrays.asList(
                getResources().getStringArray(R.array.navigation_items)));

        // drawer layout views
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mLeftDrawerLayout = (LinearLayout) findViewById(R.id.left_drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // initialize navigation drawer (fill it with icons and text) and set the click listener
        NavigationDrawerAdapter navigationDrawerAdapter =
                new NavigationDrawerAdapter(MainActivity.this, mIconNames, mNavigationItems);
        mDrawerList.setAdapter(navigationDrawerAdapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // initialize the default fragment for the content frame
        Fragment fragment = new OverviewFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
        mDrawerList.setItemChecked(mDefaultItemPosition, true);

        // set click listener for the menu button
        mMenuButton = (ImageButton) findViewById(R.id.menu_button);
        mMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(mLeftDrawerLayout);
            }
        });

        // set click listener for the menu drawer heading (close menu)
        mMenuCloseButton = (LinearLayout) findViewById(R.id.menu_close_button);
        mMenuCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.closeDrawer(mLeftDrawerLayout);
            }
        });
    }

    // adapter initializes the navigation drawer ListView and fills it with the icons and item texts
    private class NavigationDrawerAdapter extends ArrayAdapter<String> {
        Context context;
        private ArrayList<String> textValues = new ArrayList<String>();
        private ArrayList<String> iconNames = new ArrayList<String>();

        public NavigationDrawerAdapter(Context context, ArrayList<String> iconNames,
                                       ArrayList<String> textValues) {
            super(context, R.layout.navigation_drawer_item, textValues);
            this.context = context;
            this.textValues = textValues;
            this.iconNames = iconNames;
        }

        @Override
        public View getView(int position, View coverView, ViewGroup parent) {
            // fill the navigation drawer item for the row 'position'
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.navigation_drawer_item,
                    parent, false);

            // set the navigation item icon
            ImageView icon = (ImageView) rowView.findViewById(R.id.navigation_drawer_icon);
            int iconId = context.getResources().getIdentifier(iconNames.get(position), "mipmap",
                    context.getPackageName());
            icon.setImageResource(iconId);

            // set the navigation item text
            TextView text = (TextView) rowView.findViewById(R.id.navigation_drawer_text);
            text.setText(textValues.get(position));

            return rowView;
        }
    }

    // set click listener for each of the navigation drawer items
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position, view);
        }
    }

    // react to clicks on the navigation drawer items
    private void selectItem(int position, View view) {
        Fragment fragment;

        // set the fragment depending on the clicked position
        switch (position) {
            case 0:
                fragment = new OverviewFragment();
                break;
            case 1:
                fragment = new StatisticsFragment();
                break;
            case 2:
                fragment = new NotificationsFragment();
                break;
            case 3:
                fragment = new FindMyBicycleFragment();
                break;
            case 4:
                fragment = new MapFragment();
                break;
            case 5:
                fragment = new TheftProtectionFragment();
                break;
            case 6:
                fragment = new WeatherForecastFragment();
                break;
            case 7:
                fragment = new PreferencesFragment();
                break;
            case 8:
                fragment = new CreditsFragment();
                break;
            default:
                fragment = new OverviewFragment();
                break;
        }

        // exchange the old fragment with the new one
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        // set the clicked item as selected
        mDrawerList.setItemChecked(position, true);
        /*TextView textView = (TextView) ((ViewGroup) view).getChildAt(1);
        Log.d("",""+textView.getText());
        textView.setText("abc");
        textView.setTextColor(getResources().getColor(R.color.iob_app_white));*/

        // close the navigation drawer
        mDrawerLayout.closeDrawer(mLeftDrawerLayout);
    }
}