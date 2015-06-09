package de.ifgi.iobapp;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import de.ifgi.iobapp.fragments.CreditsFragment;
import de.ifgi.iobapp.fragments.FindMyBicycleFragment;
import de.ifgi.iobapp.fragments.NotificationsFragment;
import de.ifgi.iobapp.fragments.PreferencesFragment;
import de.ifgi.iobapp.fragments.TheftProtectionFragment;
import de.ifgi.iobapp.style.FontsOverride;

public class MainActivity extends Activity {
    private ArrayList<String> mIconNames;
    private ArrayList<String> mNavigationItems;

    private DrawerLayout mDrawerLayout;
    private LinearLayout mLeftDrawerLayout;
    private ListView mDrawerList;

    private ImageButton mMenuButton;
    private LinearLayout mMenuCloseButton;

    private boolean headerClosed = false;
    private float y1, y2;

    private final int MIN_DISTANCE = 150;
    private final int DEFAULT_ITEM_POSITION = 0;

    private final int HEADER_ANIMATION_DURATION = 500;

    private final int CLOSED_HEADER_BIKE_RIGHT_MARGIN = 400;
    private final double CLOSED_HEADER_BIKE_PERCENT = 0.4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // override default monospace font
        FontsOverride.setDefaultFont(this, "MONOSPACE", "Ubuntu-L.ttf");

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
        Fragment fragment = new FindMyBicycleFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
        mDrawerList.setItemChecked(DEFAULT_ITEM_POSITION, true);

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

        // swipe bikes up gesture recognition
        final View headerImage = findViewById(R.id.header_image);
        headerImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        y1 = event.getY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        y2 = event.getY();
                        float deltaY = y2 - y1;
                        if (deltaY < -MIN_DISTANCE) {
                            if (!headerClosed) {
                                animateHeader();
                                headerClosed = true;
                            }
                            return true;
                        }
                        break;
                }
                return false;
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
            String iconName = iconNames.get(position);
            if ( mDrawerList.getCheckedItemPosition() == position ) {
                iconName += "_hover";
            }
            int iconId = context.getResources().getIdentifier(iconName, "mipmap",
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
                fragment = new FindMyBicycleFragment();
                break;
            case 1:
                fragment = new NotificationsFragment();
                break;
            case 2:
                fragment = new TheftProtectionFragment();
                break;
            case 3:
                fragment = new PreferencesFragment();
                break;
            case 4:
                fragment = new CreditsFragment();
                break;
            default:
                fragment = new FindMyBicycleFragment();
                break;
        }

        // exchange the old fragment with the new one
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        // set the clicked item as selected
        mDrawerList.setItemChecked(position, true);

        // close the navigation drawer
        mDrawerLayout.closeDrawer(mLeftDrawerLayout);
    }

    /*
        Method is called to animate the close of the header image
     */
    private void animateHeader() {
        // get Views of all necessary icons and images
        final ImageView headerImage = (ImageView) findViewById(R.id.header_image);
        final ImageView largeBicycle = (ImageView) findViewById(R.id.large_bicycle);
        final ImageView smallBicycle = (ImageView) findViewById(R.id.small_bicycle);
        ImageButton menuIcon = (ImageButton) findViewById(R.id.menu_button);

        // display pixel density
        final double density = getResources().getDisplayMetrics().density;

        // calculate new top margin for the header image
        FrameLayout.LayoutParams miParams = (FrameLayout.LayoutParams) menuIcon.getLayoutParams();
        double ratio = ((menuIcon.getHeight() + miParams.topMargin * 2.0) /
                headerImage.getHeight()) - 1;
        final int headerImageTopMargin = (int) (headerImage.getHeight() * ratio);

        // save initial height, width  and right margin of the large bicycle image
        FrameLayout.LayoutParams lbParams =
                (FrameLayout.LayoutParams) largeBicycle.getLayoutParams();
        final int largeBicycleHeight = lbParams.height;
        final int largeBicycleWidth = lbParams.width;
        final int largeBicycleRightMargin = lbParams.rightMargin;

        // save initial height, width and right margin of the small bicycle image
        FrameLayout.LayoutParams sbParams =
                (FrameLayout.LayoutParams) smallBicycle.getLayoutParams();
        final int smallBicycleHeight = sbParams.height;
        final int smallBicycleWidth = sbParams.width;
        final int smallBicycleRightMargin = sbParams.rightMargin;

        // animation for the header image
        Animation headerImageAnimation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                // animate the top margin of the header image from 0 to the calculated new value
                FrameLayout.LayoutParams params =
                        (FrameLayout.LayoutParams) headerImage.getLayoutParams();
                params.topMargin = (int) (headerImageTopMargin * interpolatedTime);
                headerImage.setLayoutParams(params);
            }
        };
        headerImageAnimation.setDuration(HEADER_ANIMATION_DURATION);

        // animation of the large bicycle image
        Animation largeBicycleAnimation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                // animate the height, width and right margin using a percent value
                FrameLayout.LayoutParams params =
                        (FrameLayout.LayoutParams) largeBicycle.getLayoutParams();
                double factor = 1 - (interpolatedTime * (1 - CLOSED_HEADER_BIKE_PERCENT));
                params.height = (int) (largeBicycleHeight * factor);
                params.width = (int) (largeBicycleWidth * factor);
                params.rightMargin = (int) (largeBicycleRightMargin +
                        (CLOSED_HEADER_BIKE_RIGHT_MARGIN * density * interpolatedTime));
                largeBicycle.setLayoutParams(params);
            }
        };
        largeBicycleAnimation.setDuration(HEADER_ANIMATION_DURATION);

        // animation of the small bicycle image
        Animation smallBicycleAnimation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                // animate the height, width and right margin using a percent value
                FrameLayout.LayoutParams params =
                        (FrameLayout.LayoutParams) smallBicycle.getLayoutParams();
                double factor = 1 - (interpolatedTime * (1 - CLOSED_HEADER_BIKE_PERCENT));
                params.height = (int) (smallBicycleHeight * factor);
                params.width = (int) (smallBicycleWidth * factor);
                params.rightMargin = (int) (smallBicycleRightMargin +
                        (CLOSED_HEADER_BIKE_RIGHT_MARGIN * density * interpolatedTime));
                smallBicycle.setLayoutParams(params);
            }
        };
        smallBicycleAnimation.setDuration(HEADER_ANIMATION_DURATION);

        // start all animations
        headerImage.startAnimation(headerImageAnimation);
        largeBicycle.startAnimation(largeBicycleAnimation);
        smallBicycle.startAnimation(smallBicycleAnimation);
    }
}