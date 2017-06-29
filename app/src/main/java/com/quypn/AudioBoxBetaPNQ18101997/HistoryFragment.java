package com.quypn.AudioBoxBetaPNQ18101997;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.DataBase.VideoController;
import com.example.quypn.myapplication.R;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment implements IRefresh {

    private RecyclerView rclView;
    private RclAdapter rclAdapter;
    private ArrayList<Video> listVideo;
    private SwipeRefreshLayout swipeRefresh;

    public HistoryFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_history, null);


        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        swipeRefresh = (SwipeRefreshLayout) getView().findViewById(R.id.swipeRefresh);

        rclView = (RecyclerView) getView().findViewById(R.id.rclView);
        listVideo = new VideoController(getContext()).getVideo();


        rclAdapter = new RclAdapter(Convert(listVideo), getContext());
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rclView.setLayoutManager(manager);
        rclView.setAdapter(rclAdapter);

        swipeRefresh.setOnRefreshListener(OnRefresh);

    }

    SwipeRefreshLayout.OnRefreshListener OnRefresh = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            listVideo = new VideoController(getContext()).getVideo();
            rclAdapter = new RclAdapter(Convert(listVideo), getContext());
            LinearLayoutManager manager = new LinearLayoutManager(getContext());
            manager.setOrientation(LinearLayoutManager.VERTICAL);
            rclView.setLayoutManager(manager);
            rclView.setAdapter(rclAdapter);
            swipeRefresh.setRefreshing(false);
        }
    };

    public ArrayList<Video> Convert(ArrayList<Video> videos) {
        ArrayList<Video> lsData = new ArrayList<>();

        for (int i = videos.size() - 1; i >= 1; i--) {
            for (int j = i - 1; j >= 0; j--) {
                String a = videos.get(i).getVideoId();
                String b = videos.get(j).getVideoId();
                if (a.equalsIgnoreCase(b) && a != "") {
                    new VideoController(getContext()).deleteVideo(videos.get(j).getId());
                    videos.get(j).setVideoId("");
                }
            }
        }

        for (int i = videos.size() - 1; i >= 0; i--) {
            if (!videos.get(i).getVideoId().equalsIgnoreCase("")) {
                lsData.add(videos.get(i));

            }
        }

        return lsData;
    }

    @Override
    public void refresh(String url) {

        listVideo = new VideoController(getContext()).getVideo();

        rclAdapter = new RclAdapter(Convert(listVideo), getContext());
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rclView.setLayoutManager(manager);
        rclView.setAdapter(rclAdapter);
    }
}
