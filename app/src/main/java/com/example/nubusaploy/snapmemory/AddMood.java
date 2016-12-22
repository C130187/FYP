package com.example.nubusaploy.snapmemory;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

/**
 * Created by saowaga on 6/29/2016.
 */
public class AddMood extends AppCompatActivity {

    String mood_chosen;
    ImageButton btnAstonished, btnExcited, btnHappy, btnAnnoyed, btnNeutral, btnSatisfied, btnSad, btnTired, btnMiserable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Screen Rotation
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == 2) {
            setContentView(R.layout.add_mood_land);
        } else {
            setContentView(R.layout.add_mood);
        }

        btnAstonished = (ImageButton) findViewById(R.id.astonished);
        btnAstonished.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mood_chosen = "Astonished";
                sendResultBack(mood_chosen);
            }
        });

        btnExcited = (ImageButton) findViewById(R.id.excited);
        btnExcited.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mood_chosen = "Excited";
                sendResultBack(mood_chosen);
            }
        });

        btnHappy = (ImageButton) findViewById(R.id.happy);
        btnHappy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mood_chosen = "Happy";
                sendResultBack(mood_chosen);
            }
        });

        btnAnnoyed = (ImageButton) findViewById(R.id.annoyed);
        btnAnnoyed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mood_chosen = "Annoyed";
                sendResultBack(mood_chosen);
            }
        });

        btnNeutral = (ImageButton) findViewById(R.id.neutral);
        btnNeutral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mood_chosen = "Neutral";
                sendResultBack(mood_chosen);
            }
        });

        btnSatisfied = (ImageButton) findViewById(R.id.satisfied);
        btnSatisfied.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mood_chosen = "Satisfied";
                sendResultBack(mood_chosen);
            }
        });

        btnSad = (ImageButton) findViewById(R.id.sad);
        btnSad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mood_chosen = "Sad";
                sendResultBack(mood_chosen);
            }
        });

        btnTired = (ImageButton) findViewById(R.id.tired);
        btnTired.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mood_chosen = "Tired";
                sendResultBack(mood_chosen);
            }
        });

        btnMiserable = (ImageButton) findViewById(R.id.miserable);
        btnMiserable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mood_chosen = "Miserable";
                sendResultBack(mood_chosen);
            }
        });

        ImageButton closebtn = (ImageButton) findViewById(R.id.closeMood);
        closebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    /**
     * Send the Result Back to Tag Page
     * @param mood_chosen
     */
    public void sendResultBack(String mood_chosen){
        Intent intent = new Intent();
        intent.putExtra("mood_chosen",mood_chosen);
        setResult(RESULT_OK, intent);
        finish();
    }
}
