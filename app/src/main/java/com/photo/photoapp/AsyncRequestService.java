package com.photo.photoapp;

import android.os.AsyncTask;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;
import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.rest.ClientException;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class AsyncRequestService extends AsyncTask<String, Void, Face[]> {

    // we are on a separate thread, so we need the Activity to access it's properties
    private ResultActivity resultActivity;

    // Microsoft Oxford Cognitive Services Faces API
    private FaceServiceRestClient faceService;

    public AsyncRequestService(ResultActivity resultActivity) {
        this.resultActivity = resultActivity;
    }

    @Override
    protected Face[] doInBackground(String... params) {

        faceService = new FaceServiceRestClient(params[0], params[1]);

        try {
            return invokeOxfordAPIRequest(
                    new ByteArrayInputStream(
                            resultActivity.getOutputStream().toByteArray()
                    )
            );

        } catch (IOException e) {
            e.printStackTrace();
            return null;

        } catch (ClientException e) {
            e.printStackTrace();
            return null;
        }
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

    @Override
    protected void onPostExecute(Face[] result) {
        if(result != null) {
            if(result.length > 1)
                resultActivity
                        .getTxtResult()
                        .setText(new ResultBuilderPTBR().addErrorOnePersonAlowed().build());

            else if(result.length == 1) {

                // Saving the face on the activitie Thread!
                resultActivity.setFace(result[0]);

                // Build patterns gives a msg to the user
                resultActivity
                        .getTxtResult()
                        .setText(
                                new ResultBuilderPTBR()
                                        .addAge(result[0].faceAttributes.age)
                                        .addGender(result[0].faceAttributes.gender)
                                        .addFacialHair(result[0].faceAttributes.facialHair)
                                        .addGlasses(result[0].faceAttributes.glasses)
                                        .build()
                        );

                //TODO aparecer btns depois do resultado (chamar da activitie)
            }
            else
                resultActivity
                        .getTxtResult()
                        .setText(new ResultBuilderPTBR().addErrorBadPhoto().build());
        }
    }
}
