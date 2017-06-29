package com.example.quypn.myapplication;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.quypn.AudioBoxBetaPNQ18101997.Config;

/**
 * Created by ericbhatti on 11/24/15.
 *
 * @author Eric Bhatti
 * @since 24 November, 2015
 */
public class Floaty {

    private final View closed;
    private final View head;
    private final View body;
    private final Context context;
    private final Notification notification;
    private final int notificationId;
    private static Floaty floaty;
    private final FloatyOrientationListener floatyOrientationListener;
    private float ratioY = 0;
    private float oldWidth = 0;
    private float oldX = 0;
    private boolean confChange = false;

    private static final String LOG_TAG = "Floaty";


    /**
     * @return The body of the floaty which is assigned through the {@link #createInstance} method.
     */

    public View getBody() {
        return floaty.body;
    }


    /**
     * @return The head of the floaty which is assigned through the {@link #createInstance} method.
     */

    public View getHead() {
        return floaty.head;
    }


    public View getClosed() {
        return floaty.closed;
    }

    /**
     * Creates a Singleton of the Floating Window
     *
     * @param context                   The application context
     * @param head                      The head View, upon clicking it the body is to be opened
     * @param body                      The body View
     * @param notificationId            The notificationId for your notification
     * @param notification              The notification which is displayed for the foreground service
     * @param floatyOrientationListener The {@link FloatyOrientationListener} interface with callbacks which are called when orientation changes.
     * @return A Floating Window
     */

    public static synchronized Floaty createInstance(Context context, View closed, View head, View body, int notificationId, Notification notification, FloatyOrientationListener
            floatyOrientationListener) {
        if (floaty == null) {
            floaty = new Floaty(context, closed, head, body, notificationId, notification, floatyOrientationListener);
        }

        return floaty;
    }

    /**
     * Creates a Singleton of the Floating Window
     *
     * @param context        The application context
     * @param head           The head View, upon clicking it the body is to be opened
     * @param body           The body View
     * @param notificationId The notificationId for your notification
     * @param notification   The notification which is displayed for the foreground service
     * @return A Floating Window
     */
    public static synchronized Floaty createInstance(Context context, View closed, View head, View body, int notificationId, Notification notification) {
        if (floaty == null) {
            floaty = new Floaty(context, closed, head, body, notificationId, notification, new FloatyOrientationListener() {
                @Override
                public void beforeOrientationChange(Floaty floaty) {
                    Log.d(LOG_TAG, "beforeOrientationChange");
                }

                @Override
                public void afterOrientationChange(Floaty floaty) {
                    Log.d(LOG_TAG, "afterOrientationChange");
                }
            });
        }
        return floaty;
    }

    /**
     * @return The same instance of Floating Window, which has been created through {@link #createInstance}. Don't call this method before createInstance
     */
    public static synchronized Floaty getInstance() {
        if (floaty == null) {
            throw new NullPointerException("Floaty not initialized! First call createInstance method, then to access Floaty in any other class call getInstance()");
        }
        return floaty;
    }


    private Floaty(Context context, View closed, View head, View body, int notificationId, Notification notification, FloatyOrientationListener floatyOrientationListener) {
        this.closed = closed;
        this.head = head;
        this.body = body;
        this.context = context;
        this.notification = notification;
        this.notificationId = notificationId;
        this.floatyOrientationListener = floatyOrientationListener;

    }


    /**
     * Starts the service and adds it to the screen
     */
    public void startService() {
        if (!Config.CheckService) {
            Config.CheckService = true;
            Intent intent = new Intent(context, FloatHeadService.class);
            context.startService(intent);

        }
    }

    /**
     * Stops the service and removes it from the screen
     */
    public void stopService() {
        Config.CheckService = false;
        Intent intent = new Intent(context, FloatHeadService.class);
        context.stopService(intent);
    }


    /* public static Notification createNotification(Context context, String contentTitle, String contentText, int notificationIcon, RemoteViews remoteViews) {
         return new NotificationCompat.Builder(context)
                 .setContentTitle(contentTitle)
                 .setContentText(contentText)
                 .setCustomContentView(remoteViews)
                 .setSmallIcon(notificationIcon).build();

     }*/
    public Notification getNotification() {
        return this.notification;
    }

    public static class FloatHeadService extends Service {

