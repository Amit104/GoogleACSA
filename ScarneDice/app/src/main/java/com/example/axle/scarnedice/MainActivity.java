package com.example.axle.scarnedice;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button roll, hold, reset;
    ImageView dice , dice2;
    TextView score;
    int user_overall = 0, user_turn = 0, comp_overall = 0, comp_turn = 0, turn,turn2,turn_total;
    int[] s = {
            R.mipmap.dice1, R.mipmap.dice2, R.mipmap.dice3, R.mipmap.dice4, R.mipmap.dice5, R.mipmap.dice6
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        roll = (Button) findViewById(R.id.roll);
        hold = (Button) findViewById(R.id.hold);
        reset = (Button) findViewById(R.id.reset);
        dice = (ImageView) findViewById(R.id.dice);
        dice2 = (ImageView) findViewById(R.id.dice2);
        score = (TextView) findViewById(R.id.score);
        score.setText("Your score: " + user_overall + " computer score: " + comp_overall);
        dice.setImageResource(R.mipmap.dice1);
        dice2.setImageResource(R.mipmap.dice1);
        roll.setOnClickListener(this);
        hold.setOnClickListener(this);
        reset.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.roll:
                hold.setEnabled(true);
                int turn = helper();
                int turn2= helper2();
                int turn_total=turn + turn2;
                if (turn == 1 && turn2 == 1) {
                    user_overall = 0;
                    score.setText("Your score: " + user_overall + " computer score: " + comp_overall + "  your turn score: " + user_turn);
                    computerTurn();
                }else if(turn==1 || turn2==1){
                    user_turn = 0;
                    score.setText("Your score: " + user_overall + " computer score: " + comp_overall + "  your turn score: " + user_turn);
                    computerTurn();
                } else if(turn_total==12){
                    user_turn += turn_total;
                    score.setText("Your score: " + user_overall + " computer score: " + comp_overall + "  your turn score: " + user_turn);
                    hold.setEnabled(false);
                }else {
                    user_turn += turn_total;
                    score.setText("Your score: " + user_overall + " computer score: " + comp_overall + "  your turn score: " + user_turn);
                }
                break;

            case R.id.hold:
                score.setText("Your score: " + user_overall + " computer score: " + comp_overall + "  your turn score: " + user_turn);
                user_overall += user_turn;
                user_turn = 0;
                if (user_overall >= 100) {
                    Toast.makeText(this, "User wins", Toast.LENGTH_SHORT).show();
                    winner(true);
                }
                computerTurn();
                break;

            case R.id.reset:
                user_overall = 0;
                user_turn = 0;
                comp_overall = 0;
                comp_turn = 0;
                score.setText("Your score: " + user_overall + " computer score: " + comp_overall);
                break;
        }
    }

    public void computerTurn() {
        hold.setEnabled(false);
        roll.setEnabled(false);
        final Handler timerHandler = new Handler();
        final Runnable timerRunnable = new Runnable() {
            @Override
            public void run() {
                turn = helper();
                turn2 = helper2();
                turn_total = turn + turn2;
                Log.d("Comp turn", String.valueOf(turn_total));
                comp_turn += turn_total;
                score.setText("Your score: " + user_overall + " computer score: " + comp_overall + "  computer turn score: " + comp_turn);
                if (turn == 1 && turn2 == 1) {
                    comp_overall = 0;
                    score.setText("Your score: " + user_overall + "   computer score: " + comp_overall + "  computer turn score: " + comp_turn);
                    hold.setEnabled(true);
                    roll.setEnabled(true);
                    timerHandler.removeCallbacks(this);
                }else if(turn2 == 1 || turn == 1){
                    comp_turn = 0;
                    score.setText("Your score: " + user_overall + "   computer score: " + comp_overall + "  computer turn score: " + comp_turn);
                    hold.setEnabled(true);
                    roll.setEnabled(true);
                    timerHandler.removeCallbacks(this);
                }else if (comp_turn > 20) {
                    hold.setEnabled(true);
                    roll.setEnabled(true);
                    comp_overall += comp_turn;
                    if (comp_overall >= 100) {
                        Toast.makeText(MainActivity.this, "Comp wins", Toast.LENGTH_SHORT).show();
                        timerHandler.removeCallbacks(this);
                        winner(false);
                    }
                    score.setText("Your score: " + user_overall + " computer score: " + comp_overall + "  computer turn score: " + comp_turn);
                    comp_turn = 0;
                } else {
                    timerHandler.postDelayed(this, 500);
                }
            }
        };
        timerHandler.postDelayed(timerRunnable, 500);
    }

    public int helper() {
        Random r = new Random();
        int turnRoll = r.nextInt(6);
        dice.setImageResource(s[turnRoll]);
        turnRoll++;
        return turnRoll;
    }
    public int helper2() {
        Random r = new Random();
        int turnRoll = r.nextInt(6);
        dice2.setImageResource(s[turnRoll]);
        turnRoll++;
        return turnRoll;
    }

    public void winner(boolean win) {
        String s = null;
        if (win) {
            s = "user";
        } else {
            s = "Computer";
        }
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(true);
        builder.setMessage(s + " Won");
        builder.setTitle("Result");
        builder.setPositiveButton("Play Again", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                user_overall = 0;
                user_turn = 0;
                comp_overall = 0;
                comp_turn = 0;
                score.setText("Your score: " + user_overall + " computer score: " + comp_overall);
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });

        builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });
        android.app.AlertDialog a = builder.create();
        a.show();
    }
}
