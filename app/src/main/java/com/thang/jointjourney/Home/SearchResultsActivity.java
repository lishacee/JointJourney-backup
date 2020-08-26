package com.thang.jointjourney.Home;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;
import com.thang.jointjourney.Adapter.SearchAdapter;
import com.thang.jointjourney.R;
import com.thang.jointjourney.Utils.RidesMethod;
import com.thang.jointjourney.models.Ride;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class SearchResultsActivity extends AppCompatActivity {

    private static final String TAG = "SearchResultsActivity";

    //Recycle View variables
    private Context mContext = SearchResultsActivity.this;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mRecycleAdapter;
    private SearchAdapter myAdapter;
    private ArrayList<Ride> rides;
    private ArrayList<com.thang.jointjourney.models.LatLng> listPickup, listDrop;
    private ArrayList<com.thang.jointjourney.models.LatLng> listLatlng;
    private LatLng fromLatLng, toLatLng;
    private int radius = 1;
    private boolean isPickupFound = false;
    private String pickupId = "";


    //Firebase variables
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabse;
    private DatabaseReference mRef;


    //Variables
    private String user_id, location, destination, date, genderCurrentUser, genderDriverUser, user_id_driver;
    private RelativeLayout mNoResultsFoundLayout;
    private RelativeLayout loadingLayout;

    private GeoApiContext mGeoApiContext = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: started.");
        setContentView(R.layout.fragment_search_results);
        if (mGeoApiContext == null){
            mGeoApiContext = new GeoApiContext.Builder()
                    .apiKey(getString(R.string.google_api_key))
                    .build();
        }

        getActivityData();




        //Setup firebase object
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabse = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabse.getReference();

        if (mAuth.getCurrentUser() != null) {
            user_id = mAuth.getCurrentUser().getUid();
        }

        //setup widgets
        mNoResultsFoundLayout = (RelativeLayout) findViewById(R.id.noResultsFoundLayout);
        loadingLayout = (RelativeLayout) findViewById(R.id.loadingLayout);


        //Setup recycler view
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mRecycleAdapter);
        rides = new ArrayList<Ride>();
        listPickup = new ArrayList<>();
        listDrop = new ArrayList<>();

        mRef.child("availableRide").orderByChild("dateOfJourney").limitToFirst(10)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                                Ride r = dataSnapshot1.getValue(Ride.class);
                                if (r.getSeatsAvailable() > 0) {
                                    Date aParsed = null;
                                    Date bParsed = null;
                                    try {
                                        aParsed = parseDate(r.getDateOfJourney());
                                        Log.d(TAG, "ride date = " + aParsed);
                                        bParsed = parseDate(date);
                                        Log.d(TAG, "customer date = " + bParsed);
                                        Log.i(TAG, "onDataChange: " + aParsed +" " + bParsed);
                                        if (aParsed.after(bParsed) || aParsed.equals(bParsed)) {
                                            if (!(r.getUser_id().contains(user_id))) {
                                                com.thang.jointjourney.models.LatLng pickUpLocation;
                                                com.thang.jointjourney.models.LatLng dropLocation;
                                                com.thang.jointjourney.models.LatLng head = r.getFromLatlng() ;
                                                com.thang.jointjourney.models.LatLng tail = r.getToLatlng();
                                                RidesMethod rideMethod = new RidesMethod(SearchResultsActivity.this, mGeoApiContext);
                                                pickUpLocation = rideMethod.isLocationValid(rideMethod.possibleLocation(fromLatLng, rideMethod.convertLatlng1(head), rideMethod.convertLatlng1(tail), r.getListLatlng()), fromLatLng, 1);
                                                dropLocation = rideMethod.isLocationValid(rideMethod.possibleLocation(toLatLng, rideMethod.convertLatlng1(head), rideMethod.convertLatlng1(tail), r.getListLatlng()), toLatLng, 2);
                                                if(pickUpLocation!=null && dropLocation!= null && !rideMethod.isWrongWay(pickUpLocation, dropLocation, rideMethod.convertLatlng1(head))) {

                                                    rides.add(r);
                                                    listPickup.add(pickUpLocation);
                                                    listDrop.add(dropLocation);
                                                }
                                            }
                                        }
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                        Log.d(TAG, "AfterSearching: khong co chuyen di");
                                    }

                                }
                            }
                            if (rides.size() == 0){
                                loadingLayout.setVisibility(View.INVISIBLE);
                                mNoResultsFoundLayout.setVisibility(View.VISIBLE);
                            }
                            loadingLayout.setVisibility(View.INVISIBLE);
                            myAdapter = new SearchAdapter(SearchResultsActivity.this, rides, listPickup, listDrop, fromLatLng, toLatLng);
                            mRecyclerView.setAdapter(myAdapter);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(SearchResultsActivity.this, "Không thể lấy thông tin từ database", Toast.LENGTH_SHORT).show();
                    }
                });

        //Setup back arrow for navigating back to 'ProfileActivity'
        ImageView backArrow = (ImageView) findViewById(R.id.backArrowSearchRide);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        ImageView exitSearchRide = (ImageView) findViewById(R.id.exitSearchRide);
        exitSearchRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void getActivityData() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            location = getIntent().getStringExtra("DESTINATION");
            destination = getIntent().getStringExtra("LOCATION");
            date = getIntent().getStringExtra("DATE");
            fromLatLng = getIntent().getParcelableExtra("FromLatLng");
            toLatLng = getIntent().getParcelableExtra("ToLatLng");
            Log.d(TAG, "SearchResults: GetActivityData : from Latlng = "+ fromLatLng);
            Log.d(TAG, "SearchResults: GetActivityData : to Latlng = "+ toLatLng);


        }
    }

    private Date parseDate(String date) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        try {
            return formatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formatter.parse(date);
    }
    /***
     *  Filter the Rides of Customer's searches, get FromLatLng and ToLatLng which is close (<1 km) to the listLatLng of rides return true
     *
     */








    /***
     *  Setup the firebase object
     */
    @Override
    public void onStart() {
        super.onStart();
    }


}
