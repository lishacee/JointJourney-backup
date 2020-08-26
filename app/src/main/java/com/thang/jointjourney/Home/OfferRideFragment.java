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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.firebase.geofire.GeoFire;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thang.jointjourney.Dialogs.OfferRideCreatedDialog;
import com.thang.jointjourney.Pickup.PickupLocationActivity;
import com.thang.jointjourney.R;
import com.thang.jointjourney.Utils.FirebaseMethods;
import com.thang.jointjourney.Utils.UniversalImageLoader;
import com.thang.jointjourney.models.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.thang.jointjourney.Constant.DEFAULT_PRICE;

public class OfferRideFragment extends AppCompatActivity {

    private static final String TAG = "OfferRideFragment";
    private OfferRideFragment mContext;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseMethods mFirebaseMethods;
    private String userID;

    //Widgets
    private EditText mDateOfJourneyEditText, mCostEditText, mPickupEditText, mExtraTimeEditText, mLuggageEditText, mGoingEditText;
    private SwitchCompat mSameGender;
    private Button mSnippetOfferRideButton;
    private Boolean sameGenderBoolean = false;
    private Calendar mCalandar;
    private DatePickerDialog.OnDateSetListener date;
    private CircleImageView mCarPhoto;
    private TextView mCarNumber, mCarEditText, mSeatsEditText, mToEditText, mFromEditText, mUsername, durationTxt, distanceTxt, mCostTextView;


    //vars
    private User mUserSettings;
    private String destinationId, locationId, profile_photo, username, car_photo, costID, dateOfJourneyID, lengthOfJourneyID, extraTimeID, carNumberID,
            licencePlateID, carID, luggageID, destinationId2, locationId2, durationId, distanceId;
    private int userRating, seatsID;
    private int completeRides;
    private double currentLatitude, currentLongtitude;
    private LatLng fromLatlng, toLatlng;
    private ArrayList<com.thang.jointjourney.models.LatLng> ListLatlng;


    //GeoFire
    private DatabaseReference mRef;
    private GeoFire mGeoFire;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_offer_ride);
        Log.d(TAG, "onCreate: starting.");
        mContext = OfferRideFragment.this;

