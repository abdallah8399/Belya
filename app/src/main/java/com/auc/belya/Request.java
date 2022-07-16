package com.auc.belya;

import android.location.Location;

public class Request {

    private User sender;
    private Integer liters, offered_money;
    private Double gas_fees, total_fees;
    private String gas_type;
    private Boolean accepted;
    private Double longitude, latitude;

    public Request(User sender, Integer liters, Integer offered_money, Double gas_fees, Double total_fees, String gas_type, Boolean accepted, Double longitude, Double latitude) {
        this.sender = sender;
        this.liters = liters;
        this.offered_money = offered_money;
        this.gas_fees = gas_fees;
        this.total_fees = total_fees;
        this.gas_type = gas_type;
        this.accepted = accepted;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public Request() {
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public Integer getLiters() {
        return liters;
    }

    public void setLiters(Integer liters) {
        this.liters = liters;
    }

    public Integer getOffered_money() {
        return offered_money;
    }

    public void setOffered_money(Integer offered_money) {
        this.offered_money = offered_money;
    }

    public Double getGas_fees() {
        return gas_fees;
    }

    public void setGas_fees(Double gas_fees) {
        this.gas_fees = gas_fees;
    }

    public Double getTotal_fees() {
        return total_fees;
    }

    public void setTotal_fees(Double total_fees) {
        this.total_fees = total_fees;
    }

    public String getGas_type() {
        return gas_type;
    }

    public void setGas_type(String gas_type) {
        this.gas_type = gas_type;
    }

    public Boolean getAccepted() {
        return accepted;
    }

    public void setAccepted(Boolean accepted) {
        this.accepted = accepted;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
}

