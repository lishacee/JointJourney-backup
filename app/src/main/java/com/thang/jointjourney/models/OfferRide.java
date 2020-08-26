package com.thang.jointjourney.models;



import java.util.ArrayList;

public class OfferRide {

    private String rideID;
    private String user_id;
    private String username;
    private String currentLocation;
    private String destination;
    private String dateOfJourney;
    private int seatsAvailable;
    private String carNumber;
    private com.google.android.gms.maps.model.LatLng fromLatlng;
    private com.google.android.gms.maps.model.LatLng toLatlng;
    private boolean sameGender;
    private int luggageAllowance;
    private String car;
    private String goTime;
    private int extraTime;
    private String profile_picture;
    private int cost;
    private int completeRides;
    private int userRating;
    private String duration;
    private String distance;
    private String car_photo;
    ArrayList<com.thang.jointjourney.models.LatLng> listLatlng;

    public OfferRide() {
    }

    public OfferRide(String rideID, String user_id, String username, String currentLocation, String destination, String dateOfJourney, int seatsAvailable, String carNumber, com.google.android.gms.maps.model.LatLng fromLatlng, com.google.android.gms.maps.model.LatLng toLatlng, boolean sameGender, int luggageAllowance, String car, String goTime, int extraTime, String profile_picture,String car_photo, int cost, int completeRides, int userRating, String duration, String distance, ArrayList<LatLng> listLatlng) {
        this.rideID = rideID;
        this.user_id = user_id;
        this.username = username;
        this.currentLocation = currentLocation;
        this.destination = destination;
        this.dateOfJourney = dateOfJourney;
        this.seatsAvailable = seatsAvailable;
        this.carNumber = carNumber;
        this.fromLatlng = fromLatlng;
        this.toLatlng = toLatlng;
        this.sameGender = sameGender;
        this.luggageAllowance = luggageAllowance;
        this.car = car;
        this.goTime = goTime;
        this.extraTime = extraTime;
        this.profile_picture = profile_picture;
        this.car_photo = car_photo;
        this.cost = cost;
        this.completeRides = completeRides;
        this.userRating = userRating;
        this.duration = duration;
        this.distance = distance;
        this.listLatlng = listLatlng;
    }

    public String getRideID() {
        return rideID;
    }

    public void setRideID(String rideID) {
        this.rideID = rideID;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDateOfJourney() {
        return dateOfJourney;
    }

    public void setDateOfJourney(String dateOfJourney) {
        this.dateOfJourney = dateOfJourney;
    }

    public int getSeatsAvailable() {
        return seatsAvailable;
    }

    public void setSeatsAvailable(int seatsAvailable) {
        this.seatsAvailable = seatsAvailable;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    public com.google.android.gms.maps.model.LatLng getFromLatlng() {
        return fromLatlng;
    }

    public void setFromLatlng(com.google.android.gms.maps.model.LatLng fromLatlng) {
        this.fromLatlng = fromLatlng;
    }

    public com.google.android.gms.maps.model.LatLng getToLatlng() {
        return toLatlng;
    }

    public void setToLatlng(com.google.android.gms.maps.model.LatLng toLatlng) {
        this.toLatlng = toLatlng;
    }

    public boolean isSameGender() {
        return sameGender;
    }

    public void setSameGender(boolean sameGender) {
        this.sameGender = sameGender;
    }

    public int getLuggageAllowance() {
        return luggageAllowance;
    }

    public void setLuggageAllowance(int luggageAllowance) {
        this.luggageAllowance = luggageAllowance;
    }

    public String getCar() {
        return car;
    }

    public void setCar(String car) {
        this.car = car;
    }

    public String getGoTime() {
        return goTime;
    }

    public void setGoTime(String goTime) {
        this.goTime = goTime;
    }

    public int getExtraTime() {
        return extraTime;
    }

    public void setExtraTime(int extraTime) {
        this.extraTime = extraTime;
    }

    public String getProfile_picture() {
        return profile_picture;
    }

    public void setProfile_picture(String profile_picture) {
        this.profile_picture = profile_picture;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getCompleteRides() {
        return completeRides;
    }

    public void setCompleteRides(int completeRides) {
        this.completeRides = completeRides;
    }

    public int getUserRating() {
        return userRating;
    }

    public void setUserRating(int userRating) {
        this.userRating = userRating;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getCar_photo() {
        return car_photo;
    }

    public void setCar_photo(String car_photo) {
        this.car_photo = car_photo;
    }

    public ArrayList<LatLng> getListLatlng() {
        return listLatlng;
    }

    public void setListLatlng(ArrayList<LatLng> listLatlng) {
        this.listLatlng = listLatlng;
    }

    @Override
    public String toString() {
        return "OfferRide{" +
                "rideID='" + rideID + '\'' +
                ", user_id='" + user_id + '\'' +
                ", username='" + username + '\'' +
                ", currentLocation='" + currentLocation + '\'' +
                ", destination='" + destination + '\'' +
                ", dateOfJourney='" + dateOfJourney + '\'' +
                ", seatsAvailable=" + seatsAvailable +
                ", carNumber='" + carNumber + '\'' +
                ", fromLatlng=" + fromLatlng +
                ", toLatlng=" + toLatlng +
                ", sameGender=" + sameGender +
                ", luggageAllowance=" + luggageAllowance +
                ", car='" + car + '\'' +
                ", goTime='" + goTime + '\'' +
                ", extraTime=" + extraTime +
                ", profile_picture='" + profile_picture + '\'' +
                ", cost=" + cost +
                ", completeRides=" + completeRides +
                ", userRating=" + userRating +
                ", duration='" + duration + '\'' +
                ", distance='" + distance + '\'' +
                ", car_photo='" + car_photo + '\'' +
                ", listLatlng=" + listLatlng +
                '}';
    }
}
