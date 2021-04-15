package com.pdmurty.mycalender;

import java.nio.CharBuffer;

public class Swlib {

    static{
        System.loadLibrary("native-lib");
    }

    //// native function impementations  are named as packagename_module_thisfile_func
    ////// ex: Java_com_pdmurty_mycalender_Swlib_CalcSolarAndLunarEclipse
    public static native double GetJulDay(int year, int month, int day, double hours);
    public static native String SWeCalUT (double tjd, int ipl, int iflag, double[] xx , int err);
    public static native void SWSetSidmode ( int sidmode, double t, double ayana_t );
    public static native double[] WritePanchang(int year, int month, int day, double timezone);
    public static native void SetLocation (  float loc_lon,  float Loc_lat );
    public static native double[] CalcEphimeris(int year, int month, int day, double timezone);
    public static native int SWeSqlitetest (String dbpath);
    public static native double[] SWeCalcNextSankaranthi (int year,int month,double timezone);
    public static native double[] SWeCalcKarthariDays (int year,double timezone);
    public static native double[] CalcSolarAndLunarEclipse(int year, double timezone);


}
