package com.DataBase;

import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;

import com.quypn.AudioBoxBetaPNQ18101997.Config;
import com.quypn.AudioBoxBetaPNQ18101997.RclAdapter;
import com.quypn.AudioBoxBetaPNQ18101997.Video;

import java.util.ArrayList;


public class LoadDataNextPage extends LoadData {


    public LoadDataNextPage(ProgressBar prg_nextpage, Context context, ArrayList<Video> listVideo, RclAdapter adapter) {

        super(prg_nextpage, context, listVideo, adapter);
    }

    @Override
    protected void onPostExecute(String json) {
        super.getAdapter().notifyDataSetChanged();
        super.getPrg().setVisibility(View.GONE);
        if (Config.pThread == 1) {
            Config.pThread--;
        }
    }
}

