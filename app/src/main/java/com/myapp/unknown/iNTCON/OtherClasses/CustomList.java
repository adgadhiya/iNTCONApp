package com.myapp.unknown.iNTCON.OtherClasses;

/**
 * Created by Paul on 3/7/14.
 */
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.myapp.unknown.iNTCON.R;

import java.io.File;

public class CustomList extends ArrayAdapter<String>{
    private final Activity context;
    private final String[] web;
    final String ParentFolder;
    public CustomList(Activity context, String[] web,String path) {
        super(context, R.layout.list_single, web);
        this.context = context;
        this.web = web;
        ParentFolder = path;
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_single, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
        txtTitle.setText(web[position]);
        Glide.with(context).load(
                new File(
                        ParentFolder+"/"+web[position]
                )).placeholder(R.drawable.document).override(50,50).into(imageView);
        return rowView;
    }
}
