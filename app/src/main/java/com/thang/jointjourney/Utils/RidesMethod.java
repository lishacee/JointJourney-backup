package com.thang.jointjourney.Utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.DistanceMatrixApi;
import com.google.maps.DistanceMatrixApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.TravelMode;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RidesMethod {
    private static final String TAG = "RidesMethod";
    private FirebaseAuth mAuth;
    private Context mContext;
    private String userID;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private GeoApiContext mGeoApi;


    public RidesMethod(Context context, GeoApiContext mGeoApiContext) {
        mContext = context;
        this.mGeoApi = mGeoApiContext;
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        if (mAuth.getCurrentUser() != null) {
            userID = mAuth.getCurrentUser().getUid();
        }
    }

    public void setUserID(String id) {
        userID = id;
    }

    public String getUserID() {
        return userID;
    }

    public float getExactlyDistance(GeoApiContext mGeoApiContext, String origin, String destination){
        DirectionsApiRequest direction = new DirectionsApiRequest(mGeoApiContext);
        direction.origin(origin);
        direction.mode(TravelMode.DRIVING);
        final float[] exactDistance = {-1};
        direction.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                long distanceInMeters = result.routes[0].legs[0].distance.inMeters;
                String distanceInmeters = String.valueOf(distanceInMeters);
                exactDistance[0] = Float.parseFloat(distanceInmeters) / 1000;
            }

            @Override
            public void onFailure(Throwable e) {
                exactDistance[0] = 1000;
                Log.e(TAG, "calculateDirections: Failed to get directions: " + e.getMessage() );
            }
        });
        while (true){
            try {
                if (isDistanceUpdated(exactDistance[0])){
                    return exactDistance[0];
                }
                Thread.sleep(200);

            } catch (InterruptedException ex){
                Log.d(TAG, "Error when get exactDistance: " + ex);
            }
        }
    }
    private boolean isDistanceUpdated(float distance){
        if(distance == -1) return false;
        else
            return true;
    }



    public ArrayList<com.thang.jointjourney.models.LatLng> possibleLocation( LatLng point, LatLng head, LatLng tail, ArrayList<com.thang.jointjourney.models.LatLng> listLatLng){
        Log.d(TAG, "Possible pick up : onCreate");
        Log.d(TAG, "Possible pick up : ListLatlng =" + listLatLng);
        while (listLatLng.size() > 3){
            int  middle = listLatLng.size()/2;
            float pointToHead =  getDistance(head.latitude, head.longitude, point.latitude, point.longitude);
            float pointToMid = getDistance(listLatLng.get(middle).getLatitude(),listLatLng.get(middle).getLongitude(),point.latitude, point.longitude);
            float pointToTail = getDistance( tail.latitude, tail.longitude ,point.latitude, point.longitude);
            if(pointToHead == 0){
                listLatLng = new ArrayList<>();
                listLatLng.add(convertLatlng2(head));
                listLatLng.add(convertLatlng2(head));
            }
            else if(pointToTail == 0){
                listLatLng = new ArrayList<>();
                listLatLng.add(convertLatlng2(tail));
                listLatLng.add(convertLatlng2(tail));
            }
            else if (pointToHead > pointToMid && pointToMid < pointToTail){
                Log.d(TAG, "Possible pick up : first if");
                LatLng newHead = convertLatlng1(listLatLng.get(listLatLng.size()/4));
                LatLng newTail = convertLatlng1(listLatLng.get(listLatLng.size()*3/4));
                listLatLng = new ArrayList<>(listLatLng.subList(listLatLng.size() / 4, listLatLng.size() * 3 / 4));
                listLatLng.size();
                possibleLocation( point, newHead, newTail, listLatLng);
            }
            else if (pointToHead < pointToMid && pointToMid < pointToTail){
                Log.d(TAG, "Possible pick up : second if");
                LatLng newTail = convertLatlng1(listLatLng.get(middle));
                listLatLng = new ArrayList<>(listLatLng.subList(0, middle));
                listLatLng.size();
                possibleLocation( point, head, newTail, listLatLng);
            }

            else if (pointToHead > pointToMid && pointToMid > pointToTail)
            {
                Log.d(TAG, "Possible pick up : third if");
                LatLng newHead = convertLatlng1(listLatLng.get(middle));
                listLatLng = new ArrayList<>(listLatLng.subList(middle, listLatLng.size()));
                listLatLng.size();
                possibleLocation( point, newHead, tail, listLatLng);
            }
            else
                return null;
        }

        return listLatLng;
    }

    public float getDistance(double lat1, double lon1, double lat2, double lon2) {
        float[] results = new float[1];
        Location.distanceBetween(lat1, lon1, lat2, lon2, results);
        DecimalFormat df = new DecimalFormat("#.####");
        return Float.parseFloat(df.format(results[0] / 1000));
    }

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



    public LatLng convertLatlng1(com.thang.jointjourney.models.LatLng latLng){
        LatLng result = new LatLng(latLng.getLatitude(), latLng.getLongitude());
        return result;
    }
    public com.thang.jointjourney.models.LatLng convertLatlng2(LatLng latLng){
        com.thang.jointjourney.models.LatLng result = new com.thang.jointjourney.models.LatLng(latLng.latitude, latLng.longitude);
        return result;
    }

    public com.thang.jointjourney.models.LatLng isLocationValid(ArrayList<com.thang.jointjourney.models.LatLng> possibleLocation, LatLng point, int limit){
        if(possibleLocation != null){
            String origin = getAddressFromLatLng(convertLatlng1(possibleLocation.get(1))).getAddressLine(0);
            String destination = getAddressFromLatLng(point).getAddressLine(0);
            float distance = getExactlyDistance(mGeoApi, origin, destination);
            if(distance < limit){
                return possibleLocation.get(1);
            }
            else{
                return null;
            }
        }
        return null;
    }

    public boolean isWrongWay( com.thang.jointjourney.models.LatLng from, com.thang.jointjourney.models.LatLng to, LatLng customerFromLatLng){
        return !(getDistance( to.getLatitude(), to.getLongitude(), customerFromLatLng.latitude, customerFromLatLng.longitude)
                > getDistance( from.getLatitude(), from.getLongitude(), customerFromLatLng.latitude, customerFromLatLng.longitude));
    }
    public LatLng getLatLngFromAddress(String addressString){
        Log.d(TAG, "getLatLngFromAddress: processing");

        Geocoder geocoder = new Geocoder(mContext);
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(addressString, 1);
            if(list.size() > 0){
                Address address = list.get(0);
                LatLng latLng = new LatLng(address.getLatitude(),address.getLongitude());
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

    public Address getAddressFromLatLng(LatLng latLng){
        Geocoder geocoder=new Geocoder(mContext);
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
}


