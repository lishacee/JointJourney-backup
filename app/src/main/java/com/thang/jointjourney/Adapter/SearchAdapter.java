package com.thang.jointjourney.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.thang.jointjourney.Dialogs.BookRideDialog;
import com.thang.jointjourney.Home.SearchDetailResults;
import com.thang.jointjourney.R;
import com.thang.jointjourney.models.LatLng;
import com.thang.jointjourney.models.Ride;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;


public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MyViewHolder> {
    private String[] mDataset;
    private Context mContext;
    private ArrayList<Ride> ride;
    private ArrayList<LatLng> listPickup, listDrop;
    private com.google.android.gms.maps.model.LatLng fromCustomer, toCustomer;

    public SearchAdapter(Context mContext, ArrayList<Ride> ride, ArrayList<LatLng> listPickup, ArrayList<LatLng> listDrop, com.google.android.gms.maps.model.LatLng fromCustomer, com.google.android.gms.maps.model.LatLng toCustomer) {
        this.mContext = mContext;
        this.ride = ride;
        this.listPickup = listPickup;
        this.listDrop = listDrop;
        this.fromCustomer = fromCustomer;
        this.toCustomer = toCustomer;
    }

    public SearchAdapter(String[] myDataset) {
        mDataset = myDataset;
    }

    @Override
    public SearchAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.individual_ride_information, parent, false));
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        final String username = ride.get(position).getUsername();
        final String rides = String.valueOf(ride.get(position).getCompleteRides() + " chuyến");
        String seats = String.valueOf("Còn " + ride.get(position).getSeatsAvailable() + " chỗ trống!");
        String from = "Từ: " + ride.get(position).getCurrentLocation();
        String to = "Đến: " + ride.get(position).getDestination();
        final String date = ride.get(position).getGoTime() + " - " +  ride.get(position).getDateOfJourney() ;
        final String dateOnly = ride.get(position).getDateOfJourney();
        final String cost = String.valueOf(ride.get(position).getCost()) + " VNĐ";
        final Float rating = (float) ride.get(position).getUserRating();
        final String extraTime = String.valueOf(ride.get(position).getExtraTime() + " phút");
        final String fromOnly = ride.get(position).getCurrentLocation();
        final String toOnly = ride.get(position).getDestination();
        final String licencePlate = ride.get(position).getLicencePlate();
        final String rideID = ride.get(position).getRideID();
        final String goTime = ride.get(position).getGoTime();
        Log.d("df", "onBindViewHolder: " + rideID);
        final String duration = ride.get(position).getDuration();
        final String userID = ride.get(position).getUser_id();
        Log.i("Check", "onBindViewHolder: "+ userID);
        final String profile_photo = ride.get(position).getProfile_picture();
        final String car_photo = ride.get(position).getCar_photo();
        final String completedRides = String.valueOf(ride.get(position).getCompleteRides());
        final String distance = ride.get(position).getDistance();
        final LatLng pickupLocation = listPickup.get(position);
        final LatLng dropLocation = listDrop.get(position);
        final LatLng fromDriver = ride.get(position).getFromLatlng();
        final LatLng toDriver = ride.get(position).getToLatlng();


        if (to.length() > 30){
            to = to.substring(0 , Math.min(to.length(), 25));
            to = to + "...";
            holder.to.setText(to);
        } else {
            holder.to.setText(to);
        }

        if (from.length() > 30){
            from = from.substring(0 , Math.min(from.length(), 25));
            from = from + "...";
            holder.from.setText(from);
        } else {
            holder.from.setText(from);
        }

        if (ride.get(position).getSeatsAvailable() == 1) {
            seats = "Chỉ còn " + String.valueOf(ride.get(position).getSeatsAvailable() + " chỗ trống!");
            holder.seats.setTextColor(Color.parseColor("#920000"));
            holder.seats.setTypeface(null, Typeface.BOLD);
        }

        holder.rides.setText(rides);
        holder.seats.setText(seats);
        holder.date.setText(date);
//        holder.costs.setText(cost);
        holder.ratingBar.setRating(rating);
        Picasso.get().load(ride.get(position).getCar_photo()).into(holder.car_photo);

        final String finalSeats = seats;
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(mContext, SearchDetailResults.class);
                intent.putExtra("rideID", rideID);
                intent.putExtra("username", username);
                intent.putExtra("licencePlate", licencePlate);
                intent.putExtra("rides", rides);
                intent.putExtra("finalSeats", finalSeats);
                intent.putExtra("fromOnly", fromOnly);
                intent.putExtra("toOnly", toOnly);
                intent.putExtra("date", date);
                intent.putExtra("dateOnly", dateOnly);
                intent.putExtra("cost", cost);
                intent.putExtra("rating", rating);
                intent.putExtra("extraTime", extraTime);
                intent.putExtra("profile_photo", profile_photo);
                intent.putExtra("completedRides", completedRides);
                intent.putExtra("distance", distance);
                intent.putExtra("duration", duration);
                intent.putExtra("userID", userID);
                intent.putExtra("goTime",goTime);


                intent.putExtra("fromDriver", fromDriver);
                intent.putExtra("toDriver", toDriver);
                intent.putExtra("pickUp", pickupLocation);
                intent.putExtra("drop", dropLocation);
                Bundle bundle = new Bundle();
                bundle.putParcelable("FROMCUSTOMER", fromCustomer);
                bundle.putParcelable("TOCUSTOMER", toCustomer);

                intent.putExtra("bundle", bundle);
                mContext.startActivity(intent);
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return ride.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {
        LinearLayout view;
        TextView rides, from, to, date, seats, costs;
        CircleImageView car_photo;
        RatingBar ratingBar;

        public MyViewHolder(View itemView) {
            super(itemView);

            view = (LinearLayout) itemView.findViewById(R.id.view);
            rides = (TextView) itemView.findViewById(R.id.indivcompletedRidesTxt);
            from = (TextView) itemView.findViewById(R.id.fromTxt);
            to = (TextView) itemView.findViewById(R.id.toTxt);
            date = (TextView) itemView.findViewById(R.id.individualTimeTxt);
            seats = (TextView) itemView.findViewById(R.id.seatsTxt);
//            costs = (TextView) itemView.findViewById(R.id.priceTxt);

            ratingBar = (RatingBar) itemView.findViewById(R.id.individualRatingBar);

            car_photo = (CircleImageView) itemView.findViewById(R.id.car_photo);

        }
    }
    /**
     * parses date to a readable format
     * @param time
     * @return
     */
    public String parseDateToddMMyyyy(String time) {
        String inputPattern = "dd/MM/yy";
        String outputPattern = "dd MMMM";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    private String parseLocation(String location){
        if(location.contains(",")){
            location = location.replaceAll(",", "\n");
            location = location.replaceAll(" ", "");
            return location;
        } else {

        }

        return location;
    }
}