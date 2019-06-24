package com.example.doo.dailydieter;


import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class CameraActivity extends Activity implements SurfaceHolder.Callback{

    Camera camera;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    boolean previewing = false;
    LayoutInflater controlInflater = null;
    FloatingActionButton buttonTakePicture;
    private static final int MAX_DIMENSION = 1200;

//    final int RESULT_SAVEIMAGE = 0;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera2);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().setFormat(PixelFormat.UNKNOWN);
        surfaceView = (SurfaceView)findViewById(R.id.camerapreview);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        DrawOnTop mDraw = new DrawOnTop(this);

        controlInflater = LayoutInflater.from(getBaseContext());
        View viewControl = controlInflater.inflate(R.layout.control, null);
        LayoutParams layoutParamsControl
                = new LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.FILL_PARENT);
        this.addContentView(viewControl, layoutParamsControl);
        this.addContentView(mDraw, layoutParamsControl);

        buttonTakePicture = (FloatingActionButton)findViewById(R.id.takepicture);
        buttonTakePicture.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                camera.takePicture(myShutterCallback,
                        myPictureCallback_RAW, myPictureCallback_JPG);
            }});

        RelativeLayout layoutBackground = (RelativeLayout)findViewById(R.id.background);
        layoutBackground.setOnClickListener(new LinearLayout.OnClickListener(){

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                buttonTakePicture.setEnabled(false);
                camera.autoFocus(myAutoFocusCallback);
            }});
    }

    AutoFocusCallback myAutoFocusCallback = new AutoFocusCallback(){

        @Override
        public void onAutoFocus(boolean arg0, Camera arg1) {
            // TODO Auto-generated method stub
            buttonTakePicture.setEnabled(true);
        }};

    ShutterCallback myShutterCallback = new ShutterCallback(){

        @Override
        public void onShutter() {
            // TODO Auto-generated method stub

        }};

    PictureCallback myPictureCallback_RAW = new PictureCallback(){

        @Override
        public void onPictureTaken(byte[] arg0, Camera arg1) {
            // TODO Auto-generated method stub

        }};

    PictureCallback myPictureCallback_JPG = new PictureCallback(){

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            // TODO Auto-generated method stub
            Intent return_intent = new Intent(CameraActivity.this, DietprocessCameraActivity.class);

            Bitmap bitmapPicture
                = BitmapFactory.decodeByteArray(data, 0, data.length);

            Bitmap crop_bitmap = cropBitmap(bitmapPicture);

            crop_bitmap = scaleBitmapDown(crop_bitmap,MAX_DIMENSION);
            Uri photo_uri = getImageUri(getApplicationContext(), crop_bitmap);
            SharedPreferences pref = getSharedPreferences("Cropped Image", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.clear();
            editor.putString("camera_crop", photo_uri.toString());
            editor.commit();

            startActivity(return_intent);
            finish();

        }
    };

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

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        // TODO Auto-generated method stub
        if(previewing){
            camera.stopPreview();
            previewing = false;
        }

        if (camera != null){
            try {
                camera.setPreviewDisplay(surfaceHolder);
                camera.startPreview();
                previewing = true;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        camera = Camera.open();

        camera.setDisplayOrientation(0);

        try {

            camera.setPreviewDisplay(holder);

            int m_resWidth;

            int m_resHeight;

            m_resWidth = camera.getParameters().getPictureSize().width;

            m_resHeight = camera.getParameters().getPictureSize().height;

            Camera.Parameters parameters = camera.getParameters();

//아래 숫자를 변경하여 자신이 원하는 해상도로 변경한다
//ToDo 해상도 변경 하는 곳
//            m_resWidth = 1600;
//
//            m_resHeight = 900;

            parameters.setPictureSize(m_resWidth, m_resHeight);

            camera.setParameters(parameters);

        } catch (IOException e) {

            camera.release();

            camera = null;

        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        camera.stopPreview();
        camera.release();
        camera = null;
        previewing = false;
    }
}
class DrawOnTop extends View {


    public DrawOnTop(Context context) {

        super(context);

        // TODO Auto-generated constructor stub

    }


    @Override

    protected void onDraw(Canvas canvas) {

        // TODO Auto-generated method stub


        Paint paint = new Paint();

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.LTGRAY);                    //

        paint.setStrokeWidth(10);                     // 크기 10
        paint.setTextSize(20);
        canvas.drawRect(getWidth()/10*2, getHeight()/10*1, getWidth()/10*8,getHeight()*9/10,paint); //사각형그리기
//        canvas.drawLine(50, 50, 200, 200, paint);    // 라인그리기

        super.onDraw(canvas);

    }

}