package com.example.speech_emotion_recognition;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

public class lottie_loading_Dialog {
    private Activity activity;
    private AlertDialog dialog;
    private BlurView blurView;

    lottie_loading_Dialog(Activity activity){
        this.activity = activity;
    }
    void startLoadingDialog(String text_dialog){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        float radius = 15f;

        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.lottie_loading, null);

        blurView = view.findViewById(R.id.blurView);

        View decorView = activity.getWindow().getDecorView();
        ViewGroup rootView = (ViewGroup) decorView.findViewById(android.R.id.content);
        Drawable windowBackground = decorView.getBackground();

        blurView.setupWith(rootView, new RenderScriptBlur(activity.getApplicationContext())) // or RenderEffectBlur
                .setFrameClearDrawable(windowBackground) // Optional
                .setBlurRadius(radius);


        builder.setView(view);
        builder.setCancelable(true);

        dialog = builder.create();
        dialog.show();

    }
}