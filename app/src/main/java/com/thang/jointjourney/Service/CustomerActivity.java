package com.thang.jointjourney.Service;

import android.content.Context;
import android.os.Bundle;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thang.jointjourney.R;
import com.thang.jointjourney.Utils.FirebaseMethods;
import com.thang.jointjourney.Utils.UniversalImageLoader;
import com.thang.jointjourney.models.Notification;
import com.thang.jointjourney.models.Sender;
import com.thang.jointjourney.models.Token;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomerActivity extends AppCompatActivity {
    private static final String TAG = "CustomerActivity";

    private TextView txtUsername, txtTo, txtFrom, txtPickup, txtDrop;
    private String title, body, username, profile_photo, to, from, userID, rideID, pickup, drop;
    private Boolean rideAccepted;

    private int seatsAvailable = 0;


    //Widgets
    private FloatingActionButton acceptBtn, declineBtn;
    private CircleImageView mRequestProfilePhoto;

    //Firebase
    private DatabaseReference myRef;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseMethods mFirebaseMethods;

    private Context mContext = CustomerActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);

        final CustomerActivity c = this;

        getActivityData();
        setupDialog();

        mFirebaseMethods = new FirebaseMethods(mContext);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        acceptBtn = (FloatingActionButton) findViewById(R.id.confirmRideBtn);
        declineBtn = (FloatingActionButton) findViewById(R.id.declineRideBtn);
        mRequestProfilePhoto = (CircleImageView)findViewById(R.id.requestProfilePhoto);
        txtTo = (TextView) findViewById(R.id.to);
        txtFrom = (TextView) findViewById(R.id.from);
        txtUsername = (TextView) findViewById(R.id.message);
        txtPickup = (TextView) findViewById(R.id.pickup);
        txtDrop = (TextView) findViewById(R.id.drop);

        txtUsername.setText("Xin chào, tôi là " + username + " và tôi muốn tham gia vào chuyến đi của bạn!");


        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptRide();
            }
        });

        declineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                declineRide();
            }
        });
//     txtAddress = (TextView) findViewById(R.id.txtAddress);
//     txtTime.setText(title);
//     txtDistance.setText(body);
       txtTo.setText("Đến: " + to);
       txtFrom.setText("Từ: " + from);
       txtPickup.setText("Địa điểm đón: " + pickup);
       txtDrop.setText("Địa điểm xuống: " + drop);

        getsSeatsRemaining();

        UniversalImageLoader.setImage(profile_photo, mRequestProfilePhoto, null,"");
    }

    private void getActivityData() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userID = getIntent().getStringExtra("title");
            body = getIntent().getStringExtra("userID");
            username = getIntent().getStringExtra("username");
            profile_photo = getIntent().getStringExtra("profile_photo");
            to = getIntent().getStringExtra("to").replaceAll("\n", ", ");
            from = getIntent().getStringExtra("from").replaceAll("\n", ", ");
            rideID = getIntent().getStringExtra("rideID");
            pickup = getIntent().getStringExtra("pickup");
            drop = getIntent().getStringExtra("drop");

        }
    }

    private void setupDialog(){
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
    }

    private void getsSeatsRemaining(){
        myRef.child("availableRide").child(rideID).child("seatsAvailable").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Log.d(TAG, "onDataChange: " + dataSnapshot.toString());
                    seatsAvailable = dataSnapshot.getValue(Integer.class);
                    Log.d(TAG, "seatsAvailable = " + seatsAvailable);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void acceptRide(){
        if(seatsAvailable != 0){
            myRef.child("availableRide").child(rideID).child("seatsAvailable").setValue(seatsAvailable - 1);
            myRef.child("requestRide")
                    .child(rideID)
                    .child(userID)
                    .child("status")
                    .setValue("accepted");
            finish();
        }
        else{
            Toast.makeText(mContext, "Đã hết chỗ ngồi không thể chấp nhận thêm yêu cầu", Toast.LENGTH_SHORT).show();
        }
    }

    private void declineRide(){
        myRef.child("requestRide")
                .child(rideID)
                .child(userID)
                .child("status")
                .setValue("decline");
        finish();
    }
}
