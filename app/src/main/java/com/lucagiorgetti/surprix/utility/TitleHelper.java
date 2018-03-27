package com.lucagiorgetti.surprix.utility;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.Spanned;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.lucagiorgetti.surprix.R;
import com.lucagiorgetti.surprix.views.MainActivity;
import com.lucagiorgetti.surprix.views.OnboardActivity;

/**
 * Utility which contain all the implementations of methods which needs a connection with Firebase Database.
 *
 * Created by Luca on 13/11/2017.
 */

public class TitleHelper {
    public static void setProducerTitle(ActionBar supportActionBar) {
        if (supportActionBar != null){
            supportActionBar.setTitle(R.string.producer);
        }
    }

    public static void setSetSearchTitle(ActionBar supportActionBar, String prodName, int year) {
        if (supportActionBar != null){
            supportActionBar.setTitle(prodName + " - "+ year);
        }
    }

    public static void setSetItemsTitle(ActionBar supportActionBar, String setName) {
        if (supportActionBar != null){
            supportActionBar.setTitle(setName);
        }
    }

    public static void setDoublesTitle(Context context, ActionBar supportActionBar, int number) {
        if (supportActionBar != null){
            String title;
            if (number > 0){
                title = context.getResources().getString(R.string.doubles) + " (" + number + ")";
            } else {
                title = context.getResources().getString(R.string.doubles);
            }
            supportActionBar.setTitle(title);
        }
    }

    public static void setMissingsTitle(Context context, ActionBar supportActionBar, int number) {
        if (supportActionBar != null){
            String title;
            if (number > 0){
                title = context.getResources().getString(R.string.missings) + " (" + number + ")";
            } else {
                title = context.getResources().getString(R.string.missings);
            }
            supportActionBar.setTitle(title);
        }
    }

    public static void setYearTitle(ActionBar supportActionBar, String producerName) {
        if (supportActionBar != null){
            supportActionBar.setTitle(producerName);
        }
    }

    public static void setSettingsTitle(Context context, ActionBar supportActionBar) {
        if (supportActionBar != null){
            supportActionBar.setTitle(context.getResources().getString(R.string.settings));
        }
    }

    public static void setThanksTitle(Context context, ActionBar supportActionBar) {
        if (supportActionBar != null){
            supportActionBar.setTitle(context.getResources().getString(R.string.thanks_to_title));
        }
    }
}