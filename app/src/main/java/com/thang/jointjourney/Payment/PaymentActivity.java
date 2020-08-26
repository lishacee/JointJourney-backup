package com.thang.jointjourney.Payment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thang.jointjourney.Adapter.ParticipantsAdapter;
import com.thang.jointjourney.Booked.BookedActivity;
import com.thang.jointjourney.Common.Common;
import com.thang.jointjourney.Common.ApplicationContext;
import com.thang.jointjourney.Home.HomeActivity;
import com.thang.jointjourney.R;
import com.thang.jointjourney.Remote.IFCMService;
import com.thang.jointjourney.Utils.FirebaseMethods;
import com.thang.jointjourney.models.FCMResponse;
import com.thang.jointjourney.models.Notification;
import com.thang.jointjourney.models.Participants;
import com.thang.jointjourney.models.Sender;
import com.thang.jointjourney.models.Token;

import java.text.DecimalFormat;
import java.text.NumberFormat;


import retrofit2.Call;
import retrofit2.Callback;

import static com.thang.jointjourney.Constant.DEFAULT_PRICE;


public class PaymentActivity extends AppCompatActivity {

    private static final String TAG = "PaymentActivity";
    private static final int REQUEST_CODE = 1234;

    private Context mContext = PaymentActivity.this;


    //widgets
    private Button mPaymentBtn;
    private TextView mPrice, mCancelBtn, mTotalPrice, mDiscount;
    private LinearLayout mGroupWaiting, mGroupPayment;

    //Activity data
    private String userID, fromRide, toRide, rideID, profile_photo, profile_photo2,
            pickupLocation, currentUserID, username, cost, pickupTime, dateOnly, licencePlate,
            dropLocation, goTime;
    private int seatsAvailable = 0;
    private int participantsNumber = 0;
    private double totalCost, expectedCost, costPerKm, discount;
    private float distanceCustomer, distanceCustomerInMeters;


    //Firebase
    private IFCMService mService;
    private FirebaseMethods mFirebaseMethods;
    private DatabaseReference myRef;
    private FirebaseDatabase mFirebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        getActivityData();


        mService = Common.getFCMService();
        mFirebaseMethods = new FirebaseMethods(PaymentActivity.this);

        getUserInformation();
        getsSeatsRemaining();
        getParticipantsNumber();
        calculatePrice();
        init();




