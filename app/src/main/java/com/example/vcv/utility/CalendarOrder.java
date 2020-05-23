package com.example.vcv.utility;

public class CalendarOrder {

    public String ID;
    public String dateCalendarOrder;
    public String hourFrom;
    public String hourTo;
    public String defaultHourToWork;
    public String job;
    public Boolean confirmed;
    public String equipment;
    public String note;

    public CalendarOrder() {
    }

    public CalendarOrder(String ID, String dateCalendarOrder, String hourFrom, String hourTo, String dftHourToWork, String job, Boolean confirmed, String equipment, String note) {
        this.ID = ID;
        this.dateCalendarOrder = dateCalendarOrder;
        this.hourFrom = hourFrom;
        this.hourTo = hourTo;
        this.defaultHourToWork = dftHourToWork;
        this.job = job;
        this.confirmed = confirmed;
        this.equipment = equipment;
        this.note = note;
    }
}
