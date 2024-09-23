package com.example.firstlogin;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String accountEntityString = getIntent().getStringExtra(LoginActivity.LOGIN_ACCOUNT);

        Gson gson = new Gson();
        AccountEntity accountEntity =  gson.fromJson(accountEntityString, AccountEntity.class);
        String firstname = accountEntity.getFirstname();
        String lastname = accountEntity.getLastname();

        TextView textWelcome = findViewById(R.id.textWelcome);
        textWelcome.setText("Bienvenido "+firstname+" " + lastname );

    }
}