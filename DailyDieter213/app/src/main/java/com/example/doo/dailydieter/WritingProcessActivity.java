package com.example.doo.dailydieter;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.text.TextWatcher;
import android.widget.TextView;

import com.bumptech.glide.Glide;


public class WritingProcessActivity extends Activity {
    EditText title;
    EditText content;
    public static final int GALLERY_PERMISSIONS_REQUEST = 0;
    public static final int GALLERY_IMAGE_REQUEST = 1;
    ImageView image_view;
    public static String image;
    private final int m_nMaxLengthOfDeviceName = 60000;
    public TextView flexibleLength;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.writing);
        flexibleLength = findViewById(R.id.flexibleLength);
        title = (EditText) findViewById(R.id.title);
        content = (EditText) findViewById(R.id.content);
        content.addTextChangedListener(textWatcher);
        content.setFilters(new InputFilter[] { new InputFilter.LengthFilter(m_nMaxLengthOfDeviceName) });
        Button updateButton = (Button)findViewById(R.id.update);
        image_view = (ImageView) findViewById(R.id.imageView);


        Button gallerybutton = (Button) findViewById(R.id.Gallery_Execution);
        gallerybutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (PermissionUtils.requestPermission(WritingProcessActivity.this, GALLERY_PERMISSIONS_REQUEST, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select a photo"), GALLERY_IMAGE_REQUEST);
                }
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                String title1 = title.getText().toString();
                String content1 = content.getText().toString();

                Intent intent = new Intent(WritingProcessActivity.this, WritingPostInsertingActivity.class);

                intent.putExtra("title", title1);
                intent.putExtra("content", content1);
                intent.putExtra("image", image);

                startActivity(intent);

            }
        });
    }
    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable edit) {
            // Text가 바뀌고 동작할 코드
            flexibleLength.setText(String.valueOf(content.length()));
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // Text가 바뀌기 전 동작할 코드
        }

        //
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // 입력받은 값에 100을 곱한다
            flexibleLength.setText(String.valueOf(content.length()));
        }
    };
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            //ToDo
            image = data.getData().toString();
            Glide.with(WritingProcessActivity.this).load(image).into(image_view);
        }
    }




}