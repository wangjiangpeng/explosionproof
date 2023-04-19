package com.lanhu.explosion.task.impl;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;

import com.lanhu.explosion.AApplication;
import com.lanhu.explosion.bean.BaseInfo;
import com.lanhu.explosion.bean.PictureInfo;
import com.lanhu.explosion.store.DBManager;
import com.lanhu.explosion.task.ATask;
import com.lanhu.explosion.utils.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CameraPictureTask extends ATask {

    private static final String TAG = "CameraPictureTask";
    Object mLock = new Object();
    String savePath;

    @Override
    public boolean reset() {
        savePath = null;
        return super.reset();
    }

    @Override
    protected Object doInBackground(Object... objs) {
        try {
            Log.d(TAG, "start");
            int id = (int) objs[0];
            SurfaceTexture st = (SurfaceTexture) objs[1];
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(id, cameraInfo);
            int facing = cameraInfo.facing;
            Camera camera = Camera.open(id);
            camera.setPreviewTexture(st);

            camera.startPreview();
            camera.takePicture(null, null, new Camera.PictureCallback() {

                @Override
                public void onPictureTaken(byte[] bytes, Camera camera) {
                    ByteArrayOutputStream baos = null;
                    try {
                        if (facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            Matrix m = new Matrix();
                            m.postScale(-1, 1);
                            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);

                            baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            bytes = baos.toByteArray();
                        }

                        savePath = savePicture(bytes);
                        synchronized (mLock) {
                            mLock.notify();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (baos != null) {
                            try {
                                baos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });

            synchronized (mLock) {
                mLock.wait(5000);
            }
            camera.stopPreview();
            camera.release();

            if (savePath != null) {
                DBManager db = DBManager.getInstance();
                db.insertPicture(new PictureInfo(savePath, BaseInfo.STATUS_UPLOAD_NO));
                // todo upload service
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d(TAG, "stop");
        return savePath != null;
    }

    protected String savePicture(byte[] data) {
        try {
            File file = FileUtils.createPictureFile(AApplication.getInstance());
            FileOutputStream output = new FileOutputStream(file);
            output.write(data);
            output.close();
            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
