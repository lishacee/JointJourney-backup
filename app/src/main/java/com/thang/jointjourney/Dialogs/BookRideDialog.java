package com.thang.jointjourney.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.thang.jointjourney.Account.ProfileActivity;
import com.thang.jointjourney.Payment.PaymentActivity;
import com.thang.jointjourney.R;
import com.thang.jointjourney.Utils.SectionsStatePageAdapter;


public class BookRideDialog extends Dialog implements
        View.OnClickListener  {

    private static final String TAG = "ViewRideCreatedDialog";
    public Context c;
    public Dialog d;

    // variables
    private TextView mUsername, mRidesCompleted, mCost, mDepartureTime, mExtraTime, mFromStreet,
            mFromPostcode, mFromCity, mToStreet, mToPostcode, mToCity, mCancelDialogBtn,
            mPickupLocation, mSeats, mDateJourney, mDropLocation;
    private RatingBar mRatingBar;
    private Button mNext;
    private SectionsStatePageAdapter pageAdapter;
    private String rides, seats, from, to, date, cost, username, goTime, extraTime, rideID, dropLocation,
            duration, userID, profile_photo, completedRides, pickupLocation, dateOnly, licencePlate;
    private Float rating, distanceCustomer;
    private FloatingActionButton mViewProfileBtn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_ride_confirm);

        setupWidgets();

        mCancelDialogBtn.setOnClickListener(this);
        mNext.setOnClickListener(this);
        mViewProfileBtn.setOnClickListener(this);
    }

    public BookRideDialog(Context a, String rideID, String username, String licencePlate, String rides,String goTime, String seats, String from, String to, String date, String dateOnly, String cost, Float rating, String extraTime, String duration, String userID,
                          String profile_photo, String completedRides, String pickupLocation, String dropLocation, Float distanceCustomer) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
        this.rideID = rideID;
        this.username = username;
        this.rides = rides;
        this.seats = seats;
        this.from = from;
        this.to = to;
        this.date = date;
        this.dateOnly = dateOnly;
        this.cost = cost;
        this.rating = rating;
        this.extraTime = extraTime;
        this.duration = duration;
        this.userID = userID;
        this.profile_photo = profile_photo;
        this.completedRides = completedRides;
        this.pickupLocation = pickupLocation;
        this.licencePlate = licencePlate;
        this.dropLocation = dropLocation;
        this.goTime = goTime;
        this.distanceCustomer = distanceCustomer;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialogConfirm:
                showDialog();
                break;
            case R.id.dialogCancel:
                dismiss();
                break;
            case R.id.viewProfileBtn:
                showIntentProfile();
                break;
            default:
                break;
        }
        dismiss();
    }

    private void showDialog(){
        Intent intent = new Intent(c, PaymentActivity.class);
        intent.putExtra("userID", userID);
        intent.putExtra("fromRide", from);
        intent.putExtra("toRide", to);
        intent.putExtra("dateOfJourney", dateOnly);
        intent.putExtra("rideID", rideID);
        intent.putExtra("profile_photo", profile_photo);
        intent.putExtra("pickupLocation", pickupLocation);
        intent.putExtra("dropLocation", dropLocation);
        intent.putExtra("licencePlate", licencePlate);
        intent.putExtra("cost", cost);
        intent.putExtra("distanceCustomer", distanceCustomer);
        intent.putExtra("goTime", goTime);
        c.startActivity(intent);
    }

    private void showIntentProfile(){
        //Confirmation to delete the ride dialog
        Intent intent = new Intent(c, ProfileActivity.class);
        intent.putExtra("userID", userID);
        c.startActivity(intent);
    }


    private void setupWidgets(){
        //Setup widgets
        mUsername = (TextView) findViewById(R.id.usernameTxt);
        mRidesCompleted = (TextView) findViewById(R.id.completedRidesTxt);
        mCost = (TextView) findViewById(R.id.costTxt);
        mDepartureTime = (TextView) findViewById(R.id.timeTxt);
        mExtraTime = (TextView) findViewById(R.id.extraTimeTxt);
        mFromStreet = (TextView) findViewById(R.id.fromTxt);
        mToStreet = (TextView) findViewById(R.id.toTxt);
        mRatingBar = (RatingBar) findViewById(R.id.ratingBar);
        mSeats = (TextView) findViewById(R.id.seatsTxt);
        mNext = (Button) findViewById(R.id.dialogConfirm);
        mCancelDialogBtn = (TextView) findViewById(R.id.dialogCancel);
        mViewProfileBtn = (FloatingActionButton) findViewById(R.id.viewProfileBtn);
        mDateJourney = (TextView) findViewById(R.id.dateJourneyTxt);
        mPickupLocation = (TextView) findViewById(R.id.pickUpTxt);
        mDropLocation = (TextView) findViewById(R.id.dropTxt);


        mCost.setText(cost);
        mUsername.setText(username);
        mRatingBar.setRating(rating);
        mDepartureTime.setText(goTime);
        mExtraTime.setText(extraTime);
        mRidesCompleted.setText(completedRides + " Chuyến");
        mDateJourney.setText(dateOnly);


        mSeats.setText(seats);
        mFromStreet.setText(from);
        mToStreet.setText(to);
        mDropLocation.setText(dropLocation);
        mPickupLocation.setText(pickupLocation);
    }

}