        private WindowManager windowManager;
        private WindowManager.LayoutParams params, params_close;
        private LinearLayout mLinearLayout, closedLinearLayout;
        GestureDetectorCompat gestureDetectorCompat;
        DisplayMetrics metrics;
        private boolean didFling;
        private int[] clickLocation = new int[2];


        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public void onConfigurationChanged(Configuration newConfig) {
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE || newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                int[] location = new int[2];
                mLinearLayout.getLocationOnScreen(location);
                floaty.oldWidth = metrics.widthPixels;
                floaty.confChange = true;
                if (floaty.getBody().getVisibility() == View.VISIBLE) {
                    floaty.oldX = clickLocation[0];
                    floaty.ratioY = (float) (clickLocation[1]) / (float) metrics.heightPixels;
                } else {
                    floaty.oldX = location[0];
                    floaty.ratioY = (float) (location[1]) / (float) metrics.heightPixels;
                }
                floaty.floatyOrientationListener.beforeOrientationChange(floaty);
                floaty.stopService();
                floaty.startService();
                floaty.floatyOrientationListener.afterOrientationChange(floaty);
            }
        }

        public int onStartCommand(Intent intent, int flags, int startId) {
            Log.d(LOG_TAG, "onStartCommand");
            metrics = new DisplayMetrics();
            windowManager.getDefaultDisplay().getMetrics(metrics);
            startForeground(floaty.notificationId, floaty.notification);
            return START_STICKY;
        }

        @Override
        public void onCreate() {
            super.onCreate();
            Log.d(LOG_TAG, "onCreate");
            mLinearLayout = new LinearLayout(getApplicationContext()) {
                @Override
                public boolean dispatchKeyEvent(KeyEvent event) {
                    if (event.getKeyCode() == KeyEvent.KEYCODE_BACK || event.getKeyCode() == KeyEvent.KEYCODE_HOME) {
                        floaty.body.setVisibility(View.GONE);
                        params.x = clickLocation[0];
                        params.y = clickLocation[1] - 36;
                        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
                        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
                        mLinearLayout.setBackgroundColor(Color.argb(0, 0, 0, 0));
                        windowManager.updateViewLayout(mLinearLayout, params);
                        return true;
                    }
                    return super.dispatchKeyEvent(event);
                }
            };


            gestureDetectorCompat = new GestureDetectorCompat(floaty.context, new GestureDetector.SimpleOnGestureListener() {
                private int initialX;
                private int initialY;
                private float initialTouchX;
                private float initialTouchY;

                @Override
                public boolean onDown(MotionEvent event) {

                    floaty.closed.setVisibility(View.VISIBLE);
                    closedLinearLayout.setVisibility(View.VISIBLE);
                    Log.d(LOG_TAG, "onDown");

                    initialX = params.x;
                    initialY = params.y;
                    initialTouchX = event.getRawX();
                    initialTouchY = event.getRawY();
                    didFling = false;
                    return false;
                }

                @Override
                public void onShowPress(MotionEvent e) {
                    Log.d(LOG_TAG, "onShowPress");
                    floaty.head.setAlpha(0.8f);


                }

                @Override
                public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                    if (floaty.body.getVisibility() == View.VISIBLE) {
                        floaty.body.setVisibility(View.GONE);
                        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
                        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
                        mLinearLayout.setBackgroundColor(Color.argb(0, 0, 0, 0));
                    }
                    params.x = (initialX + (int) ((e2.getRawX() - initialTouchX)));
                    params.y = (initialY + (int) ((e2.getRawY() - initialTouchY)));
                    windowManager.updateViewLayout(mLinearLayout, params);
                    return false;
                }

                @Override
                public boolean onSingleTapConfirmed(MotionEvent e) {
                    Log.d(LOG_TAG, "onSingleTapConfirmed");
                    if (floaty.body.getVisibility() == View.GONE) {
                        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
                        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
                        floaty.head.getLocationOnScreen(clickLocation);
                        floaty.body.setVisibility(View.VISIBLE);

                    } else {
                        floaty.body.setVisibility(View.GONE);
                        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
                        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
                        mLinearLayout.setBackgroundColor(Color.argb(0, 0, 0, 0));
                    }
                    windowManager.updateViewLayout(mLinearLayout, params);
                    return false;
                }

                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {


                    closedLinearLayout.setVisibility(View.GONE);


                    Log.d(LOG_TAG, "onFling");
                    if (Config.y >= (metrics.heightPixels - 2 * getSize(getApplicationContext()))) {
                        floaty.stopService();
                        Config.webView.onPause();
                        Config.webView.pauseTimers();
                        windowManager.updateViewLayout(mLinearLayout, params);
                    }


                    didFling = true;
                    int newX = params.x;
                    if (newX > (metrics.widthPixels / 2)) {
                        params.x = metrics.widthPixels;
                    } else {
                        params.x = 0;
                    }
                    windowManager.updateViewLayout(mLinearLayout, params);

                    return false;
                }
            });


