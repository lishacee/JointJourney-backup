package com.thang.jointjourney.Pickup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.android.PolyUtil;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.thang.jointjourney.R;

import java.util.ArrayList;
import java.util.List;

import static com.thang.jointjourney.Constant.RESULT_DROP;
import static com.thang.jointjourney.Constant.RESULT_PICKUP;

public class DropLocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "DropLocationActivity";
    private Context mContext = DropLocationActivity.this;

    //Google map variables
    private GoogleMap mMap;
    private LatLng fromLatlng;
    private LatLngBounds bounds;
    private LatLngBounds.Builder builder;
    private LatLng pickupLocation, dropLocation, fromCustomer, toCustomer, fromDriver, toDriver, newDrop;
    private Polyline driverRoutePolyline, previewPolyline;
    private List<Polyline> customerPolyline;

    private Geocoder geocoder;
    private List<Address> addresses;
    private GeoApiContext mGeoApiContext;

    private String streetName;
    private static final float WIDTH = 4;
    private static final double TOLERANCE_IN_METERS = 3.0;
    private boolean isDropValid = false;


    //View viarables
    private Button mConfirm;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: started.");
        setContentView(R.layout.activity_pickup_location);

        customerPolyline = new ArrayList<Polyline>();

        getActivityData();
        setupWidgets();
        initMap();

        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isDropValid){
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("newDrop", newDrop);
                    setResult(RESULT_DROP, returnIntent);
                    finish();
                }
                else {
                    Toast.makeText(mContext, "Điểm xuống phải trên đường đi", Toast.LENGTH_SHORT).show();
                }

