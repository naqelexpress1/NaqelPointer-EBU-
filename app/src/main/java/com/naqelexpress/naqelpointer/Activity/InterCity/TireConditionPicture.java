package com.naqelexpress.naqelpointer.Activity.InterCity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.naqelexpress.naqelpointer.Activity.Constants.Constant;
import com.naqelexpress.naqelpointer.R;
import com.naqelexpress.naqelpointer.utils.SharedHelper;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TireConditionPicture extends AppCompatActivity implements View.OnClickListener {

    TextView back, next;
    Button uploadFile;
    public static final int BITMAP_SAMPLE_SIZE = 8;


    File filename;
    List<File> sendImages = new ArrayList<File>();
    List<File> tempImages = new ArrayList<File>();


    ImagesAdapter adapter;
    GridView gridview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tire_condition_pictures);


        findViewById();


    }

    public void findViewById() {
        back = findViewById(R.id.back);
        next = findViewById(R.id.next);

        uploadFile = findViewById(R.id.uploadFile);

        back.setOnClickListener(this);
        next.setOnClickListener(this);
        uploadFile.setOnClickListener(this);

        gridview = (GridView) findViewById(R.id.imagesGridview);
        SharedPreferences mySettings;
        mySettings = getSharedPreferences("ImageDetails", MODE_PRIVATE);
        int gridSize = 50 * Integer.parseInt(mySettings.getString("gridSize", "2"));
        gridview.setColumnWidth(gridSize + 10);


        adapter = new ImagesAdapter(this, tempImages);
        gridview.setAdapter(adapter);


        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent,
                                    View v, int position, long id) {
                // Send intent to SingleViewActivity
//                Toast.makeText(TireConditionPicture.this, String.valueOf(position), Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.uploadFile:
                CreateFileName();
                break;

            case R.id.back:
                onBackPressed();
                break;

            case R.id.next:
                Intent intent = new Intent(getApplicationContext(), SafetyCurtainsAndCargo.class);
                startActivity(intent);
                break;

        }
    }


    public void setImageInImageView(String imagePath) {
            adapter = new ImagesAdapter(this, tempImages);
            gridview.setAdapter(adapter);
    }


    protected void CreateFileName() {
        int count = tempImages.size() + 1;
//        filename = id + "_" + timestamp.toString() + "_" + imagesuffix + "_" + String.valueOf(count) +".png";
        try {
            filename = Constant.createImageFile(TireConditionPicture.this, "TireCondition", count);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Uri check = Uri.fromFile(filename);
        CropImage.activity(null).setOutputUri(check).setGuidelines(CropImageView.Guidelines.ON).start(TireConditionPicture.this);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                    } else {
                        Toast.makeText(getApplicationContext(), "You Must Allow Permission", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // handle result of CropImageActivity
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                tempImages.add(filename);
                compressImage(filename.getAbsolutePath());
                setImageInImageView(filename.getAbsolutePath());
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
                filename = null;

            }
        }
    }

    private void compressImage(String filePath) {

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();//me

        Bitmap bitmap = optimizeBitmap(BITMAP_SAMPLE_SIZE, filePath);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes); //me

        String imageFileName = SharedHelper.getKeyString(getApplicationContext(), "fileName");//new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File storageDir = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = new File(storageDir, imageFileName + ".jpg");
        String path = String.valueOf(image);
        FileOutputStream fo;
        try {
            fo = new FileOutputStream(path);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public static Bitmap optimizeBitmap(int sampleSize, String filePath) {
        // bitmap factory
        BitmapFactory.Options options = new BitmapFactory.Options();

        // downsizing image as it throws OutOfMemory Exception for larger
        // images
        options.inSampleSize = sampleSize;

        return BitmapFactory.decodeFile(filePath, options);
    }
}
