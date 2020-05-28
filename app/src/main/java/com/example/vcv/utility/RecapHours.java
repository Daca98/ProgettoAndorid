package com.example.vcv.utility;

/**
 * @author Mattia Da Campo e Andrea Dalle Fratte
 * @version 1.0
 */
public class RecapHours {
    public String toCalculation;
    public String totHoursShouldWork;
    public String totHoursWorked;
    public String totExtra;

    /**
     * Empty constructor
     */
    public RecapHours() {
    }

    /**
     * Constructor
     *
     * @param toCalculation
     * @param totHoursShouldWork
     * @param totHoursWorked
     * @param totExtra
     */
    public RecapHours(String toCalculation, String totHoursShouldWork, String totHoursWorked, String totExtra) {
        this.toCalculation = toCalculation;
        this.totHoursShouldWork = totHoursShouldWork;
        this.totHoursWorked = totHoursWorked;
        this.totExtra = totExtra;
    }
}
