package com.quypn.AudioBoxBetaPNQ18101997;


import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.DataBase.LoadData;
import com.DataBase.LoadDataNextPage;
import com.example.quypn.myapplication.R;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements YouTubePlayer.OnInitializedListener {

    private YouTubePlayerSupportFragment fragment;
    private static final int RECOVERY_REQUEST = 1;


    private Toolbar toolbar;

    private AdView adView;


    private static final int NOTIFICATION_ID = 1500;
    public static final int PERMISSION_REQUEST_CODE = 16;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            getWindow().setStatusBarColor(getResources().getColor(R.color.colorStatusbarwhite));
        }


        RelativeLayout adViewContainer = (RelativeLayout) findViewById(R.id.adViewContainer);
        adView = new AdView(this, "1370097283064224_1370219493052003", AdSize.BANNER_320_50);
        adViewContainer.addView(adView);
        adView.loadAd();


        fragment = (YouTubePlayerSupportFragment) getSupportFragmentManager().findFragmentById(R.id.youtube_view);
        fragment.initialize(Config.YOUTUBE_API_KEY, this);


        final Intent intent = new Intent(this, HomeActivity.class);
        RemoteViews remoteViews = new RemoteViews(getPackageName(),R.layout.control_play);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //Notification notification = Floaty.createNotification(this, "Running", "Tap to Search", R.mipmap.ic_launcher,remoteViews);


    }


    @TargetApi(Build.VERSION_CODES.M)
    public void startFloatyForAboveAndroidL() {
        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, PERMISSION_REQUEST_CODE);
        } else {

            Config.floaty.startService();

        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (Settings.canDrawOverlays(this)) {
                Config.floaty.startService();
            } else {
                Spanned message = Html.fromHtml("Please allow this permission, so <b>Floaties</b> could be drawn.");
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == RECOVERY_REQUEST) {
            fragment.initialize(Config.YOUTUBE_API_KEY, this);
        }
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, final YouTubePlayer youTubePlayer, final boolean wasRestored) {
        if (!wasRestored) {


            youTubePlayer.setFullscreen(true);
            youTubePlayer.cueVideo(Config.currentVideoId);

        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater option_inflater = getMenuInflater();
        option_inflater.inflate(R.menu.open_in_brower_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.share_vid_MN:
                Share_Video();
                break;
            case R.id.opn_MN:
                Open_in_brower();
                break;

        }
        return super.onOptionsItemSelected(item);
    }


    public void Share_Video() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = Config.PlayURL + Config.currentVideoId;
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share Video"));
    }

    public void Open_in_brower() {
        Intent Brower_intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Config.PlayURL + Config.currentVideoId));
        startActivity(Intent.createChooser(Brower_intent, "Play video on:"));
    }


    public static class SearchFragment extends Fragment implements IRefresh {


        private int visibleThreshold = 1;
        private int lastVisibleItem, totalItemCount;


        private RecyclerView rclView;
        private ProgressBar prg;
        private ProgressBar prg_nextpage;
        private LoadDataNextPage nextPage;


        private ArrayList<Video> listVideo;
        private RclAdapter adapter;
        private LoadData load;

        public SearchFragment() {

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_search, null);

            return view;
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            prg = (ProgressBar) getView().findViewById(R.id.prg);
            prg_nextpage = (ProgressBar) getView().findViewById(R.id.prg_nextpage);
            rclView = (RecyclerView) getView().findViewById(R.id.rclView);
            listVideo = new ArrayList<>();

            adapter = new RclAdapter(listVideo, getContext());
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            rclView.setLayoutManager(layoutManager);
            rclView.setAdapter(adapter);


            rclView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    super.onScrolled(recyclerView, dx, dy);
                    if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

                        totalItemCount = manager.getItemCount();
                        lastVisibleItem = manager.findLastVisibleItemPosition();
                        if (totalItemCount <= (lastVisibleItem + visibleThreshold) && Config.pThread == 0) {

                            String url = "https://www.googleapis.com/youtube/v3/search?pageToken=" +
                                    Config.nextPageToken +
                                    "&part=snippet&maxResults=20&order=relevance&q=" +
                                    Config.Text +
                                    "&key=" + Config.SEARCH_API_KEY;
                            listVideo = load.getListVideo();
                            adapter = load.getAdapter();
                            nextPage = new LoadDataNextPage(prg_nextpage, getContext(), listVideo,adapter);
                            nextPage.execute(url);
                        }
                    }
                }
            });
        }

        @Override
        public void refresh(String url) {
            if (Config.pThread == 0)
            {
                listVideo = new ArrayList<>();
                load = new LoadData(prg, getContext(), listVideo, adapter, rclView);
                load.execute(url);
            }


        }
    }
}