        mGroupWaiting.setVisibility(View.GONE);
        mGroupPayment.setVisibility(View.VISIBLE);
        mPaymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPickupHere();

            }
        });

        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void init(){
        mPaymentBtn = (Button) findViewById(R.id.paymentBtn);
        mCancelBtn = (TextView) findViewById(R.id.cancelBtn);
        mPrice = (TextView) findViewById(R.id.moneyTextview);
        mGroupWaiting = (LinearLayout) findViewById(R.id.waiting_group);
        mGroupPayment = (LinearLayout) findViewById(R.id.payment_group);
        mTotalPrice = (TextView) findViewById(R.id.totalPriceTexView);
        mDiscount = (TextView) findViewById(R.id.discountTextView);

        NumberFormat formatter = new DecimalFormat("#,###");

        mTotalPrice.setText("" + formatter.format(Math.round(totalCost)));
        mDiscount.setText("-" +  formatter.format(Math.round(discount)));
        mPrice.setText("" + formatter.format(Math.round(totalCost - discount)));
    }

    private void getActivityData(){
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userID = getIntent().getStringExtra("userID");
            fromRide = getIntent().getStringExtra("fromRide");
            toRide = getIntent().getStringExtra("toRide");
            rideID = getIntent().getStringExtra("rideID");
            profile_photo2 = getIntent().getStringExtra("profile_photo");
            pickupLocation = getIntent().getStringExtra("pickupLocation");
            dropLocation = getIntent().getStringExtra("dropLocation");
            cost = getIntent().getStringExtra("cost");
            licencePlate = getIntent().getStringExtra("licencePlate");
            dateOnly = getIntent().getStringExtra("dateOfJourney");
            goTime = getIntent().getStringExtra("goTime");
            distanceCustomerInMeters = getIntent().getFloatExtra("distanceCustomer",0);
            distanceCustomer = distanceCustomerInMeters / 1000;
            Log.d(TAG, "Payment: userid = " + userID);
            Log.d(TAG, "Payment: fromRide = " + fromRide);
            Log.d(TAG, "Payment: toRide = " + toRide);
            Log.d(TAG, "Payment: rideID = " + rideID);
            Log.d(TAG, "Payment: profile_photo = " + profile_photo);
            Log.d(TAG, "Payment: pickupLocation = " + pickupLocation);
            Log.d(TAG, "Payment: dropLocation = " + dropLocation);
            Log.d(TAG, "Payment: cost = " + cost);
            Log.d(TAG, "Payment: licencePlate = " + licencePlate);
            Log.d(TAG, "Payment: dateOfJourney = " + dateOnly);
            Log.d(TAG, "Payment: distanceCustomer = " + distanceCustomer);

        }
    }

    private void getParticipantsNumber() {
        myRef.child("requestRide").child(rideID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        Participants r = dataSnapshot1.getValue(Participants.class);
                        if (r.getStatus().equals("accepted")) {
                            participantsNumber = participantsNumber + 1;
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void calculatePrice(){
        costPerKm = Double.parseDouble(cost.trim().replaceAll(" VNĐ",""));
        if(distanceCustomer < 1){
            totalCost = DEFAULT_PRICE;
        }
        else if(participantsNumber == 0){
            totalCost = 10000 + costPerKm * (distanceCustomer - 0.5);
            discount = 0;
        }else if(participantsNumber == 1){
            totalCost = 10000 + costPerKm * (distanceCustomer - 0.5) * 0.9;
            discount = totalCost*0.1;
        }else if(participantsNumber == 2){
            totalCost = 10000 + costPerKm * (distanceCustomer - 0.5) * 0.8;
            discount = totalCost *0.2;
        }else {
            totalCost = 10000 * costPerKm * (distanceCustomer - 0.5) * 0.7;
            discount = totalCost * 0.3;
        }
    }

    /*------------------------------- Request booking after calculate the price ---------------------------------- */

    private void requestPickupHere() {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");


        tokens.orderByKey().equalTo(userID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                            Token token = dataSnapshot1.getValue(Token.class);

                            String extraData = username + ";" + profile_photo + ";" + fromRide + ";" + pickupLocation + ";"+ dropLocation;
                            Log.d(TAG, "data = " + extraData);
                            Notification data = new Notification(currentUserID, userID, rideID, extraData, toRide);
                            Sender content = new Sender(data, token.getToken());

                            mService.sendMessage(content)
                                    .enqueue(new Callback<FCMResponse>() {
                                        @Override
                                        public void onResponse(Call<FCMResponse> call, retrofit2.Response<FCMResponse> response) {
                                            Log.i(TAG, "onResponse: " + response.toString());
                                            if (response.body().success == 1 || response.code() == 200){
                                                Toast.makeText(mContext, "Gửi yêu cầu thành công!", Toast.LENGTH_SHORT).show();
                                                mFirebaseMethods.addPoints(userID, 200);
                                                Intent intent = new Intent(PaymentActivity.this, HomeActivity.class);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                Toast.makeText(mContext, "Gửi yêu cầu thất bại!", Toast.LENGTH_SHORT).show();
                                                finish();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<FCMResponse> call, Throwable t) {
                                            Log.e(TAG, "onFailure: "+ t.getMessage());
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        com.thang.jointjourney.models.Request request = new com.thang.jointjourney.models.Request(userID, profile_photo2, profile_photo,
                username, dateOnly, goTime,Float.parseFloat(cost.substring(0, cost.indexOf(" "))),1,toRide, fromRide, 1, "pending", rideID, pickupLocation,dropLocation, licencePlate, currentUserID);

        myRef.child("requestRide")
                .child(rideID)
                .child(currentUserID)
                .setValue(request);
    }

    public void getUserInformation(){
        myRef.child("user").child(currentUserID).child("username").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    username = dataSnapshot.getValue(String.class);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        myRef.child("user").child(currentUserID).child("profile_photo").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    profile_photo = dataSnapshot.getValue(String.class);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getsSeatsRemaining(){
        myRef.child("availableRide").child(rideID).child("seatsAvailable").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Log.d(TAG, "onDataChange: " + dataSnapshot.toString());
                    seatsAvailable = dataSnapshot.getValue(Integer.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateSeatsRemaining(){
        myRef.child("availableRide").child(rideID).child("seatsAvailable").setValue(seatsAvailable - 1);
    }

}
