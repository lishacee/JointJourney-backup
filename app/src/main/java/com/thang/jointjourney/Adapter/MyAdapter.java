package com.thang.jointjourney.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.thang.jointjourney.Dialogs.ViewRideCreatedDialog;
import com.thang.jointjourney.R;
import com.thang.jointjourney.models.LatLng;
import com.thang.jointjourney.models.Participants;
import com.thang.jointjourney.models.Ride;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private String[] mDataset;
    private Context mContext;
    private ArrayList<Ride> ride;

    public MyAdapter(Context c, ArrayList<Ride> o){
        this.mContext = c;
        this.ride = o;
    }

    public MyAdapter(String[] myDataset) {
        mDataset = myDataset;
    }

    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.individual_ride_information, parent, false));
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final String userID = ride.get(position).getUser_id();

        final String username = ride.get(position).getUsername();
        final String rides = ride.get(position).getCompleteRides() + " Chuyến";
        final String seats = ride.get(position).getSeatsAvailable() + " Chỗ trống!";
        String from = "Từ: " + ride.get(position).getCurrentLocation();
        String to = "Đến: " + ride.get(position).getDestination();
        final String goTime = String.valueOf(ride.get(position).getGoTime());
        final String date = goTime + " - "  + ride.get(position).getDateOfJourney() ;
        final String cost = ride.get(position).getCost() + " VNĐ";
        final Float rating = (float) ride.get(position).getUserRating();
        final String extraTime = ride.get(position).getExtraTime() + " phút";
        final String fromOnly = ride.get(position).getCurrentLocation();
        final String toOnly = ride.get(position).getDestination();
        final String rideID = ride.get(position).getRideID();
        final String duration = ride.get(position).getDuration();
        final String completeRides = String.valueOf(ride.get(position).getCompleteRides());
        final String distance = ride.get(position).getDistance();
        final String dateOnly = ride.get(position).getDateOfJourney();
        final String luggage = ride.get(position).getLuggage();
        final String rideKey = ride.get(position).getRideID();
        final ArrayList<LatLng> listLatlng = ride.get(position).getListLatlng();

        holder.rides.setText(rides);
        holder.seats.setText(seats);
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



        holder.date.setText(date);
//        holder.costs.setText(cost);
        holder.ratingBar.setRating(rating);
        Picasso.get().load(ride.get(position).getCar_photo()).into(holder.car_photo);

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewRideCreatedDialog dialog = new ViewRideCreatedDialog(mContext, rideID ,username, rides, seats.trim().replaceAll(" Chỗ trống!",""), fromOnly, toOnly, goTime, dateOnly, cost.trim().replaceAll(" VNĐ" , ""), rating, extraTime, duration, completeRides, distance ,userID, luggage, rideKey, listLatlng);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
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