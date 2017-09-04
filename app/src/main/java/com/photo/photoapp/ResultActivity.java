package com.photo.photoapp;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.microsoft.projectoxford.face.contract.Face;

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

    //TODO metoo que chama a nova activity de products

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

            new AsyncRequestService(this).execute(
                    getIntent().getExtras().getString("uri"),
                    getIntent().getExtras().getString("key")
            );

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
