package com.pdmurty.mycalender;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Locale;

public class LocalManager {

    static final String TELUGU = "te";
    /**
     * SharedPreferences Key
     */
    private static final String LANGUAGE_KEY = "lan_style";

    /**
     * set current pref locale
     */
    public static void setLocale(Context mContext) {
        updateResources(mContext, getLanguagePref(mContext));
    }

    /**
     * Set new Locale with context
     */
    public static Context setNewLocale(Context mContext, String language) {
        setLanguagePref(mContext, language);
        return updateResources(mContext, language);
    }

    /**
     * Get saved Locale from SharedPreferences
     *
     * @param mContext current context
     * @return current locale key by default return english locale
     */
    public static String getLanguagePref(Context mContext) {
        SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        return mPreferences.getString(LANGUAGE_KEY, TELUGU);
    }

    /**
     * set pref key
     */
    private static void setLanguagePref(Context mContext, String localeKey) {
        SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        mPreferences.edit().putString(LANGUAGE_KEY, localeKey).apply();
    }

    /**
     * update resource
     */
    public static Context updateResources(Context context, String language) {

       // String lang = Locale.getDefault().getLanguage();

        Locale locale = new Locale(language);
        Locale.setDefault(locale);
       // lang = Locale.getDefault().getLanguage();
        Context appctxt = context.getApplicationContext();
        Resources res = context.getResources();
        Configuration config = new Configuration(res.getConfiguration());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N /* 17*/) {
            config.setLocale(locale);
            context = appctxt.createConfigurationContext(config);

        } else {

            config.locale = locale;


        }
        res.updateConfiguration(config, res.getDisplayMetrics());
        return context;
    }

    /**
     * get current locale
     */
    public static Locale getLocale(Resources res) {
        Configuration config = res.getConfiguration();
        return Build.VERSION.SDK_INT >= 24 ? config.getLocales().get(0) : config.locale;
    }
}
