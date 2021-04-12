package com.example.feelthephoto;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.feelthephoto.ml.AslSavedModel;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class MainActivity extends AppCompatActivity {
    ImageButton imageButton;
    Button button;
    ImageView imageView;
    Intent intent;
    Bitmap bitmp;
    TextView textview;
    ByteBuffer bytebuffer=ByteBuffer.allocateDirect(4*4);
    final static int picbycamera=10;
    AslSavedModel model = AslSavedModel.newInstance(this);
    public MainActivity() throws IOException {
    }

    // Releases model resources if no longer used.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageButton=findViewById(R.id.imageButton2);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,picbycamera);

                Toast.makeText(MainActivity.this, "LOOK", Toast.LENGTH_SHORT).show();
            }
        });
        button=findViewById(R.id.button);
        textview=findViewById(R.id.textview_1);
        @SuppressLint("ResourceType") InputStream inputStream=getResources().openRawResource(R.drawable.download);
        //InputStream inputStream=getResources().openRawResource(R.drawable.download);
        bitmp= BitmapFactory.decodeStream(inputStream);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Creates inputs for reference.
                TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{64, 64, 3}, DataType.FLOAT32);
                inputFeature0.loadBuffer(bytebuffer);

                // Runs model inference and gets result.
                AslSavedModel.Outputs outputs = model.process(inputFeature0);
                TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
                model.close();
                textview.setText(outputFeature0.toString());
                Toast.makeText(MainActivity.this, "FEEL", Toast.LENGTH_SHORT).show();
            }
        });
        imageView=findViewById(R.id.imageView);
    }


    @Override

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK)
        {
            Bundle extras=data.getExtras();
            bitmp=(Bitmap)extras.get("data");
            imageView.setImageBitmap(bitmp);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        switch (id)
        {
            case R.id.item1:
                Toast.makeText(this, "Setting", Toast.LENGTH_SHORT).show();
                break;
            case R.id.item2:
                Toast.makeText(this, "Exiting", Toast.LENGTH_SHORT).show();
                break;
            case R.id.item3:
                Toast.makeText(this, "LoggingOut", Toast.LENGTH_SHORT).show();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

}
