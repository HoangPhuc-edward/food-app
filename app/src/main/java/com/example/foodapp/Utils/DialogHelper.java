package com.example.foodapp.Utils;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.foodapp.Listener.NavigateListener;
import com.example.foodapp.R;

public class DialogHelper {
    static int green = Color.rgb(117, 177, 117);
    static int red = Color.rgb(177, 76, 74);
    static  int blue = Color.rgb(84, 137, 177);

    static boolean isNegativeButtonClicked = false;

    public static void showNotifyPopup(Context context, String title, String msg, String type){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void run() {
                final Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.dialog_notification);
                dialog.show();

                TextView titleTxt = dialog.findViewById(R.id.titleTxt);
                titleTxt.setText(title);

                TextView msgTxt = dialog.findViewById(R.id.msgTxt);
                msgTxt.setText(msg);

                ImageView img = dialog.findViewById(R.id.headerImg);
                TextView closeTxt = dialog.findViewById(R.id.closeTxt);
                dialog.findViewById(R.id.closeTxt).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                int color;

                switch (type){
                    case "success":
                        color = green;
                        img.setImageResource(R.drawable.check_circle_outline);
                        break;
                    case "error":
                        color = red;
                        img.setImageResource(R.drawable.alert_circle_outline);
                        break;
                    default:
                        color = blue;
                        img.setImageResource(R.drawable.information_outline);
                        break;

                }

                img.setBackgroundColor(color);
                closeTxt.setTextColor(color);

            }
        }, 0);
    }


    private static void startCountDown(final TextView closeTxt, int time, NavigateListener listener){
        new CountDownTimer(time * 1000, 1000){
            @SuppressLint("SetTextI18n")
            @Override
            public void onTick(long l) {
                closeTxt.setText("Continue (" + l/1000 + ")");
            }

            @Override
            public void onFinish() {
                closeTxt.setText("Continue (0)");
                if (!isNegativeButtonClicked) {
                    listener.onClick();
                } else isNegativeButtonClicked = false;
            }


        }.start();
    }

    public static void showCountdownPopup(Context context, String title, String msg, String type, int time, NavigateListener listener){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void run() {
                final Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.dialog_notification);
                dialog.show();

                TextView titleTxt = dialog.findViewById(R.id.titleTxt);
                titleTxt.setText(title);

                TextView msgTxt = dialog.findViewById(R.id.msgTxt);
                msgTxt.setText(msg);

                ImageView img = dialog.findViewById(R.id.headerImg);
                TextView closeTxt = dialog.findViewById(R.id.closeTxt);


                dialog.findViewById(R.id.closeTxt).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        isNegativeButtonClicked = true;
                        dialog.dismiss();
                        listener.onClick();
                    }
                });

                int color;

                switch (type){
                    case "success":
                        color = green;
                        img.setImageResource(R.drawable.check_circle_outline);
                        break;
                    case "error":
                        color = red;
                        img.setImageResource(R.drawable.alert_circle_outline);
                        break;
                    default:
                        color = blue;
                        img.setImageResource(R.drawable.information_outline);
                        break;

                }

                img.setBackgroundColor(color);
                closeTxt.setTextColor(color);
                startCountDown(closeTxt, time, listener);

            }
        }, 0);
    }

}
