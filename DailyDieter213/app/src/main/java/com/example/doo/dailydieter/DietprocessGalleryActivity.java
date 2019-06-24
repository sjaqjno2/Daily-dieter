package com.example.doo.dailydieter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
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

public class DietprocessGalleryActivity extends AppCompatActivity {
    private static final int GALLERY_PERMISSIONS_REQUEST = 0;
    private static final int GALLERY_IMAGE_REQUEST = 1;
    private ImageView imageView;
    public Bitmap bitmap;
    private static final int MAX_DIMENSION = 1200;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dietprocess);
        Intent in2 = getIntent();


        Log.d("gallery 3", "start");
        String uriString = in2.getStringExtra("gallery_data");
        Uri uri = Uri.parse(uriString);

        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            imageView = (ImageView) findViewById(R.id.foodpartView1);
            Bitmap crop_bitmap = cropBitmap(bitmap);

            crop_bitmap = scaleBitmapDown(crop_bitmap,MAX_DIMENSION);
            imageView.setImageBitmap(crop_bitmap);
            Uri photo_uri = getImageUri(getApplicationContext(), crop_bitmap);

            SharedPreferences pref = getSharedPreferences("Cropped Image", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.clear();
            editor.putString("camera_crop", photo_uri.toString());
            editor.commit();
        } catch (IOException e) {
            e.printStackTrace();
        }



        Button confirm_button = (Button) findViewById(R.id.confirm);
        confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent process1 = new Intent(DietprocessGalleryActivity.this, DietprocessActivity2.class);
                startActivity(process1);
            }
        });

        Button takepicture_button = (Button)findViewById(R.id.takepicture);
        takepicture_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (PermissionUtils.requestPermission(DietprocessGalleryActivity.this, GALLERY_PERMISSIONS_REQUEST, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select a photo"), GALLERY_IMAGE_REQUEST);
                }
            }
        });
    }

    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public Bitmap cropBitmap(Bitmap original) {
        Bitmap result = Bitmap.createBitmap(original
                , original.getWidth()/20*3 //X 시작위치 (원본의 4/1지점)
                , original.getHeight()/10//Y 시작위치 (원본의 4/1지점)
                , original.getWidth()/20*15 // 넓이 (원본의 절반 크기)
                , original.getHeight()/10*8); // 높이 (원본의 절반 크기)
        if (result != original) {
            original.recycle();
        }
        return result;
    }

    private Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            //ToDo
            try {
                Bitmap bm = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                imageView = (ImageView) findViewById(R.id.foodpartView1);
                imageView.setImageBitmap(bm);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
