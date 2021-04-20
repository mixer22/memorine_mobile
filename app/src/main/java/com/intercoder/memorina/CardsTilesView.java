package com.intercoder.memorina;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class Card {
    Paint p = new Paint();
    int color_index;
    float x, y, col, row, max_width, max_height;
    int width, height;
    boolean isOpen = false;
    float scale_factor = (float) 557/350;
    Context ctx;
    Resources res;
    int[] res_icons = {R.drawable.card_1, R.drawable.card_2, R.drawable.card_3, R.drawable.card_4,
            R.drawable.card_5, R.drawable.card_6, R.drawable.card_7, R.drawable.card_8,
            R.drawable.card_9, R.drawable.card_10};

    public Card(float x, float y, float col, float row, int color, Context ctx) {
        this.color_index = color;
        this.x = x;
        this.y = y;
        this.col = col;
        this.row = row;
        this.ctx = ctx;
        res = ctx.getResources();
    }

    public void draw(Canvas c) {
        width = c.getWidth();
        height = c.getHeight();

        max_width = (float) width / 5;
        max_height = (float) max_width * scale_factor;

        float shift_x = max_width/2;
        float shift_y = max_height/2;
        float w = width/col;
        float h = height/row;

        Drawable image;
        if(isOpen && color_index != -1) {
            image = res.getDrawable(res_icons[color_index], null);
        }else{
            image = res.getDrawable(R.drawable.card_closed, null);
        }
        image.setBounds((int)(x*w - shift_x + w/2), (int)(y*h - shift_y + h/2), (int)(x*w+shift_x + w/2),(int)(y*h + shift_y + h/2));
        image.draw(c);
    }

    public boolean flip (float touch_x, float touch_y) {
        float shift_x = max_width/2;
        float shift_y = max_height/2;
        float w = width/col;
        float h = height/row;
        if (touch_x >= (x*w - shift_x + w/2) && touch_x <= (x*w + shift_x + w/2) &&
                touch_y >= (y*h - shift_y + h/2) && touch_y <= y*h + shift_y + h/2) {
            isOpen =! isOpen;
            return true;
        }
        return false;
    }

}

public class CardsTilesView extends View {
    ArrayList<Card> cards = new ArrayList<>();
    List<Integer> indexes_pict = new ArrayList<>();
    int COUNT_CARDS = 12;
    int TIMEOUT = 550;
    TextView scoreView;
    int SCORE = 50;

    int width, height;
    int col_count = 2, row_count = 5; // max [4; 5]
    int divider = 3;
    boolean isOnPauseNow = false;
    int openedCard = 0;
    Context context;

    public CardsTilesView(Context context) {
        super(context);
        this.context = context;
    }

    public CardsTilesView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        width = getWidth();
        height = getHeight();
        Drawable image = getContext().getResources().getDrawable(R.drawable.background_game, null);
        int size = Math.max(width, height);
        image.setBounds(- ((size - width)), - ((size - height)), size + ((size - width)/2), size + ((size - height)/2));
        image.draw(canvas);
        for (Card c: cards) {
            c.draw(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        if (event.getAction() == MotionEvent.ACTION_DOWN && !isOnPauseNow)
        {
            for (Card c: cards) {
                if(openedCard  == 0) {
                    if (c.flip(x, y)) {
                        openedCard++;
                        invalidate();
                        return true;
                    }
                }
                if (openedCard == 1) {
                    if (c.flip(x, y)) {
                        invalidate();
                        openedCard++;
                        PauseTask task = new PauseTask();
                        task.execute(TIMEOUT);
                        isOnPauseNow = true;
                        return true;
                    }
                }
            }
        }
        return true;
    }

    public void newGame(){
        cards.clear();
        indexes_pict.clear();
        SCORE = 50;
        scoreView.setText((String)(context.getResources().getString(R.string.label_score) + " " + SCORE));
        invalidate();
        int c = 0;

        for(int i=0; i<col_count;i++){
            for(int j=0; j<row_count;j++) {
                cards.add(new Card(i, j, col_count, row_count, -1, this.context));
                if(c % 2 == 0){
                    indexes_pict.add(c/2);
                }
                c++;
                if(COUNT_CARDS % divider != 0 && j == row_count - 2){
                    break;
                }
                Random random = new Random();
                int color = random.nextInt(indexes_pict.size());
                float f = random.nextFloat();
                if(f <= 0.89) // Шанс повторения
                    indexes_pict.remove(color);
            }
        }
        if(COUNT_CARDS % divider != 0){
            int col = COUNT_CARDS % divider;
            for(int i=0; i<col;i++){
                cards.add(new Card(i, row_count-1, col, row_count, -1, this.context));
                if(c % 2 == 0){
                    indexes_pict.add(c/2);
                }
                c++;
            }
        }
    }

    public void finishGame(){
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.alert_finish_game, null);
        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(context);
        TextView scoreWinText = promptsView.findViewById(R.id.textScore);
        scoreWinText.setText(String.valueOf(SCORE));
        mDialogBuilder.setView(promptsView);
        mDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Новая игра",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                newGame();
                            }
                        })
                .setNegativeButton("Отмена", null);
        AlertDialog alertDialog = mDialogBuilder.create();
        alertDialog.show();
    }

    public void setParameters(int count, int timeout, TextView textView){
        COUNT_CARDS = count;
        TIMEOUT = timeout;
        scoreView = textView;
        scoreView.setText((String)(context.getResources().getString(R.string.label_score) + " " + SCORE));

        if (divider < 6)
            divider = 2;
        if(count < 12)
            divider = 3;
        else
            divider = 4;

        if(count % divider == 0){
            row_count = count / divider;
        }else{
            row_count = count / divider + 1;
        }
        col_count = divider;
    }

    public void addScore(int step){
        SCORE += step;
        scoreView.setText((String)(context.getResources().getString(R.string.label_score) + " " + SCORE));
    }

    class PauseTask extends AsyncTask<Integer, Void, Void> {
        @Override
        protected Void doInBackground(Integer... integers) {
            try {
                Thread.sleep(integers[0]);
            } catch (InterruptedException ignored) {}
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            List<Card> duplicates = new ArrayList<>();
            for (Card c: cards) {
                if (c.isOpen) {
                    duplicates.add(c);
                    c.isOpen = false;
                }
            }
            if(duplicates.size() == 2)
                if(duplicates.get(0).color_index == duplicates.get(1).color_index){
                    cards.remove(duplicates.get(0));
                    cards.remove(duplicates.get(1));
                    addScore(10);
                }else{
                    addScore(-1);
                }
            if(cards.size() == 0){
                finishGame();
            }
            openedCard = 0;
            isOnPauseNow = false;
            invalidate();
        }
    }

}
