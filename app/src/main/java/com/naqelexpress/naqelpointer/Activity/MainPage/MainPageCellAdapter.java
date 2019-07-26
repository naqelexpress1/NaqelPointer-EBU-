package com.naqelexpress.naqelpointer.Activity.MainPage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.R;

class MainPageCellAdapter
        extends BaseAdapter
{
    private int icons [];
    private String imageTitle [];
    private Context context ;

    MainPageCellAdapter(Context context, int icons[], String[] imageTitle)
    {
        this.context = context;
        this.icons = icons;
        this.imageTitle = imageTitle;
    }
    @Override
    public int getCount() {
        return imageTitle.length;
    }

    @Override
    public Object getItem(int position) {
        return imageTitle[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View gridView = convertView;
        if (convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            gridView = inflater.inflate(R.layout.mainpagecell,null);
        }

        ImageView icon = (ImageView) gridView.findViewById(R.id.icons);
        TextView title = (TextView) gridView.findViewById(R.id.imageTitle);
        icon.setImageResource(icons[position]);
        title.setText(imageTitle[position]);

        //icon.setPadding(10,20,10,20);
        // icon.setScaleType(ImageView.ScaleType.CENTER_CROP);
        //title.setPadding(80,2,20,2);

        return gridView;
    }
}