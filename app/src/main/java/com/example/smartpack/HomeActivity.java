package com.example.smartpack;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class HomeActivity extends AppCompatActivity {

    private EditText editDestination, editDays;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Inicjalizacja widoków
        editDestination = findViewById(R.id.editDestination);
        editDays = findViewById(R.id.editDays);
        btnSubmit = findViewById(R.id.btnSubmit);

        // Obsługa kliknięcia przycisku
        btnSubmit.setOnClickListener(v -> {
            String destination = editDestination.getText().toString().trim();
            String daysStr = editDays.getText().toString().trim();

            if (destination.isEmpty() || daysStr.isEmpty()) {
                Toast.makeText(this, "Podaj miejsce docelowe i liczbę dni!", Toast.LENGTH_SHORT).show();
                return;
            }

            int days = Integer.parseInt(daysStr);

            // Wywołanie funkcji do pobrania pogody (dodamy w kolejnym kroku)
            fetchWeather(destination, days);
        });
    }

    private void fetchWeather(String destination, int days) {
        String apiKey = "279cebd82d925e73d19dd83339f2865e";
        String units = "metric"; // jednostka temperatury (Celsius)

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WeatherService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WeatherService weatherService = retrofit.create(WeatherService.class);

        Call<WeatherResponse> call = weatherService.getWeather(destination, apiKey, units);

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weather = response.body();
                    float temperature = weather.getMain().getTemp();
                    String description = weather.getWeather()[0].getDescription();

                    // Pokaż wynik w Toast lub zaktualizuj UI
                    Toast.makeText(HomeActivity.this,
                            "Temperatura w " + destination + ": " + temperature + "°C, " + description,
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(HomeActivity.this, "Błąd w pobieraniu danych pogodowych", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Toast.makeText(HomeActivity.this, "Błąd połączenia", Toast.LENGTH_SHORT).show();
            }
        });
    }

}

