package com.photo.photoapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Build;
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


    //public static final int EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE_BY_GALLERY = 0;
    //public static final int EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE_BY_CAMERA = 1;
    //public static final int CAMERA_PERMISSION_REQUEST_CODE = 3;

    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int REQUEST_SELECT_IMAGE_IN_ALBUM = 2;
    //private static final int REQUEST_CAMERA_RESULT = 11;

    // absolute path on DCIM/Camera
    private static String mCurrentPhotoPath;

    // uri saved when file is created
    private static Uri mCurrentPhotoUri;

    private MarshMallowPermission marshMallowPermission;

    // Microsoft Cognitive Services Faces API
    private static final String SUBSCRIPTION_KEY = "760d5f1601e84cd495570647f492f1af";
    private static final String URI_BASE = "https://westcentralus.api.cognitive.microsoft.com/face/v1.0/";

    /*
    private void grantPermissions() {


        // In the case the app is running on Android Marshmallow or newer, it checks for runtime permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            /*
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_TAKE_PHOTO);
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_SELECT_IMAGE_IN_ALBUM);

            }


            if(checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_TAKE_PHOTO);
            }

            /*
            if(checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.INTERNET}, REQUEST_TAKE_PHOTO);
            }

            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE , Manifest.permission.INTERNET}, REQUEST_TAKE_PHOTO);

        ActivityCompat.requestPermissions(
                this,
                new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE , Manifest.permission.INTERNET},
                1
        );

    }
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //grantPermissions();
        marshMallowPermission = new MarshMallowPermission(MainActivity.this);



        if (!marshMallowPermission.checkPermissionForExternalStorage()) {
            marshMallowPermission.requestPermissionForExternalStorage(MarshMallowPermission.EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE_BY_LOAD_PROFILE);
        }
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

                    //takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    //takePictureIntent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                    //takePictureIntent.addFlags(Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);
                    //takePictureIntent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Erro: " + e, Toast.LENGTH_LONG).show(); //TODO tirar isso daqui ... nao tem que acontecer esse erro
        }
    }

    /*
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode == REQUEST_CAMERA_RESULT && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, new ResultBuilderPTBR().addCameraPermisisonError().build(), Toast.LENGTH_LONG).show();
            finish();
        }
        else
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    */

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MarshMallowPermission.EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE_BY_LOAD_PROFILE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //permission granted successfully

                } else {

                    //permission denied

                }
                break;
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
}