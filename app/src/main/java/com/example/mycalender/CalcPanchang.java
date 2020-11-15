package com.example.mycalender;

import android.content.Context;
import android.content.res.Resources;

public class CalcPanchang {

    String thithiPrefix;
    String sukla;
    String space;
    String bahula;
    String[] strNaks;
    String strNak;
    String strYoga;
    String[] strYogas;
    String[] strThithi;
    String[] strLunarMonths;
    String[] strHinduYears;
    String suryodayam;
    String strVarjyam;
    String durmuhurtha;
    String rahukala;
    String thela;
    String sayam;
    String madhya;
    String rathri;
    String full;
    String udaya;
    String adhika;
    String kshaya;
    private static Context mapp;
    private static CalcPanchang instance = null;

     public static CalcPanchang getInstance(Context app){
        if(instance == null) {
            mapp = app;
            instance = new CalcPanchang();

        }
        return instance;
     }

    private void LoadStringResources() {
        Resources resource = mapp.getResources();
        space        = resource.getString(R.string.blank_space);
        sukla        = resource.getString(R.string.sukla);
        bahula       = resource.getString(R.string.bahula);
        thithiPrefix = resource.getString(R.string.thithi);
        strNak       = resource.getString(R.string.Nakshatra);
        strYoga      = resource.getString(R.string.Yoga) ;
        strYogas     = resource.getStringArray(R.array.yogas);
        strThithi    = resource.getStringArray(R.array.thithis);
        strNaks      = resource.getStringArray(R.array.Nakshatras);
        strLunarMonths= resource.getStringArray(R.array.lunar_months);
        strHinduYears = resource.getStringArray(R.array.hindhu_years);
        suryodayam   = resource.getString(R.string.sur_udayam);
        strVarjyam   = resource.getString(R.string.varjyam);
        durmuhurtha  = resource.getString(R.string.DurMuhurtham);
        rahukala     = resource.getString(R.string.rahukal);
        thela        = resource.getString(R.string.the);
        sayam        = resource.getString(R.string.sayam);
        madhya       = resource.getString(R.string.Madhya);
        rathri       = resource.getString(R.string.rathri);
        full         = resource.getString(R.string.full);
        udaya        = resource.getString(R.string.udayam);
        adhika       = resource.getString(R.string.adhika);
        kshaya       = resource.getString(R.string.kshaya);
    }
    public String[] getPanchangNotify(int year , int month, int dayOfMonth, int tzoff){

        LoadStringResources();
        double dTzoffset = (double) tzoff/3600000;
        String str ;
        String[] strarr = new String[2];
        double[] dbl = Swlib.WritePanchang(year,month, dayOfMonth, dTzoffset);
        double sunrise = dbl[7];
        strarr[0] = LunarMonth(dbl[13], year,month+1);
        str  = ThithiToString((int)dbl[0]);
        str += HourToString(dbl[1],sunrise);
        str+="\n";
        if( dbl[6]!= -1.1111)
        { str += ThithiToString((int)dbl[0]+1); str += HourToString(dbl[6],sunrise); str+=kshaya+ "\n" ;}
        if(dbl[3]>=sunrise)
        {
            str += NakshatraToString((int)dbl[2]);str += HourToString(dbl[3],sunrise);str+="\n";
        }
        else
        { str += NakshatraToString((int)dbl[2]+1); str += HourToString(dbl[11],sunrise);
            str+="\n";}
           strarr[1] = str;
        return strarr;


    }
    public String ShowPanchang(int year , int month, int dayOfMonth, int tzoff){
        LoadStringResources();

        double dTzoffset = (double) tzoff/3600000;

        String str ;//= String.valueOf(dayOfMonth);
        double[] dbl = Swlib.WritePanchang(year,month, dayOfMonth, dTzoffset);

        double sunrise = dbl[7];
        double sunset = dbl[8];

        //int nk_today=-1;
        str = LunarMonth(dbl[13], year,month+1);str+="\n";

        str += ThithiToString((int)dbl[0]);
        str += HourToString(dbl[1],sunrise);
        str+="\n";
        if( dbl[6]!= -1.1111)
        { str += ThithiToString((int)dbl[0]+1); str += HourToString(dbl[6],sunrise); str+=kshaya+ "\n" ;}
        // if first nk ends before sunrise, next one is the nk for the day, in this case no kshaya nk
        // if first nk end after sunrise it is nk for the day , if next nk is shorter than next sunrise,
        // it is kshaya and shown for the current day.
        if(dbl[3]>=sunrise)
        {
            str += NakshatraToString((int)dbl[2]);str += HourToString(dbl[3],sunrise);str+="\n";
            if( dbl[11]<24+sunrise)
            { str += NakshatraToString((int)dbl[2]+1); str += HourToString(dbl[11],sunrise);
                str+= kshaya+ "\n" ;}
        }
        else
        { str += NakshatraToString((int)dbl[2]+1); str += HourToString(dbl[11],sunrise);
            str+="\n";}
        str += YogaToString((int)dbl[4]);
        str += HourToString(dbl[5],sunrise);
        str += "\n" + suryodayam;
        str += HourToString(dbl[7],0);
        str += "-" + HourToString(dbl[8],0);  //sunset
        str += getStringVarjyam(dbl, sunrise);
        str += getStringDurmuhurthaAndRahukalam(dbl, sunrise, sunset);

        // str+="\n"+ String.format("%f",dbl[10]) +","
        //      + String.format(":%4.2f", dbl[3]) +","
        //      +  String.format(":% 4.2f",dbl[11]) ;
        //       String.format(":%4.2f", dblVarjyam1)+
        //      String.format(":%4.2f",dblVarjyam2);
        //////////////

        ////////////////////
        return str;
    }

