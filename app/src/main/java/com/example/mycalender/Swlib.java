package com.example.mycalender;

import java.nio.CharBuffer;

public class Swlib {

    static{
        System.loadLibrary("native-lib");
    }


    public static native double GetJulDay(int year, int month, int day, double hours);
    public static native String SWeCalUT (double tjd, int ipl, int iflag, double[] xx , int err);
    public static native void SWSetSidmode ( int sidmode, double t, double ayana_t );
    public static native double[] WritePanchang(int year, int month, int day, double timezone);


}
