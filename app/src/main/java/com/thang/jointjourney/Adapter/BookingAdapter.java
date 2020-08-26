package com.thang.jointjourney.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.thang.jointjourney.Common.Common;
import com.thang.jointjourney.Home.HomeActivity;
import com.thang.jointjourney.Payment.PaymentActivity;
import com.thang.jointjourney.Pickup.PickupActivity;
import com.thang.jointjourney.R;
import com.thang.jointjourney.Remote.IFCMService;
import com.thang.jointjourney.models.BookingResults;
import com.thang.jointjourney.models.FCMResponse;
import com.thang.jointjourney.models.Notification;
import com.thang.jointjourney.models.Sender;
import com.thang.jointjourney.models.Token;


import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.MyViewHolder> {
    private String[] mDataset;
    private Context mContext;
    private ArrayList<BookingResults> ride;
    private DatabaseReference myRef;
    private FirebaseDatabase mFirebaseDatabase;
    private IFCMService mService;

    public BookingAdapter(Context c, ArrayList<BookingResults> o){
        this.mContext = c;
        this.ride = o;
    }

    public BookingAdapter(String[] myDataset) {
        mDataset = myDataset;
    }

    @Override
    public BookingAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {
        return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.individual_booking_information, parent, false));
    }

    // Replace the contents of a view (invoked by the layout manager)
    @SuppressLint("RestrictedApi")
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final String status = ride.get(position).getStatus();
        final String username = ride.get(position).getUsername();
        final String userID = ride.get(position).getUser_id();
        final String rideID = ride.get(position).getRide_id();
        final String licencePlate = ride.get(position).getLicencePlate();
        final String goTime = ride.get(position).getGoTime();
        final String profile_photo = ride.get(position).getProfile_photo();
        String from = "Từ: " + ride.get(position).getLocation().replaceAll("\n", ", ");
        String to = "Đến: " + ride.get(position).getDestination().replaceAll("\n", ", ");
        final String cost = String.valueOf(ride.get(position).getCost()) + "VNĐ";
        final String pickupLocation = ride.get(position).getPickupLocation();
        final String date = ride.get(position).getGoTime() + " - " +  ride.get(position).getDateOfJourney();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        final String customerId = ride.get(position).getCustomerId();

        if (status.equals("accepted")){
            holder.cardView.setCardBackgroundColor(Color.rgb(234, 255, 236));
            holder.bookingStatusTextview.setText("Đã đồng ý!");
            holder.bookingStatusTextview.setTextColor(Color.rgb(0, 160, 66));

            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Intent intent = new Intent(mContext, PickupActivity.class);
////                    intent.putExtra("pickupLocation", pickupLocation);
////                    intent.putExtra("rideID", rideID);
////                    intent.putExtra("userID", userID);
////                    intent.putExtra("licencePlate", licencePlate);
////                    intent.putExtra("goTime", goTime);
////                    mContext.startActivity(intent);
                }
            });
        }
        else if(status.equals("decline")) {
            holder.cardView.setCardBackgroundColor(Color.rgb(248, 167, 165));
            holder.bookingStatusTextview.setText("Bị từ chối!");
            holder.bookingStatusTextview.setTextColor(Color.rgb(253, 51, 32));
            holder.mDelete.setVisibility(View.VISIBLE);
            holder.mDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String key;
                    myRef.child("requestRide")
                            .child(rideID).
                            child(customerId).removeValue();
                }
            });
        }
//        else if(status.equals("pending")){
//            holder.mRequest.setVisibility(View.VISIBLE);
//            holder.mRequest.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
//
//                    tokens.orderByKey().equalTo(userID)
//                            .addListenerForSingleValueEvent(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
//                                        Token token = dataSnapshot1.getValue(Token.class);
//                                        String extraData = username + ";" + profile_photo + ";" + fromRide + ";" + pickupLocation + ";"+ dropLocation;
//                                        Notification data = new Notification(currentUserID, userID, rideID, extraData, toRide);
//                                        Sender content = new Sender(data, token.getToken());
//
//                                        mService.sendMessage(content)
//                                                .enqueue(new Callback<FCMResponse>() {
//                                                    @Override
//                                                    public void onResponse(Call<FCMResponse> call, retrofit2.Response<FCMResponse> response) {
//                                                        Log.i("Booking Adapter", "onResponse: " + response.toString());
//                                                        if (response.body().success == 1 || response.code() == 200){
//                                                            Toast.makeText(mContext, "Gửi yêu cầu thành công!", Toast.LENGTH_SHORT).show();
//                                                        } else {
//                                                            Toast.makeText(mContext, "Gửi yêu cầu thất bại!", Toast.LENGTH_SHORT).show();
//                                                        }
//                                                    }
//
//                                                    @Override
//                                                    public void onFailure(Call<FCMResponse> call, Throwable t) {
//                                                        Log.e("Booking Adapter", "onFailure: "+ t.getMessage());
//                                                    }
//                                                });
//                                    }
//                                }
//
//                                @Override
//                                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                }
//                            });
//
//                }
//            });
//        }

        if (to.length() > 20){
            to = to.substring(0 , Math.min(to.length(), 21));
            to = to + "...";
            holder.to.setText(to);
        } else {
            holder.to.setText(to);
        }

        if (from.length() > 20){
            from = from.substring(0 , Math.min(from.length(), 21));
            from = from + "...";
            holder.from.setText(from);
        } else {
            holder.from.setText(from);
        }


        Picasso.get().load(ride.get(position).getProfile_photo()).into(holder.profile_photo);

        holder.date.setText(date);


    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return ride.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {
        LinearLayout view;
        TextView rides, from, to, date, seats, costs, bookingStatusTextview;
        CircleImageView profile_photo;
        RatingBar ratingBar;
        CardView cardView;
        Button mDelete, mRequest;

        public MyViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.bookingCardView);
            view = (LinearLayout) itemView.findViewById(R.id.view);
            from = (TextView) itemView.findViewById(R.id.fromTxt);
            to = (TextView) itemView.findViewById(R.id.toTxt);
            date = (TextView) itemView.findViewById(R.id.individualDateTxt);
            profile_photo = (CircleImageView) itemView.findViewById(R.id.indiviual_profile_picture);
            bookingStatusTextview = (TextView) itemView.findViewById(R.id.bookingStatusTextview);
            mDelete = (Button) itemView.findViewById(R.id.delete);
            mRequest = (Button) itemView.findViewById(R.id.requestAgain);
            mService = Common.getFCMService();

        }
    }
    private void requestAgain() {

    }


}