/*
 * Copyright (c) 2018
 */

package com.ejdash.esbn.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by ej on 2017-07-03.
 */
public class SharedPreferenceUtil {

    public static final String APP_SHARED_PREFS = "thisApp.SharedPreference";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private boolean idCheck = false;
    private String test;


    public SharedPreferenceUtil(Context context) {
        this.sharedPreferences = context.getSharedPreferences(APP_SHARED_PREFS, Activity.MODE_PRIVATE);
        this.editor = sharedPreferences.edit();
    }

    public void del (Context context, String key){

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().remove(key).apply();

    }

    public void setSharedData(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    public String getSharedData(String test) {
        String returnStr = sharedPreferences.getString(test, "");
        return returnStr; // "test"는 키, "defValue"는 키에 대한 값이 없을 경우 리턴해줄 값
    }

    public void valueCopy (String copyKey, String pasteKey) {
        String a = getSharedData(copyKey);
        setSharedData(pasteKey, a);
    }

    public Boolean getSharedIdCheck(String id) {
        String str = sharedPreferences.getString(id,"");

        if (str.equals(""))
            idCheck = true;
        else if (!str.equals("")){
            idCheck = false;
        }
        return idCheck;

    }


}

