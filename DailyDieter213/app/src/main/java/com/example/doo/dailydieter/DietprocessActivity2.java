package com.example.doo.dailydieter;
/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequest;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static com.example.doo.dailydieter.DietprocessCameraActivity.decodeBase64;


public class DietprocessActivity2 extends AppCompatActivity {
    private static final String CLOUD_VISION_API_KEY = "AIzaSyApJMlz5YIYQOm7NxAvjCVYegi9uZ3Hbec";
    private static final String ANDROID_CERT_HEADER = "X-Android-Cert";
    private static final String ANDROID_PACKAGE_HEADER = "X-Android-Package";
    private static final int MAX_LABEL_RESULTS = 7;
    private static final int MAX_DIMENSION = 1200;

    private static final int INPUT_SIZE = 299;
    private static final int IMAGE_MEAN = 0;
    private static final float IMAGE_STD = 255.0f;
    private static final String INPUT_NAME = "Placeholder";
    private static final String OUTPUT_NAME = "final_result";

    private static final String MODEL_FILE = "file:///android_asset/rounded_graph.pb";
    private static final String LABEL_FILE = "file:///android_asset/rounded_labels.txt";
    private Classifier classifier;
    private Executor executor = Executors.newSingleThreadExecutor();
    private Bitmap bm2;
    private Bitmap cropped1;
    private static final String TAG = MainActivity.class.getSimpleName();

