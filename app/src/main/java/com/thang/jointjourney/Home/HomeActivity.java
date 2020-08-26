package com.thang.jointjourney.Home;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.DistanceMatrixApi;
import com.google.maps.DistanceMatrixApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.EncodedPolyline;
import com.google.maps.model.TravelMode;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.thang.jointjourney.Adapter.PlaceAutoSuggestAdapter;
import com.thang.jointjourney.Dialogs.OfferRideCreatedDialog;
import com.thang.jointjourney.Dialogs.WelcomeDialog;
import com.thang.jointjourney.Login.LoginActivity;
import com.thang.jointjourney.MapDirectionHelper.TaskLoadedCallback;
import com.thang.jointjourney.R;
import com.thang.jointjourney.Utils.BottomNavigationViewHelper;
import com.thang.jointjourney.Utils.UniversalImageLoader;
import com.thang.jointjourney.models.Token;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements OnMapReadyCallback, TaskLoadedCallback {

    //variables linh tinh
    private static final String TAG = "HomeActivity";
    private static final int ACTIVITY_NUMBER = 0;
    private Boolean carOwner;
    private String userID;
    private Polyline currentPolyline;
    String duration, distance;

    //Google map permissions
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private static final int PLACE_PICKER_REQUEST = 1;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136));
    private Boolean mLocationPermissionsGranted = false;

    //Google map variables
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private double currentLatitude, currentLongitude, destinationLatitude, destinationLongitude;
    private LatLng fromLatlng, toLatlng;
    private Location currentLocation;
    private ArrayList<LatLng> path;
    private ArrayList<com.thang.jointjourney.models.LatLng> convertedList;
    private PolylineOptions opts;
    private GeoApiContext mGeoApiContext = null;

    private Marker fromMarker = null, toMarker = null;


    //Widgets
    private AutoCompleteTextView destinationTextview, locationTextView;
    private Button mSearchBtn, mDirectionsBtn, mSwitchTextBtn, mDelete;
    private RadioButton findButton, offerButton;
    private RadioGroup mRideSelectionRadioGroup;
    private BottomNavigationView bottomNavigationView;
    private ImageView mLocationBtn;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabse;
    private DatabaseReference mRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Log.d(TAG, "onCreate: starting.");


        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabse = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabse.getReference();


        if (mAuth.getCurrentUser() != null) {
            //lấy id user hiện tại
            userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            findButton = (RadioButton) findViewById(R.id.findButton);
            offerButton = (RadioButton) findViewById(R.id.offerButton);

            //Ẩn nút đăng chuyến đi nếu user k có xe
            getUserInformation(userID);
            ////////////////////////////////////////////////////////////////////////

            //Tạo token cho user
            updateFirebaseToken();

            checkNotifications();

            //Subscribes to a topic with that user ID so only that user can see messages with that user ID
            FirebaseMessaging.getInstance().subscribeToTopic(userID);
        }

        //kết nối biến với id widget
        destinationTextview = (AutoCompleteTextView) findViewById(R.id.destinationTextview);
        locationTextView = (AutoCompleteTextView) findViewById(R.id.locationTextview);

        mSearchBtn = (Button) findViewById(R.id.searchBtn);
        mSwitchTextBtn = (Button) findViewById(R.id.switchTextBtn);
        mDirectionsBtn = (Button) findViewById(R.id.directionsBtn);
        mRideSelectionRadioGroup = (RadioGroup) findViewById(R.id.toggle);
        mLocationBtn = (ImageView) findViewById(R.id.locationImage);
        mDelete = (Button) findViewById(R.id.deleteText);


        mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationTextView.setText("");
                destinationTextview.setText("");
            }
        });

        mLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDeviceLocationAndAddMarker();
            }
        });

        mSwitchTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (destinationTextview.getText().toString().trim().length() > 0 && locationTextView.getText().toString().trim().length() > 0) {
                    String tempDestination1 = destinationTextview.getText().toString();
                    String tempDestination12 = locationTextView.getText().toString();

                    locationTextView.setText(tempDestination1);
                    destinationTextview.setText(tempDestination12);

                    locationTextView.dismissDropDown();
                    destinationTextview.dismissDropDown();
                } else {
                    Toast.makeText(HomeActivity.this, "Bạn phải nhập điểm đi và điểm đến", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mDirectionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (destinationTextview.getText().toString().trim().length() > 0 && locationTextView.getText().toString().trim().length() > 0) {
                    String destination = destinationTextview.getText().toString();
                    String origin = locationTextView.getText().toString();
                    getRouteInformation(mGeoApiContext, origin, destination);
//                    Log.d(TAG, "Distance from direction Api ="+ distance);
//                    com.google.maps.model.LatLng originLatlng = new com.google.maps.model.LatLng(currentLatitude, currentLongitude);
//                    com.google.maps.model.LatLng destinationLatlng = new com.google.maps.model.LatLng(destinationLatitude, destinationLongitude);
//                    float distanceMatrix = estimateRouteTime(originLatlng, destinationLatlng)/1000;
//                    Log.d(TAG, "Distance from distance matrix"+ distanceMatrix);


                    if(currentPolyline != null){
                        currentPolyline.remove();
                    }
                    path = getDirectionsPathFromWebService(origin, destination);
                    convertedList = convertCoordType(path);
                    fromLatlng = new LatLng(currentLatitude, currentLongitude);
                    toLatlng = new LatLng(destinationLatitude, destinationLongitude);


                    if (path.size() > 0) {
                        opts = new PolylineOptions().addAll(path).color(Color.BLUE).width(20);
                        currentPolyline = mMap.addPolyline(opts);
                        if (path.size() % 2 == 0){
                            LatLng middleLatlng = path.get(path.size() /2);
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(middleLatlng, 13));
                        }
                        else{
                            LatLng middleLatlng = path.get((path.size() - 1) /2);
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(middleLatlng, 13));
                        }
                    }
                }
                else {
                    Toast.makeText(HomeActivity.this, "Bạn phải nhập điểm đi và điểm đến", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int whichIndex = mRideSelectionRadioGroup.getCheckedRadioButtonId();
                mDirectionsBtn.callOnClick();
                if (whichIndex == R.id.offerButton && destinationTextview.getText().toString().trim().length() > 0 && locationTextView.getText().toString().trim().length() > 0) {
                    Intent offerRideActivity = new Intent(HomeActivity.this, OfferRideFragment.class);
                    offerRideActivity.putExtra("LOCATION", destinationTextview.getText().toString());
                    offerRideActivity.putExtra("DESTINATION", locationTextView.getText().toString());
                    offerRideActivity.putExtra("DURATION", duration);
                    offerRideActivity.putExtra("DISTANCE", distance);
                    offerRideActivity.putExtra("listLatLng", convertedList);
                    fromLatlng = new LatLng(currentLatitude, currentLongitude);
                    toLatlng = new LatLng(destinationLatitude, destinationLongitude);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("fromLatlng", fromLatlng);
                    bundle.putParcelable("toLatlng", toLatlng);
//                    bundle.putParcelableArrayList("ListLatlng", path);
                    offerRideActivity.putExtra("bundle",bundle);
                    startActivityForResult(offerRideActivity,1);
                } else if (whichIndex == R.id.findButton && destinationTextview.getText().toString().trim().length() > 0 && locationTextView.getText().toString().trim().length() > 0) {
                    Intent findRideActivity = new Intent(HomeActivity.this, SearchRideActivity.class);
                    findRideActivity.putExtra("LOCATION", locationTextView.getText().toString());
                    findRideActivity.putExtra("DESTINATION", destinationTextview.getText().toString());
                    findRideActivity.putExtra("DURATION", duration);
                    findRideActivity.putExtra("DISTANCE", distance);
                    fromLatlng = new LatLng(currentLatitude, currentLongitude);
                    toLatlng = new LatLng(destinationLatitude, destinationLongitude);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("fromLatlng", fromLatlng);
                    bundle.putParcelable("toLatlng", toLatlng);
                    findRideActivity.putExtra("bundle",bundle);
                    startActivity(findRideActivity);
                } else {
                    //Toast.makeText(HomeActivity.this, "Bạn phải nhập điểm đi và điểm đến", Toast.LENGTH_SHORT).show();
                }
            }
        });



        initImageLoader();
        setupBottomNavigationView();

        boolean firstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("firstRun", true);
        if (firstRun) {
            //Giới thiệu cho user mới
            setupDialog();
            // chuyển firstRun thành false cho lần sau
            getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                    .edit()
                    .putBoolean("firstRun", false)
                    .commit();
        }
        //check services của google
        if (isServicesOk()) {
            getLocationPermission();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                OfferRideCreatedDialog dialog = new OfferRideCreatedDialog(HomeActivity.this);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
            }
        }

    }

    private void updateFirebaseToken() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference("Tokens");

        Token token = new Token(FirebaseInstanceId.getInstance().getToken());
        tokens.child(userID)
                .setValue(token);
    }
    private void checkNotifications() {
        mRef.child("Reminder").child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int reminderLength = 0;
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        reminderLength++;
                    }
                }
                //Passes the number of notifications onto the setup badge method
                setupBadge(reminderLength);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setupBadge(int reminderLength) {
        if (reminderLength > 0) {
            //Adds badge and notification number to the BottomViewNavigation
            BottomNavigationViewHelper.addBadge(HomeActivity.this, bottomNavigationView, reminderLength);
        }
    }
    private void initImageLoader() {
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(HomeActivity.this);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }
    private void setupBottomNavigationView() {
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.enableNavigation(HomeActivity.this, bottomNavigationView);
        //BottomNavigationViewHelper.addBadge(mContext, bottomNavigationView);


        //Change current highlighted icon
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUMBER);
        menuItem.setChecked(true);
    }
    private void setupDialog() {
        WelcomeDialog dialog = new WelcomeDialog(HomeActivity.this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public void getRouteInformation(GeoApiContext mGeoApiContext, String origin, String destination){
        DirectionsApiRequest direction = new DirectionsApiRequest(mGeoApiContext);
        direction.origin(origin);
        direction.mode(TravelMode.DRIVING);
        direction.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                Log.d(TAG, "calculateDirections: duration: " + result.routes[0].legs[0].duration);
                Log.d(TAG, "calculateDirections: distance: " + result.routes[0].legs[0].distance);
                duration = result.routes[0].legs[0].duration.toString();
                distance = result.routes[0].legs[0].distance.toString();
            }

            @Override
            public void onFailure(Throwable e) {
                Log.e(TAG, "calculateDirections: Failed to get directions: " + e.getMessage() );
            }
        });
    }
    public ArrayList<LatLng> getDirectionsPathFromWebService(String origin, String destination) {
        ArrayList<LatLng> path = new ArrayList();
        //Execute Directions API request
        DirectionsApiRequest req = DirectionsApi.getDirections(mGeoApiContext, origin, destination);
        try {
            DirectionsResult res = req.await();

            //Loop through legs and steps to get encoded polylines of each step
            if (res.routes != null && res.routes.length > 0) {
                DirectionsRoute route = res.routes[0];

                if (route.legs !=null) {
                    for(int i=0; i<route.legs.length; i++) {
                        DirectionsLeg leg = route.legs[i];
                        if (leg.steps != null) {
                            for (int j=0; j<leg.steps.length;j++){
                                DirectionsStep step = leg.steps[j];
                                if (step.steps != null && step.steps.length >0) {
                                    for (int k=0; k<step.steps.length;k++){
                                        DirectionsStep step1 = step.steps[k];
                                        EncodedPolyline points1 = step1.polyline;
                                        if (points1 != null) {
                                            //Decode polyline and add points to list of route coordinates
                                            List<com.google.maps.model.LatLng> coords1 = points1.decodePath();
                                            for (com.google.maps.model.LatLng coord1 : coords1) {
                                                path.add(new LatLng(coord1.lat, coord1.lng));
                                            }
                                        }
                                    }
                                } else {
                                    EncodedPolyline points = step.polyline;
                                    if (points != null) {
                                        //Decode polyline and add points to list of route coordinates
                                        List<com.google.maps.model.LatLng> coords = points.decodePath();
                                        for (com.google.maps.model.LatLng coord : coords) {
                                            path.add(new LatLng(coord.lat, coord.lng));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch(Exception ex) {
            Log.e(TAG, ex.getLocalizedMessage());
        }

        return path;
    }


    /**
     * Check Service của Google
     */

    public boolean isServicesOk() {
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(HomeActivity.this);

        if (available == ConnectionResult.SUCCESS) {
            //everything is ok and user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //an error occurred but it can be resolved
            Log.d(TAG, "isServicesOK: an error occurred but it can be fixed");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(HomeActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "Lỗi ở service google api", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called");
        mLocationPermissionsGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                    //initialize our map
                    initMap();
                }
            }
        }
    }

    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: get location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    /**
     * Khởi tạo map
     */
    private void initMap() {
        Log.d(TAG, "initMap: initializing map");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(HomeActivity.this);
        if (mGeoApiContext == null){
            mGeoApiContext = new GeoApiContext.Builder()
                    .apiKey(getString(R.string.google_api_key))
                    .build();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
       // Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;

        if (mLocationPermissionsGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }

        init();
    }

    private void init() {
        Log.d(TAG, "init: initializing");

        locationTextView.setAdapter(new PlaceAutoSuggestAdapter(HomeActivity.this,android.R.layout.simple_list_item_1));
        locationTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN
                        || event.getAction() == KeyEvent.KEYCODE_ENTER) {
                    LatLng latlng = getLatLngFromAddress(locationTextView.getText().toString().trim());
                    Log.d(TAG, "Init :"+ latlng.toString());
                    String address = ""; // fix lỗi Null Exception bằng cách cho biến bằng 1 giá trị để tránh biến = null
                    address = getAddressFromLatLng(new LatLng(latlng.latitude,latlng.longitude)).getAddressLine(0);
                    moveCamera(latlng, DEFAULT_ZOOM, address, true);
                    currentLatitude = latlng.latitude;
                    currentLongitude = latlng.longitude;
                    locationTextView.setText(address);
                    locationTextView.dismissDropDown();
                }
                return false;
            }
        });

        locationTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Address : ",locationTextView.getText().toString());
                LatLng latLng = getLatLngFromAddress(locationTextView.getText().toString());
                if(latLng!=null) {
                    Log.d("Lat Lng : ", " " + latLng.latitude + " " + latLng.longitude);
                    Address address=getAddressFromLatLng(latLng);
                    if(address!=null) {
                        Log.d("Address : ", "" + address.toString());
                        Log.d("Address Line : ",""+address.getAddressLine(0));
                        Log.d("Phone : ",""+address.getPhone());
                        Log.d("Pin Code : ",""+address.getPostalCode());
                        Log.d("Feature : ",""+address.getFeatureName());
                        Log.d("More : ",""+address.getLocality());
                        currentLatitude = latLng.latitude;
                        currentLongitude = latLng.longitude;
                        moveCamera(latLng, DEFAULT_ZOOM, address.getAddressLine(0),true);
                    }
                    else {
                        Log.d("Address","Address Not Found");
                    }
                }
                else {
                    Log.d("Lat Lng","Lat Lng Not Found");
                }

            }
        });


        destinationTextview.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN
                        || event.getAction() == KeyEvent.KEYCODE_ENTER) {
                    LatLng latlng = getLatLngFromAddress(destinationTextview.getText().toString().trim());
                    destinationLatitude = latlng.latitude;
                    destinationLongitude = latlng.longitude;
                    String address = ""; // fix lỗi Null Exception bằng cách cho biến bằng 1 giá trị để tránh biến = null
                    address = getAddressFromLatLng(new LatLng(latlng.latitude,latlng.longitude)).getAddressLine(0);
                    moveCamera(latlng, DEFAULT_ZOOM, address, false);
                    destinationTextview.setText(address);
                    destinationTextview.dismissDropDown();
                }
                return false;
            }
        });

        destinationTextview.setAdapter(new PlaceAutoSuggestAdapter(HomeActivity.this,android.R.layout.simple_list_item_1));

        destinationTextview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Address : ",destinationTextview.getText().toString());
                LatLng latLng = getLatLngFromAddress(destinationTextview.getText().toString());
                if(latLng!=null) {
                    Log.d("Lat Lng : ", " " + latLng.latitude + " " + latLng.longitude);
                    Address address=getAddressFromLatLng(latLng);
                    if(address!=null) {
                        Log.d("Address : ", "" + address.toString());
                        Log.d("Address Line : ",""+address.getAddressLine(0));
                        Log.d("Phone : ",""+address.getPhone());
                        Log.d("Pin Code : ",""+address.getPostalCode());
                        Log.d("Feature : ",""+address.getFeatureName());
                        Log.d("More : ",""+address.getLocality());
                        destinationLatitude = latLng.latitude;
                        destinationLongitude = latLng.longitude;
                        moveCamera(latLng, DEFAULT_ZOOM, address.getAddressLine(0),false);
                    }
                    else {
                        Log.d("Adddress","Address Not Found");
                    }
                }
                else {
                    Log.d("Lat Lng","Lat Lng Not Found");
                }

            }
        });
    }

    static ArrayList<com.thang.jointjourney.models.LatLng> convertCoordType(ArrayList<LatLng> list) {
        ArrayList<com.thang.jointjourney.models.LatLng> resultList = new ArrayList<>();
        for (com.google.android.gms.maps.model.LatLng item : list) {
            resultList.add(new com.thang.jointjourney.models.LatLng(item.latitude, item.longitude));
        }
        return resultList;
    }


    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (mLocationPermissionsGranted) {
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            Log.d(TAG, "onComplete getDeviceLocation: getting found location!");
                            currentLocation = (Location) task.getResult();
                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                    DEFAULT_ZOOM,
                                    "Vị trí của tôi", true);
                            currentLatitude = currentLocation.getLatitude();
                            currentLongitude = currentLocation.getLongitude();
                        } else {
                            Log.d(TAG, "onComplete getDeviceLocation: current location is null");
                            Toast.makeText(HomeActivity.this, "Không thể tìm vị trí của bạn", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }
    private void getDeviceLocationAndAddMarker() {
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (mLocationPermissionsGranted) {
                final Task location = mFusedLocationProviderClient.getLastLocation();
                if (location != null){
                    location.addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                Log.d(TAG, "onComplete getDeviceLocation: getting found location!");
                                currentLocation = (Location) task.getResult();
                                Log.d(TAG, "currentLocation :" + currentLocation.toString());
                                moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                        DEFAULT_ZOOM,
                                        "Vị trí của tôi", true);
                                currentLatitude = currentLocation.getLatitude();
                                currentLongitude = currentLocation.getLongitude();
                                String address = ""; // fix lỗi Null Exception bằng cách cho biến bằng 1 giá trị để tránh biến = null
                                address = getAddressFromLatLng(new LatLng(currentLatitude,currentLongitude)).getAddressLine(0);
                                locationTextView.setText(address);
                                locationTextView.dismissDropDown();
                            } else {
                                Log.d(TAG, "onComplete getDeviceLocation: current location is null");
                                Toast.makeText(HomeActivity.this, "Không thể tìm vị trí của bạn", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(this, "Đợi một chút để load location ", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }



    private void moveCamera(LatLng latLng, float zoom, String title, boolean isFromMarker) {
        Log.d(TAG, "moveCamera: moving the camera to: lat:" + latLng.latitude + ", lng: " + latLng.longitude);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        if (!title.equals("Vị trí của tôi")) {
            if(isFromMarker){
                if(fromMarker != null){
                    fromMarker.remove();
                }
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(latLng)
                        .title(title);

                fromMarker = mMap.addMarker(markerOptions);
            }
            else{
                if(toMarker !=null){
                    toMarker.remove();
                }
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(latLng)
                        .title(title);

                toMarker = mMap.addMarker(markerOptions);
            }
        }

        hideKeyboard(HomeActivity.this);
    }

    /**
     * Ẩn bàn phím sau khi click
     */
    public static void hideKeyboard(Activity activity) {
        Log.d(TAG, "hideKeyboard: hide keyboard after clicking");
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);

        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }

        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    /**
     * Chuyển đổi giữa address về longtitude/latitude và ngược lại
     */
    private LatLng getLatLngFromAddress(String addressString){
        Log.d(TAG, "getLatLngFromAddress: processing");

        Geocoder geocoder = new Geocoder(HomeActivity.this);
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(addressString, 1);
            if(list.size() > 0){
                Address address = list.get(0);
                LatLng latLng = new LatLng(address.getLatitude(),address.getLongitude());
                Log.d(TAG, "getLatLngFromAddress: found a location: " + address.toString());
                //Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();
//                moveCamera(latLng, DEFAULT_ZOOM, address.getAddressLine(0));
                return latLng;
            }
            else {
                return null;
            }
        }catch (IOException e){
            Log.e(TAG, "geoLocate: IOException: " + e.getMessage() );
            return null;
        }
    }

    private Address getAddressFromLatLng(LatLng latLng){
        Geocoder geocoder=new Geocoder(HomeActivity.this);
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 5);
            if(addresses!=null){
                Address address=addresses.get(0);
                return address;
            }
            else{
                return null;
            }
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }
    /** --------------------------- Firebase ---------------------------- **/

    /***
     *  Setup the firebase object
     */
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        // userID = currentUser.getUid();
        checkCurrentUser(currentUser);
    }

    /**
     * Cheks to see if the @param 'user' is logged in
     *
     * @param user
     */
    private void checkCurrentUser(FirebaseUser user) {
        Log.d(TAG, "checkCurrentUser: checking if user if logged in");

        if (user == null) {
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null) {
            currentPolyline.remove();
            currentPolyline = mMap.addPolyline((PolylineOptions) values[1]);
        }
    }

    public void getUserInformation(String uid) {
        mRef.child("user").child(uid).child("carOwner").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                carOwner = dataSnapshot.getValue(Boolean.class);
                if (carOwner == false) {
                    offerButton.setEnabled(false);
                    offerButton.setAlpha(.5f);
                    offerButton.setClickable(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

}