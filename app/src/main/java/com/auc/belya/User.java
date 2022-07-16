package com.auc.belya;

public class User {
    private String email, phone, password, name, imageURL, ID, imageCardURL;
    private Float balance;
    private Integer type;

    public User(String email, String phone, String password, String name, String imageURL, String ID,String imageCardURL, Float balance, Integer type) {
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.name = name;
        this.imageCardURL = imageCardURL;
        this.imageURL = imageURL;
        this.ID = ID;
        this.balance = balance;
        this.type = type;
    }

    public User() {

    }

    public String getImageCardURL() {
        return imageCardURL;
    }

    public void setImageCardURL(String imageCardURL) {
        this.imageCardURL = imageCardURL;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getBalance() {
        return balance;
    }

    public void setBalance(Float balance) {
        this.balance = balance;
    }
}
