package com.example.vcv.utility;

/**
 * @author Mattia Da Campo e Andrea Dalle Fratte
 * @version 1.0
 */
public class PersonalMap {

    public String path;
    public Double latitudeFrom;
    public Double longitudeFrom;
    public Double latitudeTo;
    public Double longitudeTo;

    /**
     * Empty constructor
     */
    public PersonalMap() {
    }

    /**
     * Constructor
     *
     * @param path
     * @param latitudeFrom
     * @param longitudeFrom
     * @param latitudeTo
     * @param longitudeTo
     */
    public PersonalMap(String path, Double latitudeFrom, Double longitudeFrom, Double latitudeTo, Double longitudeTo) {
        this.path = path;
        this.latitudeFrom = latitudeFrom;
        this.longitudeFrom = longitudeFrom;
        this.latitudeTo = latitudeTo;
        this.longitudeTo = longitudeTo;
    }
}
