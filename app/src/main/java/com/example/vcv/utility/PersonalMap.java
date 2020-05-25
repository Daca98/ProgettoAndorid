package com.example.vcv.utility;

public class PersonalMap {

    public String path;
    public Double latitudeFrom;
    public Double longitudeFrom;
    public Double latitudeTo;
    public Double longitudeTo;

    public PersonalMap() {

    }

    public PersonalMap(String path, Double latitudeFrom, Double longitudeFrom, Double latitudeTo, Double longitudeTo) {
        this.path = path;
        this.latitudeFrom = latitudeFrom;
        this.longitudeFrom = longitudeFrom;
        this.latitudeTo = latitudeTo;
        this.longitudeTo = longitudeTo;
    }
}
