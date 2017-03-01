package com.example.ndh.floatingball.sdk;


import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.hardware.Camera;
import android.hardware.camera2.CameraManager;
import android.media.AudioManager;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.PowerManager;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.telephony.PhoneNumberUtils;
import android.view.View;
import android.widget.Toast;
import android.util.Log;

import com.example.ndh.floatingball.sdk.camera.CameraActivity;
import com.example.ndh.floatingball.sdk.lockscreen.LockScreenActivity;
import com.example.ndh.floatingball.sdk.screenshot.ScreenShotActivity;
import com.example.ndh.floatingball.util.PermissionUtils;
import com.example.ndh.floatingball.util.Utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


/**
 * 事件能力输出
 * Created by ndh on 16/12/22.
 */

public class ActionManager {

    private ActionManager() {
    }

    @NotProguard
    public static ActionManager create() {
        return ActionManager.SingleInstance.INSTANCE;
    }

    private void lock(Context context) {
        Intent lockIntent = new Intent(context, LockScreenActivity.class);
        lockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(lockIntent);
    }

    private void goHome(Context context) {
        Intent home = new Intent(Intent.ACTION_MAIN);
        home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        home.addCategory(Intent.CATEGORY_HOME);
        home.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(home);
    }

    private void startCamera(Context context) {
        Intent intent = new Intent (context, CameraActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @NotProguard
    public void initAction(Context context) {
        setAction(Config.MenuPosition.UP, Utils.getStringBySP(context, Config.MenuPosition.UP, Config.Action.DEST));
        setAction(Config.MenuPosition.DOWN, Utils.getStringBySP(context, Config.MenuPosition.DOWN, Config.Action.LOCK_SCREEN));
        setAction(Config.MenuPosition.LEFT, Utils.getStringBySP(context, Config.MenuPosition.LEFT, Config.Action.MUTE));
        setAction(Config.MenuPosition.RIGHT, Utils.getStringBySP(context, Config.MenuPosition.RIGHT, Config.Action.CAMERA));
        setAction(Config.MenuPosition.MENU_1, Utils.getStringBySP(context, Config.MenuPosition.MENU_1, Config.Action.FLASH));
        setAction(Config.MenuPosition.MENU_2, Utils.getStringBySP(context, Config.MenuPosition.MENU_2, Config.Action.CALENDER));
        setAction(Config.MenuPosition.MENU_3, Utils.getStringBySP(context, Config.MenuPosition.MENU_3, Config.Action.WIFI));
        setAction(Config.MenuPosition.MENU_4, Utils.getStringBySP(context, Config.MenuPosition.MENU_4, Config.Action.CALL));
        setAction(Config.MenuPosition.MENU_5, Utils.getStringBySP(context, Config.MenuPosition.MENU_5, Config.Action.CONTACT));

    }

    private static class SingleInstance {
        public static final ActionManager INSTANCE = new ActionManager();
    }

    private boolean isFlash;
    Camera camera;
    Camera.Parameters params;

    private void ToggleFlash(Context context) throws Exception {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            CameraManager manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
            if (!isFlash)
                manager.setTorchMode("0", true);
            else
                manager.setTorchMode("0", false);
        } else {
            if (!isFlash) {
                camera = Camera.open();
                params = camera.getParameters();
                params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                camera.setParameters(params);
                camera.startPreview();
            } else {
                params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                camera.setParameters(params);
                camera.stopPreview();
                camera.release();
                camera = null;
                params = null;
            }
        }


        isFlash = !isFlash;
    }

    private void openCalander(Context context) {
        try {
            Intent t_intent = new Intent(Intent.ACTION_VIEW);
            t_intent.addCategory(Intent.CATEGORY_DEFAULT);
            t_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK /*| Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_TASK_ON_HOME*/);
            t_intent.setDataAndType(Uri.parse("content://com.android.calendar/"), "time/epoch");
            context.startActivity(t_intent);
        } catch (Exception e) {

            e.printStackTrace();
            Toast.makeText(context, "failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void startContact(Context context) {

        Intent intent = new Intent();

        intent.setAction(Intent.ACTION_PICK);

        intent.setData(ContactsContract.Contacts.CONTENT_URI);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void openPhoto(Context context) {
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    //TODO
    private void openRecent(Context context) {

    }

    private void mute(Context context) {
        AudioManager manager = (AudioManager) context.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        /**
         //STREAM_ALARM 警报
         STREAM_MUSIC 音乐回放即媒体音量
         STREAM_NOTIFICATION 窗口顶部状态栏Notification,
         STREAM_RING 铃声
         STREAM_SYSTEM 系统
         STREAM_VOICE_CALL 通话
         STREAM_DTMF 双音多频,拨号键的声音
         */
        manager.setStreamVolume(AudioManager.STREAM_ALARM, 0, 0);
        manager.setStreamVolume(AudioManager.STREAM_DTMF, 0, 0);
        manager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
        manager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 0, 0);
        manager.setStreamVolume(AudioManager.STREAM_RING, 0, 0);
        manager.setStreamVolume(AudioManager.STREAM_SYSTEM, 0, 0);
        int alarm = manager.getStreamVolume(AudioManager.STREAM_ALARM);
        int dtmf = manager.getStreamVolume(AudioManager.STREAM_DTMF);
        int music = manager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int notification = manager.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
        int ring = manager.getStreamVolume(AudioManager.STREAM_RING);
        int system = manager.getStreamVolume(AudioManager.STREAM_SYSTEM);
        Log.d("ndh---", "alarm=" + alarm + ",dtmf=" + dtmf + ",musit=" + music
                + ",notification=" + notification + ",ring=" + ring + ",system=" + system);
    }

    private void startAlarm(Context context) {
        PackageManager packageManager = context.getPackageManager();
        if (packageManager != null) {
//通常的rom
            Intent AlarmClockIntent = new Intent(Intent.ACTION_MAIN).addCategory(
                    Intent.CATEGORY_LAUNCHER).setComponent(
                    new ComponentName("com.android.deskclock", "com.android.deskclock.DeskClock"));

            ResolveInfo resolved = packageManager.resolveActivity(AlarmClockIntent,
                    PackageManager.MATCH_DEFAULT_ONLY);
            if (resolved != null) {
                AlarmClockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(AlarmClockIntent);
                return;
            } else {
                // required activity can not be located!
                //像魅族什么的 ，对手机rom裁剪修改过大，导致默认的路径无法直接找到系统时钟，则跳转到系统设置页面
                Intent intent = new Intent();
                ComponentName comp = new ComponentName("com.android.settings",
                        "com.android.settings.Settings");
                intent.setComponent(comp);
                intent.setAction("android.intent.action.VIEW");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        }


    }

    Map<String, String> map = new HashMap();

    @NotProguard
    public void setAction(String position, String action) {
        map.put(position, action);
    }

    @NotProguard
    public String getAction(String position) {
        return map.get(position);
    }

    @NotProguard
    public void doAction(Context context, String position) {
        switch (getAction(position)) {
            case Config.Action.CALENDER:
                openCalander(context);
                break;
            case Config.Action.CAMERA:
                startCamera(context);
                break;
//            case Config.Action.CLOCK:
//                startAlarm(context);
//                break;
            case Config.Action.CONTACT:
                startContact(context);
                break;
            case Config.Action.DEST:
                goHome(context);
                break;
            case Config.Action.FLASH:
                try {
                    ToggleFlash(context);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case Config.Action.LOCK_SCREEN:
                lock(context);
                break;
            case Config.Action.MUTE:
                mute(context);
                break;
//            case Config.Action.PHOTO:
//                openPhoto(context);
//                break;

            case Config.Action.WIFI:
                //wifi
                openWifi(context);
                break;
            case Config.Action.CALL:
                //电话
                openCall(context);
                break;
            case Config.Action.SMS:
                //短信
                doSendSMSTo(context);
                break;
            case Config.Action.SCREENSHOT:
                doScreenshot(context);
                break;
            default:
                Toast.makeText(context, "功能暂未实现", Toast.LENGTH_SHORT).show();

        }
    }

    private void doScreenshot(Context context) {
        Intent intent = new Intent(context, ScreenShotActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    /**
     * 调起系统发短信功能
     */
    private void doSendSMSTo(Context context) {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"));
        intent.putExtra("sms_body", "");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
       /* if(PhoneNumberUtils.isGlobalPhoneNumber(phoneNumber)){
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"));
            intent.putExtra("sms_body", "");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }*/
    }

    private void openCall(Context context) {
        if (!PermissionUtils.checkPermission(context, PermissionUtils.CODE_CALL_PHONE, "请先打开电话权限")) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void openWifi(Context context) {
        WifiManager wm = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        String toast = "wifi 即将关闭";
        if (!wm.isWifiEnabled()) {
            toast = "wifi 即将打开";
        }
        wm.setWifiEnabled(!wm.isWifiEnabled());

        Toast.makeText(context.getApplicationContext(), toast, Toast.LENGTH_SHORT).show();
    }

    @NotProguard
    public String[] getAllAction() {

        Class clazz = Config.Action.class;
        Field[] fields = clazz.getFields();
        String[] rtStr = new String[fields.length];
        for (int i = 0; i < fields.length; i++) {
            try {
                fields[i].setAccessible(true);
                String s = (String) (fields[i].get(fields[i].getName()));
                rtStr[i] = s;
                Log.d("ndh--", "action=" + s);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return rtStr;
    }

    ActionChangeListener mListener;

    @NotProguard
    public void registActionChangeListener(ActionChangeListener listener) {
        mListener = listener;
    }

    @NotProguard
    public void post(String position, String action) {
        if (null != mListener) {
            mListener.onChange(position, action);
        }
    }
}
