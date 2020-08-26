package com.thang.jointjourney.Home;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.DistanceMatrixApi;
import com.google.maps.DistanceMatrixApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;
import com.nostra13.universalimageloader.utils.L;
import com.thang.jointjourney.Dialogs.BookRideDialog;
import com.thang.jointjourney.Dialogs.OfferRideCreatedDialog;
import com.thang.jointjourney.Pickup.DropLocationActivity;
import com.thang.jointjourney.Pickup.PickupLocationActivity;
import com.thang.jointjourney.R;
import com.thang.jointjourney.Utils.RidesMethod;
import com.thang.jointjourney.Utils.ViewWeightAnimationWrapper;

import org.joda.time.DateTime;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import static com.thang.jointjourney.Constant.MAPVIEW_BUNDLE_KEY;
import static com.thang.jointjourney.Constant.MAP_LAYOUT_STATE_CONTRACTED;
import static com.thang.jointjourney.Constant.MAP_LAYOUT_STATE_EXPANDED;
import static com.thang.jointjourney.Constant.RESULT_DROP;
import static com.thang.jointjourney.Constant.RESULT_PICKUP;

public class SearchDetailResults extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {
    private static final String TAG = "SearchDetailResults";


    //widgets
    private MapView mMapView;
    private RelativeLayout mMapContainer;
    private RelativeLayout mCustomerRide;
    private FloatingActionButton mPickup, mDrop;
    private TextView mPickupTextView, mDropTextView, mJourneyTextView;
    private Button mNext;

