package com.example.patelnia8; // Deklaracja pakietu aplikacji

import android.graphics.Color; // Import klasy do obsługi kolorów
import android.hardware.Sensor; // Import klasy do obsługi czujników
import android.hardware.SensorEvent; // Import klasy reprezentującej dane z czujnika
import android.hardware.SensorEventListener; // Import interfejsu do obsługi zdarzeń z czujników
import android.hardware.SensorManager; // Import klasy zarządzającej czujnikami
import android.os.Bundle; // Import klasy do obsługi stanu aktywności (np. po obrocie ekranu)
import android.util.Log; // Import klasy do logowania wiadomości w konsoli
import android.view.View; // Import klasy do obsługi widoków UI
import android.os.Handler; // Import klasy do obsługi opóźnionego wykonywania kodu
import android.widget.TextView; // Import klasy do obsługi tekstu na ekranie

import androidx.constraintlayout.widget.ConstraintLayout; // Import klasy do obsługi układu ConstraintLayout
import androidx.appcompat.app.AppCompatActivity; // Import klasy dla głównej aktywności aplikacji

public class MainActivity extends AppCompatActivity implements SensorEventListener { // Deklaracja klasy głównej aktywności, implementuje obsługę sensorów

    private SensorManager sensorManager; // Obiekt zarządzający czujnikami
    private Sensor accelerometer; // Obiekt akcelerometru (przyspieszenie)
    private Sensor rotationVector; // Obiekt czujnika rotacji
    private TextView verticalAccelerationText; // Tekst wyświetlający pionowe przyspieszenie
    private View kotlet; // Obiekt reprezentujący kotleta na ekranie
    private TextView wynik; // Obiekt tekstowy do wyświetlania wyniku
    private TextView gravityText; // Obiekt tekstowy do wyświetlania nachylenia telefonu

    private TextView marginText;
    private Handler gameHandler = new Handler(); // Handler do obsługi pętli gry
    private final int FRAME_DELAY = 200; // Czas odświeżania gry (200 ms = 5 FPS)
    private boolean isRunning = true; // Flaga wskazująca, czy gra jest uruchomiona

    private float roll = 0; // Przechowywanie wartości nachylenia (Roll - oś Y)
    private float pitch = 0; // Przechowywanie wartości pochylenia (Pitch - oś X)

