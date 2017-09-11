package com.photo.photoapp.activities;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.projectoxford.face.contract.Face;
import com.photo.photoapp.task.AsyncRequestService;
import com.photo.photoapp.util.ImageUtil;
import com.photo.photoapp.util.InternetUtil;
import com.photo.photoapp.R;
import com.photo.photoapp.builder.ResultBuilderPTBR;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ResultActivity extends AppCompatActivity {

    // Activity View Instances
    private TextView txtResult;
    private ImageView imgPreview;

    private Bitmap preparedBitmap;

    // stream that contains the compressed image
    private ByteArrayOutputStream outputStream;

    // THE FACE DETECTED!
    private Face face;

    public void setFace(Face face) {
        this.face = face;
    }

    public TextView getTxtResult() {
        return txtResult;
    }

    public ByteArrayOutputStream getOutputStream() {
        return outputStream;
    }

    public void returnToMainActivity(View view) {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        initInstances();

        try {
            preparedBitmap = loadAndRotateBitmap(
                    getIntent().getExtras().getString("mCurrentPhotoPath"),
                    getIntent().getExtras().getInt("angle")
            );

            imgPreview.setImageBitmap(preparedBitmap);

            // compress inside OutputStream of Byte[]
            outputStream = new ByteArrayOutputStream();
            preparedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

            if(InternetUtil.isNetworkAvailable(this)) {
                new AsyncRequestService(this).execute(
                        getIntent().getExtras().getString("uri"),
                        getIntent().getExtras().getString("key")
                );
            } else {
                Toast
                    .makeText(this, new ResultBuilderPTBR().addNoInternetError().build(), Toast.LENGTH_LONG)
                    .show();
                finish();
            }

        } catch (IOException e) {
            e.printStackTrace();
            txtResult.setText(new ResultBuilderPTBR().addIOError().build());
        }
    }

    private void initInstances() {
        imgPreview = (ImageView) findViewById(R.id.imgPreview);
        txtResult = (TextView) findViewById(R.id.txtResponse);
    }

    private Bitmap loadAndRotateBitmap(String mCurrentPhotoPath, int angle) throws IOException {
        return ImageUtil.rotateBitmap(
                ImageUtil.loadSizeLimitedBitmap(mCurrentPhotoPath),
                angle
        );
    }
}
