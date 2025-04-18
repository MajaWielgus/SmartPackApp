package com.example.smartpack;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {

    // Deklaracja FirebaseAuth
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Inicjalizacja FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Znajdź przyciski i pola tekstowe
        Button btnLogin = findViewById(R.id.btnLogin);
        EditText edtEmail = findViewById(R.id.edtEmail);
        EditText edtPassword = findViewById(R.id.edtPassword);

        // Obsługa kliknięcia przycisku
        btnLogin.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim(); // Pobierz e-mail
            String password = edtPassword.getText().toString().trim(); // Pobierz hasło

            if (!email.isEmpty() && !password.isEmpty()) {
                loginWithEmail(email, password); // Wywołaj metodę logowania
            } else {
                Toast.makeText(MainActivity.this, "Proszę podać e-mail i hasło", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Funkcja logowania
    public void loginWithEmail(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Logowanie udane
                        FirebaseUser user = mAuth.getCurrentUser();

                        //przekierowanie użytkownika do innej aktywności po udanym logowania
                        startActivity(new Intent(MainActivity.this, HomeActivity.class));
                        finish();
                    } else {
                        // Logowanie nieudane
                        // komunikat o błędzie
                        Toast.makeText(MainActivity.this, "Logowanie nieudane. Sprawdź dane logowania.", Toast.LENGTH_SHORT).show();
                    }
                });
    }


}

