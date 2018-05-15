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
import com.parse.ParseFile;
import com.parse.ParseObject;
import java.util.List;

public class PictureObjectAdapter extends ArrayAdapter<ParseObject> {

    private final List<ParseObject> pictures;

    public PictureObjectAdapter(@NonNull Context context, int resource, @NonNull List<ParseObject> pictures) {
        super(context, resource, pictures);
        this.pictures = pictures;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // create or recycle customView
        View customView;
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            customView = layoutInflater.inflate(R.layout.picture_list_item, parent, false);
        } else {
            customView = convertView;
        }

        // get the picture object (ParseObject) at position
        ParseObject picObj = this.pictures.get(position);

        // get the url
        ParseFile file = (ParseFile) picObj.get(Const.IMAGE_FIELD);
        String url = file.getUrl();

        // set Image
        ImageView imageView = customView.findViewById(R.id.pictureListItemImageView);
        // use glide to retrieve and scale
        GlideApp.with(getContext())
                .load(url)
                .placeholder(R.drawable.photo_camera)
                .error(R.drawable.not_available)
                .fitCenter()
                .into(imageView);

        // set text
        TextView textView = customView.findViewById(R.id.pictureListItemTextView);
        textView.setText(picObj.getCreatedAt().toString());

        // view ready
        return customView;
    }

}
