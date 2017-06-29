package com.quypn.AudioBoxBetaPNQ18101997;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.DataBase.VideoController;
import com.example.quypn.myapplication.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class RclAdapter extends RecyclerView.Adapter<RclAdapter.HeaderViewHoder> {

    private ArrayList<Video> listVideo;
    private Context context;
    private List<String> listVideoId;


    private static int video_index;
    private static ArrayList<Video> videos;


    public RclAdapter(ArrayList<Video> listVideo, Context context) {
        this.listVideo = listVideo;
        this.context = context;
        videos = listVideo;


    }

    @Override
    public HeaderViewHoder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View headerItem = inflater.inflate(R.layout.item_video, null);
        return new HeaderViewHoder(headerItem);
    }

    @Override
    public void onBindViewHolder(HeaderViewHoder holder, final int position) {


        final Video video = listVideo.get(position);
        holder.viewCount.setText(video.getViewCount());
        holder.title.setText(video.getTitle());
        holder.channelTitle.setText(video.getChannelTitle());
        if (!video.getUrl().isEmpty()) {
            Picasso.with(context).load(video.getUrl()).into(holder.imageView);
        } else {
            Log.w("Link", video.getUrl());
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                video_index = position;
                listVideoId = new ArrayList<String>();
                for (int i = 1; i < listVideo.size(); i++) {
                    listVideoId.add(listVideo.get(i).getVideoId());
                }
                new VideoController(context).insertVideo(listVideo.get(position));
                if (!Config.CheckService) {
                    Config.webView.onResume();
                    Config.webView.resumeTimers();
                    Config.floaty.startService();

                }

                if (Config.floaty.getBody().getVisibility() == View.GONE) {
                    Config.floaty.getBody().setVisibility(View.VISIBLE);
                }
                Config.currentVideoId = listVideo.get(position).getVideoId();
                Config.webView.onResume();
                Config.webView.resumeTimers();
               //Config.webView.loadUrl(Config.getVideoPlayIframe(listVideoId,1,Config.currentVideoId));
                Config.webView.loadDataWithBaseURL(null, Config.getVideoPlayIframe(listVideoId, 1, Config.currentVideoId), "text/html", "UTF-8", null);
                //Config.webView.loadUrl("https://www.youtube.com/embed/" + Config.currentVideoId + "?autoplay=1;rel=0&amp;showinfo=0;frameborder=0");
                Config.img_repeat.setImageResource(R.drawable.repeat_icon);
                Config.checkRepeatVideo = false;

                Intent updateIntent = new Intent("ACTION_UPDATE");
                updateIntent.putExtra("title", video.getTitle());
                updateIntent.putExtra("channel", video.getChannelTitle());
                context.sendBroadcast(updateIntent);
            }
        });

    }
    @Override
    public int getItemCount() {
        return listVideo.size();
    }


    class HeaderViewHoder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView title;
        TextView channelTitle;
        TextView viewCount;

        HeaderViewHoder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image);
            imageView.setBackgroundResource(R.drawable.wait_video);
            title = (TextView) itemView.findViewById(R.id.name);
            channelTitle = (TextView) itemView.findViewById(R.id.channelTitle);
            viewCount = (TextView) itemView.findViewById(R.id.viewCount);

        }
    }

    public static class Next_Pre_Video extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            try {
                Video pre_video;
                Video next_video;
                if (video_index - 1 < 0) {

                    pre_video = videos.get(videos.size() - 1);
                } else {
                    pre_video = videos.get(video_index - 1);
                }


                if (video_index + 1 >= videos.size()) {
                    next_video = videos.get(0);
                } else {
                    next_video = videos.get(video_index + 1);
                }


                String action = intent.getAction();
                Video video = null;
                if (action.equalsIgnoreCase("NEXT_VIDEO")) {
                    video = next_video;
                    if (video_index + 1 >= videos.size()) video_index = 0;
                    else video_index += 1;
                } else if (action.equalsIgnoreCase("PRE_VIDEO")) {
                    video = pre_video;
                    if (video_index - 1 <= 0) video_index = videos.size() - 1;
                    else video_index -= 1;
                }
                if (video != null) {
                    Config.currentVideoId = video.getVideoId();
                    Config.webView.onResume();
                    Config.webView.resumeTimers();
                    Config.webView.loadUrl("https://www.youtube.com/embed/" + Config.currentVideoId + "?autoplay=1;rel=0&amp;showinfo=0;frameborder=0");

                    Config.img_repeat.setImageResource(R.drawable.repeat_icon);
                    Config.checkRepeatVideo = false;


                    Intent updateIntent = new Intent("ACTION_UPDATE");
                    updateIntent.putExtra("title", video.getTitle());
                    updateIntent.putExtra("channel", video.getChannelTitle());
                    context.sendBroadcast(updateIntent);
                }
            } catch (Exception e) {

            }

        }
    }
}
