package com.example.futuristic;

import com.google.firebase.Timestamp;

import java.util.Date;

public class Record {
    private String place, parkingID, floorlvl, parkingNo, payment;
    private Date bookingTime;

    public Date getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(Timestamp bookingTime) {
        this.bookingTime = bookingTime.toDate();
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getParkingID() {
        return parkingID;
    }

    public void setParkingID(String parkingID) {
        this.parkingID = parkingID;
    }

    public String getFloorlvl() {
        return floorlvl;
    }

    public void setFloorlvl(String floorlvl) {
        this.floorlvl = floorlvl;
    }

    public String getParkingNo() {
        return parkingNo;
    }

    public void setParkingNo(String parkingNo) {
        this.parkingNo = parkingNo;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }
}
