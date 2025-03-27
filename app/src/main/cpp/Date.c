//
// Created by pdmurty on 5/27/2020.
//

#include <jni.h>
#include <stdbool.h>
#include<android/log.h>
#include "sqlite3.h"
#include "swedate.h"
#include "swephexp.h"
#include "swehouse.h"

static float  mloc_lon=78.45;  // default location set to HYD, INDIA
static float   mloc_lat=17.4333;
double get_nak_tgt( double jd, int target_nak)
{

    double mlon, mspeed;
    double xx[6];
    int prev_target = target_nak;
    int count=0;
    double ddif = 0, dif=0;
    char err[255];
    int cur_nak;
    target_nak %=27;
    if(target_nak<0) target_nak +=27;

    do
    {
        swe_calc_ut(jd+ddif, SE_MOON,
                    SEFLG_MOSEPH | SEFLG_SPEED | SEFLG_SIDEREAL,
                    xx, err);
        mlon = xx[0]; mspeed = xx[3];
        cur_nak = (int)mlon * 3/40 ;
        dif = (double)(target_nak+1) * 40 / 3 - mlon;
        if (target_nak ==26 &&  cur_nak==0) dif = -mlon;
        else if (cur_nak == 26 && target_nak == 0) dif += 360;

        dif /=mspeed;
        ddif+=dif;
        count++;
    }while( (dif>0.0001 || dif<-0.0001) && count <20);

    ddif*=24;
    return ddif;

}

double get_yoga_end( double jd, int yog )
{

    double slon, mlon, sspeed, mspeed;
    double xx[6] ;
    double ddif,dif,lon;
    char err[255];
    int count=0;
    ddif = 0 ;


    do
    {


        int errn = swe_calc_ut(jd + ddif, SE_SUN,
                SEFLG_MOSEPH | SEFLG_SPEED | SEFLG_SIDEREAL,
                xx, err);
        slon = xx[0]; sspeed = xx[3];
        errn = swe_calc_ut(jd + ddif, SE_MOON,
                SEFLG_MOSEPH | SEFLG_SPEED | SEFLG_SIDEREAL,
                xx, err);
        mlon = xx[0]; mspeed = xx[3];
        lon = (mlon+slon);
        if (lon>=360.0) lon-=360;
        if (count==0) yog =(int) lon * 3 / 40;

        dif = (yog+1)*40.0/3 -lon ;

        if(yog==26 && dif >13) dif -= 360;

        //err = fabs(target-ddif);
        dif /=(mspeed+sspeed);
        ddif+=dif;
        count++;
    }while( (dif>0.0001 || dif<-0.0001) && count <20);

    ddif*=24;
    return ddif;

}

//gets target thithi after given julian-day in hrs
double get_thithi_tgt( double jd, int target)
{

    double slon, mlon, sspeed, mspeed;
    int iTarget;
    double xx[6] ;
    double ddif=0,dif=0,target_pos;
    int prev_target = target;
    char err[255];
    double cur_thithi;

    int count=0;
    ddif = 0;
    target_pos = 12.0 * (target + 1);
    do
    {
        int errn = swe_calc_ut(jd+ ddif , SE_SUN,SEFLG_MOSEPH | SEFLG_SPEED | SEFLG_SIDEREAL, xx, err);
        slon = xx[0]; sspeed = xx[3];
        errn = swe_calc_ut(jd+ddif, SE_MOON,SEFLG_MOSEPH | SEFLG_SPEED | SEFLG_SIDEREAL,xx, err);
        mlon = xx[0]; mspeed = xx[3];

//relative co-ords
        mlon -= slon;
        if (mlon < 0) mlon += 360;
        slon = 0;
        mspeed -= sspeed;
        if (count == 0)
        {
            dif = target_pos - mlon;
            if (target_pos == 372) { target_pos = 12; }
        }
        else
        {
            if (mlon > 0 && mlon < 90 && target_pos == 360) dif = -mlon;
            else dif = target_pos - mlon;
            if (mlon > 270 && mlon < 360 && target_pos > 0 && target_pos < 90) dif += 360;
        }

        dif /= mspeed;
        ddif += dif;
// relative
        count++;
    }while( (dif>0.0001 || dif<-0.0001) && count <20);
    ddif*=24;

    return ddif;

}
// get prathama after given julian-day in days
double get_thithi_pradhama(double jd)
{
    int target=0;
    double ddif = 0;
    ddif= get_thithi_tgt(jd,target);
    return ddif/24;

}

