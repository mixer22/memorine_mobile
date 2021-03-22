package com.intercoder.memorina;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

public class GameActivity extends AppCompatActivity {

    int COUNT_CARDS = 12;
    int TIMEOUT = 550;
    CardsTilesView tilesView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        tilesView = findViewById(R.id.tileView);

        COUNT_CARDS = getIntent().getIntExtra("count", 12);
        TIMEOUT = getIntent().getIntExtra("timeout", 550);

        tilesView.setParameters(COUNT_CARDS, TIMEOUT, findViewById(R.id.text_score));
        tilesView.newGame();

    }
    public void onClickNewGame(View v){
        tilesView.newGame();
    }
}