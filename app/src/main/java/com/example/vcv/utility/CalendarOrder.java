package com.example.vcv.utility;

/**
 * @author Mattia Da Campo e Andrea Dalle Fratte
 * @version 1.0
 */
public class CalendarOrder {

    public String dateCalendarOrder;
    public String hourFrom;
    public String hourTo;
    public String defaultHourToWork;
    public String job;
    public Boolean confirmed;
    public String equipment;
    public String note;
    public String realHourFrom;
    public String realHourTo;

    /**
     * Empty constructor
     */
    public CalendarOrder() {
    }

    /**
     * Constructor
     *
     * @param dateCalendarOrder
     * @param hourFrom
     * @param hourTo
     * @param dftHourToWork
     * @param job
     * @param confirmed
     * @param equipment
     * @param note
     * @param realHourFrom
     * @param realHourTo
     * @throws Exception
     */
    public CalendarOrder(String dateCalendarOrder, String hourFrom, String hourTo, String dftHourToWork, String job, Boolean confirmed, String equipment, String note, String realHourFrom, String realHourTo) throws Exception {
        String localStartAt = CalendarOrder.checkHourFormat(hourFrom);
        String localEndAt = CalendarOrder.checkHourFormat(hourTo);
        String localTotalHours = CalendarOrder.checkHourFormat(dftHourToWork);
        String localRealStartAt = CalendarOrder.checkHourFormat(realHourFrom);
        String localRealEndAt = CalendarOrder.checkHourFormat(realHourTo);

        if (!localStartAt.equals("") && !localEndAt.equals("") && !localTotalHours.equals("")) {
            this.dateCalendarOrder = dateCalendarOrder;
            this.hourFrom = hourFrom;
            this.hourTo = hourTo;
            this.defaultHourToWork = localTotalHours;
            this.job = job;
            this.confirmed = confirmed;
            this.equipment = equipment;
            this.note = note;
            this.realHourFrom = localRealStartAt;
            this.realHourTo = localRealEndAt;
        } else {
            throw new Exception("Date format invalid");
        }
    }

    /**
     * Method used to check the hour format when create the object
     *
     * @param hour
     * @return String correctly formatted
     */
    private static String checkHourFormat(String hour) {
        String newHour = "";

        if (hour.contains(":")) {
            String[] strs = hour.split(":");
            if (strs[0].matches("^[0-9]*$") && strs[1].matches("^[0-9]*$")) {
                String firstPart, secondPart;

                if (Integer.parseInt(strs[0]) < 25 && Integer.parseInt(strs[1]) < 60) {
                    if (Integer.parseInt(strs[0]) < 10) {
                        firstPart = "0" + Integer.parseInt(strs[0]);
                    } else {
                        firstPart = strs[0];
                    }
                    if (Integer.parseInt(strs[1]) < 10) {
                        secondPart = "0" + Integer.parseInt(strs[1]);
                    } else {
                        secondPart = strs[1];
                    }

                    newHour = firstPart + ":" + secondPart;
                }
            }
        }

        return newHour;
    }

    /**
     * Method to compare the object
     *
     * @param calendarOrder
     * @return boolean depends if equals or not
     */
    @Override
    public boolean equals(Object calendarOrder) {
        CalendarOrder newCalendarOrder = (CalendarOrder) calendarOrder;

        return !this.dateCalendarOrder.equals(newCalendarOrder.dateCalendarOrder) ||
                !this.hourFrom.equals(newCalendarOrder.hourFrom) ||
                !this.hourTo.equals(newCalendarOrder.hourTo) ||
                !this.defaultHourToWork.equals(newCalendarOrder.defaultHourToWork) ||
                !this.job.equals(newCalendarOrder.job) ||
                !this.confirmed == newCalendarOrder.confirmed ||
                !this.equipment.equals(newCalendarOrder.equipment) ||
                !this.note.equals(newCalendarOrder.note) ||
                !this.realHourFrom.equals(newCalendarOrder.realHourFrom) ||
                !this.realHourTo.equals(newCalendarOrder.realHourTo);
    }
}
