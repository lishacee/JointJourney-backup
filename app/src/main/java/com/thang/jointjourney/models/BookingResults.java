package com.thang.jointjourney.models;

/**
 * Created by P16174003 on 05/02/2019.
 */

public class BookingResults {
    private String user_id;
    private String profile_photo;
    private String username;
    private int seats;
    private String destination;
    private String location;
    private int luggage;
    private boolean accepted;
    private String ride_id;
    private String pickupLocation;
    private String goTime;
    private String dateOfJourney;
    private String licencePlate;
    private int cost;
    private String status;
    private String dropLocation;
    private String customerId;

    public BookingResults() {
    }



    public BookingResults(String user_id, String profile_photo, String username, String licencePlate, int seats, String destination, String dateOfJourney, String goTime, String location, int luggage, String status, String ride_id, int cost, String pickupLocation, String customerId, String dropLocation) {
        this.user_id = user_id;
        this.profile_photo = profile_photo;
        this.username = username;
        this.seats = seats;
        this.destination = destination;
        this.location = location;
        this.luggage = luggage;
        this.status = status;
        this.cost = cost;
        this.licencePlate = licencePlate;
        this.ride_id = ride_id;
        this.pickupLocation = pickupLocation;
        this.goTime = goTime;
        this.dateOfJourney = dateOfJourney;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
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

    public String getLicencePlate() {
        return licencePlate;
    }

    public void setLicencePlate(String licencePlate) {
        this.licencePlate = licencePlate;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
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

    @Override
    public String toString() {
        return "BookingResults{" +
                "user_id='" + user_id + '\'' +
                ", profile_photo='" + profile_photo + '\'' +
                ", username='" + username + '\'' +
                ", seats=" + seats +
                ", destination='" + destination + '\'' +
                ", location='" + location + '\'' +
                ", luggage=" + luggage +
                ", accepted=" + accepted +
                ", ride_id='" + ride_id + '\'' +
                ", pickupLocation='" + pickupLocation + '\'' +
                ", goTime='" + goTime + '\'' +
                ", dateOfJourney='" + dateOfJourney + '\'' +
                ", licencePlate='" + licencePlate + '\'' +
                ", cost=" + cost +
                ", status='" + status + '\'' +
                ", dropLocation='" + dropLocation + '\'' +
                ", customerId='" + customerId + '\'' +
                '}';
    }

    public String getDropLocation() {
        return dropLocation;
    }

    public void setDropLocation(String dropLocation) {
        this.dropLocation = dropLocation;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

}
