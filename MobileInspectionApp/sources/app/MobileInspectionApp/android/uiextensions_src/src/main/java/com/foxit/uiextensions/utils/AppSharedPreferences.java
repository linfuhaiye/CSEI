package com.foxit.uiextensions.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

public class AppSharedPreferences {
	private static AppSharedPreferences mAppSharedPreferences;
	private Context mAppContext = null;
	private  SharedPreferences mSharedPreferences;
	private  SharedPreferences.Editor editor;

	private AppSharedPreferences(Context context) {
		mAppContext = context;
	}

	public static AppSharedPreferences getInstance(Context context){
		if (mAppSharedPreferences == null) {
			mAppSharedPreferences = new AppSharedPreferences(context);
		}
		return mAppSharedPreferences;
	}

	public void remove(String name, String key) {
		mSharedPreferences = mAppContext.getSharedPreferences(name,0);
		editor = mSharedPreferences.edit();
		editor.remove(key);
		editor.apply();
	}

	public String getString(String name, String key, String defaultValue) {
		mSharedPreferences = mAppContext.getSharedPreferences(name, 0);
		return mSharedPreferences.getString(key, defaultValue);
	}

	public int getInteger(String name, String key, int defaultValue) {
		mSharedPreferences = mAppContext.getSharedPreferences(name, 0);
		return mSharedPreferences.getInt(key, defaultValue);
	}

	public long getLong(String name, String key, long defaultValue) {
		mSharedPreferences = mAppContext.getSharedPreferences(name, 0);
		return mSharedPreferences.getLong(key, defaultValue);
	}

	public boolean getBoolean(String name, String key, boolean defaultValue) {
		mSharedPreferences = mAppContext.getSharedPreferences(name,0);
		return mSharedPreferences.getBoolean(key, defaultValue);
	}

	public float getFloat(String name, String key, float defaultValue) {
		mSharedPreferences = mAppContext.getSharedPreferences(name, 0);
		return mSharedPreferences.getFloat(key, defaultValue);
	}

	//save data
	public void setString(String name, String key, String value) {
		mSharedPreferences = mAppContext.getSharedPreferences(name,0);
		editor = mSharedPreferences.edit();
		editor.putString(key, value);
		editor.apply();
	}

	public void setInteger(String name, String key, int value) {
		mSharedPreferences = mAppContext.getSharedPreferences(name,0);
		editor = mSharedPreferences.edit();
		editor.putInt(key, value);
		editor.apply();
	}

	public void setLong(String name, String key, long value) {
		mSharedPreferences = mAppContext.getSharedPreferences(name,0);
		editor = mSharedPreferences.edit();
		editor.putLong(key, value);
		editor.apply();
	}

	public void setBoolean(String name, String key, boolean value) {
		mSharedPreferences = mAppContext.getSharedPreferences(name,0);
		editor = mSharedPreferences.edit();
		editor.putBoolean(key, value);
		editor.apply();
	}

	public void setFloat(String name, String key, float value) {
		mSharedPreferences = mAppContext.getSharedPreferences(name,0);
		editor = mSharedPreferences.edit();
		editor.putFloat(key, value);
		editor.apply();
	}

}
