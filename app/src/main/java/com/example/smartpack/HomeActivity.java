package com.example.smartpack;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HomeActivity extends AppCompatActivity {

    private EditText edtDestination, edtDays;
    private Button btnGenerate;
    private TextView txtResult;

    private final OkHttpClient client = new OkHttpClient();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private final String API_KEY = BuildConfig.GEMINI_API_KEY;
    private final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + API_KEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        edtDestination = findViewById(R.id.editTextDestination);
        edtDays = findViewById(R.id.editTextDays);
        btnGenerate = findViewById(R.id.btnGenerateList);
        txtResult = findViewById(R.id.txtPackingList);

        btnGenerate.setOnClickListener(v -> {
            String destination = edtDestination.getText().toString().trim();
            String days = edtDays.getText().toString().trim();

            if (destination.isEmpty() || days.isEmpty()) {
                Toast.makeText(this, "Wypełnij wszystkie pola!", Toast.LENGTH_SHORT).show();
                return;
            }

            txtResult.setText("Generowanie listy...");

            String userPrompt = "Lecę do " + destination + " na " + days + " dni. Co powinienem spakować?";
            sendGeminiRequest(userPrompt);
        });
    }

    private void sendGeminiRequest(String prompt) {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        try {
            JSONObject messageObj = new JSONObject();
            JSONArray partsArray = new JSONArray();
            partsArray.put(new JSONObject().put("text", prompt));
            JSONArray contentsArray = new JSONArray();
            contentsArray.put(new JSONObject().put("parts", partsArray));
            messageObj.put("contents", contentsArray);

            RequestBody body = RequestBody.create(JSON, messageObj.toString());

            Request request = new Request.Builder()
                    .url(API_URL)
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .build();

            new Thread(() -> {
                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful() && response.body() != null) {
                        String responseData = response.body().string();

                        JSONObject json = new JSONObject(responseData);
                        String result = json.getJSONArray("candidates")
                                .getJSONObject(0)
                                .getJSONObject("content")
                                .getJSONArray("parts")
                                .getJSONObject(0)
                                .getString("text");

                        mainHandler.post(() -> txtResult.setText(result));

                    } else {
                        Log.e("Gemini", "Error: " + response.code());
                        mainHandler.post(() -> txtResult.setText("Błąd: " + response.code()));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    mainHandler.post(() -> txtResult.setText("Błąd połączenia: " + e.getMessage()));
                } catch (Exception e) {
                    e.printStackTrace();
                    mainHandler.post(() -> txtResult.setText("Błąd parsowania odpowiedzi."));
                }
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
            txtResult.setText("Błąd: " + e.getMessage());
        }
    }
}
