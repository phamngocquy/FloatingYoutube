package com.quypn.AudioBoxBetaPNQ18101997;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.SearchView;
import android.widget.Toast;

import com.DataBase.SQLiteDataController;
import com.DataBase.VideoController;
import com.example.quypn.myapplication.Floaty;
import com.example.quypn.myapplication.R;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import static com.example.quypn.myapplication.R.id.img_sharevideo;


public class HomeActivity extends AppCompatActivity {
    TabLayout tabLayout;

    FragmentAdapter adapter;

    Toolbar toolbar;
    MainActivity.SearchFragment searchFragment;
    HistoryFragment historyFragment;
    AdView adView;

    private IRefresh actionRefresh, actionClean;
    private ViewPager viewPager;

    public static RemoteViews remoteViews;
    public static Notification notification;


    public ImageView img_fullscreen, img_share_video;

    public static final int NOTIFICATION_ID = 1500;
    public static final int PERMISSION_REQUEST_CODE = 16;
    public int notification_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            getWindow().setStatusBarColor(getResources().getColor(R.color.colorStatusbarwhite));
        }

        SQLiteDataController sql = new SQLiteDataController(this);
        try {
            sql.isCreatedDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }


        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.view_page);

        RelativeLayout adViewContainer = (RelativeLayout) findViewById(R.id.adViewContainer);
        adView = new AdView(this, "1370097283064224_1370219493052003", AdSize.BANNER_320_50);
        adViewContainer.addView(adView);
        adView.loadAd();

        searchFragment = new MainActivity.SearchFragment();
        historyFragment = new HistoryFragment();

        actionRefresh = searchFragment;
        actionClean = historyFragment;

        Fragment[] screens = {searchFragment, historyFragment};
        FragmentManager fragmentManager = getSupportFragmentManager();
        adapter = new FragmentAdapter(fragmentManager, screens, HomeActivity.this);
        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setTabsFromPagerAdapter(adapter);
        init();

        if (!Config.SearchText.trim().equalsIgnoreCase("")) {
            String replace = Config.SearchText.replaceAll(" ", "%20");
            String url = "https://www.googleapis.com/youtube/v3/search" +
                    "?part=snippet&maxResults=20&order=relevance&type=video&q="
                    + replace + "&key=" + Config.SEARCH_API_KEY;
            viewPager.setCurrentItem(0);
            actionRefresh.refresh(url);
        }
        Play_Hide();
    }


    @SuppressLint("SetJavaScriptEnabled")
    private void init() {
        View closed = LayoutInflater.from(this).inflate(R.layout.closed, null);

        //notification_id = (int) System.currentTimeMillis();
        remoteViews = new RemoteViews(getPackageName(), R.layout.control_play);
        Intent play_intent = new Intent("ACTION_PLAY");
        //play_intent.putExtra("id", notification_id);

        Intent next_intent = new Intent("ACTION_NEXT");
        Intent pre_intent = new Intent("ACTION_PRE");


        PendingIntent play_pendingIntent = PendingIntent.getBroadcast(this, 123, play_intent, 0);
        remoteViews.setOnClickPendingIntent(R.id.img_pause, play_pendingIntent);


        PendingIntent next_play_pendingIntent = PendingIntent.getBroadcast(this, 123, next_intent, 0);
        remoteViews.setOnClickPendingIntent(R.id.img_next, next_play_pendingIntent);

        PendingIntent pre_play_pendingIntent = PendingIntent.getBroadcast(this, 123, pre_intent, 0);
        remoteViews.setOnClickPendingIntent(R.id.img_pre, pre_play_pendingIntent);


        notification = new Notification.Builder(this)
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setContent(remoteViews).build();


        if (Config.body == null)
            Config.body = LayoutInflater.from(this).inflate(R.layout.video_play_body, null);

        img_fullscreen = (ImageView) Config.body.findViewById(R.id.img_fullscreen);
        img_fullscreen.setOnClickListener(FullScreen);

        img_share_video = (ImageView) Config.body.findViewById(img_sharevideo);
        img_share_video.setOnClickListener(Share_Video);

        if (Config.img_repeat == null)
            Config.img_repeat = (ImageView) Config.body.findViewById(R.id.img_repeat);
        Config.img_repeat.setOnClickListener(Repeat_video);


        if (Config.head == null) {
            Config.head = LayoutInflater.from(this).inflate(R.layout.head, null);
        }


        Config.floaty = Floaty.createInstance(this, closed, Config.head, Config.body, NOTIFICATION_ID, notification);
        Config.webView = (WebView) Config.body.findViewById(R.id.webView);


        Config.webView.getSettings().setUserAgentString("Mozilla/5.0 (X11; Linux x86_64)" +
                " AppleWebKit/534.36 (KHTML, like Gecko) " +
                "Chrome/13.0.766.0 Safari/534.36");

        Config.webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        Config.webView.setWebChromeClient(new WebChromeClient());
        Config.webView.setWebViewClient(new WebViewClient());


        Config.webView.getSettings().setJavaScriptEnabled(true);
        Config.webView.getSettings().setDisplayZoomControls(false);
        Config.webView.getSettings().setBuiltInZoomControls(true);

        // Config.webView.getSettings().setUseWideViewPort(true);
        // Config.webView.canGoBack();
        // Config.webView.setLayerType(View.LAYER_TYPE_HARDWARE,null);
        // Config.webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        // Config.webView.getSettings().setAllowUniversalAccessFromFileURLs(true);

        Config.webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        Config.webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        Config.webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);

        Config.webView.clearCache(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Config.webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem item = menu.findItem(R.id.search_MN);

        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if (query.trim().length() > 0) {


                    try {
                        FileOutputStream seachHitstory = openFileOutput("history.txt", MODE_APPEND);

                        OutputStreamWriter writer = new OutputStreamWriter(seachHitstory);

                        writer.write("\n" + query);
                        writer.flush();
                        writer.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    String replace = query.replaceAll(" ", "%20");
                    Config.Text = replace;
                    String url = "https://www.googleapis.com/youtube/v3/search" +
                            "?part=snippet&maxResults=20&order=relevance&type=video&q="
                            + replace + "&key=" + Config.SEARCH_API_KEY;

                    viewPager.setCurrentItem(0);
                    actionRefresh.refresh(url);
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        MenuInflater inflater1 = getMenuInflater();
        inflater1.inflate(R.menu.option_menu, menu);
        MenuInflater play_hide_inflater = getMenuInflater();
        play_hide_inflater.inflate(R.menu.play_hide_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.share_MN:
                share_app();
                break;
            case R.id.rate_MN:
                rate_app();
                break;
            case R.id.about_MN:
                about_app();
                break;
            case R.id.cleanHistory:
                cleanHistory();
            case R.id.play_hide_MN:
                Play_Hide();
                break;


        }


        return super.onOptionsItemSelected(item);
    }

    public void share_app() {

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "https://play.google.com/store/apps/details?id=com.quypn.AudioBoxBetaPNQ18101997&hl=en";
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share Application : "));

    }

    public void rate_app() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.quypn.AudioBoxBetaPNQ18101997&hl=en"));
        startActivity(intent);
    }

    public void about_app() {
        Intent intent = new Intent(HomeActivity.this, AboutActivity.class);
        startActivity(intent);
    }

    public void cleanHistory() {
        VideoController videoController = new VideoController(this);
        ArrayList<Video> videos = videoController.getVideo();


        for (int i = 0; i < videos.size(); i++) {
            videoController.deleteVideo(videos.get(i).getId());
        }

        viewPager.setCurrentItem(1);
        actionClean.refresh("");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void Play_Hide() {


        if (!Config.CheckService) {
            Config.webView.onResume();
            Config.webView.resumeTimers();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                startFloatyForAboveAndroidL();
            } else {
                Config.floaty.startService();
            }
        }

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
    }

    View.OnClickListener Share_Video = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = Config.PlayURL + Config.currentVideoId;
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share Video"));
        }
    };

    View.OnClickListener FullScreen = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent Brower_intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Config.PlayURL + Config.currentVideoId));
            startActivity(Intent.createChooser(Brower_intent, "Play video on:"));
        }
    };

    View.OnClickListener Repeat_video = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!Config.checkRepeatVideo) {
                Config.checkRepeatVideo = true;
                Config.img_repeat.setImageResource(R.drawable.repeat_icon_red);
                Config.webView.loadUrl("https://www.youtube.com/embed/" + Config.currentVideoId + "?autoplay=1;rel=0&amp;showinfo=0&?Version=3&loop=1&playlist=" + Config.currentVideoId);
            } else {
                Config.checkRepeatVideo = false;
                Config.img_repeat.setImageResource(R.drawable.repeat_icon);
                Config.webView.loadUrl("https://www.youtube.com/embed/" + Config.currentVideoId + "?autoplay=1;rel=0&amp;showinfo=0");
            }
        }
    };

    public static class MyReceive extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            String action = intent.getAction();


            if (action.equalsIgnoreCase("ACTION_PLAY")) {

                if (Config.webView != null && Config.WEBVIEW_ISPLAYING) {

                    remoteViews.setImageViewResource(R.id.img_pause, R.mipmap.ic_play_vid);
                    manager.notify(NOTIFICATION_ID, notification);

                    Config.WEBVIEW_ISPLAYING = false;
                    Config.webView.onPause();
                    Config.webView.pauseTimers();


                } else {

                    remoteViews.setImageViewResource(R.id.img_pause, R.mipmap.ic_pause_video);
                    manager.notify(NOTIFICATION_ID, notification);

                    Config.WEBVIEW_ISPLAYING = true;
                    Config.webView.resumeTimers();
                    Config.webView.onResume();

                }
            } else if (action.equalsIgnoreCase("ACTION_UPDATE")) {


                String title = intent.getExtras().getString("title");
                String channel = intent.getExtras().getString("channel");

                remoteViews.setTextViewText(R.id.txt_title, title);
                remoteViews.setTextViewText(R.id.txt_channel, channel);
                manager.notify(NOTIFICATION_ID, notification);
            } else if (action.equalsIgnoreCase("ACTION_NEXT")) {

                Intent next_video = new Intent();
                next_video.setAction("NEXT_VIDEO");
                context.sendBroadcast(next_video);
                remoteViews.setImageViewResource(R.id.img_pause, R.mipmap.ic_pause_video);
                manager.notify(NOTIFICATION_ID, notification);

            } else if (action.equalsIgnoreCase("ACTION_PRE")) {

                Intent pre_video = new Intent();
                pre_video.setAction("PRE_VIDEO");
                context.sendBroadcast(pre_video);
                remoteViews.setImageViewResource(R.id.img_pause, R.mipmap.ic_pause_video);
                manager.notify(NOTIFICATION_ID, notification);

            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Config.webView.onPause();
        Config.webView.pauseTimers();
        Config.floaty.stopService();
    }
}
