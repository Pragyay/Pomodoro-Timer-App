package com.example.pomodorotimer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentManager;


import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.Locale;

public class MainActivity extends AppCompatActivity  {

    boolean running=false;
    public boolean isBreak=false;
    boolean fragmentIsVisible = false;

    public int pomodoroSeconds = 25*60;
    public int breakSeconds = 5*60;

    boolean fullScreen = false;

    public String time;

    int pomodoroStartingTime = pomodoroSeconds;
    int breakStartingTime = breakSeconds;

    int seconds;
    int startingTime;

    private Button mainButton;
    private Button fullScreenButton;
    private Button pomodoroButton;
    private Button breakButton;
    private Button dropDownButton;

    TextView timer;

    TextView textView;

    LinearLayout layout;
    LinearLayout buttonLayout;


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN);
        }
    }

    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);

        layout = findViewById(R.id.layout);
        buttonLayout = findViewById(R.id.buttonLayout);

        timer = findViewById(R.id.Timer);

        textView = findViewById(R.id.textView);
        textView.setText( "Time to do work!");

        mainButton = findViewById(R.id.button);
        mainButton.setOnClickListener(v -> {

            vibrateNow(100);
            if(running){
                mainButton.setText("Start");
            }else{
                mainButton.setText("Pause");
            }
            running = !running;
        });

        Button resetButton = findViewById(R.id.resetButton);
        resetButton.setOnClickListener(v -> {

            vibrateNow(100);
            seconds = startingTime;
            running = false;
            mainButton.setText("Start");
        });

        fullScreenButton = findViewById(R.id.fullScreenButton);
        fullScreenButton.setOnClickListener(v -> {

            fullScreen = !fullScreen;

            vibrateNow(100);

            viewVisibility();

        });

        pomodoroButton = findViewById(R.id.pomodoroButton);
        pomodoroButton.setClickable(false);

        breakButton = findViewById(R.id.breakButton);
        breakButton.setClickable(false);

        dropDownButton = findViewById(R.id.dropDownButton);
        dropDownButton.setOnClickListener(v -> {

            vibrateNow(100);

            if(!fragmentIsVisible) {
                layout.setVisibility(View.VISIBLE);
                buttonLayout.setVisibility(View.GONE);
                fullScreenButton.setVisibility(View.GONE);

                EditTimeFragment fragment = new EditTimeFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.layout, fragment)
                        .commit();
                textView.setVisibility(View.GONE);
            }else{
                layout.setVisibility(View.GONE);
                buttonLayout.setVisibility(View.VISIBLE);
                textView.setVisibility(View.VISIBLE);
                fullScreenButton.setVisibility(View.VISIBLE);
            }
            fragmentIsVisible = !fragmentIsVisible;

        });


        if(getIntent().getExtras() != null) {
            Bundle b = getIntent().getExtras();
            pomodoroSeconds = b.getInt("pomodoroTime",0);
            pomodoroStartingTime = pomodoroSeconds;

            breakSeconds = b.getInt("breakTime",0);
            breakStartingTime = breakSeconds;

        }else {
            pomodoroStartingTime = pomodoroSeconds;
            breakStartingTime = breakSeconds;
        }
        if (!isBreak) {
            seconds = pomodoroSeconds;
        } else {
            seconds = breakSeconds;
        }
        startingTime = seconds;


        final Handler handler = new Handler();
        Runnable runnable = new Runnable(){
            @Override
            public void run() {

                int min = seconds /60;
                int sec = seconds %60;
                time = String.format(Locale.getDefault(),"%02d:%02d",min,sec);


                if(startingTime == seconds){
                    timer.setText(time);
                }

                if(running) {

                    timer.setText(time);
                    seconds--;

                    if (seconds < 0) {
                        vibrateNow(500);
                        running = false;
                        mainButton.setText("Start");
                        isBreak = !isBreak;

                        if (isBreak) {
                            seconds = breakSeconds;
                            startingTime = breakStartingTime;
                            textView.setText("Take a break.");
                            breakButton.setBackground(ResourcesCompat.getDrawable(getResources(),
                                    R.drawable.switch_button_clicked,
                                    null));
                            pomodoroButton.setBackground(ResourcesCompat.getDrawable(getResources(),
                                    R.drawable.switch_button,
                                    null));


                        } else {
                            seconds = pomodoroSeconds;
                            startingTime = pomodoroStartingTime;
                            textView.setText("Time to do work!");
                            pomodoroButton.setBackground(ResourcesCompat.getDrawable(getResources(),
                                    R.drawable.switch_button_clicked,
                                    null));
                            breakButton.setBackground(ResourcesCompat.getDrawable(getResources(),
                                    R.drawable.switch_button,
                                    null));
                        }
                    }
                }
                handler.postDelayed(this,1000);

            }
        };
        handler.post(runnable);
    }

    private void vibrateNow(long millis){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ((Vibrator) getSystemService(Context.VIBRATOR_SERVICE))
                    .vibrate(VibrationEffect.createOneShot(millis,VibrationEffect.EFFECT_TICK));
        }else{
            ((Vibrator)getSystemService(VIBRATOR_SERVICE)).vibrate(millis);
        }

    }

    public void viewVisibility(){
        if(fullScreen) {
            buttonLayout.setVisibility(View.INVISIBLE);
            pomodoroButton.setVisibility(View.INVISIBLE);
            breakButton.setVisibility(View.INVISIBLE);
            dropDownButton.setVisibility(View.INVISIBLE);
            textView.setVisibility(View.INVISIBLE);

            fullScreenButton.setBackground(ResourcesCompat.getDrawable(getResources(),
                    R.drawable.ic_baseline_fullscreen_exit_24,
                    null));

            int orientation = this.getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_PORTRAIT){
                timer.setTextSize(TypedValue.COMPLEX_UNIT_SP, 122);
            }else{
                timer.setTextSize(TypedValue.COMPLEX_UNIT_SP,160);
            }

        }else{
            buttonLayout.setVisibility(View.VISIBLE);
            pomodoroButton.setVisibility(View.VISIBLE);
            breakButton.setVisibility(View.VISIBLE);
            dropDownButton.setVisibility(View.VISIBLE);
            textView.setVisibility(View.VISIBLE);

            fullScreenButton.setBackground(ResourcesCompat.getDrawable(getResources(),
                    R.drawable.ic_fullscreen,
                    null));

            timer.setTextSize(TypedValue.COMPLEX_UNIT_SP,110);
        }
    }

    private void updateButtons(){
        timer.setText(time);
        if(running){
            mainButton.setText("Pause");
        }else{
            mainButton.setText("Play");
        }
        if(isBreak){
            breakButton.setBackground(ResourcesCompat.getDrawable(getResources(),
                    R.drawable.switch_button_clicked,
                    null));
            pomodoroButton.setBackground(ResourcesCompat.getDrawable(getResources(),
                    R.drawable.switch_button,
                    null));
            textView.setText("Take a break.");
        }else{
            pomodoroButton.setBackground(ResourcesCompat.getDrawable(getResources(),
                R.drawable.switch_button_clicked,
                null));
            breakButton.setBackground(ResourcesCompat.getDrawable(getResources(),
                    R.drawable.switch_button,
                    null));
            textView.setText("Time to do work!");
        }
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putInt("seconds",seconds);
        outState.putBoolean("running",running);
        outState.putBoolean("isBreak",isBreak);
        outState.putBoolean("fullScreen",fullScreen);
        outState.putString("time",time);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        seconds = savedInstanceState.getInt("seconds");
        time = savedInstanceState.getString("time");
        running = savedInstanceState.getBoolean("running");
        isBreak = savedInstanceState.getBoolean("isBreak");
        fullScreen = savedInstanceState.getBoolean("fullScreen");
        updateButtons();
        viewVisibility();
    }
}