package com.thang.jointjourney.Home;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.geofire.GeoFire;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thang.jointjourney.Common.ApplicationContext;
import com.thang.jointjourney.Dialogs.OfferRideCreatedDialog;
import com.thang.jointjourney.R;
import com.thang.jointjourney.Rides.RidesActivity;
import com.thang.jointjourney.Utils.FirebaseMethods;
import com.thang.jointjourney.Utils.UniversalImageLoader;
import com.thang.jointjourney.models.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditRideActivity extends AppCompatActivity {

    private static final String TAG = "EditRideActivity";
    private EditRideActivity mContext = EditRideActivity.this;;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseMethods mFirebaseMethods;
    private String userID;

    //Widgets
    private EditText mDateOfJourneyEditText, mExtraTimeEditText, mLuggageEditText, mGoingEditText;

    private Button mSnippetEditRideButton;
    private Boolean sameGenderBoolean = false;
    private Calendar mCalandar;
    private DatePickerDialog.OnDateSetListener date;
    private CircleImageView mCarPhoto;
    private TextView mCarNumber, mCarEditText, mSeatsEditText, mToEditText, mFromEditText, mUsername, durationTxt, distanceTxt,mCostTextView;


    //vars
    private User mUserSettings;
    private String destinationId, locationId, profile_photo,car_photo, username, pickupTimeID, costID, dateOfJourneyID, lengthOfJourneyID,
            extraTimeID, licencePlateID, carID, luggageID, destinationId2, locationId2, duration, distance, carNumberID, goTimeID, seatsID, rideKeyID;
    private int userRating ;
    private int completeRides;
    private LatLng currentLocation, fromLatlng, toLatlng;
    private ArrayList<com.thang.jointjourney.models.LatLng> listLatlng;


    //GeoFire
    private DatabaseReference mRef;
    private GeoFire mGeoFire;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_edit_ride);
        Log.d(TAG, "onCreate: starting.");

        //Disables focused keyboard on view startup
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        getActivityData();
        setupFirebase();
        setupFirebaseAuth();

        mUsername = (TextView) findViewById(R.id.usernameTxt);
        mToEditText = (TextView) findViewById(R.id.toEditText);
        mFromEditText = (TextView) findViewById(R.id.fromEditText);
        mCostTextView = (TextView) findViewById(R.id.costEditText);
        mCarNumber = (TextView) findViewById(R.id.carNumberEditText);
        mExtraTimeEditText = (EditText) findViewById(R.id.extraTimeEditText);
        mSeatsEditText = (TextView) findViewById(R.id.seatsEditText);
        mCarEditText = (TextView) findViewById(R.id.carEditText);
        mLuggageEditText = (EditText) findViewById(R.id.luggageEditText);
        mDateOfJourneyEditText = (EditText) findViewById(R.id.DateOfJourneyEditText);
        mGoingEditText = (EditText) findViewById(R.id.goTimeEditText);
        mCarPhoto = (CircleImageView) findViewById(R.id.car_image);
        durationTxt = (TextView) findViewById(R.id.durationTxt);
        distanceTxt = (TextView) findViewById(R.id.distanceTxt);
        mDateOfJourneyEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DATE, 0);

                DatePickerDialog datePickerDialog = new DatePickerDialog(EditRideActivity.this, date, mCalandar
                        .get(Calendar.YEAR), mCalandar.get(Calendar.MONTH),
                        mCalandar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
                datePickerDialog.show();
            }
        });

        mGoingEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(EditRideActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        if(selectedMinute < 10)
                        {
                            mGoingEditText.setText( selectedHour + ":0" + selectedMinute); // fix thoi gian 1:1 -> 1:01
                        }
                        else{
                            mGoingEditText.setText( selectedHour + ":" + selectedMinute);
                        }
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Chọn thời gian");
                mTimePicker.show();
            }
        });

        mFromEditText.setText(locationId);
        mToEditText.setText(destinationId);
        mCostTextView.setText(costID);
        mExtraTimeEditText.setText(extraTimeID);
        mSeatsEditText.setText(seatsID);
        mDateOfJourneyEditText.setText(dateOfJourneyID);
        mGoingEditText.setText(goTimeID);
        durationTxt.setText("Thời gian: " + duration);
        distanceTxt.setText("Khoảng cách: "+ distance);
        mLuggageEditText.setText(luggageID);



        ImageView backArrow = (ImageView) findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back to HomeActivity");
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
            }
        });

        mSnippetEditRideButton = (Button) findViewById(R.id.snippetEditRideButton);
        mSnippetEditRideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int cost = Integer.parseInt(mCostTextView.getText().toString());
                String dateOfJourney = mDateOfJourneyEditText.getText().toString();

                String luggageStr = mLuggageEditText.getText().toString();

                String carNumber = mCarNumber.getText().toString();
                String goTime = mGoingEditText.getText().toString();
                String car = mCarEditText.getText().toString();
                String to = mToEditText.getText().toString();
                String from = mFromEditText.getText().toString();
                String duration = durationTxt.getText().toString().replaceAll("Thời gian: " , "");
                String distance = distanceTxt.getText().toString().replaceAll("Khoảng cách: " , "");
                String extraTime = mExtraTimeEditText.getText().toString().replaceAll(" phút","");
                String seatsAvailable = mSeatsEditText.getText().toString().replaceAll(" Chỗ trống!","");

                if(!isIntNull(cost) && !isStringNull(goTime) &&
                        !isStringNull(luggageStr) && !isStringNull(extraTime)){
                    int extraTime2 = Integer.parseInt(extraTime);
                    int seats = Integer.parseInt(seatsAvailable);
                    int luggageAllowance = Integer.parseInt(luggageStr);
                    mFirebaseMethods.editRide(rideKeyID, userID , username, from, to, dateOfJourney, seats, carNumber,  fromLatlng, toLatlng,
                            sameGenderBoolean, luggageAllowance, car, goTime, extraTime2, profile_photo, car_photo, cost, completeRides, userRating, duration, distance, listLatlng);

                    mFirebaseMethods.checkNotifications(getCurrentDate(), "Chuyến đi của bạn đã được chỉnh sửa!");
                    Intent returnIntent = new Intent();
                    setResult(Activity.RESULT_OK,returnIntent);
                    Intent intent = new Intent(EditRideActivity.this, RidesActivity.class);
                    startActivity(intent);
                    finish();

                } else {
                    Toast.makeText(EditRideActivity.this, "Bạn phải nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    private boolean isStringNull(String string){
        if (string.equals("")){
            return true;
        } else {
            return false;
        }
    }

    private boolean isIntNull(int integer){
        if (integer < 0 || integer == 0){
            return true;
        } else {
            return false;
        }
    }

    private String getCurrentDate(){
        Date todayDate = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String todayString = formatter.format(todayDate);

        return todayString;
    }


    private void updateLabel() {
        String dateFormat = "dd/MM/yy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.UK);

        mDateOfJourneyEditText.setText(simpleDateFormat.format(mCalandar.getTime()));
    }

    private void getActivityData() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
                locationId = getIntent().getStringExtra("FROM");
                destinationId = getIntent().getStringExtra("TO");
                costID = getIntent().getStringExtra("COST");
                dateOfJourneyID = getIntent().getStringExtra("DATE");
                duration = getIntent().getStringExtra("DURATION");
                extraTimeID = getIntent().getStringExtra("EXTRATIME");
                seatsID = getIntent().getStringExtra("SEATS");
                luggageID = getIntent().getStringExtra("LUGGAGE");
                goTimeID = getIntent().getStringExtra("GOTIME");
                distance = getIntent().getStringExtra("DISTANCE");
                rideKeyID = getIntent().getStringExtra("RIDEKEY");
                listLatlng =(ArrayList<com.thang.jointjourney.models.LatLng>) getIntent().getSerializableExtra("listLatlng");
        }
    }

    private void setProfileWidgets(User userSettings){
        Log.d(TAG, "setProfileWidgets: setting user widgets from firebase data");

        User user = userSettings;

        mUserSettings = userSettings;

        UniversalImageLoader.setImage(user.getCar_photo(), mCarPhoto, null,"");

        username = user.getUsername();
        userRating = user.getUserRating();
        completeRides = user.getCompletedRides();
        profile_photo = user.getProfile_photo();
        car_photo = user.getCar_photo();
        carID = user.getCar();
        carNumberID = user.getCarNumber();

        mUsername.setText(username);
        mCarNumber.setText(carNumberID);
        mFromEditText.setText(locationId);
        mCarEditText.setText(carID);
        mSeatsEditText.setText(String.valueOf(seatsID) + " Chỗ trống!");


        mCalandar = Calendar.getInstance();
        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                mCalandar.set(Calendar.YEAR, year);
                mCalandar.set(Calendar.MONTH, month);
                mCalandar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };
    }


    @Override
    public void onResume() {
        super.onResume();
//        mPickupLocationEditText.setText(com.thang.jointjourney.Common.Common.getClassName());
    }

    /*----------------------------- SETUP FIREBASE -----------------------------------*/

    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth");

        userID = mAuth.getCurrentUser().getUid();

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //retrieve user information from the database
                setProfileWidgets(mFirebaseMethods.getUserSettings(dataSnapshot));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setupFirebase() {
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();
        mFirebaseMethods = new FirebaseMethods(this);

//        mGeoFire = new GeoFire(mRef);
//
//        mGeoFire.setLocation("availableRides", new GeoLocation(37.7853889, -122.4056973), new GeoFire.CompletionListener() {
//            @Override
//            public void onComplete(String key, DatabaseError error) {
//                if (error != null) {
//                    System.err.println("There was an error saving the location to GeoFire: " + error);
//                } else {
//                    System.out.println("Location saved on server successfully!");
//                }
//            }
//        });

    }
}
