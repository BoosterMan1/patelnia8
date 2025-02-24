package com.example.patelnia8;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;


public class GameActivity extends AppCompatActivity implements SensorEventListener {
        private SensorManager sensorManager;
        private Sensor accelerometer, rotationVector;
        private ImageView kotlet;
        private final Handler gameHandler = new Handler();
        private final int FRAME_DELAY = 200; // opóźnienie klatek w milisekundach
        private boolean isRunning = true;
        private boolean isFlipped = false;
        private boolean flipCooldown = false;
        private boolean flipTriggered = false;
        private float roll = 0, pitch = 0; // przechylenie telefonu
        private float cookTimeSide1 = 0, cookTimeSide2 = 0; // Wysmażenie z obu stron (czas)
        private float velocityX = 1, velocityY = 1; // Prędkość ruchu kotleta
        private String leftImage;
        private String rightImage;

        private int goalLevel;

        private Button endButton;
        private int userResultLeft, userResultRight;
        public int userResult;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.game_layout);

            endButton = findViewById(R.id.end_cooking_button);
            endButton.setOnClickListener(v -> {
                goToResult();

            });

            goalLevel = getIntent().getIntExtra("goalLevel", 0);

            kotlet = findViewById(R.id.steak);
            endButton = findViewById(R.id.end_cooking_button);

            sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            rotationVector = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);


            if (accelerometer != null) {
                sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
            }
            if (rotationVector != null) {
                sensorManager.registerListener(this, rotationVector, SensorManager.SENSOR_DELAY_GAME);
            }

            startGameLoop();
        }

        private void startGameLoop() {
            // rozpoczęcie gry
            kotlet.setTranslationX(0);
            kotlet.setTranslationY(0);
            velocityX = 0;
            velocityY = 0;
            cookTimeSide2 = 0;
            cookTimeSide1 = 0;
            isFlipped = false;
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

            velocityX += roll * 0.002f; // im bardziej przechylisz telefon tym szybciej się ześlizguje
            velocityY += -pitch * 0.002f;

            kotlet.setTranslationX(kotlet.getTranslationX() + velocityX); // dzięki temu kotlet się ślizga!
            kotlet.setTranslationY(kotlet.getTranslationY() + velocityY);

            if (!flipCooldown) { // kiedy kotlet jest obrócony dodaje się czas smażenia po jednej stronie, kiedy nie, to na drugiej
                if (!isFlipped) {
                    cookTimeSide2 += 0.02f;
                } else {
                    cookTimeSide1 += 0.02f;
                }
            }

            if (cookTimeSide1 < 5) { // breakpointy zmiany obrazu dka lewej strony
                leftImage = "left_raw";
                userResultLeft = 1;
            } else if (cookTimeSide1 >= 5 && cookTimeSide1 < 10) {
                leftImage = "left_rare";
                userResultLeft = 2;
            } else if (cookTimeSide1 >= 10 && cookTimeSide1 < 15) {
                leftImage = "left_medium_rare";
                userResultLeft = 3;
            } else if (cookTimeSide1 >= 15 && cookTimeSide1 < 20) {
                leftImage = "left_medium_well";
                userResultLeft = 4;
            } else if (cookTimeSide1 >= 20 && cookTimeSide1 < 25) {
                leftImage = "left_well_done";
                userResultLeft = 5;
            } else if (cookTimeSide1 >= 25 && cookTimeSide1 < 30){
                leftImage = "left_congratulations";
                userResultLeft = 6;
            } else {
                userResult = 7;
            }

            if (cookTimeSide2 < 5) { // dla prawej
                rightImage = "right_raw";
                userResultRight = 1;
            } else if (cookTimeSide2 >= 5 && cookTimeSide2 < 10) {
                rightImage = "right_rare";
                userResultRight = 2;
            } else if (cookTimeSide2 >= 10 && cookTimeSide2 < 15) {
                rightImage = "right_medium_rare";
                userResultRight = 3;
            } else if (cookTimeSide2 >= 15 && cookTimeSide2 < 20) {
                rightImage = "right_medium_well";
                userResultRight = 4;
            } else if (cookTimeSide2 >= 20 && cookTimeSide2 < 25) {
                rightImage = "right_well_done";
                userResultRight = 5;
            } else if (cookTimeSide2 >= 25 && cookTimeSide2 < 30){
                rightImage = "right_congratulations";
                userResultRight = 6;
            } else{
                userResult = 7;
            }
            if (userResult == 7){
                leftImage = "left_fire";
                rightImage = "right_fire";
                imageChange(leftImage, rightImage);
            }else if (userResultLeft == userResultRight){
                userResult = userResultLeft;
            }else{
                userResult = 0;
            }


            if (kotlet.getTranslationX() < -240 || kotlet.getTranslationX() > 250 || kotlet.getTranslationY() < -240 || kotlet.getTranslationY() > 230) {
                gameOver();
            } // jeśli kotlet wyleci poza obręcz patelni, gra się kończy przegraną
        }
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                float x = event.values[0], y = event.values[1], z = event.values[2];
                float totalAcceleration = (float) Math.sqrt(x * x + y * y + z * z) - 9.81f;

                if (totalAcceleration > 12 && !flipCooldown && !flipTriggered) { // jeśli kotlet się nie obraca a telefon został podrzucony, wywołuje animację obrotu kotleta
                    animateFlip();
                    flipTriggered = true;
                    flipCooldown = true;
                    gameHandler.postDelayed(() -> flipCooldown = false, 1000); // podczas obrotu kotlet się nie smaży
                } else if (totalAcceleration < 5) {
                    flipTriggered = false;
                }

            }

            if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) { // jeżeli wykryje czujnik obrotu, wykorzysta go i pobierze wartości do zmiennych pitch i roll

                float[] rotationMatrix = new float[9];
                float[] orientationAngles = new float[3];

                SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
                SensorManager.getOrientation(rotationMatrix, orientationAngles);

                pitch = (float) Math.toDegrees(orientationAngles[1]);
                roll = (float) Math.toDegrees(orientationAngles[2]);
            }
            if (isRunning){
                updateGame();
            }
        }

        private void animateFlip() { // metoda animacji kotleta
            isRunning = false; // Pauza w grze podczas animacji

            // Animacje powiększania, obracania i pomniejszania
            ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(kotlet, "scaleX", 1f, 1.5f);
            ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(kotlet, "scaleY", 1f, 1.5f);
            scaleUpX.setDuration(300);
            scaleUpY.setDuration(300);
            scaleUpX.setInterpolator(new AccelerateDecelerateInterpolator());
            scaleUpY.setInterpolator(new AccelerateDecelerateInterpolator());

            ObjectAnimator rotateHalf1 = ObjectAnimator.ofFloat(kotlet, "rotationY", 0f, 90f);
            rotateHalf1.setDuration(250);
            rotateHalf1.setInterpolator(new AccelerateDecelerateInterpolator());


            ObjectAnimator rotateHalf2 = ObjectAnimator.ofFloat(kotlet, "rotationY", -90f, 0f);
            rotateHalf2.setDuration(250);
            rotateHalf2.setInterpolator(new AccelerateDecelerateInterpolator());

            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(kotlet, "scaleX", 1.5f, 1f);
            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(kotlet, "scaleY", 1.5f, 1f);
            scaleDownX.setDuration(300);
            scaleDownY.setDuration(300);
            scaleDownX.setInterpolator(new AccelerateDecelerateInterpolator());
            scaleDownY.setInterpolator(new AccelerateDecelerateInterpolator());

            scaleUpX.start();
            scaleUpY.start();
            scaleUpX.addListener(new AnimatorListenerAdapter() { // kiedy kotlet skończy rosnąć obróci się w połowie znikając z pola widzenia
                @Override
                public void onAnimationEnd(Animator animation) {
                    rotateHalf1.start();
                }

            });

            rotateHalf1.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                        imageChange(leftImage, rightImage);

                    kotlet.setRotationY(-90f);
                    rotateHalf2.start();
                }

            });

            rotateHalf2.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    scaleDownX.start();
                    scaleDownY.start();
                    isFlipped = !isFlipped;
                    scaleDownX.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            isRunning = true;
                        }
                    });
                }
            });
        }

        // Metoda zmiany zdjęcia
        private void imageChange(String leftImageName, String rightImageName) {

            String imageName = isFlipped ? leftImageName : rightImageName;
            Integer resId = isFlipped ? leftImagesMap.get(imageName) : rightImagesMap.get(imageName);


            if (resId != null) {
                kotlet.setImageResource(resId);
            } else {
                Log.e("ImageChange", "Nie znaleziono zasobu dla: " + imageName);
            }

        }



    private void goToResult() {

        isRunning = false; // Zatrzymywanie oświeżania gierki
        sensorManager.unregisterListener(this); // Wyłączenie czujników
        gameHandler.removeCallbacksAndMessages(null); // Zatrzymuje pętlę gry

        Intent resultIntent = new Intent(this, ResultActivity.class);
        resultIntent.putExtra("userResult", userResult);
        resultIntent.putExtra("goalLevel", goalLevel);
        startActivity(resultIntent);
        finish();
    }
    private void gameOver(){
            isRunning = false;

            Intent loseIntent = new Intent(GameActivity.this, LoseActivity.class);
            startActivity(loseIntent); // przechodzenie do ekranu przegranej
            finish();
    }

    // Mapowanie nazw obrazów dla pierwszej i drugiej strony kotleta + tekst i wyniki
    @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) { }

        // Mapy obrazków, tekstu dla lewej i prawej strony + wyniki
        private static final Map<String, Integer> leftImagesMap = new HashMap<>();
        static {
            leftImagesMap.put("left_raw", R.drawable.left_raw);
            leftImagesMap.put("left_rare", R.drawable.left_rare);
            leftImagesMap.put("left_medium_rare", R.drawable.left_medium_rare);
            leftImagesMap.put("left_medium_well", R.drawable.left_medium_well);
            leftImagesMap.put("left_well_done", R.drawable.left_well_done);
            leftImagesMap.put("left_congratulations", R.drawable.left_congratulations);
            leftImagesMap.put("left_fire", R.drawable.left_fire);
        }



        private static final Map<String, Integer> rightImagesMap = new HashMap<>();
        static {
            rightImagesMap.put("right_raw", R.drawable.right_raw);
            rightImagesMap.put("right_rare", R.drawable.right_rare);
            rightImagesMap.put("right_medium_rare", R.drawable.right_medium_rare);
            rightImagesMap.put("right_medium_well", R.drawable.right_medium_well);
            rightImagesMap.put("right_well_done", R.drawable.right_well_done);
            rightImagesMap.put("right_congratulations", R.drawable.right_congratulations);
            rightImagesMap.put("right_fire", R.drawable.right_fire);
        }


    }
