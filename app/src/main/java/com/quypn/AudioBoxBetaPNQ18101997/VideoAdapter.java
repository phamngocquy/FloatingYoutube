package com.quypn.AudioBoxBetaPNQ18101997;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.quypn.myapplication.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class VideoAdapter extends BaseAdapter {

    class ViewHolder {
        ImageView imageView;
        TextView title;
        TextView channelTitle;
        TextView viewCount;
    }

    LayoutInflater inflater;
    Context context;
    ArrayList<Video> listVideo;

    public VideoAdapter(LayoutInflater inflater, Context context, ArrayList<Video> listVideo) {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.listVideo = listVideo;
    }

    public ArrayList<Video> getListVideo() {
        return listVideo;
    }

    @Override
    public int getCount() {
        return listVideo.size();
    }

    @Override
    public Video getItem(int i) {
        return listVideo.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        Video video = getItem(i);
        if (view == null) {
            view = inflater.inflate(R.layout.item_video, null);
            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) view.findViewById(R.id.image);
            viewHolder.imageView.setBackgroundResource(R.drawable.wait_video);
            viewHolder.title = (TextView) view.findViewById(R.id.name);
            viewHolder.channelTitle = (TextView) view.findViewById(R.id.channelTitle);
            viewHolder.viewCount = (TextView) view.findViewById(R.id.viewCount);

            view.setTag(viewHolder);
        }


        viewHolder = (ViewHolder) view.getTag();


        viewHolder.viewCount.setText(video.getViewCount());
        viewHolder.title.setText(video.getTitle());
        viewHolder.channelTitle.setText(video.getChannelTitle());
        Picasso.with(context).load(video.getUrl()).into(viewHolder.imageView);

        return view;
    }
}