// get amavasya after given julian day in days
double get_thithi_amantha( double jd)
{

    double ddif = 0;
    int target = 29;
    ddif= get_thithi_tgt(jd,target);
    return ddif/24;

}
// get next-sankarthi day after given jdn
double get_sankranthi_day( double jdn, int *tgt_sak /*sankranthi count.*/)
{
    double  xx[6], slon,sspeed,dif=0,ddif=0;
    char err[255];
    int cur_sak,count=0, next_sak;

    swe_calc_ut(jdn, SE_SUN,SEFLG_MOSEPH | SEFLG_SPEED | SEFLG_SIDEREAL,xx, err);
    slon = xx[0]; sspeed = xx[3];
    next_sak = (int)slon/30 +1;
    do
    {
        swe_calc_ut(jdn + ddif, SE_SUN,
                    SEFLG_MOSEPH | SEFLG_SPEED | SEFLG_SIDEREAL,
                    xx, err);
        slon = xx[0]; sspeed = xx[3];
        cur_sak = (int)slon/30 ;
        dif = (double)(next_sak)*30 - slon;
        if (next_sak ==12 &&  cur_sak==0) dif = -slon;
        else if (cur_sak == 12 && next_sak == 0) dif += 360;

        dif /=sspeed;
        ddif+=dif;
        count++;
      //  __android_log_print(ANDROID_LOG_DEBUG, "SANK", "slon=%f:count=%d\n",slon,count);
    }while( (dif>0.0001 || dif<-0.0001) && count <20);
    *tgt_sak = next_sak;
    //ddif*=24;
    //__android_log_print(ANDROID_LOG_DEBUG, "SANK", "retddif=%f\n",ddif);
    return ddif;

}
// get next-karthari day after given jdn, 27 kartharis in one full sun-cycle.
double get_karthari_day( double jdn, int *tgt_kar /*karthari count.*/) {
    double xx[6], slon, sspeed, dif = 0, ddif = 0;
    char err[255];
    int cur_kar, count = 0, next_kar;

    swe_calc_ut(jdn, SE_SUN, SEFLG_MOSEPH | SEFLG_SPEED | SEFLG_SIDEREAL, xx, err);
    slon = xx[0];
    sspeed = xx[3];
    next_kar = (int) (slon * 3 / 40) + 1;
    do {
        swe_calc_ut(jdn + ddif, SE_SUN,
                    SEFLG_MOSEPH | SEFLG_SPEED | SEFLG_SIDEREAL,
                    xx, err);
        slon = xx[0];
        sspeed = xx[3];
        cur_kar = (int) (slon * 3 / 40);
        dif = (double) (next_kar) * 40 / 3 - slon;
        if (next_kar == 27 && cur_kar == 0) dif = -slon;
        else if (cur_kar == 27 && next_kar == 0) dif += 360;

        dif /= sspeed;
        ddif += dif;
        count++;
        //  __android_log_print(ANDROID_LOG_DEBUG, "SANK", "slon=%f:count=%d\n",slon,count);
    } while ((dif > 0.0001 || dif < -0.0001) && count < 20);
    *tgt_kar = next_kar;
    //ddif*=24;
    //__android_log_print(ANDROID_LOG_DEBUG, "SANK", "retddif=%f\n",ddif);
    return ddif;

}