    private ImageView imageView;
    String[] items = { "Kimchi", "GImbap", "Rice", "Kimchi Jjigae", "sullungtang", "fried egg", "scramble", "tomato", "cabbage kimchi", "myeolchi bokkeum"};

    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory
                .decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity2_dietprocess);

        //Get original image
        SharedPreferences spref = getSharedPreferences("Cropped Image", Activity.MODE_PRIVATE);
        String cropped = spref.getString("camera_crop", null);
        Uri get_uri = Uri.parse(cropped);

        try {
            cropped1 = scaleBitmapDown(
                            MediaStore.Images.Media.getBitmap(getContentResolver(), get_uri),
                            MAX_DIMENSION);
        } catch (IOException e) {
            e.printStackTrace();
        }
        cropped1 = cropBitmap1(cropped1);

        int i = 0;
        while(i==0){
            i = initTensorFlowAndLoadModel();
        }

        imageView = (ImageView)findViewById(R.id.main_image);
        imageView.setImageBitmap(cropped1);
        TextView textView = (TextView)findViewById(R.id.image_details);
        String str = "Wait for getting labels from image";
        textView.setText(str);

        Button direct_typing = (Button)findViewById(R.id.typing_1);
        direct_typing.setEnabled(false);

        uploadImage(cropped1);

        direct_typing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder ad = new AlertDialog.Builder(DietprocessActivity2.this);

                ad.setTitle("Food Classification");       // 제목 설정
                ad.setMessage("Type name of food");   // 내용 설정

                final AutoCompleteTextView et = new AutoCompleteTextView(DietprocessActivity2.this);
                ad.setView(et);
                et.setAdapter(new ArrayAdapter<String>(et.getContext(), android.R.layout.simple_dropdown_item_1line, items));



                ad.setPositiveButton("Type", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String value = et.getText().toString();
                        Intent topintent = new Intent(DietprocessActivity2.this ,DietprocessActivity3.class);
                        topintent.putExtra("choice_food1", value);
                        startActivity(topintent);
                        dialog.dismiss();     //닫기
                        finish();

                        // Event
                    }
                });
                ad.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();     //닫기
                        // Event
                    }
                });

                ad.show();
            }
        });
    }
    public Bitmap cropBitmap1(Bitmap original) {
        Bitmap result = Bitmap.createBitmap(original
                , 0 //X 시작위치 (원본의 4/1지점)
                , 0//Y 시작위치 (원본의 4/1지점)
                , original.getWidth()/3 // 넓이 (원본의 절반 크기)
                , original.getHeight()/5*2); // 높이 (원본의 절반 크기)
        if (result != original) {
            original.recycle();
        }
        return result;
    }

    public void callCloudVision(final Bitmap bitmap) {

        // Do the real work in an async task, because we need to use the network anyway
        try {
            AsyncTask<Object, Void, ArrayList<FoodRecognition>> labelDetectionTask = new LabelDetectionTask(this, prepareAnnotationRequest(bitmap));
            labelDetectionTask.execute();
        } catch (IOException e) {
            Log.d(TAG, "failed to make API request because of other IOException " +
                    e.getMessage());
        }
    }

    public void uploadImage(Bitmap bitmap) {
        if (bitmap != null) {
            callCloudVision(bitmap);
        }
    }

    private Vision.Images.Annotate prepareAnnotationRequest(Bitmap bitmap) throws IOException {
        HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

        VisionRequestInitializer requestInitializer =
                new VisionRequestInitializer(CLOUD_VISION_API_KEY) {
                    /**
                     * We override this so we can inject important identifying fields into the HTTP
                     * headers. This enables use of a restricted cloud platform API key.
                     */
                    @Override
                    protected void initializeVisionRequest(VisionRequest<?> visionRequest)
                            throws IOException {
                        super.initializeVisionRequest(visionRequest);

                        String packageName = getPackageName();
                        visionRequest.getRequestHeaders().set(ANDROID_PACKAGE_HEADER, packageName);

                        String sig = PackageManagerUtils.getSignature(getPackageManager(), packageName);

                        visionRequest.getRequestHeaders().set(ANDROID_CERT_HEADER, sig);
                    }
                };

        Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
        builder.setVisionRequestInitializer(requestInitializer);

        Vision vision = builder.build();

        BatchAnnotateImagesRequest batchAnnotateImagesRequest =
                new BatchAnnotateImagesRequest();
        batchAnnotateImagesRequest.setRequests(new ArrayList<AnnotateImageRequest>() {{
            AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();

            // Add the image
            Image base64EncodedImage = new Image();
            // Convert the bitmap to a JPEG
            // Just in case it's a format that Android understands but Cloud Vision
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();

            // Base64 encode the JPEG
            base64EncodedImage.encodeContent(imageBytes);
            annotateImageRequest.setImage(base64EncodedImage);

            // add the features we want
            annotateImageRequest.setFeatures(new ArrayList<Feature>() {{
                Feature labelDetection = new Feature();
                labelDetection.setType("LABEL_DETECTION");
                labelDetection.setMaxResults(MAX_LABEL_RESULTS);
                add(labelDetection);
            }});

            // Add the list of one thing to the request
            add(annotateImageRequest);
        }});

        Vision.Images.Annotate annotateRequest =
                vision.images().annotate(batchAnnotateImagesRequest);
        // Due to a bug: requests to Vision API containing large images fail when GZipped.
        annotateRequest.setDisableGZipContent(true);
        return annotateRequest;
    }

    private class LabelDetectionTask extends AsyncTask<Object, Void, ArrayList<FoodRecognition>> {
        private final WeakReference<DietprocessActivity2> mActivityWeakReference;
        private Vision.Images.Annotate mRequest;

        LabelDetectionTask(AppCompatActivity activity, Vision.Images.Annotate annotate) {
            mActivityWeakReference = new WeakReference(activity);
            mRequest = annotate;

        }

        @Override
        protected ArrayList<FoodRecognition> doInBackground(Object... params) {
            try {
                mRequest.setDisableGZipContent(true);
                BatchAnnotateImagesResponse response = mRequest.execute();
                Log.d(TAG, "created Cloud Vision request object, sending request");

                return convertResponseToString(response);

            } catch (GoogleJsonResponseException e) {
                Log.d(TAG, "failed to make API request because " + e.getContent());
            } catch (IOException e) {
                Log.d(TAG, "failed to make API request because of other IOException " +
                        e.getMessage());
            }
            ArrayList<FoodRecognition> arraylist = new ArrayList<>();
            float a = 0;
            FoodRecognition fr2 = new FoodRecognition(a, "Cloud Vision API request failed. Check logs for details.");
            arraylist.add(fr2);
            return arraylist;
        }

        protected void onPostExecute(ArrayList<FoodRecognition> result) {
            super.onPostExecute(result);
            TextView textView = (TextView)findViewById(R.id.image_details);
            String str = "Complete classification. Select below or Type food pushing the button.";
            textView.setText(str);

            Button direct_typing = (Button)findViewById(R.id.typing_1);
            direct_typing.setEnabled(true);

            DietprocessActivity2 activity = mActivityWeakReference.get();
            ListView listview = (ListView) activity.findViewById(R.id.listview1) ;

            try {
                Log.d("thread sleep", "start");
                Thread.sleep(1000);
                Log.d("thread wake", "end");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 학습 모델 적용
            for(int i =0; i< result.size(); i++) {
                String check_name = result.get(i).getNamefood();
                if(check_name.equals("korean food") | check_name.equals("side dish") | check_name.equals("chinese food") | check_name.equals("chinese noodles")) {
                    recognize_bitmap(cropped1, result);
                }
            }
            if(result.size() <= 3){
                recognize_bitmap(cropped1, result);

            }
            tfDestroy();
            result = checkName2(result);
            Collections.sort(result, (o1, o2) -> {
                if(o1.getScore() < o2.getScore()){
                    return 1;
                } else if(o1.getScore() > o2.getScore()) {
                    return -1;
                }
                return 0;
            });

            //리스트에서 눌렀을 때, 보내야 할 값과 선택된 값 보내기
            Intent in3 = getIntent();

//            byte[] arr3 = in3.getByteArrayExtra("picture_process_2");
//            byte[] arr4 = in3.getByteArrayExtra("picture_process_3");
//            byte[] arr5 = in3.getByteArrayExtra("picture_process_4");
//            byte[] arr6 = in3.getByteArrayExtra("picture_process_5");

            if (activity != null && !activity.isFinishing()) {
                ListView list = (ListView) findViewById(R.id.listview1);
                // Pass results to ListViewAdapter Class
                ListViewAdapterFoodRecognition adapter = new ListViewAdapterFoodRecognition(DietprocessActivity2.this, result);

                // Binds the Adapter to the ListView
                listview.setAdapter(adapter);
            }
            ArrayList<FoodRecognition> finalResult = result;
            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView parent, View v, int position, long id) {


                    Intent topintent = new Intent(activity ,DietprocessActivity3.class);

//                    topintent.putExtra("picture_process2",arr3);
//                    topintent.putExtra("picture_process3",arr4);
//                    topintent.putExtra("picture_process4",arr5);
//                    topintent.putExtra("picture_process5",arr6);
                    String strText = finalResult.get(position).getNamefood();
                    topintent.putExtra("choice_food1", strText);
                    activity.startActivity(topintent);
                    finish();
                }
            }) ;
        }
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

    private static ArrayList<FoodRecognition> convertResponseToString(BatchAnnotateImagesResponse response) {
//        StringBuilder message = new StringBuilder("I found these things:\n\n");
        ArrayList<FoodRecognition> arraylist = new ArrayList<>();
        List<EntityAnnotation> labels = response.getResponses().get(0).getLabelAnnotations();
        try {

            if (labels != null) {
                for (EntityAnnotation label : labels) {
                    FoodRecognition fr = new FoodRecognition(label.getScore(), label.getDescription());
                    if(fr.namefood != null) {
                        arraylist.add(fr);
                    }
//                message.append(String.format(Locale.US, "%.3f: %s", label.getScore(), label.getDescription()));
//                message.append("\n");
                }
                checkName(arraylist);
            } else {
//            message.append("nothing");
            }return arraylist;
        }catch (Exception e) {
            float a = 0;
            FoodRecognition fr2 = new FoodRecognition(a, "결과값이 나오지 않았습니다. 직접기입을 해주세요");
            arraylist.add(fr2);
            return arraylist;
        }

//        return message.toString();
    }
    public static ArrayList<FoodRecognition> checkName(ArrayList<FoodRecognition> al) {
        String[] name_list_remove = new String[]{"food", "cuisine",  "japanese cuisine", "asian food", "dish", "fried food", "italian food", "european food",
                "fish products", "ingredient", "recipe", "comfort food", "natural foods", "fruit", "vegetables", "local food", "produce", "superfood", "vegetarian food", "dairy food", "commodity", "finger food",
                "chinese food", "appetizer", "animal source foods", "noodle soup", "southeast food", "vegetable", "nightshade family", "potato and tomato genus", "still life photography"};


        Stack stack = new Stack(al.size());

        for(int i =0; i<al.size(); i++){
            String getname = al.get(i).getNamefood();
            for(int j=0; j<name_list_remove.length; j++){
                if(name_list_remove[j].equals(getname)){
                    stack.push(i);
                }
            }
        }

        while(!stack.isEmpty()) {
            int i = (int) stack.pop();
            al.remove(i);
        }
        return al;
    }
    public static ArrayList<FoodRecognition> checkName2(ArrayList<FoodRecognition> al) {
        String[] name_list_remove = new String[]{"korean food", "side dish", "chinese food", "chinese noodles"};
        Stack stack = new Stack(al.size());

        for(int i =0; i<al.size(); i++){
            String getname = al.get(i).getNamefood();
            for(int j=0; j<name_list_remove.length; j++){
                if(name_list_remove[j].equals(getname)){
                    stack.push(i);
                }
            }
        }

        while(!stack.isEmpty()) {
            int i = (int) stack.pop();
            al.remove(i);
        }
        return al;
    }
    private void recognize_bitmap(Bitmap bitmap, ArrayList<FoodRecognition> al) {

        // 비트맵을 처음에 정의된 INPUT SIZE에 맞춰 스케일링
        bitmap = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, false);
        Log.d("recognize bitmap", bitmap.toString());

// classifier 의 recognizeImage 부분이 실제 inference 를 호출해서 인식작업을 하는 부분입니다.
        final List<Classifier.Recognition> results = classifier.recognizeImage(bitmap);
        // 결과값은 Classifier.Recognition 구조로 리턴, 원래는 여기서 결과값을 배열로 추출가능
        for ( int i=0; i<results.size(); i++) {

            FoodRecognition fr = new FoodRecognition( results.get(i).getConfidence(), results.get(i).getTitle());
            al.add(fr);
            Log.d("들어옴", "들어옴");
        }
    }
    private int initTensorFlowAndLoadModel() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    classifier = TensorFlowImageClassifier.create(
                            getAssets(),
                            MODEL_FILE,
                            LABEL_FILE,
                            INPUT_SIZE,
                            IMAGE_MEAN,
                            IMAGE_STD,
                            INPUT_NAME,
                            OUTPUT_NAME);
                } catch (final Exception e) {
                    throw new RuntimeException("Error initializing TensorFlow!", e);
                }
            }
        });
        return 1;
    }


    protected void tfDestroy() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                classifier.close();
            }
        });
    }
}
