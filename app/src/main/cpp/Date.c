//
// Created by pdmurty on 5/27/2020.
//

#include <jni.h>
#include <stdbool.h>
#include "swedate.h"
#include "swephexp.h"
#include "swehouse.h"

JNIEXPORT jdouble JNICALL
Java_com_example_mycalender_Swlib_GetJulDay(JNIEnv *env, jobject thiz, jint year, jint month,
                                            jint day, jdouble hours) {
    int m_day[] = {0/*J*/,31/*F*/,59/*Mr*/,90/*Ap*/,120/*My*/,
                   151/*Jn*/,181/*Jl*/,212/*au*/,243/*se*/,273/*oc*/,304/*N*/,334/*D*/};
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
    return swe_julday(year,month,day,hours,1);
}

JNIEXPORT jstring JNICALL
Java_com_example_mycalender_Swlib_SWeCalUT(JNIEnv *env, jclass clazz, jdouble tjd, jint ipl,
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
   // (*env)->GetStringRegion(env, err, 0, 255, (jchar *) error);
}

JNIEXPORT void JNICALL
Java_com_example_mycalender_Swlib_SWSetSidmode(JNIEnv *env, jclass clazz, jint sidmode, jdouble t,
                                               jdouble ayana_t) {
    // TODO: implement SWSetSidmode()
    swe_set_sid_mode(sidmode,t,ayana_t );
}
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
double get_nak_end( double jd, int target_nak)
{

    double mlon, mspeed;
    double xx[6];
    int prev_target = target_nak;
    int count=0;
    double ddif = 0, dif=0;
    char err[255];




    do
    {
        swe_calc_ut(jd+ddif, SE_MOON,
                SEFLG_MOSEPH | SEFLG_SPEED | SEFLG_SIDEREAL,
                xx, err);
        mlon = xx[0]; mspeed = xx[3];
        if (count == 0)
        {
            target_nak = (int)mlon * 3 / 40;
            if (target_nak == prev_target + 2)
            { target_nak = prev_target + 1; ddif = -1; count++;
                //dif = (target_nak + 1) * 40.0 / 3 - mlon;
                //    continue;
            }
        }

        dif = (target_nak+1)*40.0/3- mlon;
        if(target_nak==26 && dif >13) dif -= 360;

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

double get_thithiend( double jd, int target)
{

    double slon, mlon, sspeed, mspeed;
    int iTarget;
    double xx[6] ;
    double ddif=0,dif=0;
    int prev_target = target;
    char err[255];

    int count=0;
    ddif = 0; // (target * 12 - ddif) / (mspeed - sspeed);  // longitude diff. converted to JDs

    do
    {
        int errn = swe_calc_ut(jd+ ddif , SE_SUN,
                SEFLG_MOSEPH | SEFLG_SPEED | SEFLG_SIDEREAL,
                xx, err);
        slon = xx[0]; sspeed = xx[3];
        errn = swe_calc_ut(jd+ddif, SE_MOON,
                SEFLG_MOSEPH | SEFLG_SPEED | SEFLG_SIDEREAL,
                xx, err);
        mlon = xx[0]; mspeed = xx[3];

        dif = (mlon-slon);
        if (count == 0 ){  // get target thithi first loop
            if (dif < 0)
                iTarget = (int)(dif +360) / 12 + 1;
            else
                iTarget = (int)(dif ) / 12 + 1;
            if (iTarget == prev_target + 2)
            {
                iTarget = prev_target + 1; ddif = -1; count++; continue; }
        }

        if (dif < 0 && iTarget != 30) dif += 360;

        if (iTarget !=30)
            dif = (double)iTarget*12.0-dif;
        else dif= -dif;

        dif /=(mspeed-sspeed);
        ddif+=dif;

        count++;
    }while( (dif>0.0001 || dif<-0.0001) && count <20);
     ddif*=24;
     //*target = iTarget;
    return ddif;

}
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
double get_thithi_pradhama(double jd)
{
    int target=0;
    double ddif = 0;
    ddif= get_thithi_tgt(jd,target);
    return ddif/24;

}

double get_thithi_amantha( double jd)
{

    double ddif = 0;
    int target = 29;
    ddif= get_thithi_tgt(jd,target);
    return ddif/24;

}
double get_sankranthi_day( double jdn, int *tgt_sak)
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
    }while( (dif>0.0001 || dif<-0.0001) && count <20);
    *tgt_sak = next_sak;
    //ddif*=24;
    return ddif;

}
void get_all_sankaranthis(int year, double* darraydays)
{
    double jdn, tmpdays;
    jdn = swe_julday(year,1,0,0, SE_GREG_CAL);
    int sankranthi;

    for (int i=0; i<12 ; ++i) {
        tmpdays = get_sankranthi_day(jdn, &sankranthi) ;
        jdn += tmpdays;
        darraydays[i] = jdn;//tmpdays;
    }

}
void get_all_LunarMonths(int year, double* darraydays)
{
    double jdn;

    jdn = swe_julday(year,1,0,0, SE_GREG_CAL);

    for (int i=0; i<12 ; ++i){
        darraydays[i] = get_thithi_tgt(jdn,29)*24 +jdn;
        jdn = darraydays[i];
    }

}
JNIEXPORT jdoubleArray JNICALL
Java_com_example_mycalender_Swlib_WritePanchang(JNIEnv *env, jclass clazz, jint year, jint month,
                                                jint day, jdouble timezone) {
    // TODO: implement WritePanchang()
    double jdn,jdn_prathama,jdn_amantha, jdn_sank=0, retjdn[1], xx[14], slon,mlon,dif;
    double geopos[3];
    int thithi=0 , yog;
    int sankranthi =0;
    char err[255];
    char starname[30];
    double nkStart, nextLength, thithiEnd, thithiNext;
    double sunrise, sunset;
    int prev_thithi =0;
    swe_set_ephe_path("");
    //get julian-day
    jdn = swe_julday(year,month+1,day,0, SE_GREG_CAL);
    swe_calc_ut(jdn, SE_SUN,SEFLG_MOSEPH | SEFLG_SPEED | SEFLG_SIDEREAL,xx, err);
    slon = xx[0];
    swe_calc_ut(jdn, SE_MOON,SEFLG_MOSEPH | SEFLG_SPEED | SEFLG_SIDEREAL,xx, err);
    mlon = xx[0];
    //sunrise
    geopos[0] = 78.45;
    geopos[1] = 17.4333;
    geopos[2] = 0;
    swe_rise_trans(jdn, SE_SUN , starname,0,  SE_CALC_RISE, geopos, 0, 0, retjdn, err);
    //sunset
    sunrise= xx[7]= (retjdn[0] - jdn )*24.0 + timezone;
    swe_rise_trans(jdn, SE_SUN , starname,0,  SE_CALC_SET|SE_BIT_HINDU_RISING, geopos, 0, 0, retjdn, err);
    sunset =xx[8]= (retjdn[0] - jdn )*24.0 + timezone;  // sunset
    double d = (xx[8]-xx[7])/15;
    thithi = (int) (jdn+1)%7;  //weekday mon=0
    xx[9] = (double) thithi;  // weekday


    dif = (mlon-slon);
    //do thithi
    if (dif < 0) {
    thithi = (int)(dif+360) / 12 ; }
    else thithi = (int)(dif) / 12 ;
    prev_thithi=thithi;
    thithiEnd = get_thithi_tgt(jdn, thithi)+timezone;
    thithiNext = get_thithi_tgt(jdn, prev_thithi+1)+timezone;
    if (thithiEnd >= sunrise) {
        xx[0] = thithi; xx[1] = thithiEnd ;
    }else {xx[0] = thithi+1; xx[1] = thithiNext ;}
    //kshaya thithi
    if (thithiEnd > sunrise && thithiNext < 24 + sunrise) {
       xx[6] = thithiNext;

    } else xx[6] = -1.1111; // let caller know there is thithi kshaya

    // sankranthi and lunar month
    jdn_prathama = get_thithi_pradhama(jdn);
    jdn_sank     = get_thithi_amantha(jdn +jdn_prathama-2); // get start of pradhama
    jdn_prathama = jdn +jdn_prathama-2 + jdn_sank;

    jdn_sank = get_sankranthi_day( jdn_prathama, &sankranthi) + jdn_prathama;
    xx[13]= sankranthi%12;
    xx[12]  = get_thithi_amantha(jdn);
    if(jdn_sank > xx[12]+jdn) xx[13]-=0.5;

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




    jdoubleArray jdbl = (*env)->NewDoubleArray(env,14);
    (*env)->SetDoubleArrayRegion(env,jdbl,0,14,xx);



    return jdbl;
  }


