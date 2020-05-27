package com.example.vcv.utility;

public class RecapHours {
    public String toCalculation;
    public String totHoursShouldWork;
    public String totHoursWorked;
    public String totExtra;

    public RecapHours() {
    }

    public RecapHours(String toCalculation, String totHoursShouldWork, String totHoursWorked, String totExtra) {
        this.toCalculation = toCalculation;
        this.totHoursShouldWork = totHoursShouldWork;
        this.totHoursWorked = totHoursWorked;
        this.totExtra = totExtra;
    }
}
