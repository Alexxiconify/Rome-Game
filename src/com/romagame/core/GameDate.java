package com.romagame.core;

public class GameDate {
    private int year;
    private int month;
    private int day;
    private int hour;
    
    public GameDate(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = 0;
    }
    
    public void advance() {
        // Advance by one day for grand strategy game pacing
        day++;
        if (day > getDaysInMonth()) {
            day = 1;
            month++;
            if (month > 12) {
                month = 1;
                year++;
            }
        }
    }
    
    private int getDaysInMonth() {
        return switch (month) {
            case 2 -> isLeapYear() ? 29 : 28;
            case 4, 6, 9, 11 -> 30;
            default -> 31;
        };
    }
    
    private boolean isLeapYear() {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }
    
    public String getFormattedDate() {
        return String.format("%d-%02d-%02d", year, month, day);
    }
    
    public int getYear() { return year; }
    public int getMonth() { return month; }
    public int getDay() { return day; }
    public int getHour() { return hour; }
    
    public void setDate(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = 0;
    }
} 