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

        // Domyślne wartości w razie błędu
        if (goalText == null) goalText = "Unknown";
        if (userResultText == null) userResultText = "Unknown";

        // Pobranie widoków
        resultText = findViewById(R.id.result_text);
        yourSteakText = findViewById(R.id.your_steak_text);
        goalSteakText = findViewById(R.id.goal_steak_text);
        ImageView goalSteakImage = findViewById(R.id.goal_steak_image);
        ImageView yourSteakImage = findViewById(R.id.your_steak_image);
        Button backButton = findViewById(R.id.result_button);

        // Ustawienie obrazków i tekstów
        goalSteakImage.setImageResource(getImageResource(goalLevel));
        yourSteakImage.setImageResource(getImageResource(userResult));

        goalSteakText.setText(getTextResource(goalLevel));
        yourSteakText.setText(getTextResource(userResult));

        // Obsługa przycisku powrotu
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(ResultActivity.this, MenuActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            System.exit(0);
        });

        // Ocena wyniku
        if (userResult == goalLevel) {
            resultText.setText("You win!");
        } else {
            resultText.setText("You lose!");
        }
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
            case 7: return "It's dust...";
            default: return "???";
        }
    }

}