JNIEXPORT jdoubleArray JNICALL
Java_com_pdmurty_mycalender_Swlib_CalcSolarAndLunarEclipse(JNIEnv *env, jclass clazz, jint year,
                                                           jdouble timezone) {
    double juday;
    double tjuret[10];
    double attrs[20];
    char err[255];
    int ephflg=0;
    int flg=0;
    int arrSkip=0;
    int eclloop=0;
    int ecyear, month, day, weekday;
    double time;
    double lunarEclBeg, lunarEclEnd, eclStart;
    double out[40];
    double geopos[3];
    geopos[0] = mloc_lon; //78.45; // hard coded to be used from geoloc.
    geopos[1] = mloc_lat; //17.4333;
    geopos[2] = 0;
    juday = swe_julday(year,1,1,0,SE_GREG_CAL);

/*
 *  tjuret[0] time of maximum eclipse
    tjuret[1] time of first contact solar
    tjuret[2] time of second contact/ lunar partial begin
    tjuret[3] time of third contact /lunar partial phase end
    tjuret[4] time of forth contact / lunar totality begin
    tjuret[5] time of totality end - lunar
     tjuret[6] time of penumbral phase begin -lunar
     tjuret[7] time of penumbral phase end -lunar
     tjuret[8] time of moonrise, if it occurs during the eclipse - lunar
     tjuret[9] time of moonset, if it occurs during the eclipse - lunar

    retflag -1 (ERR) on error (e.g. if swe_calc() for sun or moon fails)
    SE_ECL_TOTAL or SE_ECL_ANNULAR or SE_ECL_PARTIAL or SE_ECL_VISIBLE, SE_ECL_PENUMBRAL for lunar
    */
    eclStart=juday;
    while(eclloop<2) {
        ephflg = swe_sol_eclipse_when_loc(eclStart, SEFLG_MOSEPH, geopos, tjuret, attrs, false, err);
        if ((tjuret[0] - juday) < 366) {
            swe_revjul(tjuret[1], SE_GREG_CAL, &ecyear, &month, &day, &time);
            if (time <= 12.0)
                weekday = (int) (tjuret[1] + 1) % 7;
            else weekday = (int) (tjuret[1]) % 7;
            if (ephflg & SE_ECL_TOTAL) flg = 1;
            if (ephflg & SE_ECL_ANNULAR) flg = 2;
            if (ephflg & SE_ECL_PARTIAL) flg = 3;
            arrSkip = eclloop*7;
            out[0+arrSkip] = flg;
            out[1+arrSkip] = ecyear;
            out[2+arrSkip] = month;
            out[3+arrSkip] = day;
            out[4+arrSkip] = weekday;
            out[5+arrSkip] = time + timezone;
            out[6+arrSkip] = time + timezone + (tjuret[4] - tjuret[1]) * 24;
            eclloop++;
            //  __android_log_print(ANDROID_LOG_DEBUG, "ECLSOL", "flg=%f:year=%f:month = %f:day=%f:weekday=%f:eclbeg=%f:eclend=%f \n",
            //                    out[0],out[1],out[2],out[3],out[4],out[5],out[6]);
        }else break;
        eclStart = tjuret[0] +1;
    }

    flg=0;
    eclStart=juday;
    while(eclloop<3) {
        ephflg = swe_lun_eclipse_when_loc(eclStart, SEFLG_MOSEPH, geopos, tjuret, attrs, false, err);

        if ((tjuret[0] - juday) < 367) {
            if (ephflg & SE_ECL_TOTAL) {
                flg = 1;
                lunarEclBeg = tjuret[4];
                lunarEclEnd = tjuret[5];
            }
            if (ephflg & SE_ECL_PENUMBRAL) {
                flg = 2;
                lunarEclBeg = tjuret[6];
                lunarEclEnd = tjuret[7];
            }
            if (ephflg & SE_ECL_PARTIAL) {
                flg = 3;
                lunarEclBeg = tjuret[2];
                lunarEclEnd = tjuret[3];
            }
            if (lunarEclBeg == 0) lunarEclBeg = tjuret[8];
            if (lunarEclEnd == 0) lunarEclEnd = tjuret[9];
            swe_revjul(lunarEclBeg, SE_GREG_CAL, &ecyear, &month, &day, &time);
            if (time <= 12.0)
                weekday = (int) (lunarEclBeg + 1) % 7;
            else weekday = (int) (lunarEclBeg) % 7;
            arrSkip = 14+eclloop*7;
            out[0+arrSkip] = flg;
            out[1+arrSkip] = ecyear;
            out[2+arrSkip] = month;
            out[3+arrSkip] = day;
            out[4+arrSkip] = weekday;
            out[5+arrSkip] = time + timezone;
            out[6+arrSkip] = time + timezone + (lunarEclEnd - lunarEclBeg) * 24;
            eclloop++;
            //    __android_log_print(ANDROID_LOG_DEBUG, "ECLLUN", "flg=%f:year=%f:month = %f:day=%f:weekday=%f:eclbeg=%f:eclend=%f \n",
            //                     out[0+arrSkip],out[1+arrSkip],out[2+arrSkip],out[3+arrSkip],out[4+arrSkip],out[5+arrSkip],out[6+arrSkip]);
        }else break;
        eclStart = tjuret[0] +1;
    }
    jdoubleArray jdbl = (*env)->NewDoubleArray(env,35);
    (*env)->SetDoubleArrayRegion(env,jdbl,0,35,out);
    return jdbl;


}