//                // Truyền data vào intent
//                data.putExtra(EXTRA_DATA, streetName);
//
//                // Đặt resultCode là Activity.RESULT_OK to
//                // thể hiện đã thành công và có chứa kết quả trả về
//                setResult(Activity.RESULT_OK, data);
//                finish();
            }
        });

    }

    private void setupWidgets() {
        mConfirm = (Button) findViewById(R.id.confirmLocationBtn);
        helpDialog();

    }


    private void getActivityData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bundle");
        if(bundle != null){
            pickupLocation = bundle.getParcelable("pickup");
            dropLocation = bundle.getParcelable("drop");
            fromDriver = bundle.getParcelable("fromDriver");
            toDriver = bundle.getParcelable("toDriver");
            fromCustomer = bundle.getParcelable("fromCustomer");
            toCustomer = bundle.getParcelable("toCustomer");
        }
    }

    private void helpDialog(){

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_drop_help);
        dialog.setCanceledOnTouchOutside(true);

        View masterView = dialog.findViewById(R.id.coach_mark_master_view);
        masterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    /**
     * sets up map from the view
     */
    private void initMap() {
        Log.d(TAG, "initMap: initializing map");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.pickupLocationMap);
        mapFragment.getMapAsync(DropLocationActivity.this);

        if (mGeoApiContext == null) {
            mGeoApiContext = new GeoApiContext.Builder()
                    .apiKey(getString(R.string.google_api_key))
                    .build();
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;

        addDriverPolilyne();
        addCustomerPolilyne();
        addMarker();

        int height = 120;
        int width = 120;
        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.marker_pickup_location);
        Bitmap b=bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

        mMap.addMarker(new MarkerOptions().position(dropLocation).title("Pickup location").draggable(true).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(dropLocation, 15));

        builder = new LatLngBounds.Builder();
        builder.include(dropLocation);
        bounds = builder.build();


        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {
                if(PolyUtil.isLocationOnEdge(marker.getPosition(), driverRoutePolyline.getPoints(), true, TOLERANCE_IN_METERS)) {
                    driverRoutePolyline.setWidth(WIDTH * 5);
                } else {
                    driverRoutePolyline.setWidth(WIDTH);
                }
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                if(PolyUtil.isLocationOnEdge(marker.getPosition(), driverRoutePolyline.getPoints(), true, TOLERANCE_IN_METERS)){
                    isDropValid = true;
                    newDrop = marker.getPosition();
                    if(previewPolyline != null){
                        previewPolyline.remove();
                    }
                    calculateDirections(toCustomer, marker.getPosition(), "preview");
                }
                else {
                    isDropValid = false;
                    Toast.makeText(mContext, "Điểm đón phải nằm trên chuyến đi", Toast.LENGTH_SHORT).show();
                }

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), mMap.getCameraPosition().zoom));
            }
        });
    }


    private String getAddressFromLatLng(LatLng latLng){
        Geocoder geocoder=new Geocoder(DropLocationActivity.this);
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 5);
            if(addresses!=null){
                String street = addresses.get(0).getAddressLine(0);
                return street;
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
    @Override
    public void onBackPressed() {

        setResult(Activity.RESULT_CANCELED);
        super.onBackPressed();
    }
    private void addMarker() {
        //Customer Marker
        MarkerOptions fromCustomerMarker = new MarkerOptions().position(fromCustomer).title("Điểm đi của bạn");
        MarkerOptions toCustomerMarker = new MarkerOptions().position(toCustomer).title("Điểm đến của bạn");
        mMap.addMarker(fromCustomerMarker);
        mMap.addMarker(toCustomerMarker);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(fromCustomer, 13));
        //PickUp and Drop
        MarkerOptions pickupMarker = new MarkerOptions().position(pickupLocation).title("Điểm đón của bạn");
        mMap.addMarker(pickupMarker);

        //Driver Marker
        MarkerOptions fromDriverMarker = new MarkerOptions().position(fromDriver).title("Điểm đi của chuyến đi");
        mMap.addMarker(fromDriverMarker);
        MarkerOptions toDriverMarker = new MarkerOptions().position(toDriver).title("Điểm đến của chuyến đi");
        mMap.addMarker(toDriverMarker);
    }


    private void addCustomerPolilyne() {
        calculateDirections(fromCustomer, pickupLocation, "customer");
        calculateDirections(toCustomer, dropLocation, "customer");

    }

    private void addDriverPolilyne() {
        calculateDirections(fromDriver, toDriver, "driver");

    }

    private void calculateDirections(com.google.android.gms.maps.model.LatLng from, com.google.android.gms.maps.model.LatLng to, final String polylineType){
        Log.d(TAG, "calculateDirections: calculating directions.");
        final String type = polylineType;

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
                float distance = result.routes[0].legs[0].distance.inMeters;
                Log.d(TAG, "onResult: successfully retrieved directions.");
                if (type == "driver"){
                    addDriverPolylineToMap(result);
                }
                else if(type == "customer"){
                    addCustomerPolylineToMap(result);
                }else{
                    addPreviewPolylineToMap(result);
                }
            }
            @Override
            public void onFailure(Throwable e) {
                Log.e(TAG, "calculateDirections: Failed to get directions: " + e.getMessage() );

            }
        });
    }
    private void addDriverPolylineToMap(final DirectionsResult result){
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
                    driverRoutePolyline = mMap.addPolyline(new PolylineOptions().addAll(newDecodedPath).width(WIDTH));
                    driverRoutePolyline.setColor(ContextCompat.getColor(DropLocationActivity.this, R.color.blue));

                }
            }
        });
    }

    private void addCustomerPolylineToMap(final DirectionsResult result){
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
                    Polyline polyline = mMap.addPolyline(new PolylineOptions().addAll(newDecodedPath).width(20));
                    polyline.setColor(ContextCompat.getColor(DropLocationActivity.this, R.color.grey));
                    customerPolyline.add(polyline);
                }
            }
        });
    }
    private void addPreviewPolylineToMap(final DirectionsResult result){
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
                    previewPolyline = mMap.addPolyline(new PolylineOptions().addAll(newDecodedPath).width(20));
                    previewPolyline.setColor(ContextCompat.getColor(DropLocationActivity.this, R.color.green));
                }
            }
        });
    }
}