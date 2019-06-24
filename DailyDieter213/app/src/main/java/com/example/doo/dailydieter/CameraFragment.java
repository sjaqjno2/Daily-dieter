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

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import static android.app.Activity.RESULT_OK;


public class CameraFragment extends Fragment {
    private static final int GALLERY_PERMISSIONS_REQUEST = 0;
    private static final int GALLERY_IMAGE_REQUEST = 1;
    public static final int CAMERA_PERMISSIONS_REQUEST = 2;

    public CameraFragment() {

    }
    public static CameraFragment newInstance() {
        CameraFragment fragment = new CameraFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            final RelativeLayout mRelativeLayout = (RelativeLayout) inflater.inflate(R.layout.fragment_camera, container, false);
            Button camerabutton = (Button) mRelativeLayout.findViewById(R.id.Camera_Execution);
            camerabutton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (PermissionUtils.requestPermission(getActivity(), CAMERA_PERMISSIONS_REQUEST, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)) {
                        Intent intent = new Intent(getActivity(), CameraActivity.class);
                        startActivity(intent);
                    }
                }
            });
            Button gallerybutton = (Button) mRelativeLayout.findViewById(R.id.Gallery_Execution);
            gallerybutton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (PermissionUtils.requestPermission(getActivity(), GALLERY_PERMISSIONS_REQUEST, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        Log.d("gallery", "start");
                        startActivityForResult(Intent.createChooser(intent, "Select a photo"), GALLERY_IMAGE_REQUEST);
                    }
                }
            });




        return mRelativeLayout;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            //ToDo
            Intent start_intent = new Intent(getActivity(), DietprocessGalleryActivity.class);
            start_intent.putExtra("gallery_data", data.getData().toString());
            Log.d("gallery2", "start");
            startActivity(start_intent);
        }
    }
}
