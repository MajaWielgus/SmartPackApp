package com.example.smartpack;

import android.os.Bundle;
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

    private EditText edtDestination;
    private EditText edtDays;
    private TextView txtPackingList;
    private Button btnGenerateList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        edtDestination = findViewById(R.id.editTextDestination);
        edtDays = findViewById(R.id.editTextDays);
        txtPackingList = findViewById(R.id.txtPackingList);
        btnGenerateList = findViewById(R.id.btnGenerateList);

        btnGenerateList.setOnClickListener(v -> {
            String destination = edtDestination.getText().toString().trim();
            String days = edtDays.getText().toString().trim();

            if (!destination.isEmpty() && !days.isEmpty()) {
                generatePackingList(destination, days);
            } else {
                Toast.makeText(HomeActivity.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void generatePackingList(String destination, String days) {
        String prompt = "I'm going to " + destination + " for " + days + " days. What should I pack?";

        OkHttpClient client = new OkHttpClient();
        String apiKey = "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" ; // <- Wklej swój prawdziwy klucz API
        String url = "https://api.openai.com/v1/chat/completions";

        try {
            JSONObject messageObj = new JSONObject();
            messageObj.put("role", "user");
            messageObj.put("content", prompt);

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("model", "gpt-3.5-turbo");
            JSONArray messagesArray = new JSONArray();
            messagesArray.put(messageObj);
            jsonBody.put("messages", messagesArray);
            jsonBody.put("max_tokens", 200);

            Request request = new Request.Builder()
                    .url(url)
                    .post(RequestBody.create(jsonBody.toString(), MediaType.parse("application/json")))
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .build();

            client.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> txtPackingList.setText("Error: " + e.getMessage()));
                }

                @Override
                public void onResponse(okhttp3.Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        try {
                            JSONObject jsonObject = new JSONObject(responseBody);
                            JSONArray choices = jsonObject.getJSONArray("choices");
                            JSONObject message = choices.getJSONObject(0).getJSONObject("message");
                            String reply = message.getString("content");

                            runOnUiThread(() -> txtPackingList.setText(reply.trim()));
                        } catch (Exception e) {
                            e.printStackTrace();
                            runOnUiThread(() -> txtPackingList.setText("Error parsing response"));
                        }
                    } else {
                        String errorBody = response.body() != null ? response.body().string() : "null";
                        Log.e("OpenAI", "Failed: " + response.code() + " - " + errorBody);
                        runOnUiThread(() -> txtPackingList.setText("Request failed: " + response.code() + "\n" + errorBody));

                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            txtPackingList.setText("Exception: " + e.getMessage());
        }
    }
}
