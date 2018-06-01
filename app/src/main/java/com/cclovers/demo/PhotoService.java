package com.cclovers.demo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import com.unity3d.player.UnityPlayer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PhotoService {

    private static final int NONE = 0;
    private static final int ALBUM = 1;     //相册
    private static final int CAMERA = 2;    //拍照
    private static final int RESULT = 3;    //结果

    public static final String IMAGE_UNSPECIFIED = "image/*";
    public final static String PHOTO_NAME = "temp.jpg";
    public final static String CROP_NAME = "temp.png";
    public final static String PERSISTANT_PATH = "/Android/data/com.cclovers.demo/files/images/";

    private PhotoService() {
        // 1. 根目录 Environment.getExternalStorageDirectory() = storage/emulated/0
        photoPath = Environment.getExternalStorageDirectory().toString() + "/" + PHOTO_NAME;
        // 2. 程序读写目录Applicatio.persistantDataPath = /Android/data/com.xxx.xxx/files/
        cropPath = Environment.getExternalStorageDirectory().getAbsolutePath() + PERSISTANT_PATH;
    }

    private static PhotoService instance;

    public static PhotoService GetInstance() {
        if (instance == null) {
            instance = new PhotoService();
        }
        return instance;
    }

    public static boolean HasInstance() {
        return instance != null;
    }

    private String photoPath;   //相机拍照图片路径
    private String cropPath;    //裁剪结果路径

    /**
     * 调用相机
     */
    public void openCamera() {
        try {
            File file = new File(photoPath);
            //先删除该文件再创建
            if (file.exists()) {
                file.delete();
            }

            //图片路径
            Uri imageUrl = Uri.fromFile(file);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUrl);
            MainActivity.Instance.startActivityForResult(intent, CAMERA);
        } catch (Exception ex) {
            LogUtil.logError(ex.getMessage());
        }
    }

    /**
     * 调用相册
     */
    public void openAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_UNSPECIFIED);
        MainActivity.Instance.startActivityForResult(intent, ALBUM);
    }

    /**
     * 调用裁剪
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");

        intent.setDataAndType(uri, IMAGE_UNSPECIFIED);
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 100);
        intent.putExtra("outputY", 100);
        intent.putExtra("return-data", true);

        MainActivity.Instance.startActivityForResult(intent, RESULT);
    }

    /**
     * 将裁剪结果保存到本地（只能是Unity具有访问权限的路径）
     */
    public void SaveBitmap(Bitmap bitmap) throws IOException {

        FileOutputStream stream = null;
        try {
            if (bitmap != null) {
                //检查路径是否存在，如果没有则创建
                File destDir = new File(cropPath);
                if (!destDir.exists()) {
                    destDir.mkdirs();
                }

                //获取文件流
                stream = new FileOutputStream(cropPath + CROP_NAME);

                //写入本地
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                stream.flush();
            }
        } catch (Exception e) {
            LogUtil.logError(e.getMessage());
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }

    /**
     * 回调
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {

            //相册
            if (requestCode == ALBUM) {
                if (data != null) {
                    //跳转到裁剪
                    Uri uri = data.getData();
                    if (uri != null) {
                        startPhotoZoom(uri);
                    }
                }
            }
            //相机
            else if (requestCode == CAMERA) {
                File file = new File(photoPath);
                if (file != null && file.exists()) {
                    //跳转到裁剪
                    Uri uri = Uri.fromFile(file);
                    if (uri != null) {
                        startPhotoZoom(uri);
                    }
                }
            }
            //裁剪结果
            else if (requestCode == RESULT) {
                if (data != null) {
                    Bundle extras = data.getExtras();
                    if (extras != null) {
                        Bitmap photo = extras.getParcelable("data");
                        if (photo != null) {
                            SaveBitmap(photo);
                            //调用unity中方法 GetImage
                            UnityPlayer.UnitySendMessage(MainActivity.UNITY_GAMEOBJECT, "GetImage", CROP_NAME);
                        }
                    }
                }
            }
        } catch (Exception e) {
            LogUtil.logError("Error:" + e.getMessage());
        }
    }

}
