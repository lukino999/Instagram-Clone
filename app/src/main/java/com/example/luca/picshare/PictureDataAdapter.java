package com.example.luca.picshare;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class PictureDataAdapter extends ArrayAdapter<PictureData> {

    public PictureDataAdapter(@NonNull Context context, int resource, @NonNull ArrayList<PictureData> pictures) {
        super(context, resource, pictures);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View customView = layoutInflater.inflate(R.layout.picture_list_item, parent, false);


        ImageView imageView = customView.findViewById(R.id.pictureListItemImageView);
        imageView.setImageBitmap(getItem(position).getBitmap());


        TextView textView = customView.findViewById(R.id.pictureListItemTextView);
        textView.setText(getItem(position).getAddedDate());

        return customView;
    }
}
