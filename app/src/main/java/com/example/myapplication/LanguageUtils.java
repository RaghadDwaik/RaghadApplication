package com.example.myapplication;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;

import java.util.Locale;

public class LanguageUtils {
    public static void setAppLanguage(Context context, String languageCode) {
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        Locale locale = new Locale(languageCode);
        configuration.setLocale(locale);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    }
}
