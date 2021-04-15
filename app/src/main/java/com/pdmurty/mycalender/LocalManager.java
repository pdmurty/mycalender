package com.pdmurty.mycalender;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Locale;

import static android.util.Log.d;

public class LocalManager {

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({ ENGLISH, HINDI, TELUGU })
    public @interface LocaleDef {
        String[] SUPPORTED_LOCALES = { ENGLISH, HINDI, TELUGU };
    }

    static final String ENGLISH = "en";
    static final String HINDI = "hi";
    static final String TELUGU = "te";

    /**
     * SharedPreferences Key
     */
    private static final String LANGUAGE_KEY = "lan_style";

    /**
     * set current pref locale
     */
    public static Context setLocale(Context mContext) {
        return updateResources(mContext, getLanguagePref(mContext));
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
        Log.d("LM", "RES-lan="+language);
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
       // lang = Locale.getDefault().getLanguage();

        Resources res = context.getResources();
        Configuration config = new Configuration(res.getConfiguration());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N /* 17*/) {
            config.setLocale(locale);
            context = context.createConfigurationContext(config);

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
