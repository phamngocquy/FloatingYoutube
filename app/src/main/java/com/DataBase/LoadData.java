package com.DataBase;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ProgressBar;

import com.quypn.AudioBoxBetaPNQ18101997.Config;
import com.quypn.AudioBoxBetaPNQ18101997.RclAdapter;
import com.quypn.AudioBoxBetaPNQ18101997.Video;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class LoadData extends AsyncTask<String, Void, String> {
    private ProgressBar prg;
    private Context context;
    private ArrayList<Video> listVideo;
    private RclAdapter adapter;
    private RecyclerView rclView;

    public LoadData(ProgressBar prg, Context context, ArrayList<Video> listVideo, RclAdapter adapter, RecyclerView rclView) {
        this.prg = prg;
        this.context = context;
        this.listVideo = listVideo;
        this.adapter = adapter;
        this.rclView = rclView;
    }

    public LoadData(ProgressBar prg, Context context, ArrayList<Video> listVideo, RclAdapter adapter) {
        this.prg = prg;
        this.context = context;
        this.listVideo = listVideo;
        this.adapter = adapter;

    }

    @Override
    protected void onPreExecute() {
        if (Config.pThread == 0) {
            Config.pThread++;
        }
        prg.setVisibility(View.VISIBLE);
    }

    @Override
    protected String doInBackground(String... params) {
        String json = getDataFromServer(params[0]);

        if (json != null && json.length() > 0) {
            JSONObject data;
            try {
                data = new JSONObject(json);
                Config.nextPageToken = data.getString("nextPageToken");
                JSONArray array = data.getJSONArray("items");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);

                    JSONObject id = obj.getJSONObject("id");

                    String videoId = id.getString("videoId");

                    JSONObject snippet = obj.getJSONObject("snippet");
                    String title = snippet.getString("title");


                    String channelTitle = snippet.getString("channelTitle");

                    JSONObject thumbnails = snippet.getJSONObject("thumbnails");
                    JSONObject iMage;
                    switch (context.getResources().getDisplayMetrics().densityDpi) {
                        case DisplayMetrics.DENSITY_LOW:
                            iMage = thumbnails.getJSONObject("default");
                            break;
                        case DisplayMetrics.DENSITY_MEDIUM:
                            iMage = thumbnails.getJSONObject("default");
                            break;
                        case DisplayMetrics.DENSITY_HIGH:
                            iMage = thumbnails.getJSONObject("default");
                            break;
                        case DisplayMetrics.DENSITY_XHIGH:
                            iMage = thumbnails.getJSONObject("medium");
                            break;
                        case DisplayMetrics.DENSITY_XXHIGH:
                            iMage = thumbnails.getJSONObject("medium");
                            break;
                        case DisplayMetrics.DENSITY_TV:
                            iMage = thumbnails.getJSONObject("default");
                            break;
                        case DisplayMetrics.DENSITY_XXXHIGH:
                            iMage = thumbnails.getJSONObject("medium");
                            break;
                        default:
                            iMage = thumbnails.getJSONObject("medium");
                            break;
                    }
                    String url = iMage.getString("url");
                    Video video = new Video(videoId, "♫:  " + title, url, "☻: " + channelTitle);
                    listVideo.add(video);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
           // Toast.makeText(context, "Null", Toast.LENGTH_SHORT).show();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String json) {

        adapter = new RclAdapter(listVideo, context);
        LinearLayoutManager manager = new LinearLayoutManager(context);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rclView.setLayoutManager(manager);
        rclView.setAdapter(adapter);
        prg.setVisibility(View.GONE);
        if (Config.pThread == 1) {
            Config.pThread--;
        }

    }


    public String getDataFromServer(String api) {
        String json = "";
        try {
            URL _url = new URL(api);
            HttpURLConnection connection =
                    (HttpURLConnection) _url.openConnection();
            connection.setConnectTimeout(60000);
            connection.setDoInput(true);
            connection.connect();

            InputStream input = connection.getInputStream();

            if (input != null) {
                InputStreamReader reader = new InputStreamReader(input);
                BufferedReader bf = new BufferedReader(reader);
                String line = bf.readLine();
                while (line != null) {
                    json += line;
                    line = bf.readLine();
                }
                reader.close();
                bf.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            json = null;
        }
        return json;
    }


    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public ArrayList<Video> getListVideo() {
        return listVideo;
    }

    public RclAdapter getAdapter() {
        return adapter;
    }

    public ProgressBar getPrg() {
        return prg;
    }
}

