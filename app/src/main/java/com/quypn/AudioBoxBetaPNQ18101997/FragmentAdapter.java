package com.quypn.AudioBoxBetaPNQ18101997;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


public class FragmentAdapter extends FragmentPagerAdapter {

    private  Fragment[] sreens;
    private Context context;
    public FragmentAdapter(FragmentManager fm,Fragment[] fragment,Context context) {
        super(fm);
        this.sreens = fragment;
        this.context = context;
      
    }

    @Override
    public Fragment getItem(int position) {
        return sreens[position];
    }

    @Override
    public int getCount() {
        return sreens.length;
    }


    public CharSequence getPageTitle(int i)
    {
        String title = "";
        switch (i)
        {
            case 0:
              title = " Search";
                break;
            case 1:
                title= "History";
                break;
        }
        return title;
    }
}
