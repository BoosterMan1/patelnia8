package com.example.patelnia8;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.view.View;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor gravity;
    private TextView verticalAccelerationText;
    private View kotlet;
    private View patelnia;
    private TextView wynik;
    private int strona1 = 0;
    private int strona2 = 0;
    private boolean odlicza = true;
    private final int white = Color.parseColor("#FFFFFF");
    private final int olive = Color.parseColor("#9BB601");
    private TextView strona1view;
    private TextView strona2view;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        kotlet = findViewById(R.id.kotlet);
        patelnia = findViewById(R.id.patelnia);
        wynik = findViewById(R.id.wynik);
        verticalAccelerationText = findViewById(R.id.vertical_acceleration_text);
        strona1view = findViewById(R.id.strona1view);
        strona2view = findViewById(R.id.strona2view);


        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gravity = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        }else{
            Log.e("SensorError", "Accelerometer sensor not available");
        }
        if (gravity != null) {
            sensorManager.registerListener(this, gravity, SensorManager.SENSOR_DELAY_NORMAL);
        }else{
            Log.e("SensorError", "Rotation vector sensor not available");
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];


            float totalAcceleration = (float) Math.sqrt(x * x + y * y + z * z);

            z -= 9.81f;
            verticalAccelerationText.setText(String.format("%.2f m/s²\nTotal Acceleration: %.2f m/s²", z, totalAcceleration));



            if (z > -4.0) {
                zacznijOdliczanie();
            }
        }
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            float[] rotationMatrix = new float[9];
            float[] orientationAngles = new float[3];

            // Konwersja wektora rotacji na macierz rotacji
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);

            // Obliczenie kątów orientacji
            SensorManager.getOrientation(rotationMatrix, orientationAngles);

            // Konwersja radianów na stopnie
            int pitch = (int) Math.toDegrees(orientationAngles[1]);   // Oś X
            int roll = (int) Math.toDegrees(orientationAngles[2]);    // Oś Y

            TextView field2 = findViewById(R.id.gravity_text);
            field2.setText(String.format("Pitch: %d.f2 Roll: %d.f2", pitch, roll));

            // Wyświetlenie wartości
            System.out.println("Pitch (Oś X): " + pitch);
            System.out.println("Roll (Oś Y): " + roll);

            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) kotlet.getLayoutParams();
            params.setMargins(calcMargin(roll), calcMargin(-1 * pitch), 0, 0);
            kotlet.setLayoutParams(params);
        }
    }
    public int calcMargin(int degree) {
        int margin = (int) ((degree * 5 + 70) * getResources().getDisplayMetrics().density);
        if (margin < -30 || margin > 350){
            wynik.setText("Przegrałeś!");
        }else{
            wynik.setText("");
        }
        return Math.max(-31, Math.min(margin, 351)); // Ograniczenie do zakresu 0-500
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }

    }
    private void zacznijOdliczanie() {
        for (int i = 0; i <= 15; i++){
            if (odlicza) {
                strona1++; // Zwiększ licznik1, jeśli flaga isCountingFirst jest true
            } else {
                strona2++; // Zwiększ licznik2, jeśli flaga isCountingFirst jest false
            }
            // Aktualizacja interfejsu użytkownika
            strona1view.setText(String.format("Strona 1: %d", strona1));
            strona2view.setText(String.format("Strona 2: %d", strona2));
            try {
                Thread.sleep(1000); // Odczekanie 1 sekundy
            } catch (InterruptedException e) {
                e.printStackTrace(); // Obsługa wyjątku, jeśli wątek zostanie przerwany
            }
        }
    }

//    @Override
//    protected void gameStart(){
//        onResume();
//
//    }
}

//private Handler handler = new Handler();
//private Runnable counterTask;
//private boolean odlicza = true; // Flaga określająca, który licznik zwiększać
//private int strona1 = 0;
//private int strona2 = 0;
//
//private void zacznijOdliczanie() {
//    counterTask = new Runnable() {
//        @Override
//        public void run() {
//            // Zwiększ licznik w zależności od flagi
//            if (odlicza) {
//                strona1++;
//            } else {
//                strona2++;
//            }
//
//            // Aktualizacja widoków
//            strona1view.setText(String.format("Strona 1: %d", strona1));
//            strona2view.setText(String.format("Strona 2: %d", strona2));
//
//            // Zaplanowanie kolejnego wywołania po 1 sekundzie
//            handler.postDelayed(this, 1000);
//        }
//    };
//
//    // Uruchomienie pierwszego zadania
//    handler.post(counterTask);
//}
//
//@Override
//protected void onPause() {
//    super.onPause();
//    // Przerwanie odliczania przy pauzowaniu aktywności
//    handler.removeCallbacks(counterTask);
//}
//
//@Override
//protected void onResume() {
//    super.onResume();
//    // Wznowienie odliczania po powrocie
//    if (counterTask != null) {
//        handler.post(counterTask);
//    }
//}