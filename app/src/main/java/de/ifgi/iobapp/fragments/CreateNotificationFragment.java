package de.ifgi.iobapp.fragments;

import android.app.FragmentManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import de.ifgi.iobapp.R;
import de.ifgi.iobapp.map.ScrollMapView;
import de.ifgi.iobapp.model.Notification;
import de.ifgi.iobapp.persistance.InternalStorage;

public class CreateNotificationFragment extends Fragment implements TagFragment {

    public static final String INTERNAL_STORAGE_KEY = "notifications";
    private static final String TAG = "CreateNotification";
    private static final LatLng GERMANY = new LatLng(51.429946, 10.355242);
    private static final double COORD_NO_VALUE = 1000.0;
    private static final int DEFAULT_REGION_RADIUS = 5;
    private static final int REGION_RADIUS_DIAMETER = 500;
    private static final float REGION_RADIUS_TRANSPARENCY = 0.4f;

    private EditText mNotificationNameEdit;
    private EditText mNotificationTextEdit;
    private ScrollMapView mMapView;
    private EditText mNotificationRadiusEdit;
    private RadioGroup mRadioEntersLeaves;
    private Button mCreateButton;

    private Notification mCreatedNotification;
    private boolean mEditMode;

    private GoogleMap mMap;
    private Marker mRegionCenterMarker;
    private GroundOverlay mRegionCircle;

    private int mRegionRadius = DEFAULT_REGION_RADIUS;
    private String mNotificationValidationMessage = "";

    public CreateNotificationFragment() {

    }

    public static CreateNotificationFragment newInstance(boolean editMode, int notificationId) {
        CreateNotificationFragment createNotificationFragment = new CreateNotificationFragment();

        Bundle args = new Bundle();
        args.putBoolean("editMode", editMode);
        args.putInt("notificationId", notificationId);
        createNotificationFragment.setArguments(args);

        return createNotificationFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_notification, container, false);

        mEditMode = getArguments().getBoolean("editMode");

        setUpMap(view, savedInstanceState);

