package com.example.patelnia8;

import android.content.Intent;
import android.os.Bundle;
import java.util.Random;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;



public class GoalActivity extends AppCompatActivity {
    private int goalLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.goal_layout);

        // Losowanie poziomu wysmażenia od 1 do 5
        Random random = new Random();
        goalLevel = random.nextInt(5) + 1; // Liczby 1-5

        // Wybranie odpowiedniego obrazka dla poziomu
        ImageView goalImage = findViewById(R.id.goal_image);
        TextView goalText = findViewById(R.id.goal_text);
        goalText.setText(getGoalTextResource(goalLevel));

        Integer goalImageRes = getGoalImageResource(goalLevel);
        goalImage.setImageResource(goalImageRes);


        // Przekazanie wartości do kolejnej aktywności
        Button startGameButton = findViewById(R.id.goal_start_button);
        startGameButton.setOnClickListener(v -> {
            Intent intent = new Intent(GoalActivity.this, GameActivity.class);
            intent.putExtra("goalLevel", goalLevel);
            intent.putExtra("goalImageRes", goalImageRes);
            startActivity(intent);
        });
    }

    private int getGoalImageResource(int level) {
            switch (level) {
                case 1: return R.drawable.rare_result;
                case 2: return R.drawable.medium_rare_result;
                case 3: return R.drawable.medium_well_result;
                case 4: return R.drawable.well_done_result;
                case 5: return R.drawable.congratulations_result;
                default: return R.drawable.medium_rare_result;
            }
        };
        private String getGoalTextResource(int level) {
            switch (level) {
                case 1: return "Rare";
                case 2: return "Medium rare";
                case 3: return "Medium well";
                case 4: return "Well done";
                case 5: return "Very well done!";
                default: return "Rare";
            }
        };
}

