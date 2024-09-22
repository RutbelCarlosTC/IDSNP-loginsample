package com.example.firstlogin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.firstlogin.databinding.ActivityMainBinding;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class LoginActivity extends AppCompatActivity {
    private static String TAG = "MainActivity";
    private ActivityMainBinding binding;
    private AccountEntity accountEntity;
    private String accountEntityString;
    private File accountsFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        EditText edtUsername = binding.edtUsername;
        EditText edtPassword = binding.edtPassword;
        Button btnLogin = binding.btnLogin;
        Button btnAddAccount = binding.btnAddAccount;

        accountsFile = new File(getFilesDir(),"cuentas.txt");

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = edtUsername.getText().toString();
                String password = edtPassword.getText().toString();

                if(checkCredentials (username, password)){
                    Toast.makeText(getApplicationContext(), "Bienvenido "+ username, Toast.LENGTH_LONG).show();
                    Log.d(TAG,"Bienvenido " + username);

                    Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                    intent.putExtra("ACCOUNT", accountEntityString);
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(), "Cuenta no ecnontrada", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "Cuenta no encontrada");
                }
            }
        });

        btnAddAccount.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), AccountActivity.class);
            activityResultLauncher.launch(intent);

        });
    }

    //Verficar las credenciales en el archivo de cuentas
    private boolean checkCredentials(String username, String password){
        try {
                BufferedReader reader = new BufferedReader(new FileReader(accountsFile));
                String line;
                Gson gson = new Gson();
                while ((line = reader.readLine()) != null){
                AccountEntity account = gson.fromJson(line, AccountEntity.class);
                if (account.getUsername().equals(username) && account.getPassword().equals(password)){
                    accountEntityString = line;
                    return true;
                }
            }
            reader.close();

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    //Manejar el resultado de AccountActivity
    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult activityResult) {
                    Integer resultCode = activityResult.getResultCode();
                    if (resultCode==AccountActivity.ACCOUNT_ACEPTAR){
                        Intent data = activityResult.getData();
                        accountEntityString = data.getStringExtra(AccountActivity.ACCOUNT_RECORD);
                        Gson gson = new Gson();
                        accountEntity =  gson.fromJson(accountEntityString, AccountEntity.class);

                        //Guardar la cuenta en el archivo
                        saveAccountToFile(accountEntityString);

                        String firstname = accountEntity.getFirstname();

                        Toast.makeText(getApplicationContext(),"Nombre: "+ firstname, Toast.LENGTH_LONG).show();
                        Log.d("LoginActivity", "Nombre:" + firstname);

                    }
                    else if (resultCode==AccountActivity.ACCOUNT_CANCELAR){
                        Toast.makeText(getApplicationContext(),"Cancelado", Toast.LENGTH_LONG).show();
                    }

                }
            }
    );

    private void saveAccountToFile(String accountJson) {
        try {
            FileWriter writer = new FileWriter(accountsFile, true);
            writer.write(accountJson + "\n");
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}