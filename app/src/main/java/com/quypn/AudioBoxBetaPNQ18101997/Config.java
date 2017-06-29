package com.quypn.AudioBoxBetaPNQ18101997;

import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;

import com.example.quypn.myapplication.Floaty;

import java.util.List;


public class Config {

    private Config() {
    }

    public static final String YOUTUBE_API_KEY = "AIzaSyAqqdJKGDaqNBTDIQ7hPOIoOSZihW3E1xE";
    public static final String SEARCH_API_KEY = "AIzaSyBoqcV78JBXdAOOYtlrnLQlGk174rBurWk";
    public static String nextPageToken;
    public static String Text;
    public static int pThread = 0;
    public static String PlayURL = "https://www.youtube.com/watch?v=";
    public static String SearchText = "";
    public static Boolean CheckService = false;
    public static Boolean CheckOpenApp = true;
    public static WebView webView;
    public static Floaty floaty;
    public static String currentVideoId;
    public static Boolean checkRepeatVideo = false;
    public static View body, head;
    public static ImageView img_repeat;
    public static int x;
    public static int y;
    public static boolean WEBVIEW_ISPLAYING = true;


    public static String getVideoPlayIframe(List<String> listVideoId, int loop, String videoId) {


        String list = "";
        for (int i = 0; i < listVideoId.size() - 1; i++) {
            list += listVideoId.get(i);
            list += ",";
        }
        list += listVideoId.get(listVideoId.size() - 1);

        String iframe = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<body>\n" +
                "<!-- 1. The <iframe> (and video player) will replace this <div> tag. -->\n" +
                "<div id=\"player\"></div>\n" +
                "\n" +
                "<!-- The Play-Link will appear in that div after the video was loaded -->\n" +
                "<div id=\"play\"></div>\n" +
                "\n" +
                "<script>\n" +
                "    // 2. This code loads the IFrame Player API code asynchronously.\n" +
                "    var tag = document.createElement('script');\n" +
                "\n" +
                "    tag.src = \"https://www.youtube.com/iframe_api\";\n" +
                "    var firstScriptTag = document.getElementsByTagName('script')[0];\n" +
                "    firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);\n" +
                "\n" +
                "    // 3. This function creates an <iframe> (and YouTube player)\n" +
                "    //    after the API code downloads.\n" +
                "    var player;\n" +
                "    function onYouTubeIframeAPIReady() {\n" +
                "        player = new YT.Player('player', {\n" +
                "            height: '360',\n" +
                "            width: '640',\n" +
                "            videoId: 'rWIsNCdfPAM',\n" +
                "\n" +
                "            playerVars: {\n" +
                "                rel:0,\n" +
                "                playsinline: 0,\n" +
                "                fs: 0,\n" +
                "                modestbranding: 1,\n" +
                "                showinfo: 0,\n" +
                "                autoplay: 1,\n" +
                "                loop: 1,\n" +
                "                playlist: \"Sf9_daKZrnY,viwkhkkMopo\"\n" +
                "            },\n" +
                "            events: {\n" +
                "                'onReady': onPlayerReady,\n" +
                "                'onStateChange': onPlayerStateChange\n" +
                "            }\n" +
                "        });\n" +
                "    }\n" +
                "\n" +
                "    // 4. The API will call this function when the video player is ready.\n" +
                "    function onPlayerReady(event) {\n" +
                "        event.target.playVideo();\n" +
                "    }\n" +
                "\n" +
                "\n" +
                "    // 5. The API calls this function when the player's state changes.\n" +
                "    //    The function indicates that when playing a video (state=1),\n" +
                "    //    the player should play for six seconds and then stop.\n" +
                "    var done = false;\n" +
                "    function onPlayerStateChange(event) {\n" +
                "        if (event.data == 0) {\n" +
                "            //event.target.playVideo();\n" +
                "        }\n" +
                "        else if (event.data == 2) {\n" +
                "            event.target.playVideo();\n" +
                "        }\n" +
                "    }\n" +
                "    function stopVideo() {\n" +
                "        player.stopVideo();\n" +
                "    }\n" +
                "</script>\n" +
                "</body>\n" +
                "</html>";
        return iframe;
    }


}