JNIEXPORT jdoubleArray JNICALL
Java_com_pdmurty_mycalender_Swlib_SWeCalcKarthariDays(JNIEnv *env, jclass clazz, jint year,
                                                      jdouble timezone) {
    double jdays[5*27];
    int Year, month, day, weekday, skip;
    int karthari=0;
    double time;
    double  jdn, tmpdays,tmpkr;
    jdn= swe_julday(year,1,1,0,SE_GREG_CAL);

    for(int i=0;i<27;++i) {
        // __android_log_print(ANDROID_LOG_DEBUG, "sank", "jd=%f:sank=%d",jdn,sankranthi);

        skip= 5*i;

        tmpkr = get_karthari_day(jdn, &karthari);
        jdn=jdn+tmpkr;

        swe_revjul(jdn, SE_GREG_CAL, &Year, &month, &day, &time);
        if (time<=12.0)
            weekday = (int)(jdn+1)%7;
        else weekday = (int)(jdn)%7;
        jdays[0+skip]= (double)weekday;
        jdays[1+skip]= (double)day;
        jdays[2+skip]= (double)time+timezone;
        jdays[3+skip]= (double)(karthari%27);
        jdays[4+skip]=  (double)month;

        // __android_log_print(ANDROID_LOG_DEBUG, "karthari", "jd=%f:kar=%d:y=%d:m=%d:d=%d:time=%f\n",jdn+tmpkr,karthari,Year,month,day,time);
        jdn++;
    }
    jdoubleArray jdbl = (*env)->NewDoubleArray(env,135);
    (*env)->SetDoubleArrayRegion(env,jdbl,0,135,jdays);
    return jdbl;

}


JNIEXPORT jdoubleArray JNICALL
Java_com_pdmurty_mycalender_Swlib_SWeCalcNextSankaranthi(JNIEnv *env, jclass clazz, jint year,
                                                         jint month, jdouble timezone) {
    double jdays[4];
    int Year, smonth, day, weekday;
    int sankranthi=0;
    double time;
    double  jdn, tmpdays,tmpkr;
    jdn= swe_julday(year,month+1,1,0,SE_GREG_CAL);


    tmpdays=get_sankranthi_day(jdn, &sankranthi);


    jdn =jdn+tmpdays;
    swe_revjul(jdn, SE_GREG_CAL, &Year, &month, &day, &time);
    if (time<=12.0)
        weekday = (int)(jdn+1)%7;
    else weekday = (int)(jdn)%7;
    jdays[0]=(double)weekday;
    jdays[1]=(double)day;
    jdays[2]=(double)time+timezone;
    jdays[3]=(double)(sankranthi%12);

    jdoubleArray jdbl = (*env)->NewDoubleArray(env,4);
    (*env)->SetDoubleArrayRegion(env,jdbl,0,4,jdays);
    return jdbl;
}

