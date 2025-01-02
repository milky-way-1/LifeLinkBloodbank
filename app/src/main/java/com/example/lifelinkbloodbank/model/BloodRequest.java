package com.example.lifelinkbloodbank.model;

public class BloodRequest {
    private String id;
    private String hospitalId;
    private String hospitalName;
    private String bloodType;
    private String contactNumber;

    private String address;

    private String status;


    // Constructor
    public BloodRequest() {}

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }


    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getAddress(){
        return this.address;
    }

    public String getStatus(){
        return this.status;
    }

}
