package com.example.ndh.floatingball.sdk.camera;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.example.ndh.floatingball.util.PermissionUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * Created by ndh on 17/1/13.
 */

public class CameraActivity extends Activity {
    private static final int REQUEST_CODE = 0;
    private String mNameImage;
    private File mFileImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        super.onCreate(savedInstanceState);
        if (checkPermission()) return;
        String nameImage = prepareFileDir();
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(nameImage)));
        startActivityForResult(intent, REQUEST_CODE);
    }

    private boolean checkPermission() {
        if (!PermissionUtils.checkPermission(this, PermissionUtils.CODE_CAMERA, "请先授予相机权限")) {
            return true;
        }
        if (!PermissionUtils.checkPermission(this, PermissionUtils.CODE_WRITE_EXTERNAL_STORAGE, "请先授予存储空间权限")) {
            return true;
        }
        return false;
    }

    @NonNull
    private String prepareFileDir() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss");
        String strDate = dateFormat.format(new java.util.Date());
        String pathImage = Environment.getExternalStorageDirectory().getPath() + "/DCIM/";
        mNameImage = pathImage + strDate + ".png";

        File dir = new File(pathImage);
        if (!dir.exists() && !dir.mkdirs()) {
            //最多创建两次文件夹
            dir.mkdirs();

        }
        mFileImage = new File(mNameImage);
        try {
            if (!mFileImage.exists() && !mFileImage.createNewFile()) {
                mFileImage.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mNameImage;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            switch (resultCode) {
                case RESULT_OK:
                    Toast.makeText(this, "图片已经保存在:" + mNameImage, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    if (null != mFileImage && mFileImage.exists() && mFileImage.isFile()) {
                        if (!mFileImage.delete()) {
                            // 最多删两次，避免偶尔删不掉问题
                            mFileImage.delete();
                        }
                    }
                    break;
            }
        }
        finish();
    }
}
