package com.storedata.com.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;

import java.util.UUID;

/**
 * Created by ${3embed} on ${27-10-2017}.
 * Banglore
 */

public class DeviceUuidFactory {
    private static final String PREFS_FILE = "yelodIdPreferences";
    private static final String PREFS_DEVICE_ID = "deviceId";
    private static DeviceUuidFactory df=new DeviceUuidFactory();
    private DeviceUuidFactory(){}
    public static DeviceUuidFactory getInstance()
    {
        return df;
    }
    public String getDeviceUuid(Context context)
    {
        return collectID(context);
    }
    private String collectID(Context context)
    {
        SharedPreferences prefs = context
                .getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        String id = prefs.getString(PREFS_DEVICE_ID,null);
        if(id!=null&&!id.isEmpty())
        {
            return id;
        }else
        {
            String temp_ID;
            temp_ID= Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            if(temp_ID==null)
            {
                temp_ID= UUID.randomUUID().toString();
            }
            if(temp_ID!=null&&!temp_ID.isEmpty())
            {
                prefs.edit().putString(PREFS_DEVICE_ID,temp_ID).apply();
                return temp_ID;
            }else
            {
                prefs.edit().putString(PREFS_DEVICE_ID,null).apply();
                return "9774d56d682e549c";
            }
        }
    }
}
