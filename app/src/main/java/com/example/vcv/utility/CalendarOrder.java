package com.example.vcv.utility;

public class CalendarOrder {

    public String dateCalendarOrder;
    public String hourFrom;
    public String hourTo;
    public String dftHourToWork;
    public String job;
    public String confirmed;
    public String equipment;
    public String note;

    public CalendarOrder() {
    }

    public CalendarOrder(String dateCalendarOrder, String hourFrom, String hourTo, String dftHourToWork, String job, String confirmed, String equipment, String note) {
        this.dateCalendarOrder = dateCalendarOrder;
        this.hourFrom = hourFrom;
        this.hourTo = hourTo;
        this.dftHourToWork = dftHourToWork;
        this.job = job;
        this.confirmed = confirmed;
        this.equipment = equipment;
        this.note = note;
    }
}