    //vars
    private int mMapLayoutState = 0;
    private GeoApiContext mGeoApiContext;
    private GoogleMap mGoogleMap;
    private com.google.android.gms.maps.model.LatLng fromCustomer, toCustomer;
    private com.thang.jointjourney.models.LatLng fromDriver, toDriver;
    private com.thang.jointjourney.models.LatLng pickUpLocation;
    private com.thang.jointjourney.models.LatLng dropLocation;
    private Context mContext = SearchDetailResults.this;
    private String strPickup,strDrop,strFromDriver, strFromCustomer,strToDriver, strToCustomer;
    private String rideID, username, licencePlate, rides, finalSeats, fromOnly, toOnly, date, dateOnly, cost, extraTime,
            profile_photo, completedRides, distanceRide, duration, userID, goTime;
    float rating, distanceCustomer;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_detail_results);
        Log.d(TAG, "SearchDetailResults:On Create");

        findViewById(R.id.btn_full_screen_map).setOnClickListener(this);
        mMapView = (MapView) findViewById(R.id.map);
        mMapContainer = (RelativeLayout) findViewById(R.id.map_container);
        mCustomerRide = (RelativeLayout)findViewById(R.id.customer_ride_layout);
        mPickupTextView =(TextView) findViewById(R.id.pickUpTextView);
        mDropTextView = (TextView) findViewById(R.id.dropTextView);
        mJourneyTextView = (TextView) findViewById(R.id.journeyTextView);
        mNext = (Button) findViewById(R.id.btnNext);

        mPickup = (FloatingActionButton)findViewById(R.id.btn_pickup);
        mDrop = (FloatingActionButton) findViewById(R.id.btn_drop);




        initGoogleMap(savedInstanceState);
        getActivityData();

        distanceCustomer = getDistance2(mGeoApiContext, pickUpLocation.getLatitude(), pickUpLocation.getLongitude(), dropLocation.getLatitude(), dropLocation.getLongitude());

        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BookRideDialog dialog = new BookRideDialog(mContext, rideID ,username, licencePlate, rides,goTime,  finalSeats, fromOnly, toOnly, date, dateOnly,
                        cost, rating, extraTime, duration, userID, profile_photo, completedRides, strPickup, strDrop, distanceCustomer);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

            }
        });

        mPickup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchDetailResults.this, PickupLocationActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("pickup", convertLatlng1(pickUpLocation));
                bundle.putParcelable("drop", convertLatlng1(dropLocation));
                bundle.putParcelable("fromDriver", convertLatlng1(fromDriver));
                bundle.putParcelable("toDriver", convertLatlng1(toDriver));
                bundle.putParcelable("toCustomer", toCustomer);
                bundle.putParcelable("fromCustomer", fromCustomer);

                intent.putExtra("bundle", bundle);
                startActivityForResult(intent, 1);
            }
        });

        mDrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchDetailResults.this, DropLocationActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("pickup", convertLatlng1(pickUpLocation));
                bundle.putParcelable("drop", convertLatlng1(dropLocation));
                bundle.putParcelable("fromDriver", convertLatlng1(fromDriver));
                bundle.putParcelable("toDriver", convertLatlng1(toDriver));
                bundle.putParcelable("toCustomer", toCustomer);
                bundle.putParcelable("fromCustomer", fromCustomer);

                intent.putExtra("bundle", bundle);
                startActivityForResult(intent, 1);
            }
        });




    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_PICKUP){
                com.google.android.gms.maps.model.LatLng newPickup = data.getParcelableExtra("newPickup");
                Log.d(TAG, "pickUpLocation old : " + pickUpLocation);
                pickUpLocation = convertLatlng2(newPickup);
                Log.d(TAG, "pickUpLocation new : " + pickUpLocation);
                mGoogleMap.clear();
                onMapReady(mGoogleMap);
            }
            if (resultCode == RESULT_DROP) {
                com.google.android.gms.maps.model.LatLng newPickup = data.getParcelableExtra("newDrop");
                Log.d(TAG, "dropLocation old : " + dropLocation);
                dropLocation = convertLatlng2(newPickup);
                Log.d(TAG, "dropLocation new : " + dropLocation);
                mGoogleMap.clear();
                onMapReady(mGoogleMap);
            }
        }

    }

    private void getActivityData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getParcelableExtra("bundle");
        if (bundle != null) {
            fromCustomer = bundle.getParcelable("FROMCUSTOMER");
            toCustomer = bundle.getParcelable("TOCUSTOMER");
            fromDriver = (com.thang.jointjourney.models.LatLng) intent.getSerializableExtra("fromDriver");
            toDriver = (com.thang.jointjourney.models.LatLng) intent.getSerializableExtra("toDriver");
            pickUpLocation = (com.thang.jointjourney.models.LatLng) intent.getSerializableExtra("pickUp");
            dropLocation = (com.thang.jointjourney.models.LatLng) intent.getSerializableExtra("drop");


            rideID = intent.getStringExtra("rideID");
            username = intent.getStringExtra("username");
            licencePlate = intent.getStringExtra("licencePlate");
            rides = intent.getStringExtra("rides");
            finalSeats = intent.getStringExtra("finalSeats");
            fromOnly = intent.getStringExtra("fromOnly");
            toOnly = intent.getStringExtra("toOnly");
            date = intent.getStringExtra("date");
            dateOnly = intent.getStringExtra("dateOnly");
            cost = intent.getStringExtra("cost");
            goTime = intent.getStringExtra("goTime");

            rating = intent.getFloatExtra("rating",0);
            extraTime = intent.getStringExtra("extraTime");
            profile_photo = intent.getStringExtra("profile_photo");
            completedRides = intent.getStringExtra("completedRides");
            distanceRide = intent.getStringExtra("distance");
            duration = intent.getStringExtra("duration");
            userID = intent.getStringExtra("userID");


        }

    }

    private void initGoogleMap(Bundle savedInstanceState) {
        // *** IMPORTANT ***
        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
        // objects or sub-Bundles.
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        if (mGeoApiContext == null) {
            mGeoApiContext = new GeoApiContext.Builder()
                    .apiKey(getString(R.string.google_api_key))
                    .build();
        }

        mMapView.onCreate(mapViewBundle);

        mMapView.getMapAsync(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mMapView.onSaveInstanceState(mapViewBundle);
    }


    @Override
    public void onMapReady(GoogleMap map) {
        mGoogleMap = map;
        addDriverPolilyne();
        addCustomerPolilyne();
        addMarker();



        strPickup = getAddressFromLatLng(convertLatlng1(pickUpLocation)).getAddressLine(0);
        strDrop = getAddressFromLatLng(convertLatlng1(dropLocation)).getAddressLine(0);
        strToCustomer = getAddressFromLatLng(toCustomer).getAddressLine(0);

        mPickupTextView.setText("Đi đến " + strPickup);
        mJourneyTextView.setText("Đi đến " + strDrop);
        mDropTextView.setText("Xuống xe và đi đến " + strToCustomer);
    }


    private void addMarker() {
        //Customer Marker
        MarkerOptions fromCustomerMarker = new MarkerOptions().position(fromCustomer).title("Điểm đi của bạn");
        MarkerOptions toCustomerMarker = new MarkerOptions().position(toCustomer).title("Điểm đến của bạn");
        mGoogleMap.addMarker(fromCustomerMarker);
        mGoogleMap.addMarker(toCustomerMarker);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(fromCustomer, 13));
        //PickUp and Drop
        MarkerOptions pickupMarker = new MarkerOptions().position(convertLatlng1(pickUpLocation)).title("Điểm đón của bạn");
        MarkerOptions dropMarker = new MarkerOptions().position(convertLatlng1(dropLocation)).title("Điểm xuống của bạn");
        mGoogleMap.addMarker(pickupMarker);
        mGoogleMap.addMarker(dropMarker);

        //Driver Marker
        MarkerOptions fromDriverMarker = new MarkerOptions().position(convertLatlng1(fromDriver)).title("Điểm đi của chuyến đi");
        mGoogleMap.addMarker(fromDriverMarker);
        MarkerOptions toDriverMarker = new MarkerOptions().position(convertLatlng1(toDriver)).title("Điểm đến của chuyến đi");
        mGoogleMap.addMarker(toDriverMarker);
    }


    private void addCustomerPolilyne() {
        calculateDirections(fromCustomer, convertLatlng1(pickUpLocation), false);
        calculateDirections(toCustomer, convertLatlng1(dropLocation), false);

    }

    private void addDriverPolilyne() {
        calculateDirections(convertLatlng1(fromDriver), convertLatlng1(toDriver), true);

    }

    /**
     *
     * get direction and add polilyne
     */
    public float getDistance2(GeoApiContext mGeoApiContext, double lat1, double lon1, double lat2, double lon2) {
        try {
            DistanceMatrixApiRequest req = DistanceMatrixApi.newRequest(mGeoApiContext);
            DistanceMatrix trix = req.origins(new com.google.maps.model.LatLng(lat1, lon1))
                    .destinations(new com.google.maps.model.LatLng(lat2, lon2))
                    .mode(TravelMode.DRIVING)
                    .language("vi")
                    .await();
            float distance = trix.rows[0].elements[0].distance.inMeters;
            return distance;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return -1;
    }
    private void calculateDirections(com.google.android.gms.maps.model.LatLng from, com.google.android.gms.maps.model.LatLng to, final boolean isDriverPolilyne){
        Log.d(TAG, "calculateDirections: calculating directions.");
        final boolean driverPolilyne = isDriverPolilyne;

        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
               to.latitude, to.longitude
        );
        DirectionsApiRequest directions = new DirectionsApiRequest(mGeoApiContext);

        directions.origin(
                new com.google.maps.model.LatLng(
                        from.latitude, from.longitude
                )
        );
        Log.d(TAG, "calculateDirections: destination: " + destination.toString());
        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                Log.d(TAG, "onResult: successfully retrieved directions.");
                if (driverPolilyne){
                    addDriverPolilyneToMap(result);
                }
                else {
                    addCustomerPolilyneToMap(result);
                }
            }
            @Override
            public void onFailure(Throwable e) {
                Log.e(TAG, "calculateDirections: Failed to get directions: " + e.getMessage() );

            }
        });
    }



    private void addCustomerPolilyneToMap(final DirectionsResult result){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: result routes: " + result.routes.length);

                for(DirectionsRoute route: result.routes){
                    Log.d(TAG, "run: leg: " + route.legs[0].toString());
                    List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());

                    List<com.google.android.gms.maps.model.LatLng> newDecodedPath = new ArrayList<>();

                    // This loops through all the LatLng coordinates of ONE polyline.
                    for(com.google.maps.model.LatLng latLng: decodedPath){

//                        Log.d(TAG, "run: latlng: " + latLng.toString());

                        newDecodedPath.add(new com.google.android.gms.maps.model.LatLng(
                                latLng.lat,
                                latLng.lng
                        ));
                    }
                    Polyline polyline = mGoogleMap.addPolyline(new PolylineOptions().addAll(newDecodedPath).width(20));
                    polyline.setColor(ContextCompat.getColor(SearchDetailResults.this, R.color.grey));
                    polyline.setClickable(true);

                }
            }
        });
    }

    private void addDriverPolilyneToMap(final DirectionsResult result){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: result routes: " + result.routes.length);

                for(DirectionsRoute route: result.routes){
                    Log.d(TAG, "run: leg: " + route.legs[0].toString());
                    List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());

                    List<com.google.android.gms.maps.model.LatLng> newDecodedPath = new ArrayList<>();

                    // This loops through all the LatLng coordinates of ONE polyline.
                    for(com.google.maps.model.LatLng latLng: decodedPath){

//                        Log.d(TAG, "run: latlng: " + latLng.toString());

                        newDecodedPath.add(new com.google.android.gms.maps.model.LatLng(
                                latLng.lat,
                                latLng.lng
                        ));
                    }
                    Polyline polyline = mGoogleMap.addPolyline(new PolylineOptions().addAll(newDecodedPath).width(20));
                    polyline.setColor(ContextCompat.getColor(SearchDetailResults.this, R.color.blue));
                    polyline.setClickable(true);

                }
            }
        });
    }

    public com.google.android.gms.maps.model.LatLng convertLatlng1(com.thang.jointjourney.models.LatLng latLng){
        com.google.android.gms.maps.model.LatLng result = new com.google.android.gms.maps.model.LatLng(latLng.getLatitude(), latLng.getLongitude());
        return result;
    }
    public com.thang.jointjourney.models.LatLng convertLatlng2(com.google.android.gms.maps.model.LatLng latLng){
        com.thang.jointjourney.models.LatLng result = new com.thang.jointjourney.models.LatLng(latLng.latitude, latLng.longitude);
        return result;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }
    public Address getAddressFromLatLng(com.google.android.gms.maps.model.LatLng latLng){
        Geocoder geocoder=new Geocoder(SearchDetailResults.this);
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 5);
            if(addresses!=null){
                return addresses.get(0);
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
    /**
     * Add expand and contract in mapView
     */
    private void expandMapAnimation(){
        ViewWeightAnimationWrapper mapAnimationWrapper = new ViewWeightAnimationWrapper(mMapContainer);
        ObjectAnimator mapAnimation = ObjectAnimator.ofFloat(mapAnimationWrapper,
                "weight",
                70,
                100);
        mapAnimation.setDuration(1000);

        ViewWeightAnimationWrapper recyclerAnimationWrapper = new ViewWeightAnimationWrapper(mCustomerRide);
        ObjectAnimator recyclerAnimation = ObjectAnimator.ofFloat(recyclerAnimationWrapper,
                "weight",
                30,
                0);
        recyclerAnimation.setDuration(1000);

        recyclerAnimation.start();
        mapAnimation.start();
    }

    private void contractMapAnimation(){
        ViewWeightAnimationWrapper mapAnimationWrapper = new ViewWeightAnimationWrapper(mMapContainer);
        ObjectAnimator mapAnimation = ObjectAnimator.ofFloat(mapAnimationWrapper,
                "weight",
                100,
                70);
        mapAnimation.setDuration(1000);

        ViewWeightAnimationWrapper recyclerAnimationWrapper = new ViewWeightAnimationWrapper(mCustomerRide);
        ObjectAnimator recyclerAnimation = ObjectAnimator.ofFloat(recyclerAnimationWrapper,
                "weight",
                0,
                30);
        recyclerAnimation.setDuration(1000);

        recyclerAnimation.start();
        mapAnimation.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_full_screen_map: {

                if (mMapLayoutState == MAP_LAYOUT_STATE_CONTRACTED) {
                    mMapLayoutState = MAP_LAYOUT_STATE_EXPANDED;
                    expandMapAnimation();
                } else if (mMapLayoutState == MAP_LAYOUT_STATE_EXPANDED) {
                    mMapLayoutState = MAP_LAYOUT_STATE_CONTRACTED;
                    contractMapAnimation();
                }
                break;
            }
        }
    }
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        finish();
    }
}