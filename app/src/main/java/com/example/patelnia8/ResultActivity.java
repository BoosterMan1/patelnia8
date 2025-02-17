package com.example.patelnia8;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ResultActivity extends AppCompatActivity {
    private int goalLevel, userResult;
    private String userResultText, goalText;
    private TextView yourSteakText, goalSteakText, resultText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_layout);

        // Pobranie danych z Intent
        goalLevel = getIntent().getIntExtra("goalLevel", 0);
        userResult = getIntent().getIntExtra("userResult", 0);
        goalText = getIntent().getStringExtra("goalText");
        userResultText = getIntent().getStringExtra("userResultText");

        // Domyślne wartości w razie błędu
        if (goalText == null) goalText = "Unknown";
        if (userResultText == null) userResultText = "Unknown";

        // Pobranie widoków
        resultText = findViewById(R.id.result_text);
        yourSteakText = findViewById(R.id.your_steak_text);
        goalSteakText = findViewById(R.id.goal_steak_text);
        ImageView goalSteakImage = findViewById(R.id.goal_steak_image);
        ImageView yourSteakImage = findViewById(R.id.your_steak_image);
        Button backButton = findViewById(R.id.back_button);

        // Ustawienie obrazków i tekstów
        goalSteakImage.setImageResource(getGoalImageResource(goalLevel));
        yourSteakImage.setImageResource(getResultImageResource(userResult));

        goalSteakText.setText(goalText);
        yourSteakText.setText(userResultText);

        // Obsługa przycisku powrotu
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, MenuActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        // Ocena wyniku
        if (userResultText.equals(goalText)) {
            resultText.setText("You win!");
        } else {
            resultText.setText("You lose!");
        }
    }

    private int getResultImageResource(int level) {
        switch (level) {
            case 1: return R.drawable.raw_result;
            case 2: return R.drawable.rare_result;
            case 3: return R.drawable.medium_rare_result;
            case 4: return R.drawable.medium_well_result;
            case 5: return R.drawable.well_done_result;
            default: return R.drawable.raw_result;
        }
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
    }
}
