package com.example.camerascan;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

public class DataAdapter extends BaseAdapter {
    private ArrayList<PhotoDto> photos;
    private Context context;

    public DataAdapter(ArrayList<PhotoDto> photos,Context context) {
        this.context = context;
        this.photos = photos;

    }

    @Override
    public int getCount() {
        return photos.size();
    }

    @Override
    public Object getItem(int i) {
        return photos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.custom_layout_gridview,null);
        TextView txtName= view.findViewById(R.id.txtName);
        ImageView img = view.findViewById(R.id.image);


//        TextView linkUrl = view.findViewById(R.id.linkUrl);
//        linkUrl.setText(photos.get(i).getUrl());
//        linkUrl.setMovementMethod(LinkMovementMethod.getInstance());



        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse(photos.get(i).getUrl()));
                context.startActivity(intent);
            }
        });


        txtName.setText(photos.get(i).getNamePhoto());
        Glide.with(context).load(photos.get(i)
                .getUrl())
                .apply(new RequestOptions()
                        .placeholder(R.mipmap.ic_launcher)
                        .centerCrop())
                .into(img);



        return view;
    }


}