JNIEXPORT jint JNICALL
Java_com_pdmurty_mycalender_Swlib_SWeSqlitetest(JNIEnv *env, jclass clazz, jstring dbpath) {
    sqlite3 *db;
    jchar *result = "data base open success";
    jchar *resultfail = "data base open failed";
    jstring strret;
    int iresult;
    int ERROR_STATE =-1;
    //iresult =sqlite3_open(dbpath,&db);

    // strret = (*env)->NewString(env,result,20);
//else strret = (*env)->NewString(env,resultfail,30);

    char TAG[20] = "NATIVE_SQL";

    char *err = 0;
    // __android_log_print(ANDROID_LOG_DEBUG, TAG, "calling sqliteopen.%s" ,(*env)->GetCharArrayElements(env,dbpath,false));

    if(iresult=sqlite3_open(dbpath, &db)) {
        __android_log_print(ANDROID_LOG_DEBUG, TAG, "Error opened database.%s\n",sqlite3_errmsg(db));
    }

    return iresult;

}

JNIEXPORT jdoubleArray JNICALL
Java_com_pdmurty_mycalender_Swlib_CalcEphimeris(JNIEnv *env, jclass clazz, jint year, jint month,
                                                jint day, jdouble timezone) {
    double jdn, retjdn[1], xx[14], slon,sspeed,mlon,mspeed,meqlon,marlon,juplon,venlon,satlon,rahlon;
    char err[255];
    int weekday;
    jdn = swe_julday(year,month,day,0, SE_GREG_CAL);
    swe_calc_ut(jdn, SE_SUN,SEFLG_MOSEPH | SEFLG_SPEED | SEFLG_SIDEREAL,xx, err);
    slon = xx[0]; sspeed = xx[3];
    swe_calc_ut(jdn, SE_MOON,SEFLG_MOSEPH | SEFLG_SPEED | SEFLG_SIDEREAL,xx, err);
    mlon = xx[0]; mspeed = xx[3];
    swe_calc_ut(jdn, SE_VENUS,SEFLG_MOSEPH | SEFLG_SPEED | SEFLG_SIDEREAL,xx, err);
    venlon = xx[0];
    swe_calc_ut(jdn, SE_JUPITER,SEFLG_MOSEPH | SEFLG_SPEED | SEFLG_SIDEREAL,xx, err);
    juplon = xx[0];
    swe_calc_ut(jdn, SE_MARS,SEFLG_MOSEPH | SEFLG_SPEED | SEFLG_SIDEREAL,xx, err);
    marlon = xx[0];
    swe_calc_ut(jdn, SE_MERCURY,SEFLG_MOSEPH | SEFLG_SPEED | SEFLG_SIDEREAL,xx, err);
    meqlon = xx[0];
    swe_calc_ut(jdn, SE_MEAN_NODE,SEFLG_MOSEPH | SEFLG_SPEED | SEFLG_SIDEREAL,xx, err);
    rahlon = xx[0];
    swe_calc_ut(jdn, SE_SATURN,SEFLG_MOSEPH | SEFLG_SPEED | SEFLG_SIDEREAL,xx, err);
    satlon = xx[0];
    //Planet positions.
    xx[0] = slon; xx[1]=mlon;xx[2] = meqlon; xx[3]= venlon; xx[4]= marlon;
    xx[5]= juplon; xx[6]= satlon; xx[7]=rahlon;
    xx[10]= sspeed; xx[11]=mspeed; //speed of SUN and MOON
    weekday = (int) (jdn+1)%7;
    xx[8] = (double) weekday;
    jdoubleArray jdbl = (*env)->NewDoubleArray(env,14);
    (*env)->SetDoubleArrayRegion(env,jdbl,0,14,xx);
    return jdbl;

}

