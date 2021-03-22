package com.intercoder.memorina;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

import javax.security.auth.callback.Callback;

public class MainActivity extends AppCompatActivity {

    int COUNT_CARDS = 12;
    int TIMEOUT = 550;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // -- Анимация колец --
        ImageView imageView = findViewById(R.id.imageView);
        Animation rotation = AnimationUtils.loadAnimation(this, R.anim.rotate);
        rotation.setRepeatCount(Animation.INFINITE);
        rotation.setDuration(42000);
        imageView.startAnimation(rotation);

        ImageView imageView_small = findViewById(R.id.imageView2);
        Animation rotation2 = AnimationUtils.loadAnimation(this, R.anim.rotate_small);
        rotation2.setRepeatCount(Animation.INFINITE);
        rotation2.setDuration(80000);
        imageView_small.startAnimation(rotation2);
        // -- Анимация колец --



    }

    public void onClickNewGame(View v){
        Intent intent = new Intent(MainActivity.this, GameActivity.class);
        intent.putExtra("count", COUNT_CARDS);
        intent.putExtra("timeout", TIMEOUT);
        startActivity(intent);
    }

    public void onClickExit(View v){
        finish();
    }

    public void onClickSettings(View v){
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        intent.putExtra("count", COUNT_CARDS);
        intent.putExtra("timeout", TIMEOUT);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            COUNT_CARDS = data.getIntExtra("count", 12);
            TIMEOUT = data.getIntExtra("timeout", 550);

        }
    }
}