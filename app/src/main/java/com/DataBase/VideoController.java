package com.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

import com.quypn.AudioBoxBetaPNQ18101997.Video;

import java.util.ArrayList;




public class VideoController extends SQLiteDataController {
    public VideoController(Context con) {
        super(con);
    }

    public ArrayList<Video> getVideo() {
        ArrayList<Video> lsData = new ArrayList<>();
        try {
            //Bước 1: Mở kết nối DB
            openDataBase();
            //Bước 2: Truy vấn
            Cursor cs = database.rawQuery(
                    "select id,title,videoid,url,channelTitle " +
                            "from Video", null);
            while (cs.moveToNext()) {

                //Trỏ đến từng dòng
                int id = cs.getInt(0);
                String title = cs.getString(1);
                String videoId = cs.getString(2);
                String url = cs.getString(3);
                String  channelTitle = cs.getString(4);

                //Đọc thông tin từng trường sau đấy tạo ra object student
                Video video = new Video();
                video.setId(id);
                video.setTitle(title);
                video.setChannelTitle(channelTitle);
                video.setUrl(url);
                video.setVideoId(videoId);
                //Add vào danh sách
                lsData.add(video);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //Bước 3: Đóng kết nối: Tại vì là nếu như bước 1,2 có lỗi
            //thì tôi vẫn phải đảm bảo DB được đóng lại
            close();
        }
        return lsData;
    }


    public boolean insertVideo(Video video) {
        boolean rs = false;
        try {
            openDataBase();
            //Đây là một bản ghi
            ContentValues values = new ContentValues();
            values.put("title", video.getTitle());
            values.put("videoid", video.getVideoId());
            values.put("url",video.getUrl());
            values.put("channelTitle",video.getChannelTitle());

            long id = database.insert("Video", null, values);

            //id chính là trường ID
            if (id > 0) {
                //Insert thanh cong
                rs = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return rs;
    }

    public boolean updateVideo(Video video) {
        boolean rs = false;
        try {
            openDataBase();
            //Đây là một bản ghi
            ContentValues values = new ContentValues();
            values.put("title", video.getTitle());
            values.put("videoid", video.getVideoId());
            values.put("url",video.getUrl());
            values.put("channelTitle",video.getChannelTitle());


            long id = database.update("Video", values, "id=" + video.getId(), null);
            //id chính là trường ID
            if (id >= 0) {
                //update thanh cong
                rs = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return rs;
    }

    public boolean deleteVideo(int id) {
        boolean result = false;
        try {
            openDataBase();
            int _id = database.delete("Video", "id=" + id, null);
            if (_id > 0)
                result = true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return result;
    }

}