JNIEXPORT void JNICALL
Java_com_pdmurty_mycalender_Swlib_SetLocation(JNIEnv *env, jclass clazz, jfloat loc_lon,
                                              jfloat loc_lat) {
    mloc_lat = loc_lat;
    mloc_lon = loc_lon;

}

JNIEXPORT jdoubleArray JNICALL
Java_com_pdmurty_mycalender_Swlib_WritePanchang(JNIEnv *env, jclass clazz, jint year, jint month,
                                                jint day, jdouble timezone) {
    double jdn,jdn_prathama,jdn_prathama_start,jdn_amantha, jdn_sank=0, retjdn[1], xx[15], slon,mlon,dif;
    double geopos[3];
    double tmp;
    int wkday;
    int thithi=0 , yog;
    int sankranthi =0;
    char err[255];
    char starname[30];
    double nkStart, nextLength, thithiEnd, thithiNext;
    double sunrise, nextsunrise, sunset;
    int syear,smonth,sday;
    double shour;
    int prev_thithi =0;
    int errcode;
    //__android_log_print(ANDROID_LOG_DEBUG, "DATEC", "yr=%d:m=%d:d=%d:tz=%4.2f\n",year,month,day,timezone);

    swe_set_ephe_path("");
    //get julian-day at UT
    jdn = swe_julday(year,month+1,day,0, SE_GREG_CAL);

    // __android_log_print(ANDROID_LOG_DEBUG, "DATEC", "jdn=%f\n",jdn);

    swe_calc_ut(jdn, SE_SUN,SEFLG_MOSEPH | SEFLG_SPEED | SEFLG_SIDEREAL,xx, err);
    slon = xx[0];
    swe_calc_ut(jdn, SE_MOON,SEFLG_MOSEPH | SEFLG_SPEED | SEFLG_SIDEREAL,xx, err);
    mlon = xx[0];
    //__android_log_print(ANDROID_LOG_DEBUG, "DATEC", "lon=%f:lat=%f\n",mloc_lon,mloc_lat);

    //sunrise
    geopos[0] = mloc_lon; //78.45; // hard coded to be used from geoloc.
    geopos[1] = mloc_lat; //17.4333;
    geopos[2] = 0;

    double tjd = jdn - timezone/24;

    swe_rise_trans(tjd, SE_SUN , starname,0,  SE_CALC_RISE|SE_BIT_HINDU_RISING, geopos, 0, 0, retjdn, err);
    sunrise= xx[7]=(retjdn[0] - jdn )*24.0 + timezone;

    swe_rise_trans(tjd+1, SE_SUN , starname,0,  SE_CALC_RISE|SE_BIT_HINDU_RISING, geopos, 0, 0, retjdn, err);
    nextsunrise =(retjdn[0] - jdn-1 )*24.0 + timezone;
    //sunset
    swe_rise_trans(tjd, SE_SUN , starname,0,  SE_CALC_SET|SE_BIT_HINDU_RISING, geopos, 0, 0, retjdn, err);
    xx[8]= (retjdn[0] - jdn )*24.0 + timezone;  // sunset
    //double d = (xx[8]-xx[7])/15;
    wkday = (int) (jdn+1)%7;  //weekday mon=0
    xx[9] = (double) wkday;  // weekday
    dif = (mlon-slon);
    //do thithi
    if (dif < 0) {
        thithi = (int)(dif+360) / 12 ; }
    else thithi = (int)(dif) / 12 ;
    prev_thithi=thithi;
    thithiEnd = get_thithi_tgt(jdn, thithi)+timezone;
    thithiNext = get_thithi_tgt(jdn, prev_thithi+1)+timezone;
//    __android_log_print(ANDROID_LOG_DEBUG, "LUN", "thithi=%d;thithiend=%f;thithinxt=%f;SR=%f;NSR=%f\n",
    //        thithi,thithiEnd,thithiNext,sunrise,nextsunrise);
    if (thithiEnd > sunrise) { //thithi normal
        xx[0] = thithi; xx[1] = thithiEnd ;
    }else {xx[0] = thithi+1; xx[1] = thithiNext ;} // there is thithikshaya
    //kshaya thithi
    if (thithiEnd > sunrise && thithiNext <= 24.0 + nextsunrise) {
        xx[6] = thithiNext;

    } else xx[6] = -1.1111; // let caller know there is thithi kshaya
    // sankranthi and lunar month
    jdn_prathama = get_thithi_pradhama(jdn);  // end of prathama before the jdn
     //__android_log_print(ANDROID_LOG_DEBUG, "LUN", "jdn=%f:jdn_pra=%f\n",jdn,jdn_prathama);

    jdn_prathama_start     = get_thithi_amantha(jdn +jdn_prathama-2); // get start of pradhama,

    jdn_prathama = jdn +jdn_prathama-2 + jdn_prathama_start;  // start of pradhama
    //__android_log_print(ANDROID_LOG_DEBUG, "LUN", "jdn_prastrt=%f\n",jdn_prathama);
    jdn_sank = get_sankranthi_day( jdn_prathama, &sankranthi) + (jdn_prathama);

    // __android_log_print(ANDROID_LOG_DEBUG, "DATEC", "jdn_sank=%f: snk= %d\n",jdn_sank,sankranthi);
    // first sankranthi after pradhama start
    xx[13]= sankranthi%12; // lunar month
    xx[12]  = get_thithi_amantha(jdn); // immdetiate amavasya after jdn.
    //__android_log_print(ANDROID_LOG_DEBUG, "DATEC", "jdn_aman=%f\n",xx[12]);
    // no sankranthi between pradham and amavasya, it is adhikamasa
    // reduce the month by 0.5 to let the caller know.
    if(jdn_sank > xx[12]+jdn) xx[13]-=0.5;

    jdn_sank = get_sankranthi_day( jdn, &sankranthi); // next sankarathi time
    // when the day is pradhama lunar month decided from next sankranthi, panchang calculated at 5:30 AM IST for the day
    // in some cases where at that time it is not pradhama but by sunrise it is pradhama, lunar month is written as
    // prevoius one for the day.
    if(xx[0]==0 || xx[0]==30) xx[13] = sankranthi%12;

    double jdn_prev_sank= get_sankranthi_day( jdn+jdn_sank-32, &sankranthi); // prev sankarathi time;
    double solarmonthdays = 32-jdn_sank-jdn_prev_sank-timezone/24;
    double jdnp_sank = jdn+jdn_sank-32 + jdn_prev_sank;
    //jdnp_sank = round(jdnp_sank);
  //  __android_log_print(ANDROID_LOG_DEBUG, "DATEC", "jdn=%f:san_prev=%f:san_nxt=%f\n",jdn,jdnp_sank,jdn_sank);

    //solarmonthdays*=100;
   // xx[13]= sankranthi%12; // snakranthi number
    xx[12] = sankranthi;
   // xx[12]*=100;
    jdn_sank+=timezone/24;
    jdn_sank*=100;

    //if( jdn_sank <=1.0 )
    xx[12]*=10000;
    xx[12]+= (int)(round(jdn_sank)); //next sankranthi time
    xx[12]*=10000;
    xx[12]+=(int)(round(solarmonthdays*100)); // daycount from last sankranthi
   //__android_log_print(ANDROID_LOG_DEBUG, "DATEC", "jdn=%f:san_prev=%f:san_nxt=%f:dbl12:%f\n",jdn,solarmonthdays,jdn_sank,xx[12]);
    // do nakshatra
    int nkCount = (int) (mlon) * 3 / 40;

    double nkEnd = get_nak_tgt(jdn, nkCount)+timezone;
    nkStart      = get_nak_tgt(jdn, nkCount-1) + timezone;
    nextLength   = get_nak_tgt(jdn, nkCount+1) + timezone;
    xx[2]= nkCount;
    xx[3] = nkEnd;
    xx[10] = nkStart;
    xx[11] = nextLength;
    // do yoga
    slon = (mlon+slon);
    if (slon>=360.0) slon-=360;
    yog =(int) slon * 3 / 40;

    dif = get_yoga_end(jdn,yog);
    xx[4]= yog;
    xx[5]= dif+timezone;
    xx[14]=jdn;
    jdoubleArray jdbl = (*env)->NewDoubleArray(env,15);
    (*env)->SetDoubleArrayRegion(env,jdbl,0,15,xx);
// xx[]0-thithicount,1-thith-end-time,6-nxt-thithi-end or -1.1111,
// 2-nakcount,3-nak-end-time,10-nak-start,11-next-nak-length
// 4-yoga-count,5-yoga-end-time,
// 7-sunrise,8-sunset, 9-weekday,12-sankranthi-count+time,13-lunarmonth
    return jdbl;

}

