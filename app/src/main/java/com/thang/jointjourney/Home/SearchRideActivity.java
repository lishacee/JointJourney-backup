package com.thang.jointjourney.Home;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.thang.jointjourney.R;
import com.thang.jointjourney.Utils.FirebaseMethods;
import com.thang.jointjourney.models.User;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;



public class SearchRideActivity extends AppCompatActivity {

    private static final String TAG = "SearchRideActivity";

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabse;
    private DatabaseReference mRef;
    private FirebaseMethods mFirebaseMethods;
    private String userID;
    private LatLng fromLatlng, toLatlng;

    private Context mContext;

    //Fragment view
    private View view;

    private EditText mDestinationEditText, mLocationEditText, mDateOfJourneyEditText;
    private Button mSnippetSeachARideBtn;
    private Switch mSameGenderSearchSwitch;
    private Boolean sameGender;

    //vars
    private User mUserSettings;
    private Calendar mCalandar;
    private DatePickerDialog.OnDateSetListener date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_search_ride);

        mContext = SearchRideActivity.this;

        setupWidgets();
        setupFirebaseAuth();
        getActivityData();

        //Setup back arrow for navigating back to 'ProfileActivity'
        ImageView backArrow = (ImageView) findViewById(R.id.backArrowFindRide);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

//        mSameGenderSearchSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked){
//                    sameGender = true;
//                } else {
//                    sameGender = false;
//                }
//            }
//        });

        mDateOfJourneyEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DATE, 0);

                DatePickerDialog datePickerDialog = new DatePickerDialog(SearchRideActivity.this, date, mCalandar
                        .get(Calendar.YEAR), mCalandar.get(Calendar.MONTH),
                        mCalandar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
                datePickerDialog.show();
            }
        });


        mSnippetSeachARideBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDateOfJourneyEditText.getText().length() > 0 &&  mLocationEditText.getText().length() > 0 && mDestinationEditText.getText().length() > 0) {
                    Intent intent = new Intent(mContext, SearchResultsActivity.class);
                    intent.putExtra("LOCATION", mLocationEditText.getText().toString());
                    intent.putExtra("DESTINATION", mDestinationEditText.getText().toString());
                    intent.putExtra("DATE", mDateOfJourneyEditText.getText().toString());
                    intent.putExtra("FromLatLng", fromLatlng);
                    intent.putExtra("ToLatLng", toLatlng);
                    startActivity(intent);
                } else {
                    Toast.makeText(mContext, "Bạn phải điền đủ thông tin", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getActivityData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getParcelableExtra("bundle");
        if (bundle != null){
            fromLatlng = bundle.getParcelable("fromLatlng");
            toLatlng = bundle.getParcelable("toLatlng");
            Log.d(TAG, "fromLatlng= " + fromLatlng);
            Log.d(TAG, "toLatLng= " + toLatlng);
            mLocationEditText.setText(getIntent().getStringExtra("LOCATION"));
            mDestinationEditText.setText(getIntent().getStringExtra("DESTINATION"));
        }
    }

    private void setupWidgets(){
        //Firebase setup
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabse = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabse.getReference();
        mFirebaseMethods = new FirebaseMethods(this);

        //Widget setup
        mDestinationEditText = (EditText) findViewById(R.id.destinationEditText);
        mLocationEditText = (EditText) findViewById(R.id.locationEditText);
        mDateOfJourneyEditText = (EditText) findViewById(R.id.dateEditText);
//        mSameGenderSearchSwitch = (Switch) findViewById(R.id.sameGenderSearchSwitch);
        mSnippetSeachARideBtn = (Button) findViewById(R.id.snippetSeachARideBtn);

        //Calander setup
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




    /** --------------------------- Firebase ---------------------------- **/

    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth");

        userID = mAuth.getCurrentUser().getUid();
    }

    /***
     *  Setup the firebase object
     */
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }
}