    @Override
    protected void onCreate(Bundle savedInstanceState) { // Metoda wywoływana przy uruchomieniu aplikacji
        super.onCreate(savedInstanceState); // Wywołanie metody nadrzędnej
        setContentView(R.layout.activity_main); // Ustawienie układu interfejsu

        kotlet = findViewById(R.id.kotlet); // Znalezienie widoku reprezentującego kotleta
        wynik = findViewById(R.id.wynik); // Znalezienie widoku do wyświetlania wyniku
        verticalAccelerationText = findViewById(R.id.vertical_acceleration_text); // Znalezienie widoku do wyświetlania przyspieszenia
        gravityText = findViewById(R.id.gravity_text); // Znalezienie widoku do wyświetlania kąta nachylenia telefonu
        marginText = findViewById(R.id.margin_text);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE); // Pobranie menedżera czujników
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER); // Pobranie czujnika akcelerometru
        rotationVector = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR); // Pobranie czujnika wektora rotacji

        if (accelerometer != null) { // Sprawdzenie, czy akcelerometr jest dostępny
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME); // Rejestracja nasłuchiwania danych z akcelerometru
        } else {
            Log.e("SensorError", "Brak akcelerometru!"); // Logowanie błędu, jeśli czujnik nie jest dostępny
        }

        if (rotationVector != null) { // Sprawdzenie, czy czujnik wektora rotacji jest dostępny
            sensorManager.registerListener(this, rotationVector, SensorManager.SENSOR_DELAY_GAME); // Rejestracja nasłuchiwania danych z czujnika rotacji
        } else {
            Log.e("SensorError", "Brak sensora wektora rotacji!"); // Logowanie błędu, jeśli czujnik nie jest dostępny
        }

        startGameLoop(); // Uruchomienie pętli gry
    }

    private void startGameLoop() { // Metoda uruchamiająca pętlę gry
        gameHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isRunning) { // Sprawdzanie, czy gra powinna działać
                    updateGame(); // Aktualizacja stanu gry (ruch kotleta)
                    gameHandler.postDelayed(this, FRAME_DELAY); // Powtórne wywołanie po 50 ms
                }
            }
        }, FRAME_DELAY);
    }

    private void updateGame() { // Metoda aktualizująca stan gry

        Log.d("GameUpdate", "Pitch: " + pitch + ", Roll: " + roll); // Logowanie aktualnych wartości

        // Konwersja nachylenia na przesunięcie w pikselach
        float translationX = roll * 5 * getResources().getDisplayMetrics().density;
        float translationY = -pitch * 5 * getResources().getDisplayMetrics().density;

        // Ustawienie nowej pozycji kotleta
        kotlet.setTranslationX(translationX);
        kotlet.setTranslationY(translationY);

        // Aktualizacja wyświetlanego tekstu
        gravityText.setText(String.format("X: %.1f, Y: %.1f", translationX, translationY));

        // Sprawdzenie, czy kotlet wypadł poza patelnię
        if (translationX < -200 || translationX > 200 || translationY < -160 || translationY > 240) {
            wynik.setText("Przegrałeś!");
            isRunning = false; // Zatrzymanie gry
        } else {
            wynik.setText("");
        }
    }
    @Override
    public void onSensorChanged(SensorEvent event) { // Obsługa zmiany wartości czujników
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) { // Sprawdzenie, czy to akcelerometr
            float x = event.values[0]; // Odczytanie wartości osi X
            float y = event.values[1]; // Odczytanie wartości osi Y
            float z = event.values[2]; // Odczytanie wartości osi Z


            float totalAcceleration = (float) Math.sqrt(x * x + y * y + z * z); // Obliczenie całkowitego przyspieszenia
            totalAcceleration = z - 9.81f; // Korekta o przyciąganie ziemskie

            if (totalAcceleration > 12){
                TextView flipText = findViewById(R.id.flip_text);
                flipText.setText("Podrzucenie!");

            }

            verticalAccelerationText.setText(String.format("Z: %.2f m/s²\nTotal: %.2f m/s²", z, totalAcceleration)); // Wyświetlenie wartości przyspieszenia
        }

        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) { // Sprawdzenie, czy to czujnik rotacji
            float[] rotationMatrix = new float[9];
            float[] orientationAngles = new float[3];

            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values); // Przekształcenie danych na macierz rotacji
            SensorManager.getOrientation(rotationMatrix, orientationAngles); // Przekształcenie macierzy na kąty

            pitch = (float) Math.toDegrees(orientationAngles[1]); // Konwersja na stopnie (oś X)
            roll = (float) Math.toDegrees(orientationAngles[2]); // Konwersja na stopnie (oś Y)
        }
        updateGame();
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Nie musisz nic tutaj robić, jeśli nie zależy ci na dokładności czujnika
    }

    public int calcMargin(int degree) { // Obliczanie marginesu dla kotleta
        int margin = (int) ((degree * 2 + 25) * getResources().getDisplayMetrics().density);

        marginText.setText(String.format("Margin: %d", (int) margin));
        return Math.max(-11, Math.min(margin, 131)); // Ograniczenie marginesu do zakresu -30 do 350

    }

    @Override
    protected void onPause() { // Metoda wywoływana, gdy aplikacja przechodzi w tło
        super.onPause();
        sensorManager.unregisterListener(this); // Wyrejestrowanie czujników
        isRunning = false; // Zatrzymanie gry
    }

    @Override
    protected void onResume() { // Metoda wywoływana, gdy aplikacja wraca na pierwszy plan
        super.onResume();
        isRunning = true;
        startGameLoop(); // Restart gry
    }
}