JNIEXPORT void JNICALL
Java_com_pdmurty_mycalender_Swlib_SWSetSidmode(JNIEnv *env, jclass clazz, jint sidmode, jdouble t,
                                               jdouble ayana_t) {
    // TODO: implement SWSetSidmode()
    swe_set_sid_mode(sidmode,t,ayana_t );
}

JNIEXPORT jstring JNICALL
Java_com_pdmurty_mycalender_Swlib_SWeCalUT(JNIEnv *env, jclass clazz, jdouble tjd, jint ipl,
                                           jint iflag, jdoubleArray xx, jint err) {
    // TODO: implement SWeCalUT()
    jchar  error [255] ; //(*env)->GetCharArrayElements(env , err, NULL);

    jdouble *x = (*env)->GetDoubleArrayElements(env, xx, NULL);
    err = 255;
    swe_calc_ut(tjd  , ipl, iflag, x, error);
    //x[0]=5; x[1]=6; x[2]=7;x[3]=8;x[4]=9; x[5]=10;
    //(*env)->SetCharArrayRegion(env,err,0,200,error);

    (*env)->SetDoubleArrayRegion(env,xx,0,6,x);
    //(*env)->SetCharArrayRegion(env,err,0,255,error);
    return (*env)->NewStringUTF(env,error);

}

