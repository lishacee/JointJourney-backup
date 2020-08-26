package com.thang.jointjourney.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thang.jointjourney.Account.ProfileActivity;
import com.thang.jointjourney.Adapter.ParticipantsAdapter;
import com.thang.jointjourney.Home.EditRideActivity;
import com.thang.jointjourney.R;
import com.thang.jointjourney.Rides.RidesActivity;
import com.thang.jointjourney.Utils.FirebaseMethods;
import com.thang.jointjourney.models.LatLng;
import com.thang.jointjourney.models.Participants;
import com.thang.jointjourney.models.User;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


public class ViewRideCreatedDialog extends Dialog implements
        View.OnClickListener  {

    private static final String TAG = "ViewRideCreatedDialog";
    public Context c;
    public Dialog d;

    // variables
    private TextView mUsername, mRidesCompleted, mCost, mDepartureTime, mExtraTime, mFromStreet, mFromPostcode, mFromCity, mToStreet, mToPostcode, mToCity, mCancelDialogBtn, durationTextView, distanceTextView;
    private RatingBar mRatingBar;
    private Button mEditRideBtn, mStartBtn, mFinishBtn;
    private FloatingActionButton mDeleteRideBtn, mPaticipantsRideBtn, mViewProfileBtn;
    private String userID, rides, seats, from, to, date, cost, username, goTime, extraTime, rideID, duration, ridesCompleted, distance, luggage, rideKey;
    private Float rating;
    private ArrayList<LatLng> listLatlng;
    private int numberParticipants = 0;

    //Firebase
    private FirebaseMethods mFirebaseMethods;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabse;
    private DatabaseReference mRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_ride_details);
        mFirebaseMethods = new FirebaseMethods(c);
        mFirebaseDatabse = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabse.getReference();

        setupWidgets();
        findParticipantDetails();
        mPaticipantsRideBtn.setOnClickListener(this);
        mCancelDialogBtn.setOnClickListener(this);
        mEditRideBtn.setOnClickListener(this);
        mStartBtn.setOnClickListener(this);
        mFinishBtn.setOnClickListener(this);
        mDeleteRideBtn.setOnClickListener(this);
        mViewProfileBtn.setOnClickListener(this);
    }

    public ViewRideCreatedDialog(Context a, String rideID, String username, String rides, String seats, String from, String to, String goTime, String date, String cost,
                                 Float rating, String extraTime,
                                 String duration, String ridesCompleted, String distance, String userID, String luggage, String rideKey, ArrayList<LatLng> listLatlng) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
        this.rideID = rideID;
        this.username = username;
        this.rides = rides;
        this.seats = seats;
        this.from = from;
        this.to = to;
        this.goTime = goTime;
        this.cost = cost;
        this.rating = rating;
        this.extraTime = extraTime;
        this.duration = duration;
        this.ridesCompleted = ridesCompleted;
        this.distance = distance;
        this.userID = userID;
        this.date = date;
        this.luggage = luggage;
        this.rideKey = rideKey;
        this.listLatlng = listLatlng;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialogConfirm:
                dismiss();
                Intent intent1 = new Intent(c, EditRideActivity.class);
                intent1.putExtra("COST", cost);
                intent1.putExtra("EXTRATIME", extraTime);
                intent1.putExtra("DATE", date);
                intent1.putExtra("SEATS", seats);
                intent1.putExtra("TO", to);
                intent1.putExtra("FROM", from);
                intent1.putExtra("GOTIME", goTime);
                intent1.putExtra("DURATION", duration);
                intent1.putExtra("DISTANCE", distance);
                intent1.putExtra("LUGGAGE", luggage);
                intent1.putExtra("RIDEKEY", rideKey);
                intent1.putExtra("listLatlng", listLatlng);
                c.startActivity(intent1);
                break;
            case R.id.dialogCancel:
                dismiss();
                break;
            case R.id.deleteRideBtn:
                showDialog();
                dismiss();
                break;
            case R.id.paticipantsRideBtn:
                showDialogParticpants();
                break;
            case R.id.viewProfileBtn:
                showIntentProfile();
                break;
            case R.id.dialog_start:
                mStartBtn.setVisibility(View.INVISIBLE);
                mFinishBtn.setVisibility(View.VISIBLE);
                break;
            case R.id.dialog_finish:
                Intent intent4 = new Intent(c, RidesActivity.class);
                c.startActivity(intent4);
                mRef.child("availableRide").child(rideID).removeValue();
