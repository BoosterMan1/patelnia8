package com.example.patelnia8;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor accelerometer, rotationVector;
    private TextView verticalAccelerationText, gravityText, wynik;
    private View kotlet;
    private Handler gameHandler = new Handler();
    private final int FRAME_DELAY = 200;
    private boolean isRunning = true;
    private boolean isFlipped = false;
    private boolean flipCooldown = false;
    private boolean flipTriggered = false;
    private float roll = 0, pitch = 0;
    private float cookTimeSide1 = 0, cookTimeSide2 = 0;
    private float velocityX = 0, velocityY = 0; // Prędkość ruchu kotleta

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        kotlet = findViewById(R.id.kotlet);
        wynik = findViewById(R.id.wynik);
        verticalAccelerationText = findViewById(R.id.vertical_acceleration_text);
        gravityText = findViewById(R.id.gravity_text);
        Button endButton = findViewById(R.id.button);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        rotationVector = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        }
        if (rotationVector != null) {
            sensorManager.registerListener(this, rotationVector, SensorManager.SENSOR_DELAY_GAME);
        }

        endButton.setOnClickListener(v -> endGame());

        startGameLoop();
    }

    private void startGameLoop() {
        gameHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isRunning) {
                    updateGame();
                    gameHandler.postDelayed(this, FRAME_DELAY);
                }
            }
        }, FRAME_DELAY);
    }

    private void updateGame() {
        Log.d("GameUpdate", "Pitch: " + pitch + ", Roll: " + roll);

        // Kotlet ślizga się zamiast zmieniać natychmiastową pozycję
        velocityX += roll * 0.002f; // Im większe przechylenie, tym większa prędkość
        velocityY += -pitch * 0.002f;

        kotlet.setTranslationX(kotlet.getTranslationX() + velocityX);
        kotlet.setTranslationY(kotlet.getTranslationY() + velocityY);

        gravityText.setText(String.format("X: %.1f, Y: %.1f", kotlet.getTranslationX(), kotlet.getTranslationY()));

        if (isFlipped) {
            cookTimeSide2 += 0.01f;
        } else {
            cookTimeSide1 += 0.01f;
        }

        // Sprawdzenie, czy kotlet wyszedł poza patelnię i restart gry
        if (kotlet.getTranslationX() < -200 || kotlet.getTranslationX() > 200 || kotlet.getTranslationY() < -160 || kotlet.getTranslationY() > 240) {
            restartGame();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0], y = event.values[1], z = event.values[2];
            float totalAcceleration = (float) Math.sqrt(x * x + y * y + z * z) - 9.81f;

            // Wykrywanie podrzucenia, ale tylko raz na jeden ruch
            if (totalAcceleration > 12 && !flipCooldown && !flipTriggered) {
                flipKotlet();
                flipTriggered = true;
                flipCooldown = true;
                gameHandler.postDelayed(() -> flipCooldown = false, 500);
            } else if (totalAcceleration < 5) {
                flipTriggered = false;
            }

            verticalAccelerationText.setText(String.format("Z: %.2f m/s²\nTotal: %.2f m/s²", z, totalAcceleration));
        }

        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            float[] rotationMatrix = new float[9];
            float[] orientationAngles = new float[3];

            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
            SensorManager.getOrientation(rotationMatrix, orientationAngles);

            pitch = (float) Math.toDegrees(orientationAngles[1]);
            roll = (float) Math.toDegrees(orientationAngles[2]);
        }
        updateGame();
    }

    private void flipKotlet() {
        isFlipped = !isFlipped;
        Log.d("Kotlet", "Obrócono kotlet! Teraz smaży się " + (isFlipped ? "druga" : "pierwsza") + " strona.");
    }

    private void restartGame() {
        Log.d("GameRestart", "Restart gry!");
        isRunning = false;
        cookTimeSide1 = 0;
        cookTimeSide2 = 0;
        isFlipped = false;
        velocityX = 0;
        velocityY = 0;
        kotlet.setTranslationX(0);
        kotlet.setTranslationY(0);
        wynik.setText("Restart gry!");

        gameHandler.postDelayed(() -> {
            isRunning = true;
            startGameLoop();
        }, 1000);
    }

    private void endGame() {
        isRunning = false;
        String result = String.format("Wynik:\nStrona 1: %.1f s\nStrona 2: %.1f s", cookTimeSide1, cookTimeSide2);
        wynik.setText(result);
        Log.d("GameEnd", result);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        isRunning = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isRunning = true;
        startGameLoop();
    }
}
