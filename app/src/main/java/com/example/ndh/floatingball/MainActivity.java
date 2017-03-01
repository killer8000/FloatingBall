package com.example.ndh.floatingball;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.PersistableBundle;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.Preference;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import com.example.ndh.floatingball.sdk.ActionChangeListener;
import com.example.ndh.floatingball.sdk.ActionManager;
import com.example.ndh.floatingball.sdk.Config;
import com.example.ndh.floatingball.sdk.FloatingService;
import com.example.ndh.floatingball.sdk.FloatingWindowManager;
import com.example.ndh.floatingball.util.PermissionUtils;
import com.example.ndh.floatingball.util.Utils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, PermissionUtils.PermissionGrant {

    private TextView mUp;
    private TextView mDown;
    private TextView mLeft;
    private TextView mRight;
    private TextView mMenu_1;
    private TextView mMenu_2;
    private TextView mMenu_3;
    private TextView mMenu_4;
    private TextView mMenu_5;
    private String mTextUp;
    private String mTextDown;
    private String mTextLeft;
    private String mTextRight;
    private String mTextMenu1;
    private String mTextMenu2;
    private String mTextMenu3;
    private String mTextMenu4;
    private String mTextMenu5;
    private TextView mOverLay;
    private TextView mActive;
    private String mTextOverlay;
    private String mTextActive;
    private Drawable mOverLayRightDrawble;
    private Drawable mActiveDrawble;
    private Button mButton;
    private TextView mUnInstall;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PermissionUtils.requestMultiPermissions(this, this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        boolean toggle = Utils.isServiceWork(getApplicationContext(), FloatingService.class.getName());
        if (toggle) {
            mButton.setText(getString(R.string.stop_use));
        } else {
            mButton.setText(getString(R.string.start_use));
        }
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle();
            }
        });
        ActionManager.create().registActionChangeListener(new ActionChangeListener() {
            @Override
            public void onChange(String position, String action) {
                setText();
                FloatingWindowManager.create(MainActivity.this).removeAllView();
                FloatingWindowManager.create(MainActivity.this).init();
            }
        });
    }

    private long[] mHint = new long[3];

    private void toggle() {
        Intent intent = new Intent(MainActivity.this, FloatingService.class);
        boolean toggle = Utils.isServiceWork(getApplicationContext(), FloatingService.class.getName());
        if (toggle) {
            // 正在运行点击开关就关闭服务
            System.arraycopy(mHint, 1, mHint, 0, mHint.length - 1);
            mHint[mHint.length - 1] = SystemClock.uptimeMillis();
            if (mHint[0] >= SystemClock.uptimeMillis() - 500) {
                //500 毫秒点击了3次
                intent.getBooleanExtra("toggle", false);
                stopService(intent);
                mButton.setText(getString(R.string.start_use));
            }
        } else {
            intent.getBooleanExtra("toggle", true);
            startService(intent);
            mButton.setText(getString(R.string.stop_use));
        }


    }

    private void initView() {
        mButton = (Button) findViewById(R.id.bt);
        mUnInstall = (TextView) findViewById(R.id.uninstall);
        mUp = (TextView) findViewById(R.id.up);
        mDown = (TextView) findViewById(R.id.down);
        mLeft = (TextView) findViewById(R.id.left);
        mRight = (TextView) findViewById(R.id.right);
        mMenu_1 = (TextView) findViewById(R.id.menu_1);
        mMenu_2 = (TextView) findViewById(R.id.menu_2);
        mMenu_3 = (TextView) findViewById(R.id.menu_3);
        mMenu_4 = (TextView) findViewById(R.id.menu_4);
        mMenu_5 = (TextView) findViewById(R.id.menu_5);

        mOverLay = (TextView) findViewById(R.id.over_lay);
        mActive = (TextView) findViewById(R.id.active);

        mUp.setTag(Config.MenuPosition.UP);
        mDown.setTag(Config.MenuPosition.DOWN);
        mLeft.setTag(Config.MenuPosition.LEFT);
        mRight.setTag(Config.MenuPosition.RIGHT);
        mMenu_1.setTag(Config.MenuPosition.MENU_1);
        mMenu_2.setTag(Config.MenuPosition.MENU_2);
        mMenu_3.setTag(Config.MenuPosition.MENU_3);
        mMenu_4.setTag(Config.MenuPosition.MENU_4);
        mMenu_5.setTag(Config.MenuPosition.MENU_5);

        mUp.setOnClickListener(this);
        mDown.setOnClickListener(this);
        mLeft.setOnClickListener(this);
        mRight.setOnClickListener(this);
        mMenu_1.setOnClickListener(this);
        mMenu_2.setOnClickListener(this);
        mMenu_3.setOnClickListener(this);
        mMenu_4.setOnClickListener(this);
        mMenu_5.setOnClickListener(this);
        mUnInstall.setOnClickListener(this);
        setText();
    }

    private void setText() {

        mTextUp = getResources().getString(R.string.up);
        mTextDown = getResources().getString(R.string.down);
        mTextLeft = getResources().getString(R.string.left);
        mTextRight = getResources().getString(R.string.right);
        mTextMenu1 = getResources().getString(R.string.menu_1);
        mTextMenu2 = getResources().getString(R.string.menu_2);
        mTextMenu3 = getResources().getString(R.string.menu_3);
        mTextMenu4 = getResources().getString(R.string.menu_4);
        mTextMenu5 = getResources().getString(R.string.menu_5);

        mTextOverlay = getResources().getString(R.string.over_lay);
        mTextActive = getResources().getString(R.string.active);

        mTextUp = String.format(mTextUp, Utils.getStringBySP(this, Config.MenuPosition.UP, Config.Action.DEST));
        mTextDown = String.format(mTextDown, Utils.getStringBySP(this, Config.MenuPosition.DOWN, Config.Action.LOCK_SCREEN));
        mTextLeft = String.format(mTextLeft, Utils.getStringBySP(this, Config.MenuPosition.LEFT, Config.Action.MUTE));
        mTextRight = String.format(mTextRight, Utils.getStringBySP(this, Config.MenuPosition.RIGHT, Config.Action.CAMERA));
        mTextMenu1 = String.format(mTextMenu1, Utils.getStringBySP(this, Config.MenuPosition.MENU_1, Config.Action.FLASH));
        mTextMenu2 = String.format(mTextMenu2, Utils.getStringBySP(this, Config.MenuPosition.MENU_2, Config.Action.CALENDER));
        mTextMenu3 = String.format(mTextMenu3, Utils.getStringBySP(this, Config.MenuPosition.MENU_3, Config.Action.WIFI));
        mTextMenu4 = String.format(mTextMenu4, Utils.getStringBySP(this, Config.MenuPosition.MENU_4, Config.Action.CALL));
        mTextMenu5 = String.format(mTextMenu5, Utils.getStringBySP(this, Config.MenuPosition.MENU_5, Config.Action.CONTACT));

        mUp.setText(mTextUp);
        mDown.setText(mTextDown);
        mLeft.setText(mTextLeft);
        mRight.setText(mTextRight);
        mMenu_1.setText(mTextMenu1);
        mMenu_2.setText(mTextMenu2);
        mMenu_3.setText(mTextMenu3);
        mMenu_4.setText(mTextMenu4);
        mMenu_5.setText(mTextMenu5);

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onResume() {

        if (Utils.canDrawOverlays(this)) {
            mTextOverlay = String.format(mTextOverlay, getResources().getString(R.string.granted));
            mOverLayRightDrawble = getResources().getDrawable(R.drawable.ic_checked, null);
            mOverLay.setOnClickListener(null);
        } else {
            mTextOverlay = String.format(mTextOverlay, getResources().getString(R.string.not_granted));
            mOverLayRightDrawble = getDrawable(R.drawable.ic_no_checked);
            mOverLay.setOnClickListener(this);
        }
        mOverLay.setText(mTextOverlay);
        mOverLayRightDrawble.setBounds(0, 0, mOverLayRightDrawble.getMinimumWidth(), mOverLayRightDrawble.getMinimumHeight());
        mOverLay.setCompoundDrawables(null, null, mOverLayRightDrawble, null);

        if (Utils.isActive(this)) {
            mTextActive = String.format(mTextActive, getString(R.string.actived));
            mActiveDrawble = getResources().getDrawable(R.drawable.ic_checked, null);
            mActive.setOnClickListener(null);

        } else {
            mTextActive = String.format(mTextActive, getString(R.string.not_actived));
            mActiveDrawble = getDrawable(R.drawable.ic_no_checked);
            mActive.setOnClickListener(this);
        }
        mActiveDrawble.setBounds(0, 0, mActiveDrawble.getMinimumWidth(), mActiveDrawble.getMinimumHeight());
        mActive.setText(mTextActive);
        mActive.setCompoundDrawables(null, null, mActiveDrawble, null);
        super.onResume();
    }


    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.up:
            case R.id.down:
            case R.id.left:
            case R.id.right:
            case R.id.menu_1:
            case R.id.menu_2:
            case R.id.menu_3:
            case R.id.menu_4:
            case R.id.menu_5:
                String tag = (String) v.getTag();
                Intent intent = new Intent(this, SelectActivity.class);
                intent.putExtra("position", tag);
                startActivityForResult(intent, 0);
                break;
            case R.id.active:
                if (Utils.canDrawOverlays(this)) {
                    Utils.startActivePage(this);
                } else {
                    Toast.makeText(this, getResources().getString(R.string.grant_overlay), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.over_lay:
                Utils.startOverlayGrante(this);
                break;
            case R.id.uninstall:
                System.arraycopy(mHint, 1, mHint, 0, mHint.length - 1);
                mHint[mHint.length - 1] = SystemClock.uptimeMillis();
                if (mHint[0] >= SystemClock.uptimeMillis() - 500) {
                    //500 毫秒点击了3次
                    Utils.removeAdmin(this);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!Utils.isActive(MainActivity.this.getApplicationContext()))
                                Utils.unInstallPackage(MainActivity.this.getApplicationContext(), getPackageName());
                            else
                                Toast.makeText(MainActivity.this.getApplicationContext(), getResources().getString(R.string.remove_admin), Toast.LENGTH_SHORT).show();
                        }
                    }, 50);
                }
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        setText();
    }

    @Override
    public void onPermissionGranted(int requestCode) {

    }
}
