package com.example.patelnia8;

import android.content.Intent;
import android.os.Bundle;
import java.util.Random;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class GoalActivity extends AppCompatActivity {
    public int goalLevel;
    private String goalText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.goal_layout);

        // Losowanie poziomu wysmażenia od 1 do 5
        Random random = new Random();
        goalLevel = random.nextInt(7) + 1; // Losuje wartości od 1 do 7

        // Pobranie widoków
        ImageView goalImage = findViewById(R.id.goal_image);
        TextView goalTextView = findViewById(R.id.goal_text);
        Button startGameButton = findViewById(R.id.goal_button);

        // Pobranie zasobów
        goalText = getTextResource(goalLevel);
        int goalImageRes = getImageResource(goalLevel);

        // Ustawienie obrazu i tekstu
        goalImage.setImageResource(goalImageRes);
        goalTextView.setText(goalText);

        // Przekazanie wartości do GameActivity
        startGameButton.setOnClickListener(v -> {
            Intent intent = new Intent(GoalActivity.this, GameActivity.class);
            intent.putExtra("goalLevel", goalLevel);
            startActivity(intent);
            finish();
        });
    }

    private int getImageResource(int level) {
        switch (level) {
            case 0: return R.drawable.question_mark;
            case 1: return R.drawable.raw_result;
            case 2: return R.drawable.rare_result;
            case 3: return R.drawable.medium_rare_result;
            case 4: return R.drawable.medium_well_result;
            case 5: return R.drawable.well_done_result;
            case 6: return R.drawable.burnt_result;
            case 7: return R.drawable.dust;
            default: return R.drawable.question_mark;
        }
    }

    private String getTextResource(int level) {
        switch (level) {
            case 0: return "Not equal on both sides";
            case 1: return "Raw";
            case 2: return "Rare";
            case 3: return "Medium rare";
            case 4: return "Medium well";
            case 5: return "Well done";
            case 6: return "Burnt";
            case 7: return "DUST";
            default: return "???";
        }
    }
}
