package com.udl.android.bloodpressuremonitor.utils;

import android.content.Context;

import java.util.Locale;

/**
 * Created by adrian on 18/3/15.
 */
public class Language {

    public static void changeApplicationLanguage(String lang, Context context){

        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = locale;
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }

    public static String checkAppLanguage(){

        return Locale.getDefault().getLanguage();
    }

    public static boolean isEqualsTo(String lang){

        if (!lang.equalsIgnoreCase(Language.checkAppLanguage())){
            return false;
        }

        return true;
    }

    public static String getAppCompleteStringLanguage(boolean ignoreCase){

        if (ignoreCase){
            return Locale.getDefault().getDisplayLanguage().toLowerCase();
        }
        return Locale.getDefault().getDisplayLanguage();
    }
}
