package com.yahya.shadow;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.slider.Slider;
import com.google.android.material.slider.Slider.OnSliderTouchListener;

public class UselessActivity extends AppCompatActivity {

    private Slider slider;
    private TextView textView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_useless);

        slider = findViewById(R.id.pickup);
        textView = findViewById(R.id.status);
        slider.addOnSliderTouchListener(touchListener);


    }
    private final OnSliderTouchListener touchListener =
            new OnSliderTouchListener() {
                @Override
                public void onStartTrackingTouch(Slider slider) {
                    if (slider.getValue()>=75.0){
                        //textView.setTextColor(Color.parseColor("#FFFFFF"));

                    }

            }
                @Override
                public void onStopTrackingTouch(Slider slider) {

                }
    };
}