JNIEXPORT jdouble JNICALL
Java_com_pdmurty_mycalender_Swlib_GetJulDay(JNIEnv *env, jclass clazz, jint year, jint month,
                                            jint day, jdouble hours) {
    // TODO: implement GetJulDay()
    //int m_day[] = {0/*J*/,31/*F*/,59/*Mr*/,90/*Ap*/,120/*My*/,
      //             151/*Jn*/,181/*Jl*/,212/*au*/,243/*se*/,273/*oc*/,304/*N*/,334/*D*/};
    /*
    jboolean leapyear = JNI_FALSE;
    double jn= 2451545;  // JDN = 2451545 for year 2000AD
    jn += (year-2000)*365;
    int iy = (year-2000)/4;
    iy -= (year-2000)/100; // not leap year
    iy += (year-2000)/400;
    jn +=iy; // leap years
    jn += m_day[month-1];
    jn += day-1;
    jn += (hours-12)/24;
    if (year%4==0) leapyear = JNI_TRUE;
    if(year %100 == 0 && year%400!=0) leapyear = JNI_FALSE;

    if( month >2 && leapyear) ++jn; // leap year <2000
    else if (!leapyear && year >2000) ++jn;  // non-leapyears >2000
    // return jn;
     */
    return swe_julday(year,month,day,hours,1);
}