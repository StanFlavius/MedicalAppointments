package com.example.medicalappointments.service;

import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class Helper {

    public final static String DATE_TIME_PATTERN = "dd-MM-yyyy HH:mm";
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat(DATE_TIME_PATTERN);

    public static String formatDate(Date date) {
        return SIMPLE_DATE_FORMAT.format(date);
    }

}
