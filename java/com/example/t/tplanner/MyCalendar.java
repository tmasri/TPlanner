package com.example.t.tplanner;

import android.util.Log;

import java.util.Calendar;
import java.util.Date;

public class MyCalendar {

    private Calendar calendar;

    public MyCalendar() {
        calendar = Calendar.getInstance();
    }

    public MyCalendar(String date) {
        int[] vals = parseCalendar(date);
        calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, vals[0]-1);
        calendar.set(Calendar.MONTH, vals[1]);
        calendar.set(Calendar.YEAR, vals[2]);
    }

    public MyCalendar(int d, int m, int y) {

        calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, d);
        calendar.set(Calendar.MONTH, m);
        calendar.set(Calendar.YEAR, y);

    }

    public int getMonthInt() {
        return calendar.get(Calendar.MONTH);
    }

    public String getMonth() {

        switch (calendar.get(Calendar.MONTH)) {
            case 0:
                return "Jan";
            case 1:
                return "Feb";
            case 2:
                return "Mar";
            case 3:
                return "Apr";
            case 4:
                return "May";
            case 5:
                return "Jun";
            case 6:
                return "Jul";
            case 7:
                return "Aug";
            case 8:
                return "Sep";
            case 9:
                return "Oct";
            case 10:
                return "Nov";
            case 11:
                return "Dec";
            default:
                return "NAM";
        }

    }

    public int getDayOfWeek() {
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    public String getDayOfWeekString() {

        switch (getDayOfWeek()) {
            case 1:
                return "Sunday";
            case 2:
                return "Monday";
            case 3:
                return "Tuesday";
            case 4:
                return "Wednesday";
            case 5:
                return "Thursday";
            case 6:
                return "Friday";
            case 7:
                return "Saturday";
            default:
                return "Not a day of the week";
        }

    }

    public int getDay() {
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public String dayToString() {
        return String.valueOf(getDay());
    }

    public int getYear() {
        return calendar.get(Calendar.YEAR);
    }

    public String yearToString() {
        return String.valueOf(getYear());
    }

    public String getDate() {
        return getMonth() + " " + dayToString() + ", " + getYear();
    }

    public int getHour() {
        return calendar.getTime().getHours();
    }

    public int getMinute() {
        return calendar.getTime().getMinutes();
    }

    public String getTime() {

        Date d = calendar.getTime();
        int hour = getHour()%12;
        int minute = getMinute();

        if (d.getHours() >= 12) {
            return hour + ":" + minute + " PM";
        } else {
            return hour + ":" + minute + " AM";
        }
    }

    public void setDay(int d) {

        if (d <= 0) {
            // date from previous month - d
            // set new dates
            calendar.set(Calendar.MONTH, getMonthInt()-1);
            int numOfDays = calendar.getActualMaximum(Calendar.DATE);
            d = numOfDays - d;
        } else if (d > calendar.getActualMaximum(Calendar.DATE)) {
            int numOfDays = calendar.getActualMaximum(Calendar.DATE);
            calendar.set(Calendar.MONTH, getMonthInt()+1);
            d = d - numOfDays;
        }
        calendar.set(Calendar.DAY_OF_MONTH, d);
    }

    public void setMonth(int m) {
        calendar.set(Calendar.MONTH, m);
    }

    public void setYear(int y) {
        calendar.set(Calendar.YEAR, y);
    }

    public void setHour(int h) {
        calendar.set(Calendar.HOUR, h);
    }

    public void setMinute(int m) {
        calendar.set(Calendar.MINUTE, m);
    }

    public void setTime(String t) {

        String[] time = t.split(" ");
        String pmAM = time[1];
        String[] hourMinute = time[0].split(":");

        int minute = Integer.parseInt(hourMinute[1]);
        int hour = Integer.parseInt(hourMinute[0]);
        if (pmAM.equals("PM")) {
            hour += 12;
        }

        setHour(hour);
        setMinute(minute);

    }

    private int[] parseCalendar(String date) {

        // get month
        int month = parseMonth(date.substring(0,3));

        // get year
        String yearString = date.substring(
                date.length()-4,
                date.length()
        );
        int year = Integer.parseInt(yearString);

        // get day
        date = date.substring(4, date.length()); // remove month from string
        date = date.substring(0, date.length()-6); // remove year from string

        int day = Integer.parseInt(date);

        return new int[]{day, month, year};

    }

    private int parseMonth(String month) {

        switch (month) {
            case "Jan":
                return 0;
            case "Feb":
                return 1;
            case "Mar":
                return 2;
            case "Apr":
                return 3;
            case "May":
                return 4;
            case "Jun":
                return 5;
            case "Jul":
                return 6;
            case "Aug":
                return 7;
            case "Sep":
                return 8;
            case "Oct":
                return 9;
            case "Nov":
                return 10;
            case "Dec":
                return 11;
            default:
                return -1;
        }

    }

}
