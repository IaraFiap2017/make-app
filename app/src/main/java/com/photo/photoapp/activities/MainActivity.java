package com.photo.photoapp.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.photo.photoapp.util.AbsolutePathUtil;
import com.photo.photoapp.BuildConfig;
import com.photo.photoapp.util.ImageUtil;
import com.photo.photoapp.util.PermissionManagerUtil;
import com.photo.photoapp.R;
import com.photo.photoapp.builder.ResultBuilderPTBR;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity  {

    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int REQUEST_SELECT_IMAGE_IN_ALBUM = 2;

    // absolute path on DCIM/Camera
    private static String mCurrentPhotoPath;

    // uri saved when file is created
    private static Uri mCurrentPhotoUri;

    private PermissionManagerUtil permissionManagerUtil;

    // Microsoft Cognitive Services Faces API
    private static final String SUBSCRIPTION_KEY = "6ceb3cc127d446119b9c1527119b721e";
    private static final String URI_BASE = "https://westcentralus.api.cognitive.microsoft.com/face/v1.0/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //grantPermissions();
        permissionManagerUtil = new PermissionManagerUtil(MainActivity.this);
        permissionManagerUtil.managePermissions(REQUEST_TAKE_PHOTO);
    }

    public void takePicture(View view) {
        try {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

                // Create the File where the photo should go
                File photoFile = null;
                photoFile = createImageFile();

                // Continue only if the File was successfully created
                if (photoFile != null) {

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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(!permissionManagerUtil.checkPermissions()) {
            Toast.makeText(
                    this,
                    new ResultBuilderPTBR().addNoPermissionClosesApp().build(),
                    Toast.LENGTH_SHORT
            ).show();

            finish();
        }
    }

    public void selectPicture(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        if (intent.resolveActivity(getPackageManager()) != null)
            startActivityForResult(intent, REQUEST_SELECT_IMAGE_IN_ALBUM);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if((resultCode == RESULT_OK) && (requestCode == REQUEST_TAKE_PHOTO || requestCode == REQUEST_SELECT_IMAGE_IN_ALBUM)) {
            boolean hasURI = true;

            if (requestCode == REQUEST_SELECT_IMAGE_IN_ALBUM) {
                try{
                    mCurrentPhotoPath = AbsolutePathUtil.getRealPathFromURI_API19(this, data.getData());
                    mCurrentPhotoUri = Uri.fromFile(new File(mCurrentPhotoPath));
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast
                        .makeText(this, new ResultBuilderPTBR().addNoIndexImageError().build(), Toast.LENGTH_LONG)
                        .show();
                    hasURI = false;
                }
            }

            if (hasURI) {

                // creating the intent for ResultActivity
                Intent resultActivityIntent = new Intent(this, ResultActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("key", SUBSCRIPTION_KEY);
                bundle.putString("uri", URI_BASE);

                // adding the adjust rotation angle from photo on the bundle
                try {
                    bundle.putInt(
                            "angle",
                            ImageUtil.getImageRotationAngle(mCurrentPhotoUri, getContentResolver())
                    );

                    /* Use instead the angle below with an emulator on a webcam */
                    //bundle.putInt("angle", 90);

                    bundle.putString("mCurrentPhotoPath", mCurrentPhotoPath.toString());
                    resultActivityIntent.putExtras(bundle);
                    resetStaticAttributes();
                    startActivity(resultActivityIntent);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast
                        .makeText(this, new ResultBuilderPTBR().addIOError().build(), Toast.LENGTH_LONG)
                        .show();
                }
            }

        }
    }

    private void resetStaticAttributes() {
        mCurrentPhotoPath = null;
        mCurrentPhotoUri = null;
    }
}