    private String getStringDurmuhurthaAndRahukalam(double[] dbl, double sunrise, double sunset) {
        String str = "";
        double durmuhurtha1 = 0; double durmuhurtha2 = 0; double rhkalam=0;
        int weekday = (int) dbl[9];
        double muhurtha = (dbl[8]-dbl[7])/15;
        switch (weekday)
        {
            case 0:    //mon
                durmuhurtha1 =sunrise + muhurtha *8; //9th muhurtha
                durmuhurtha2 = sunrise + muhurtha * 11 ; //12th muhurtha
                rhkalam = sunrise  + 2.0 ;
                break; //mon
            case 1:
                durmuhurtha1 =sunrise + muhurtha *3; //9th muhurtha
                durmuhurtha2 = sunset + (1.6 - muhurtha) * 6 ; //12th muhurtha
                rhkalam = sunrise  + 9.5 ;

                break; //tue
            case 2:
                durmuhurtha1 =sunrise + muhurtha *7; //7th muhurtha

                rhkalam = sunrise  + 6.5 ;

                break;  //wed
            case 3:
                durmuhurtha1 =sunrise + muhurtha *5; //5th muhurtha
                durmuhurtha2 = sunrise + muhurtha * 11 ; //11th muhurtha
                rhkalam = sunrise  + 8.0 ;

                break;  //thu
            case 4:
                durmuhurtha1 =sunrise + muhurtha *3; //3rd muhurtha
                durmuhurtha2 = sunrise + muhurtha * 8 ; //8th muhurtha
                rhkalam = sunrise  + 5.0 ;

                break;  //fri
            case 5:
                durmuhurtha1 =sunrise ; //1st muhurtha
                durmuhurtha2 = sunrise + muhurtha ; //2nd muhurtha
                rhkalam = sunrise  + 3.5 ;

                break;  //sat
            case 6:
                durmuhurtha1 =sunrise + muhurtha *13; //9th muhurtha
                rhkalam = sunrise  + 11.0 ;
                break;  //sun

        }
        str += "\n"+durmuhurtha;
        str += HourToString(durmuhurtha1,0);
        str += ","+HourToString(durmuhurtha2,0);
        str += "\n" + rahukala ;
        str += HourToString(rhkalam,0) + "-" + HourToString(rhkalam +1.6,0);
        return str;
    }