//                mRef.child("requestRide").child(rideID).addListenerForSingleValueEvent();
//                mRef.child("user").addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if(snapshot.exists()){
//                            for(DataSnapshot postSnapshot : snapshot.getChildren()){
//                                User user = postSnapshot.getValue(User.class);
//                                if(userID.equals(user.getUser_id())){
//                                    DatabaseReference newReference = mRef.child(snapshot.getKey()).child("completedRides");
//                                    newReference.setValue(user.getCompletedRides() + 1);
//                                }
//                            }
//                        }
//                    }
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });

                break;
            default:
                break;
        }
    }

    private void showDialog(){
        //Confirmation to delete the ride dialog
        DeleteConfirmationDialog dialog = new DeleteConfirmationDialog(c, rideID);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
    private void showDialogParticpants(){
        //Confirmation to delete the ride dialog
        ParticipantsDialog dialog = new ParticipantsDialog(c, userID, rideID);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void showIntentProfile(){
        //Confirmation to delete the ride dialog
        Intent intent = new Intent(c, ProfileActivity.class);
        c.startActivity(intent);
    }

    private void findParticipantDetails(){

        mRef.child("requestRide").child(rideID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        Participants r = dataSnapshot1.getValue(Participants.class);
                        if (r.getStatus().equals("accepted")){
                            numberParticipants = numberParticipants + 1;
                            Log.d(TAG, "number in loop =" + numberParticipants);
                        }
                    }
                    if (numberParticipants >0) {
                        mEditRideBtn.setVisibility(View.INVISIBLE);
                        mStartBtn.setVisibility(View.VISIBLE);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }



    private void setupWidgets(){
        //Setup widgets
        mUsername = (TextView) findViewById(R.id.usernameTxt);
        mRidesCompleted = (TextView) findViewById(R.id.completedRidesTxt);
        mCost = (TextView) findViewById(R.id.costTxt);
        mDepartureTime = (TextView) findViewById(R.id.timeTxt);
        mExtraTime = (TextView) findViewById(R.id.extraTimeTxt);
        mFromStreet = (TextView) findViewById(R.id.streetNameTxt);
        mToStreet = (TextView) findViewById(R.id.streetName2Txt);
        durationTextView = (TextView) findViewById(R.id.durationNew);
        distanceTextView = (TextView) findViewById(R.id.distanceTxt);

        mRatingBar = (RatingBar) findViewById(R.id.ratingBar);


        mEditRideBtn = (Button) findViewById(R.id.dialogConfirm);
        mStartBtn = (Button) findViewById(R.id.dialog_start);
        mFinishBtn = (Button) findViewById(R.id.dialog_finish);
        mDeleteRideBtn = (FloatingActionButton) findViewById(R.id.deleteRideBtn);
        mPaticipantsRideBtn = (FloatingActionButton) findViewById(R.id.paticipantsRideBtn);
        mViewProfileBtn = (FloatingActionButton) findViewById(R.id.viewProfileBtn);
        mCancelDialogBtn = (TextView) findViewById(R.id.dialogCancel);

        mCost.setText(cost);
        mUsername.setText(username);
        mRatingBar.setRating(rating);
        mDepartureTime.setText(goTime);
        mExtraTime.setText(extraTime);
        mFromStreet.setText(from);
        mToStreet.setText(to);
        durationTextView.setText("Thời gian: " + duration);
        mRidesCompleted.setText(ridesCompleted + " Chuyến");
        distanceTextView.setText("Khoảng cách: " + distance);
    }

}
