package com.example.patelnia8;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_layout);

        // Obsługa przycisku "Start Cooking" – uruchomienie sekwencji startowej
        Button startButton = findViewById(R.id.menu_button);
        startButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, GoalActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
