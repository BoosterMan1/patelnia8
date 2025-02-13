package com.example.patelnia8;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ResultActivity extends AppCompatActivity {
    private int goalLevel, userResult, goalImageRes, yourImageRes;
    private String userResultText, goalText;
    private TextView yourSteakText, goalSteakText, resultText, textView3, textView6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_layout);

        yourImageRes = getIntent().getIntExtra("yourImageRes", 0);
        goalLevel = getIntent().getIntExtra("goalLevel", 0);
        userResult = getIntent().getIntExtra("userResult", 0);
        goalImageRes = getIntent().getIntExtra("goalImageRes", 0);

        goalText = getIntent().getStringExtra("goalText");
        if (goalText == null) goalText = "Unknown";

        userResultText = getIntent().getStringExtra("userResultText");
        if (userResultText == null) userResultText = "Unknown";

        resultText = findViewById(R.id.result_text);
        yourSteakText = findViewById(R.id.your_steak_text);
        goalSteakText = findViewById(R.id.goal_steak_text);
        ImageView goalSteakImage = findViewById(R.id.goal_steak_image);
        ImageView yourSteakImage = findViewById(R.id.your_steak_image);

        goalSteakImage.setImageResource(goalImageRes);

        Integer yourImageRes = getGoalImageResource(userResult);
        yourSteakImage.setImageResource(yourImageRes);

        yourSteakText.setText(userResultText);
        goalSteakText.setText(goalText);

        textView3 = findViewById(R.id.textView3);
        textView6 = findViewById(R.id.textView6);
        textView3.setText(String.valueOf(goalLevel));
        textView6.setText(String.valueOf(userResult));
        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, MenuActivity.class);
            startActivity(intent);
            finish();
        });

        if (userResult == goalLevel) {
            resultText.setText("You win!");
        } else {
            resultText.setText("You lose!");
        }
    }
    private int getGoalImageResource(int level) {
        switch (level) {
            case 1: return R.drawable.raw_result;
            case 2: return R.drawable.rare_result;
            case 3: return R.drawable.medium_rare_result;
            case 4: return R.drawable.medium_well_result;
            case 5: return R.drawable.well_done_result;
            case 6: return R.drawable.congratulations_result;
            case 7: return R.drawable.dust;
            default: return R.drawable.raw_result;
        }
    };
}