        mNotificationNameEdit = (EditText) view.findViewById(R.id.notification_name_edit);
        mNotificationNameEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                editTextFocusChange(hasFocus, (EditText) view,
                        getString(R.string.notification_name));
            }
        });

        mNotificationTextEdit = (EditText) view.findViewById(R.id.notification_text_edit);
        mNotificationTextEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                editTextFocusChange(hasFocus, (EditText) view,
                        getString(R.string.notification_text_with_description));
            }
        });

        mNotificationRadiusEdit = (EditText) view.findViewById(R.id.notification_radius_edit);
        mNotificationRadiusEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().trim().equals("")) {
                    mRegionRadius = Integer.parseInt(editable.toString());
                } else {
                    mRegionRadius = 0;
                }
                if (mRegionCenterMarker != null) {
                    updateMarkerRadius();
                }
            }
        });

        mRadioEntersLeaves = (RadioGroup) view.findViewById(R.id.radio_enters_leaves);

        mCreateButton = (Button) view.findViewById(R.id.create_button);
        mCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readNotificationValues();

                if (validateNotificationValues()) {
                    if (mEditMode) {
                        if (editNotification()) {
                            Toast.makeText(getActivity(), getString(R.string.notification_was_edited),
                                    Toast.LENGTH_LONG).show();

                            Fragment notificationsFragment = new NotificationsFragment();
                            FragmentManager fragmentManager = getActivity().getFragmentManager();
                            String fragmentTag = ((TagFragment) notificationsFragment).getFragmentTag();
                            fragmentManager.beginTransaction().replace(R.id.content_frame,
                                    notificationsFragment).addToBackStack(fragmentTag).commit();

                        } else {
                            Toast.makeText(getActivity(), getString(R.string.notification_edit_error),
                                    Toast.LENGTH_LONG).show();
                        }
                    } else {
                        if (createNotification()) {
                            Toast.makeText(getActivity(), getString(R.string.notification_was_created),
                                    Toast.LENGTH_LONG).show();

                            Fragment notificationsFragment = new NotificationsFragment();
                            FragmentManager fragmentManager = getActivity().getFragmentManager();
                            String fragmentTag = ((TagFragment) notificationsFragment).getFragmentTag();
                            fragmentManager.beginTransaction().replace(R.id.content_frame,
                                    notificationsFragment).addToBackStack(fragmentTag).commit();

                        } else {
                            Toast.makeText(getActivity(), getString(R.string.notification_error),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    Toast.makeText(getActivity(), mNotificationValidationMessage,
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        if ( mEditMode ) {
            setEditMode(view);
        }

        return view;
    }

    private void setEditMode(View view) {
        ArrayList<Notification> notifications = new ArrayList<Notification>();
        try {
            notifications = (ArrayList<Notification>) InternalStorage.readObject(getActivity(),
                    CreateNotificationFragment.INTERNAL_STORAGE_KEY);
        } catch (IOException e) {
            Log.e(TAG, "" + e.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "" + e.getMessage());
        }

        int notificationId = getArguments().getInt("notificationId");
        Notification notification = notifications.get(notificationId);

        TextView headline = (TextView) view.findViewById(R.id.headline);
        headline.setText(getString(R.string.edit_notification));

        mNotificationNameEdit.setText(notification.getName());
        mNotificationNameEdit.setTextColor(getResources().getColor(R.color.iob_app_grey));

        mNotificationTextEdit.setText(notification.getText());
        mNotificationTextEdit.setTextColor(getResources().getColor(R.color.iob_app_grey));

        LatLng position = new LatLng(notification.getRegionCenterLat(),
                notification.getRegionCenterLon());
        mRegionCenterMarker = mMap.addMarker(new MarkerOptions()
                .position(position)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.marker)));

        mNotificationRadiusEdit.setText(notification.getRegionRadius() + "");

        RadioButton radioEnters = (RadioButton) view.findViewById(R.id.radio_enters);
        RadioButton radioLeaves = (RadioButton) view.findViewById(R.id.radio_leaves);

        if ( notification.isEnters() ) {
            radioEnters.setChecked(true);
            radioLeaves.setChecked(false);
        }
        else {
            radioEnters.setChecked(false);
            radioLeaves.setChecked(true);
        }

        mCreateButton.setText(getString(R.string.edit));
    }

    private void editTextFocusChange(boolean hasFocus, EditText editText, String string) {
        if (hasFocus && editText.getText().toString().equals(string)) {
            editText.setText("");
            editText.setTextColor(getResources().getColor(R.color.iob_app_grey));
        }
        else if (! hasFocus && editText.getText().toString().trim().equals("") ) {
            editText.setText(string);
            editText.setTextColor(getResources().getColor(R.color.iob_app_medium_grey));
        }
    }

    private void readNotificationValues() {
        String name = mNotificationNameEdit.getText().toString();
        String text = mNotificationTextEdit.getText().toString();

        double regionCenterLat;
        double regionCenterLon;

        if ( mRegionCenterMarker != null ) {
            regionCenterLat = mRegionCenterMarker.getPosition().latitude;
            regionCenterLon = mRegionCenterMarker.getPosition().longitude;
        }
        else {
            regionCenterLat = COORD_NO_VALUE;
            regionCenterLon = COORD_NO_VALUE;
        }

        int regionRadius = mRegionRadius;

        boolean enters;
        if ( mRadioEntersLeaves.getCheckedRadioButtonId() == R.id.radio_enters ) {
            enters = true;
        }
        else {
            enters = false;
        }

        mCreatedNotification = new Notification(name, text, regionCenterLat, regionCenterLon,
                regionRadius, enters);
    }

    private boolean validateNotificationValues() {
        if ( mCreatedNotification.getName().equals(getString(R.string.notification_name)) ||
                mCreatedNotification.getName().trim().equals("") ) {
            mNotificationValidationMessage = getString(R.string.notification_name_not_valid);
            return false;
        }
        else if ( mCreatedNotification.getText().equals(getString(
                R.string.notification_text_with_description)) ||
                mCreatedNotification.getText().trim().equals("") ) {
            mNotificationValidationMessage = getString(R.string.notification_text_not_valid);
            return false;
        }
        else if ( mCreatedNotification.getRegionCenterLat() == COORD_NO_VALUE ||
                mCreatedNotification.getRegionCenterLon() == COORD_NO_VALUE ) {
            mNotificationValidationMessage = getString(R.string.notification_no_marker);
            return false;
        }
        else if ( mCreatedNotification.getRegionRadius() == 0.0 ) {
            mNotificationValidationMessage = getString(R.string.notification_radius_null);
            return false;
        }

        return true;
    }

    private boolean createNotification() {
        try {
            ArrayList<Notification> notifications = (ArrayList<Notification>)
                    InternalStorage.readObject(getActivity(), INTERNAL_STORAGE_KEY);
            notifications.add(mCreatedNotification);

            InternalStorage.writeObject(getActivity(), INTERNAL_STORAGE_KEY, notifications);
            return true;
        } catch (FileNotFoundException f) {
            ArrayList<Notification> notifications = new ArrayList<Notification>();
            try {
                InternalStorage.writeObject(getActivity(), INTERNAL_STORAGE_KEY, notifications);
                createNotification();
                return true;
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
                return false;
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            return false;
        } catch (ClassNotFoundException e) {
            Log.e(TAG, e.getMessage());
            return false;
        }
    }

    private boolean editNotification() {
        try {
            ArrayList<Notification> notifications = (ArrayList<Notification>)
                    InternalStorage.readObject(getActivity(), INTERNAL_STORAGE_KEY);
            int notificationId = getArguments().getInt("notificationId");
            notifications.set(notificationId, mCreatedNotification);

            InternalStorage.writeObject(getActivity(), INTERNAL_STORAGE_KEY, notifications);
            return true;
        } catch (FileNotFoundException f) {
            ArrayList<Notification> notifications = new ArrayList<Notification>();
            try {
                InternalStorage.writeObject(getActivity(), INTERNAL_STORAGE_KEY, notifications);
                createNotification();
                return true;
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
                return false;
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            return false;
        } catch (ClassNotFoundException e) {
            Log.e(TAG, e.getMessage());
            return false;
        }
    }

    private void setUpMap(View view, Bundle savedInstanceState) {
        MapsInitializer.initialize(getActivity());

        switch (GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity())) {
            case ConnectionResult.SUCCESS:
                mMapView = (ScrollMapView) view.findViewById(R.id.map);
                mMapView.onCreate(savedInstanceState);

                if (mMapView != null) {
                    mMap = mMapView.getMap();

                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(GERMANY, 5));

                    mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(LatLng point) {
                            if ( mRegionCenterMarker == null ) {
                                mRegionCenterMarker = mMap.addMarker(new MarkerOptions()
                                        .position(point)
                                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.marker)));
                            }
                            else {
                                mRegionCenterMarker.setPosition(point);
                            }

                            updateMarkerRadius();
                        }
                    });
                }
                break;
            case ConnectionResult.SERVICE_MISSING:
                Toast.makeText(getActivity(), getString(R.string.google_play_services_missing),
                        Toast.LENGTH_SHORT).show();
                break;
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                Toast.makeText(getActivity(), getString(R.string.please_update_google_play_service),
                        Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(getActivity(), GooglePlayServicesUtil
                        .isGooglePlayServicesAvailable(getActivity()), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateMarkerRadius() {
        if ( mRegionCircle != null ) {
            mRegionCircle.remove();
        }

        LatLng latLng = mRegionCenterMarker.getPosition();

        int diameter = REGION_RADIUS_DIAMETER;
        Bitmap bitmap = Bitmap.createBitmap(diameter, diameter, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        paint.setColor(getResources().getColor(R.color.iob_app_red));

        canvas.drawCircle(diameter / 2, diameter / 2, diameter / 2, paint);
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bitmap);

        mRegionCircle = mMap.addGroundOverlay(new GroundOverlayOptions()
                .image(bitmapDescriptor)
                .position(latLng, mRegionRadius * 2, mRegionRadius * 2)
                .transparency(REGION_RADIUS_TRANSPARENCY));
    }

    @Override
    public void onResume() {
        if (mMapView != null) {
            mMapView.onResume();
        }

        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mMapView != null) {
            mMapView.onDestroy();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

        if (mMapView != null) {
            mMapView.onLowMemory();
        }
    }

    public String getFragmentTag() {
        return TAG;
    }

}
