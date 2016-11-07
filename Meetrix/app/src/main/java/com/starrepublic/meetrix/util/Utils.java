package com.starrepublic.meetrix.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;


/**
 * Created by admin on 2015-08-09.
 */
public class Utils {



    public static int dpToPx(Context context, float dp){
        return (int)(context.getResources().getDisplayMetrics().density*dp);
    }

    public static int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int result = 0;
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId);
        }
        return result;
    }


    public static int parseColor(String value) {
        if (value == null) {
            return -1;
        } else {
            try {
                return Color.parseColor(value);
            } catch (IllegalArgumentException e) {
                return -1;
            }
        }
    }
    public static String millisToString(long l) {
        long h, m;
        h = l / 3600000;
        m = (l % 3600000) / 60000;
        if (h == 0) {
            return m + "m";
        } else {
            return h + "h " + m + "m";
        }
    }

    public static int getNavigationBarHeight(Context context){
        Resources resources = context.getResources();

        int id = resources.getIdentifier(
                resources.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ? "navigation_bar_height" : "navigation_bar_height_landscape",
                "dimen", "android");
        if (id > 0) {
            return resources.getDimensionPixelSize(id);
        }
        return 0;
    }

    public static int getActionBarHeight(Context context) {
        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
                new int[]{android.R.attr.actionBarSize});
        int actionBarSize = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();
        return actionBarSize;
    }



    public static String dateToRelativeString(Date date, String strTomorrow, String strYesterday) {



        long today = new Date().getTime();
        today -= today%86400000;

        long due = date.getTime();
        due -= due%86400000;

        long diff = ((due-today)/86400000);


        String relative = "";

        if(diff == 1){
            relative = strTomorrow + " ";
        }else if(diff == -1){
            relative = strYesterday + " ";
        }else if(diff != 0){
            //relative = Constants.DATE_FORMAT_DATE.format(due) + " ";
        }

        return relative;
    }

    public static Date removeTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * <p>Intent to show an applications details page in (Settings) com.android.settings</p>
     *
     * @param context       The context associated to the application
     * @param packageName   The package name of the application
     * @return the intent to open the application info screen.
     */
    public static Intent newAppDetailsIntent(Context context, String packageName) {

        if(packageName==null){
            packageName = context.getApplicationContext().getPackageName();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse("package:" + packageName));
            return intent;
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.FROYO) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setClassName("com.android.settings",
                    "com.android.settings.InstalledAppDetails");
            intent.putExtra("pkg", packageName);
            return intent;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClassName("com.android.settings",
                "com.android.settings.InstalledAppDetails");
        intent.putExtra("com.android.settings.ApplicationPkgName", packageName);
        return intent;
    }
}
