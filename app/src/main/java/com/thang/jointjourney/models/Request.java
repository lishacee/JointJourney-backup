package com.thang.jointjourney.models;


public class Request {
    private String user_id;
    private String profile_photo;
    private String userProfilePhoto;
    private String username;
    private String dateOfJourney;
    private String goTime;
    private Float cost;
    private int seats;
    private String destination;
    private String location;
    private int luggage;
    private String status;
    private String ride_id;
    private String pickupLocation;
    private String licencePlate;
    private String dropLocation;
    private String customerId;

    public Request() {
    }



    public Request(String user_id, String profile_photo, String userProfilePhoto, String username, String dateOfJourney,
                   String goTime, Float cost, int seats, String destination, String location, int luggage, String status,
                   String ride_id, String pickupLocation, String dropLocation, String licencePlate, String customerId) {
        this.user_id = user_id;
        this.profile_photo = profile_photo;
        this.userProfilePhoto = userProfilePhoto;
        this.username = username;
        this.dateOfJourney = dateOfJourney;
        this.goTime = goTime;
        this.cost = cost;
        this.seats = seats;
        this.destination = destination;
        this.location = location;
        this.luggage = luggage;
        this.status = status;
        this.ride_id = ride_id;
        this.pickupLocation = pickupLocation;
        this.licencePlate = licencePlate;
        this.dropLocation = dropLocation;
        this.customerId = customerId;
    }


    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getProfile_photo() {
        return profile_photo;
    }

    public void setProfile_photo(String profile_photo) {
        this.profile_photo = profile_photo;
    }

    public String getUserProfilePhoto() {
        return userProfilePhoto;
    }

    public void setUserProfilePhoto(String userProfilePhoto) {
        this.userProfilePhoto = userProfilePhoto;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public Float getCost() {
        return cost;
    }

    public void setCost(Float cost) {
        this.cost = cost;
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }

    public String getDropLocation() {
        return dropLocation;
    }

    public void setDropLocation(String dropLocation) {
        this.dropLocation = dropLocation;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getLuggage() {
        return luggage;
    }

    public void setLuggage(int luggage) {
        this.luggage = luggage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRide_id() {
        return ride_id;
    }

    public void setRide_id(String ride_id) {
        this.ride_id = ride_id;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public String getLicencePlate() {
        return licencePlate;
    }

    public void setLicencePlate(String licencePlate) {
        this.licencePlate = licencePlate;
    }
    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    @Override
    public String toString() {
        return "Request{" +
                "user_id='" + user_id + '\'' +
                ", profile_photo='" + profile_photo + '\'' +
                ", userProfilePhoto='" + userProfilePhoto + '\'' +
                ", username='" + username + '\'' +
                ", dateOfJourney='" + dateOfJourney + '\'' +
                ", goTime='" + goTime + '\'' +
                ", cost=" + cost +
                ", seats=" + seats +
                ", destination='" + destination + '\'' +
                ", location='" + location + '\'' +
                ", luggage=" + luggage +
                ", status='" + status + '\'' +
                ", ride_id='" + ride_id + '\'' +
                ", pickupLocation='" + pickupLocation + '\'' +
                ", licencePlate='" + licencePlate + '\'' +
                ", dropLocation='" + dropLocation + '\'' +
                ", customerId='" + customerId + '\'' +
                '}';
    }

}
