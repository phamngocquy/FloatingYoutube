package com.quypn.AudioBoxBetaPNQ18101997;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.example.quypn.myapplication.R;

public class SplashActivity extends AppCompatActivity {

    private static final int PERMS_REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            getWindow().setStatusBarColor(getResources().getColor(R.color.colorStatusbar));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (!hasPermission()) {
                requestPermission();
            } else {
                Splash_Screen();
            }
        }

        else
        {
            Splash_Screen();
        }



    }

    private boolean hasPermission() {
        int res = 0;
        String[] permission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

        for (String perms : permission) {
            res = checkCallingOrSelfPermission(perms);
            if (res != PackageManager.PERMISSION_GRANTED) {
                return false;
            }

        }
        return true;
    }

    private void requestPermission() {
        String[] permission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permission, PERMS_REQUEST_CODE);
        }
    }

    private void Splash_Screen() {
        if (Config.CheckOpenApp) {
            Handler hd = new Handler();
            //Tạo ra một luồng con xử lý ngầm 1 giây
            hd.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Việc bạn sẽ thao tác với UI

                    Intent main = new Intent(SplashActivity.this, HomeActivity.class);
                    Config.CheckOpenApp = false;
                    startActivity(main);

                    finish();
                }
            }, 800);//1000 là điểm dừng chạy background
        } else {
            Intent main = new Intent(SplashActivity.this, HomeActivity.class);
            startActivity(main);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean allowed = true;
        switch (requestCode) {
            case PERMS_REQUEST_CODE:
                for (int res : grantResults) {
                    allowed = (allowed && (res == PackageManager.PERMISSION_GRANTED));
                }
                break;
            default:
                allowed = false;
                break;
        }
        if (allowed) {
            Splash_Screen();
        } else {
            finish();
        }
    }
}
