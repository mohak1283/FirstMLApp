package com.example.android.firstmlapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.media.Image;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.google.firebase.ml.vision.label.FirebaseVisionLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionLabelDetector;
import com.google.firebase.ml.vision.label.FirebaseVisionLabelDetectorOptions;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class MainActivity extends AppCompatActivity {

    FloatingActionButton cameraButton;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    Bitmap imageBitmap;
    ImageView imageView;
    private static final String TAG = MainActivity.class.getSimpleName();
    int result;
    Button detectButton;
    TextView textTextView;
    TextView confidenceTextView;
    RecyclerView recyclerView;
    MainAdapter mainAdapter;
    ArrayList<FirebaseVisionLabel> firebaseVisionLabels = new ArrayList<>() ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);

        cameraButton = findViewById(R.id.camera_fab);
        imageView = findViewById(R.id.captured_image);
        detectButton = findViewById(R.id.detect_button);
        textTextView = findViewById(R.id.text);
        confidenceTextView = findViewById(R.id.confidence);

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dispatchTakePictureIntent();

            }
        });


        detectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.e(TAG, "Value of firebaseImageLabels " + firebaseVisionLabels.size());
                runImageRecognition();

                if (firebaseVisionLabels.isEmpty()) {

                    Toast.makeText(getApplicationContext(), "Can't Identify Image, Try different one", Toast.LENGTH_SHORT).show();
                }

            }
        });


        recyclerView = findViewById(R.id.rec_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
        }
    }

    private void runImageRecognition() {

       // FirebaseVisionImage image = FirebaseVisionImage.fromMediaImage(imageBitmap, result);
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(imageBitmap);

      /**  FirebaseVisionLabelDetectorOptions options =
                new FirebaseVisionLabelDetectorOptions.Builder()
                        .setConfidenceThreshold(0.8f)
                        .build(); */

         FirebaseVisionLabelDetector detector = FirebaseVision.getInstance()
         .getVisionLabelDetector();

        Task<List<FirebaseVisionLabel>> resultImage =
                detector.detectInImage(image)
                        .addOnSuccessListener(
                                new OnSuccessListener<List<FirebaseVisionLabel>>() {
                                    @Override
                                    public void onSuccess(List<FirebaseVisionLabel> labels) {
                                        // Task completed successfully
                                        // ...
                                        detectButton.setEnabled(false);
                                        for (FirebaseVisionLabel label: labels) {
                                            String text = label.getLabel();
                                            String entityId = label.getEntityId();
                                            float confidence = label.getConfidence();

                                            firebaseVisionLabels.add(label);

                                         //   textTextView.setText(text);
                                           // confidenceTextView.setText(String.valueOf(confidence));
                                            mainAdapter = new MainAdapter(getApplicationContext(), firebaseVisionLabels);
                                            recyclerView.setAdapter(mainAdapter);



                                        }
                                    }
                                })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Task failed with an exception
                                        // ...
                                        detectButton.setEnabled(true);
                                        Toast.makeText(getApplicationContext(), "Something Went wrong! Try Again", Toast.LENGTH_SHORT).show();
                                    }
                                });


    }



    @Override
    protected void onResume() {
        super.onResume();
        detectButton.setEnabled(true);
        detectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runImageRecognition();
            }
        });
        firebaseVisionLabels.clear();
    }
}
