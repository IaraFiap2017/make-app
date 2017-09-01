package com.photo.photoapp;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity  {

    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int REQUEST_SELECT_IMAGE_IN_ALBUM = 2;

    // absolute path on DCIM/Camera
    private String mCurrentPhotoPath;

    // uri saved when file is created
    private Uri mCurrentPhotoUri;

    // Microsoft Cognitive Services Faces API
    private static final String SUBSCRIPTION_KEY = "760d5f1601e84cd495570647f492f1af";
    private static final String URI_BASE = "https://westcentralus.api.cognitive.microsoft.com/face/v1.0/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        grantPermissions();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {
            Intent resultActivityIntent = new Intent(this, ResultActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("key", SUBSCRIPTION_KEY);
            bundle.putString("uri", URI_BASE);

            if (requestCode == REQUEST_SELECT_IMAGE_IN_ALBUM) {
                mCurrentPhotoPath = AbsolutePathUtil.getRealPathFromURI_API19(this, data.getData());
                bundle.putInt("angle", 0);
            }

            if (requestCode == REQUEST_TAKE_PHOTO) {
                // adding the adjust rotation angle from taked photo on the bundle
                try {
                    bundle.putInt(
                            "angle",
                            ImageUtil.getImageRotationAngle(mCurrentPhotoUri, getContentResolver())
                    );
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            bundle.putString("mCurrentPhotoPath", mCurrentPhotoPath);
            resultActivityIntent.putExtras(bundle);
            startActivity(resultActivityIntent);
        }
    }

    private void grantPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE , Manifest.permission.INTERNET},
                1
        );
    }

    private File createImageFile() throws IOException {

        // create a unique filename
        String imageFileName = "JPEG_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + "_";

        // file directory on DCIM/Camera (external)
        File storageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
                "Camera"
        );

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // uri to use with ACTION_VIEW intents
        mCurrentPhotoUri = Uri.fromFile(image);

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void executeImageCaptureIntent() throws IOException {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            // Create the File where the photo should go
            File photoFile = null;
            photoFile = createImageFile();

            // Continue only if the File was successfully created
            if (photoFile != null) {

                // way to work the uri on 24+ api, using FileProvider with xml config on manifest
                /*
                mCurrentPhotoUri = FileProvider.getUriForFile(
                        this,
                        BuildConfig.APPLICATION_ID + ".provider",
                        photoFile
                );
                */

                takePictureIntent.putExtra(
                        MediaStore.EXTRA_OUTPUT,

                        // way to work the uri on 24+ api, using FileProvider with xml config on manifest
                        FileProvider.getUriForFile(
                            this,
                            BuildConfig.APPLICATION_ID + ".provider",
                            photoFile
                        )
                );

                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private void executeImageSelectIntent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        if (intent.resolveActivity(getPackageManager()) != null)
            startActivityForResult(intent, REQUEST_SELECT_IMAGE_IN_ALBUM);
    }

    public void take(View view) {
        try {
            executeImageCaptureIntent();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Um erro ocorreu =( " + e, Toast.LENGTH_LONG).show();
        }
    }

    public void select(View view) {
        executeImageSelectIntent();
    }
}