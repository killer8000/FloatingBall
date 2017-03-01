package com.example.ndh.floatingball.sdk.lockscreen;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.example.ndh.floatingball.util.Utils;


/**
 * Created by ndh on 16/12/15.
 */

public class LockScreenActivity extends Activity {
    DevicePolicyManager policyManager;
    ComponentName componentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //获取设备管理服务
        policyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        componentName = new ComponentName(this.getApplicationContext(), MyAdmin.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this.getApplicationContext())) {
            Toast.makeText(this, "请先允许快捷助手能顶层显示", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + this.getApplicationContext().getPackageName()));
            startActivityForResult(intent, 1);
        } else {
            lock();
        }

    }

    private void lock() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this.getApplicationContext())) {
            finish();
            return;
        }
        if (!Utils.isActive(this)) {//若无权限
            activeManage();//去获得权限
        } else {
            lockScreen();
        }
    }

    private void activeManage() {
        // 启动设备管理(隐式Intent) - 在AndroidManifest.xml中设定相应过滤器
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);

        //权限列表
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);

        //描述(additional explanation)
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "------ 其他描述 ------");

        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("ndh--", "requestcode=" + requestCode + ",resultColde=" + resultCode);
        if (requestCode == 0 && resultCode == -1) {
            Toast.makeText(this, "激活成功,请放心使用", Toast.LENGTH_SHORT).show();
            lockScreen();
        }
        if (resultCode == 0 && requestCode == 0) {
            Toast.makeText(this, "激活失败...", Toast.LENGTH_SHORT).show();
            finish();
        }
        if (requestCode == 1 && resultCode == 0) {
            lock();
        } else {
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void lockScreen() {
        policyManager.lockNow();
        finish();
//        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
