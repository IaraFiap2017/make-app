package com.photo.photoapp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;
import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.rest.ClientException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ResultActivity extends AppCompatActivity {

    // Activity View Instances
    /*
    private ImageView imgPreview;
    private TextView txtResult;
    */

    // Angle to rotate bitmap before sending
    private int angle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        //initInstances();

        angle = getIntent().getExtras().getInt("angle");

        new ResultActivity.AsyncRequestService(this).execute(
                getIntent().getExtras().getString("uri"),
                getIntent().getExtras().getString("key"),
                getIntent().getExtras().getString("mCurrentPhotoPath")
        );
    }

    /*
    private void initInstances() {
        imgPreview = (ImageView) findViewById(R.id.imgPreview);
        txtResult = (TextView) findViewById(R.id.txtResponse);
    }
    */

    private class AsyncRequestService extends AsyncTask<String, Void, Face[]> {

        // we are on a separate thread, so we need the Activity and it's instances here
        private Activity activity;
        //private ImageView imgPreview;
        private TextView txtResult;

        private Bitmap preparedBitmap;

        // Microsoft Oxford Cognitive Services Faces API
        private FaceServiceRestClient faceService;

        public AsyncRequestService(Activity activity) {
            this.activity = activity;
            initInstances();
        }

        @Override
        protected Face[] doInBackground(String... params) {

            faceService = new FaceServiceRestClient(params[0], params[1]);

            try {
                preparedBitmap = loadAndRotateBitmap(params[2]);
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                preparedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
                return invokeOxfordAPIRequest(new ByteArrayInputStream(output.toByteArray()));

            } catch (IOException e) {
                e.printStackTrace();
                return null;

            } catch (ClientException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Face[] result) {
            ImageView imgPreview = (ImageView) findViewById(R.id.imgPreview);
            imgPreview.setImageBitmap(preparedBitmap);

            if(result != null) {
                if(result.length > 1)
                    txtResult.setText(new ResultBuilderPTBR().addOnlyOneMsg().build());

                else if(result.length == 1) {
                    txtResult.setText(
                            new ResultBuilderPTBR()
                                    .addAge(result[0].faceAttributes.age)
                                    .addGender(result[0].faceAttributes.gender)
                                    .addFacialHair(result[0].faceAttributes.facialHair)
                                    .addGlasses(result[0].faceAttributes.glasses)
                                    .build()
                    );
                }
                else
                    txtResult.setText(new ResultBuilderPTBR().addBadPhotoMsg().build());
            }
        }

        private void initInstances() {
            //imgPreview = activity.findViewById(R.id.imgPreview);
            txtResult = activity.findViewById(R.id.txtResponse);
        }

        private Bitmap loadAndRotateBitmap(String mCurrentPhotoPath) throws IOException {
            return ImageUtil.rotateBitmap(
                    ImageUtil.loadSizeLimitedBitmap(mCurrentPhotoPath),
                    angle
            );
        }


        /* ADITIONAL WORKING PARAMETERS */
        /*
            FaceServiceClient.FaceAttributeType.Smile,
            FaceServiceClient.FaceAttributeType.FacialHair,
            FaceServiceClient.FaceAttributeType.HeadPose,
         */
        private Face[] invokeOxfordAPIRequest(ByteArrayInputStream inputStream) throws IOException, ClientException {
            return faceService.detect(
                    inputStream,    // Input stream of image to detect
                    true,           // Whether to return face ID
                    true,           // Whether to return face landmarks
                    new FaceServiceClient.FaceAttributeType[]{
                            FaceServiceClient.FaceAttributeType.Age,
                            FaceServiceClient.FaceAttributeType.Gender,
                            FaceServiceClient.FaceAttributeType.Glasses,
                            FaceServiceClient.FaceAttributeType.FacialHair
                    }
            );
        }
    }
}
