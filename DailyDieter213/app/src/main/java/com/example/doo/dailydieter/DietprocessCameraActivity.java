package com.example.doo.dailydieter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class DietprocessCameraActivity extends AppCompatActivity {

    private ImageView imageView;
    private Bitmap bitmap;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dietprocess);

        SharedPreferences spref = getSharedPreferences("Cropped Image", Activity.MODE_PRIVATE);
        String cropped = spref.getString("camera_crop", null);
        Uri get_uri = Uri.parse(cropped);

        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), get_uri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        imageView = (ImageView)findViewById(R.id.foodpartView1);
        imageView.setImageBitmap(bitmap);

        Button confirm_button = (Button)findViewById(R.id.confirm);
        confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent process1 = new Intent(DietprocessCameraActivity.this, DietprocessActivity2.class);
                startActivity(process1);
            }
        });

        Button takepicture_button = (Button)findViewById(R.id.takepicture);
        takepicture_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent camera_intent = new Intent(DietprocessCameraActivity.this, CameraActivity.class);
                startActivity(camera_intent);
                finish();
            }
        });
    }
    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory
                .decodeByteArray(decodedByte, 0, decodedByte.length);
    }


}
//    public Bitmap cropBitmap1(Bitmap original) {
//        Bitmap result = Bitmap.createBitmap(original
//                , 0 //X 시작위치 (원본의 4/1지점)
//                , 0//Y 시작위치 (원본의 4/1지점)
//                , original.getWidth()/3 // 넓이 (원본의 절반 크기)
//                , original.getHeight()/5*2); // 높이 (원본의 절반 크기)
//        if (result != original) {
//            original.recycle();
//        }
//        return result;
//    }
//
//    public Bitmap cropBitmap2(Bitmap original) {
//        Bitmap result = Bitmap.createBitmap(original
//                , original.getWidth()/3 //X 시작위치 (원본의 4/1지점)
//                , 0//Y 시작위치 (원본의 4/1지점)
//                , original.getWidth()/3 // 넓이 (원본의 절반 크기)
//                , original.getHeight()/5*2); // 높이 (원본의 절반 크기)
//        if (result != original) {
//            original.recycle();
//        }
//        return result;
//    }
//
//    public Bitmap cropBitmap3(Bitmap original) {
//        Bitmap result = Bitmap.createBitmap(original
//                , original.getWidth()/3*2 //X 시작위치 (원본의 4/1지점)
//                , 0//Y 시작위치 (원본의 4/1지점)
//                , original.getWidth()/3 // 넓이 (원본의 절반 크기)
//                , original.getHeight()/5*2); // 높이 (원본의 절반 크기)
//        if (result != original) {
//            original.recycle();
//        }
//        return result;
//    }
//
//    public Bitmap cropBitmap4(Bitmap original) {
//        Bitmap result = Bitmap.createBitmap(original
//                , 0 //X 시작위치 (원본의 4/1지점)
//                , original.getHeight()/5*2//Y 시작위치 (원본의 4/1지점)
//                , original.getWidth()/2 // 넓이 (원본의 절반 크기)
//                , original.getHeight()/5*3); // 높이 (원본의 절반 크기)
//        if (result != original) {
//            original.recycle();
//        }
//        return result;
//    }
//
//    public Bitmap cropBitmap5(Bitmap original) {
//        Bitmap result = Bitmap.createBitmap(original
//                , original.getWidth()/2 //X 시작위치 (원본의 4/1지점)
//                , original.getHeight()/5*2//Y 시작위치 (원본의 4/1지점)
//                , original.getWidth()/2 // 넓이 (원본의 절반 크기)
//                , original.getHeight()/5*3); // 높이 (원본의 절반 크기)
//        if (result != original) {
//            original.recycle();
//        }
//        return result;
//    }

//        Intent in2 = getIntent();
//        byte[] arr = in2.getByteArrayExtra("camera_crop");
//        Bitmap bm = BitmapFactory.decodeByteArray(arr,0,arr.length);

//                Bitmap bm_copy_1 = bm.copy(Bitmap.Config.ARGB_8888,true);
//                Bitmap bm_copy_2 = bm.copy(Bitmap.Config.ARGB_8888,true);
//                Bitmap bm_copy_3 = bm.copy(Bitmap.Config.ARGB_8888,true);
//                Bitmap bm_copy_4 = bm.copy(Bitmap.Config.ARGB_8888,true);
//                Bitmap bm_copy_5 = bm.copy(Bitmap.Config.ARGB_8888,true);
//
//                Bitmap crop_bitmap1 = cropBitmap1(bm_copy_1);
//                Bitmap crop_bitmap2 = cropBitmap2(bm_copy_2);
//                Bitmap crop_bitmap3 = cropBitmap3(bm_copy_3);
//                Bitmap crop_bitmap4 = cropBitmap4(bm_copy_4);
//                Bitmap crop_bitmap5 = cropBitmap5(bm_copy_5);
//
//                ByteArrayOutputStream stream_1 = new ByteArrayOutputStream();
//                crop_bitmap1.compress(Bitmap.CompressFormat.PNG, 100, stream_1);
//                byte[] byteArray_1 = stream_1.toByteArray();
//
//                ByteArrayOutputStream stream_2 = new ByteArrayOutputStream();
//                crop_bitmap2.compress(Bitmap.CompressFormat.PNG, 100, stream_2);
//                byte[] byteArray_2 = stream_2.toByteArray();
//
//                ByteArrayOutputStream stream_3 = new ByteArrayOutputStream();
//                crop_bitmap3.compress(Bitmap.CompressFormat.PNG, 100, stream_3);
//                byte[] byteArray_3 = stream_3.toByteArray();
//
//                ByteArrayOutputStream stream_4 = new ByteArrayOutputStream();
//                crop_bitmap4.compress(Bitmap.CompressFormat.PNG, 100, stream_4);
//                byte[] byteArray_4 = stream_4.toByteArray();
//
//                ByteArrayOutputStream stream_5 = new ByteArrayOutputStream();
//                crop_bitmap5.compress(Bitmap.CompressFormat.PNG, 100, stream_5);
//                byte[] byteArray_5 = stream_5.toByteArray();

//                process1.putExtra("picture_process_1", byteArray_1);
//                process1.putExtra("picture_process_2", byteArray_2);
//                process1.putExtra("picture_process_3", byteArray_3);
//                process1.putExtra("picture_process_4", byteArray_4);
//                process1.putExtra("picture_process_5", byteArray_5);