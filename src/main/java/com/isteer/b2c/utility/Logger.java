package com.isteer.b2c.utility;

import android.content.pm.PackageManager;
import android.util.Log;

import com.isteer.b2c.app.B2CApp;


public class Logger {


    public static final boolean D = isDebugBuild();

    public static void LogInfo(String TAG, String msg) {
        if (D) {
            Log.i(TAG, msg);
        }
    }

    public static void LogWarrning(String TAG, String msg) {
        if (D) {
            Log.w(TAG, msg);
        }
    }

    public static void LogDebug(String TAG, String msg) {
        if (D) {
            Log.d(TAG, msg);
        }
    }

    public static void LogError(String TAG, String msg) {
        if (D) {
            Log.e(TAG, msg);
        }
    }
    public static Boolean isDebugBuild()  {
        try {
            return   B2CApp.getINSTANCE().getPackageManager().getPackageInfo(
                      B2CApp.getINSTANCE().getPackageName(), 0).versionName.contains("L");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }
}