    private String getStringVarjyam(double[] dl, double sunrise) {
        String str = "";
        double nksStart = dl[10];
        double nextLength = dl[11]- dl[3];
        double dblVarjyamoffset=0;
        double dblVarjyam1=0;
        double dblVarjyam2=0;
        double dblVarjyam_moola=0;
        double dblVarjyam_moola1=0;
        double dblVarjyam_moola2=0;
        int loop=0;
        int nk = (int) dl[2];
        double nkLength = dl[3]- dl[10];


        //varjyam start
        while(loop<2) {
            loop++;
            if(nk>=27) nk =0;
            switch (nk) {
                case 0: //aswini
                    dblVarjyamoffset = 20.0;
                    break;
                case 1:  //bharani
                case 19:
                case 25:
                    dblVarjyamoffset = 9.6;
                    break;
                case 2:
                case 6:
                case 9:
                case 26:
                    dblVarjyamoffset = 12.0;
                    break;
                case 3:
                    dblVarjyamoffset = 16.0;
                    break;
                case 4:
                case 14:
                case 15:
                case 17:
                    dblVarjyamoffset = 5.6;
                    break;
                case 5:
                case 12:
                    dblVarjyamoffset = 8.4;
                    break;
                case 7:
                case 10:
                case 13:
                case 20:
                    dblVarjyamoffset = 8.0;
                    break;
                case 8:
                    dblVarjyamoffset = 12.8;
                    break;
                case 11:
                case 23:
                    dblVarjyamoffset = 7.2;
                    break;
                case 16:
                case 21:
                case 22:
                    dblVarjyamoffset = 4.0;
                    break;
                case 18:
                    dblVarjyamoffset = 22.4;
                    dblVarjyam_moola = 8.0;
                    break;
                case 24:
                    dblVarjyamoffset = 6.4;
                    break;
            }

            if(loop==1) {
                dblVarjyam1 = nksStart+dblVarjyamoffset*(nkLength/24);
                if(dblVarjyam_moola!=0) dblVarjyam_moola1 = nksStart + dblVarjyam_moola*(nkLength/24);
                nksStart = dl[3]; nk = (int) dl[2]+1;

            }
            else {
                dblVarjyam2 = nksStart + dblVarjyamoffset * (nextLength / 24);
                if(dblVarjyam_moola!=0) dblVarjyam_moola2 = nksStart + dblVarjyam_moola * (nextLength / 24);
            }

        }

        str += "\n" + strVarjyam ;
        if( (int) dl[2]==18 && dblVarjyam_moola1 >sunrise && (int) nkLength!=0 )str += HourToString(dblVarjyam_moola1,sunrise) + ";" ;
        if(dblVarjyam1 >sunrise && (int) nkLength!=0) str += HourToString(dblVarjyam1,sunrise) + ";" ;

        if ((int) dl[2] == 17 && dblVarjyam_moola2 < 24+sunrise)
            str += HourToString(dblVarjyam_moola2, sunrise) + ";";
        if (dblVarjyam2 < 24+sunrise && (int) nextLength != 0)
            str += HourToString(dblVarjyam2, sunrise);
        return str;
    }

    private String LunarMonth(double v, int greg_year, int greg_month) {
        int lunar = (int)v;
        int hYear = greg_year - 1867;
        String strReturn;
        hYear %= 60;
        if(greg_month<=4 && lunar<=11 && lunar!=0) hYear-=1;
        strReturn = strHinduYears[hYear] + " -  ";
        if ((v-lunar)==0) {
            strReturn += strLunarMonths[(int) v];
        }
        else {
            strReturn +=  adhika + strLunarMonths[(int) v + 1];
        }

        return strReturn;
    }
    String ThithiToString(int thithi){
        String dispThithi    = thithiPrefix + space+space;
        if(thithi==30) thithi=0;
        if (thithi>14) { dispThithi += bahula ;
            if (thithi==29) thithi++;
            thithi-=15; }
        else dispThithi  += sukla;

        return dispThithi + strThithi[thithi] +space;

    }
    String HourToString(double hrs, double sunrise){


        String strPrefix = "";
        if(sunrise!=0)
            if(hrs>24+sunrise) return full;

        if(hrs>=24) hrs-=24;
        if (hrs>=3 && hrs<sunrise){strPrefix=thela;}
        if (hrs>=sunrise && hrs<12) {strPrefix = udaya; }
        if (hrs>=12 && hrs<16) {strPrefix = madhya; }
        if (hrs>=16 && hrs<19) {strPrefix = sayam; }
        if (hrs>=19 || hrs<3 ){strPrefix = rathri; }
        if (hrs>13 ) hrs-=12;
        double min = hrs*60;
        String strhrs =strPrefix + (int) min / 60;
        strhrs += ":";
        strhrs += String.format("%02d",(int)min%60);



        return strhrs;
    }
    String NakshatraToString(int nak){

        return strNak +space + strNaks[nak]+ space;


    }
    String YogaToString(int yoga){
        return strYoga + space+ strYogas[yoga] + space;
    }

}
