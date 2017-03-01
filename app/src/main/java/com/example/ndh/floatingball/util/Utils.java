package com.example.ndh.floatingball.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import com.example.ndh.floatingball.sdk.lockscreen.MyAdmin;
import com.example.ndh.floatingball.sdk.screenshot.ScreenShotActivity;

import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by ndh on 16/12/20.
 */

public class Utils {
    /**
     * 获取屏幕尺寸
     *
     * @param context
     * @return
     */
    public static Point getScreenSize(Context context) {
        WindowManager mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point p = new Point();
        mWindowManager.getDefaultDisplay().getSize(p);
        return p;
    }

    public static int pix2dp(Context context, int px) {
        int dp;
        final float scale = context.getResources().getDisplayMetrics().density;
        dp = (int) (px / scale + 0.5f);
        return dp;
    }

    public static int dp2pix(Context context, int dp) {
        int px;
        final float scale = context.getResources().getDisplayMetrics().density;
        px = (int) (dp * scale + 0.5f);
        return px;
    }

    public static boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(100);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName().toString();
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }

    public static synchronized String getStringBySP(Context context, String name, String defaultValue) {
        SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        return sp.getString(name, defaultValue);
    }

    public static synchronized void putStringBySP(Context context, String name, String value) {
        SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(name, value).commit();
    }

    public static boolean canDrawOverlays(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(context.getApplicationContext())) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean isActive(Context context) {
        DevicePolicyManager policyManager;
        ComponentName componentName;
        policyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        componentName = new ComponentName(context.getApplicationContext(), MyAdmin.class);
//     policyManager.clearDeviceOwnerApp(context.getPackageName());
        return policyManager.isAdminActive(componentName);
    }

    public static void unActive(Context context) {
        DevicePolicyManager policyManager;
        policyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        policyManager.clearDeviceOwnerApp(context.getPackageName());
    }

    public static void startActivePage(Activity context) {
        // 启动设备管理(隐式Intent) - 在AndroidManifest.xml中设定相应过滤器
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        DevicePolicyManager policyManager;
        ComponentName componentName;
        policyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        componentName = new ComponentName(context.getApplicationContext(), MyAdmin.class);
        //权限列表
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
        //描述(additional explanation)
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "------ 其他描述 ------");
        context.startActivityForResult(intent, 0);
    }

    public static void startOverlayGrante(Context context) {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + context.getApplicationContext().getPackageName()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static String showPhoneNum(Context context) {
        TelephonyManager phoneMgr = (TelephonyManager) context.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
//        String strPhoneNum = phoneMgr.getLine1Number();
//        return strPhoneNum;
        return "";
    }

    static long[] mHint;

    /**
     * 真正调用的时候还是把方法 构造到activit等里面，避免内存泄漏
     *
     * @param times
     * @param view
     */
    public static void threeTimesClick(int times, final View view) {
        mHint = new long[times];
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.arraycopy(mHint, 1, mHint, 0, mHint.length - 1);
                mHint[mHint.length - 1] = SystemClock.uptimeMillis();

                if (mHint[0] >= SystemClock.uptimeMillis() - 500) {
                    //500 毫秒点击了3次
                    Toast.makeText(view.getContext().getApplicationContext(), "3击事件", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public static void removeAdmin(Context context) {
        DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName cn = new ComponentName(context, MyAdmin.class);//组件名字
        dpm.removeActiveAdmin(cn);//移除操作
    }

    public static DisplayMetrics getScreenInfo(Activity context) {
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
//        screenWidth = dm.widthPixels;
//        screenHeight = dm.heightPixels;
//        densityDpi = dm.densityDpi;
//        scale = dm.density;
//        fontScale = dm.scaledDensity;
        return dm;
    }

    public static void saveBitmap(Bitmap mBitmap, String bitName) {
        File f = new File("/sdcard/Note/" + bitName + ".jpg");
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void installPackage(Context context,String packagePath){
        String str = "/CanavaCancel.apk";
        String fileName = Environment.getExternalStorageDirectory() + str;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//
        intent.setDataAndType(Uri.fromFile(new File(fileName)), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }
    public static void unInstallPackage(Context context,String packageName){
        Uri packageURI = Uri.parse("package:"+packageName);
        Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
        uninstallIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(uninstallIntent);
    }
}