//        Ẩn bàn phím sau khi vào layout
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        getActivityData();
        setupFirebase();
        setupFirebaseAuth();

        mUsername = (TextView) findViewById(R.id.usernameTxt);
        mToEditText = (TextView) findViewById(R.id.toEditText);
        mFromEditText = (TextView) findViewById(R.id.fromEditText);
        mCostTextView = (TextView) findViewById(R.id.costTextView);
        mCarNumber = (TextView) findViewById(R.id.carNumberEditText);
        mExtraTimeEditText = (EditText) findViewById(R.id.extraTimeEditText);
        mSeatsEditText = (TextView) findViewById(R.id.seatsEditText);
        mCarEditText = (TextView) findViewById(R.id.carEditText);
        mLuggageEditText = (EditText) findViewById(R.id.luggageEditText);
        mDateOfJourneyEditText = (EditText) findViewById(R.id.DateOfJourneyEditText);
        mCarPhoto = (CircleImageView) findViewById(R.id.car_image);
        durationTxt = (TextView) findViewById(R.id.durationTxt);
        distanceTxt = (TextView) findViewById(R.id.distanceTxt);
        mDateOfJourneyEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DATE, 0);

                DatePickerDialog datePickerDialog = new DatePickerDialog(OfferRideFragment.this, date, mCalandar
                        .get(Calendar.YEAR), mCalandar.get(Calendar.MONTH),
                        mCalandar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
                datePickerDialog.show();
            }
        });



        mGoingEditText = (EditText) findViewById(R.id.pickupEditText);
        mGoingEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(OfferRideFragment.this, new TimePickerDialog.OnTimeSetListener() {
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

//        mPickupLocationEditText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(mContext, PickupLocationActivity.class);
//
//                Bundle bundle = new Bundle();
//
//                bundle.putParcelable("fromLatlng", fromLatlng);
//
//                intent.putExtra("bundle", bundle);
//
//                startActivityForResult(intent, 1);
//            }
//        });

        mSnippetOfferRideButton = (Button) findViewById(R.id.snippetOfferRideButton);
        mSnippetOfferRideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int cost = DEFAULT_PRICE;
                String dateOfJourney = mDateOfJourneyEditText.getText().toString();

                int seatsAvailable = seatsID;
                String luggageAllowance = mLuggageEditText.getText().toString();
                String extraTime = mExtraTimeEditText.getText().toString();

                String carNumber = mCarNumber.getText().toString().replaceAll("Biển số xe: " , "");
                String goTime = mGoingEditText.getText().toString();
                String car = mCarEditText.getText().toString().replaceAll("Hãng: " , "");
                String from = mFromEditText.getText().toString();
                String to = mToEditText.getText().toString();
                String duration = durationTxt.getText().toString().replaceAll("Thời gian: " , "");
                String distance = distanceTxt.getText().toString().replaceAll("Khoảng cách: " , "");

                if(!isStringNull(goTime) &&
                  !isStringNull(luggageAllowance) && !isStringNull(extraTime)){
                    int luggage = Integer.parseInt(luggageAllowance);
                    int extraTime2 = Integer.parseInt(extraTime);

                    //Creates the ride information and adds it to the database

                    mFirebaseMethods.offerRide(userID , username, from, to, dateOfJourney, seatsAvailable, carNumber,  fromLatlng, toLatlng,
                            sameGenderBoolean, luggage, car, goTime, extraTime2, profile_photo, car_photo, cost, completeRides, userRating, duration, distance, ListLatlng);

                    //Adds a notification to firebase
                    mFirebaseMethods.checkNotifications(getCurrentDate(), "Bạn vừa đăng chuyến đi!");

                    mFirebaseMethods.addPoints(userID, 100);

                    //Shows the ride has been created successfully
                    Intent returnIntent = new Intent();
                    setResult(Activity.RESULT_OK,returnIntent);
                    finish();



                } else {
                    Toast.makeText(OfferRideFragment.this, "Bạn phải điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
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

    private void getActivityData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getParcelableExtra("bundle");
        if (bundle != null){
            fromLatlng = bundle.getParcelable("fromLatlng");
            toLatlng = bundle.getParcelable("toLatlng");
            locationId = intent.getStringExtra("DESTINATION");
            destinationId = intent.getStringExtra("LOCATION");
            durationId = intent.getStringExtra("DURATION");
            distanceId = intent.getStringExtra("DISTANCE");
            ListLatlng = (ArrayList<com.thang.jointjourney.models.LatLng>) intent.getSerializableExtra("listLatLng");

            Log.d(TAG, "fromLatlng =" + fromLatlng);
            Log.d(TAG, "toLatlng =" + toLatlng);
            Log.d(TAG, "locationId =" + locationId);
            Log.d(TAG, "destinationId =" + destinationId);
            Log.d(TAG, "ListLatlng =" + ListLatlng);
            Log.d(TAG, "durationId =" + durationId);
            Log.d(TAG, "distanceId =" + distanceId);

        }
        else{
            Toast.makeText(mContext, "getActivityData : bundle = null, không chuyển được dữ liệu từ HomeActivity sang", Toast.LENGTH_SHORT).show();
        }


//            currentLocation = bundle.getParcelable("LatLng");

//        Bundle bundle = intent.getExtras();
//        if (bundle != null) {
            //  if (extras.containsKey("DESTINATION")){
            //from Home view passed to this class

//            } else {
//
//                //from ViewRideCreatedDialog passed to this class
//                pickupTimeID = getIntent().getStringExtra("PICKUPTIME");
//                costID = getIntent().getStringExtra("COST");
//                dateOfJourneyID = getIntent().getStringExtra("DATE");
//                lengthOfJourneyID = getIntent().getStringExtra("LENGTH");
//                extraTimeID = getIntent().getStringExtra("EXTRATIME");
//                seatsID = getIntent().getStringExtra("SEATS");
//                licencePlateID = getIntent().getStringExtra("LICENCE");
//                carID = getIntent().getStringExtra("CAR");
//                luggageID = getIntent().getStringExtra("LUGGAGE");
//                destinationId2 = getIntent().getStringExtra("DESTINATION2");
//                locationId2 = getIntent().getStringExtra("FROM2");
//            }
//        }
//            currentLatitude = Long.parseLong(getIntent().getStringExtra("currentLatitue"));
//            currentLongtitude = Long.parseLong(getIntent().getStringExtra("currentLongtitude"));
    }

    @Override
    public void onResume() {
        super.onResume();
//        mPickupLocationEditText.setText(com.thang.jointjourney.Common.Common.getClassName());
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
        seatsID = user.getSeats() - 1;
        carNumberID = user.getCarNumber();

        mUsername.setText(username);
        mCarNumber.setText("Biển số xe: " + carNumberID);
        mToEditText.setText(destinationId);
        mFromEditText.setText(locationId);
        mCarEditText.setText("Hãng: "+ carID);
        mSeatsEditText.setText(String.valueOf(seatsID) + " chỗ trống!");
        durationTxt.setText("Thời gian: "+ durationId);
        distanceTxt.setText("Khoảng cách: "+ distanceId);
        mCostTextView.setText("" + DEFAULT_PRICE);

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

    private void updateLabel() {
        String dateFormat = "dd/MM/yy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.UK);

        mDateOfJourneyEditText.setText(simpleDateFormat.format(mCalandar.getTime()));
    }

}
