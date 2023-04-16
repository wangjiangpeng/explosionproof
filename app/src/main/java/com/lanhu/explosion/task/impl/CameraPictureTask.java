package com.lanhu.explosion.task.impl;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;

import com.lanhu.explosion.task.ATask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CameraPictureTask extends ATask {

    Object mLock = new Object();
    boolean success = false;
    String mPath;

    @Override
    protected Object doInBackground(Object... objs) {
        try {
            int id = (int) objs[0];
            mPath = (String) objs[1];
            SurfaceTexture st = (SurfaceTexture) objs[2];
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

                        success = savePicture(bytes);
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


        } catch (Exception e) {
            e.printStackTrace();
        }

        return success;
    }

    protected boolean savePicture(byte[] data) {
        try {
            File photoFile = new File(mPath);
            if (!photoFile.exists()) {
                photoFile.mkdirs();
            }
            File file = new File(photoFile, String.valueOf(System.currentTimeMillis()) + ".jpg");
            FileOutputStream output = new FileOutputStream(file);
            output.write(data);
            output.close();

            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
