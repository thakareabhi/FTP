package com.example.feelthephoto;



import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.feelthephoto.ml.ASLmodel;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;

import static java.lang.Boolean.TRUE;

public class MainActivity extends AppCompatActivity {
    private ImageView imgView;
    private ImageButton capture;
    private Button predict;
    private TextView tv;
    private Bitmap img;
    Intent intent;
    final static int picbycamera=10;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgView=findViewById(R.id.imageView);
        tv=findViewById(R.id.textview_1);
        capture=findViewById(R.id.imageButton2);
        predict=findViewById(R.id.button);

        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,
                        picbycamera);
                Toast.makeText(MainActivity.this, "LOOK", Toast.LENGTH_SHORT).show();

            }
        });
        predict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 img= Bitmap.createScaledBitmap(img, 64, 64, true);

                try {
                    ASLmodel model = ASLmodel.newInstance(getApplicationContext());

                    // Creates inputs for reference.
                    TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 64, 64, 3}, DataType.FLOAT32);TensorImage tensorImage = new TensorImage(DataType.FLOAT32);
                    tensorImage.load(img);
                    ByteBuffer byteBuffer = tensorImage.getBuffer();
                    inputFeature0.loadBuffer(byteBuffer);
                    ASLmodel.Outputs outputs = model.process(inputFeature0);
                    TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
                    //tv.setText(outputFeature0.getFloatArray()[0] + "\n"+outputFeature0.getFloatArray()[1]);
                    float[] arr=outputFeature0.getFloatArray();
                    int max=getMax(arr);


                    for(int j=0;j<29;j++)
                    {
                        Log.d("val",arr[j]+"g");
                    }

                    int[] labels_list=new int[29];
                    for(int j=0;j<29;j++)
                    {
                        labels_list[j]=j+1;
                    }

                    tv.setText("ff="+labels_list[max]+"\n");

                } catch (IOException e) {
                    // TODO Handle the exception
                }

            }
        });


    }
    private int getMax(@NonNull float[] arr) {
        int ind = 0;
        float min = 0.0f;
        for (int i = 0; i < 29; i++) {
            if (arr[i] > min) {
                ind = i;
                min = arr[i];
            }
        }
        return ind;
    }

        @Override
    protected void onActivityResult(int requestCode,int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode,resultCode,data);


        if(requestCode == picbycamera) {
           img = (Bitmap) data.getExtras().get("data");
            imgView.setImageBitmap(img);
        }
    }
}