            mLinearLayout.setOrientation(LinearLayout.VERTICAL);
            closedLinearLayout = new LinearLayout(getApplicationContext());
            closedLinearLayout.setOrientation(LinearLayout.VERTICAL);
            windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            metrics = new DisplayMetrics();
            windowManager.getDefaultDisplay().getMetrics(metrics);


            params_close = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PRIORITY_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSPARENT);
            params_close.gravity = Gravity.BOTTOM | Gravity.CENTER;


            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PRIORITY_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSPARENT);
            params.gravity = Gravity.TOP | Gravity.START;


            if (floaty.confChange) {
                floaty.confChange = false;
                if (floaty.oldX < (floaty.oldWidth / 2)) {
                    params.x = 0;
                } else {
                    params.x = metrics.widthPixels;
                }
                params.y = (int) (metrics.heightPixels * floaty.ratioY);
            } else {
                params.x = metrics.widthPixels;
                params.y = 0;
            }


            floaty.body.setVisibility(View.GONE);
            floaty.closed.setVisibility(View.GONE);


            floaty.head.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    gestureDetectorCompat.onTouchEvent(event);
                    int[] Location = new int[2];
                    v.getLocationOnScreen(Location);
                    Config.x = Location[0];
                    Config.y = Location[1];

                    if (Config.y >= (metrics.heightPixels - 2 * getSize(getApplicationContext())))
                        closedLinearLayout.setBackgroundColor(getResources().getColor(R.color.closed_bar));
                    else
                        closedLinearLayout.setBackgroundColor(getResources().getColor(R.color.transparent));


                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        floaty.head.setAlpha(0.8f);

                        if (!didFling) {
                            closedLinearLayout.setVisibility(View.GONE);
                            Log.d(LOG_TAG, "ACTION_UP");
                            if (Config.y >= (metrics.heightPixels - 2 * getSize(getApplicationContext()))) {
                                floaty.stopService();
                                Config.webView.onPause();
                                Config.webView.pauseTimers();
                                windowManager.updateViewLayout(mLinearLayout, params);
                            }
                            int newX = params.x;
                            if (newX > (metrics.widthPixels / 2))
                                params.x = metrics.widthPixels;
                            else
                                params.x = 0;

                            windowManager.updateViewLayout(mLinearLayout, params);


                        }
                    }
                    return true;
                }
            });

            windowManager.addView(mLinearLayout, params);
            windowManager.addView(closedLinearLayout, params_close);

            if (floaty.body.getParent() != null) {
                ((ViewGroup) floaty.body.getParent()).removeView(floaty.body);
            }
            mLinearLayout.setFocusable(true);

            LinearLayout.LayoutParams headParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams bodyParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);


            headParams.gravity = Gravity.TOP | Gravity.END;
            bodyParams.gravity = Gravity.TOP;
            mLinearLayout.addView(floaty.head, headParams);
            mLinearLayout.addView(floaty.body, bodyParams);


            LinearLayout.LayoutParams closeParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            closeParams.gravity = Gravity.BOTTOM | Gravity.CENTER;
            closedLinearLayout.addView(floaty.closed, closeParams);


        }

        public void onDestroy() {
            super.onDestroy();
            if (mLinearLayout != null) {
                mLinearLayout.removeAllViews();
                closedLinearLayout.removeAllViews();
                windowManager.removeView(mLinearLayout);
                windowManager.removeView(closedLinearLayout);
            }
            stopForeground(true);
        }

    }

    public static int getSize(Context context) {
        switch (context.getResources().getDisplayMetrics().densityDpi) {
            case DisplayMetrics.DENSITY_HIGH:
                return 72;
            case DisplayMetrics.DENSITY_LOW:
                return 48;
            case DisplayMetrics.DENSITY_MEDIUM:
                return 48;
            case DisplayMetrics.DENSITY_TV:
                return 72;
            case DisplayMetrics.DENSITY_XHIGH:
                return 96;
            case DisplayMetrics.DENSITY_XXHIGH:
                return 144;
            case DisplayMetrics.DENSITY_XXXHIGH:
                return 196;
        }
        return 196;
    }


}