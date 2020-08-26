package com.thang.jointjourney.models;


import java.util.ArrayList;

public class Ride {

    private String rideID;
    private String username;
    private String profile_picture;
    private String currentLocation;
    private String destination;
    private String car;
    private String dateOfJourney;
    private String goTime;
    private Boolean sameGender;
    private int completeRides;
    private int seatsAvailable;
    private int extraTime;
    private int cost;
    private int userRating;
    private String duration;
    private String user_id;
    private String licencePlate;
    private String distance;
    private String luggage;
    private ArrayList<com.thang.jointjourney.models.LatLng> listLatlng;
    private LatLng fromLatlng;
    private LatLng toLatlng;
    private String car_photo;

    public Ride() {
    }

    public Ride(String rideID, String username, String profile_picture,String car_photo, String currentLocation, String destination, String car, String dateOfJourney, String goTime, Boolean sameGender, int completeRides, int seatsAvailable, int extraTime, int cost, int userRating, String duration, String user_id, String licencePlate, String distance, String luggageAllowance, ArrayList<LatLng> listLatlng, LatLng fromLatlng, LatLng toLatlng) {
        this.rideID = rideID;
        this.username = username;
        this.profile_picture = profile_picture;
        this.currentLocation = currentLocation;
        this.destination = destination;
        this.car = car;
        this.dateOfJourney = dateOfJourney;
        this.goTime = goTime;
        this.sameGender = sameGender;
        this.completeRides = completeRides;
        this.seatsAvailable = seatsAvailable;
        this.extraTime = extraTime;
        this.cost = cost;
        this.userRating = userRating;
        this.duration = duration;
        this.user_id = user_id;
        this.licencePlate = licencePlate;
        this.distance = distance;
        this.luggage = luggageAllowance;
        this.listLatlng = listLatlng;
        this.fromLatlng = fromLatlng;
        this.toLatlng = toLatlng;
        this.car_photo = car_photo;
    }

    public String getRideID() {
        return rideID;
    }

    public void setRideID(String rideID) {
        this.rideID = rideID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfile_picture() {
        return profile_picture;
    }

    public void setProfile_picture(String profile_picture) {
        this.profile_picture = profile_picture;
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

    public String getCar() {
        return car;
    }

    public void setCar(String car) {
        this.car = car;
    }

    public String getDateOfJourney() {
        return dateOfJourney;
    }

    public void setDateOfJourney(String dateOfJourney) {
        this.dateOfJourney = dateOfJourney;
    }

    public String getGoTime() {
        return goTime;
    }

    public void setGoTime(String goTime) {
        this.goTime = goTime;
    }

    public Boolean getSameGender() {
        return sameGender;
    }

    public void setSameGender(Boolean sameGender) {
        this.sameGender = sameGender;
    }

    public int getCompleteRides() {
        return completeRides;
    }

    public void setCompleteRides(int completeRides) {
        this.completeRides = completeRides;
    }

    public int getSeatsAvailable() {
        return seatsAvailable;
    }

    public void setSeatsAvailable(int seatsAvailable) {
        this.seatsAvailable = seatsAvailable;
    }

    public int getExtraTime() {
        return extraTime;
    }

    public void setExtraTime(int extraTime) {
        this.extraTime = extraTime;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
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

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getLicencePlate() {
        return licencePlate;
    }

    public void setLicencePlate(String licencePlate) {
        this.licencePlate = licencePlate;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getLuggage() {
        return luggage;
    }

    public void setLuggage(String luggage) {
        this.luggage = luggage;
    }

    public ArrayList<LatLng> getListLatlng() {
        return listLatlng;
    }

    public void setListLatlng(ArrayList<LatLng> listLatlng) {
        this.listLatlng = listLatlng;
    }

    public LatLng getFromLatlng() {
        return fromLatlng;
    }

    public void setFromLatlng(LatLng fromLatlng) {
        this.fromLatlng = fromLatlng;
    }

    public LatLng getToLatlng() {
        return toLatlng;
    }

    public void setToLatlng(LatLng toLatlng) {
        this.toLatlng = toLatlng;
    }

    public String getCar_photo() {
        return car_photo;
    }

    public void setCar_photo(String car_photo) {
        this.car_photo = car_photo;
    }

    @Override
    public String toString() {
        return "Ride{" +
                "rideID='" + rideID + '\'' +
                ", username='" + username + '\'' +
                ", profile_picture='" + profile_picture + '\'' +
                ", currentLocation='" + currentLocation + '\'' +
                ", destination='" + destination + '\'' +
                ", car='" + car + '\'' +
                ", dateOfJourney='" + dateOfJourney + '\'' +
                ", goTime='" + goTime + '\'' +
                ", sameGender=" + sameGender +
                ", completeRides=" + completeRides +
                ", seatsAvailable=" + seatsAvailable +
                ", extraTime=" + extraTime +
                ", cost=" + cost +
                ", userRating=" + userRating +
                ", duration='" + duration + '\'' +
                ", user_id='" + user_id + '\'' +
                ", licencePlate='" + licencePlate + '\'' +
                ", distance='" + distance + '\'' +
                ", luggage='" + luggage + '\'' +
                ", listLatlng=" + listLatlng +
                ", fromLatlng=" + fromLatlng +
                ", toLatlng=" + toLatlng +
                ", car_photo='" + car_photo + '\'' +
                '}';
    }
}
