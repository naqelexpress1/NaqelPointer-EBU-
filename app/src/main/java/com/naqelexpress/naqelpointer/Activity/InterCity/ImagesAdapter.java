package com.naqelexpress.naqelpointer.Activity.InterCity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.naqelexpress.naqelpointer.R;

import java.io.File;
import java.util.List;

public class ImagesAdapter extends ArrayAdapter<File> {


    public ImagesAdapter(@NonNull Context context, List<File> courseModelArrayList) {
        super(context, 0, courseModelArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listitemView = convertView;
        if (listitemView == null) {
            // Layout Inflater inflates each item to be displayed in GridView.
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.images_list_item, parent, false);
        }

        String courseModel = getItem(position).getAbsolutePath();
        ImageView courseIV = listitemView.findViewById(R.id.image);
        ImageView deleteImage = listitemView.findViewById(R.id.deleteImage);

        Bitmap checkBitmap = BitmapFactory.decodeFile(courseModel);
        courseIV.setImageBitmap(checkBitmap);



        deleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(getContext().getApplicationContext(), String.valueOf(position), Toast.LENGTH_SHORT).show();
                File file = getItem(position);
                remove(file);
            }
        });
        return listitemView;
    }
}