package com.example.ndh.floatingball.sdk.screenshot;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Surface;
import android.widget.Toast;

import com.example.ndh.floatingball.R;
import com.example.ndh.floatingball.util.PermissionUtils;
import com.example.ndh.floatingball.util.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;

/**
 * Created by ndh on 17/1/9.
 */

public class ScreenShotActivity extends Activity implements PermissionUtils.PermissionGrant {

    private MediaProjectionManager mManager;
    private ImageReader mImageReader;
    private Image mImage;
    VirtualDisplay mVirtualDisplay;
    private Surface mSurface;
    private static final int SUCESS = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //6.0 动态权限
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        super.onCreate(savedInstanceState);
        if (!PermissionUtils.checkPermission(ScreenShotActivity.this, PermissionUtils.CODE_WRITE_EXTERNAL_STORAGE, "请先授予存储空间权限")) {
            return;
        }
        mManager = (MediaProjectionManager) getApplicationContext().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        Intent intent = mManager.createScreenCaptureIntent();
        startActivityForResult(intent, 0);
        mImageReader = ImageReader.newInstance(Utils.getScreenSize(this).x, Utils.getScreenSize(this).y, 0x1, 2);
        mSurface = mImageReader.getSurface();
    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Toast.makeText(this, "正在生成截图...", Toast.LENGTH_SHORT).show();
        new Thread() {
            @Override
            public void run() {
                savePic(resultCode, data);
            }
        }.start();

    }

    private void savePic(int resultCode, Intent data) {
        final MediaProjection mediaProjection = mManager.getMediaProjection(resultCode, data);
        if (mediaProjection == null) {
            Log.e("@@", "media projection is null");
            return;
        }
        //ImageFormat.RGB_565

        mVirtualDisplay = mediaProjection.createVirtualDisplay("ndh",
                Utils.getScreenSize(this).x, Utils.getScreenSize(this).y, Utils.getScreenInfo(this).densityDpi, DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC,
                mSurface, null, null);
        Handler handler2 = new Handler(getMainLooper());
        handler2.postDelayed(new Runnable() {
            public void run() {
                //capture the screen
                mImage = mImageReader.acquireLatestImage();
                if (mImage == null) {
                    Log.d("ndh--", "img==null");
                    return;
                }
                int width = mImage.getWidth();
                int height = mImage.getHeight();
                final Image.Plane[] planes = mImage.getPlanes();
                final ByteBuffer buffer = planes[0].getBuffer();
                int pixelStride = planes[0].getPixelStride();
                int rowStride = planes[0].getRowStride();
                int rowPadding = rowStride - pixelStride * width;
                Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
                bitmap.copyPixelsFromBuffer(buffer);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height);
                mImage.close();


                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss");
                String strDate = dateFormat.format(new java.util.Date());
                String pathImage = Environment.getExternalStorageDirectory().getPath() + "/DCIM/";
                String nameImage = pathImage + strDate + ".png";

                if (bitmap != null) {
                    try {

                        File dir = new File(pathImage);
                        if (!dir.exists() && !dir.mkdirs()) {
//最多创建两次文件夹
                            dir.mkdirs();

                        }
                        File fileImage = new File(nameImage);
                        if (!fileImage.exists() && !fileImage.createNewFile()) {
                            fileImage.createNewFile();
                        }
                        FileOutputStream out = new FileOutputStream(fileImage);
                        if (out != null) {
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                            out.flush();
                            out.close();
                            Intent media = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                            Uri contentUri = Uri.fromFile(fileImage);
                            media.setData(contentUri);
                            ScreenShotActivity.this.sendBroadcast(media);
                            Toast.makeText(ScreenShotActivity.this, "图片保存成功:" + nameImage, Toast.LENGTH_SHORT).show();
                            mediaProjection.stop();
                            finish();
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        finish();
                    }
                }
            }
        }, 50);

    }

    @Override
    public void onPermissionGranted(int requestCode) {
        switch (requestCode) {
            case SUCESS:
                break;
        }
    }
}
