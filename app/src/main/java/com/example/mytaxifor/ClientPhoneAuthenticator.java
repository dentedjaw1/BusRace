package com.example.mytaxifor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ClientPhoneAuthenticator extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_phone_authenticator);

        Button btnLogin = findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText phoneField = findViewById(R.id.phone_field);
                EditText passwordField = findViewById(R.id.password_field);
                String phoneNumber = phoneField.getText().toString();
                String password = passwordField.getText().toString();
                signInWithPhoneAndPassword(phoneNumber, password);
            }
        });


    }

    private void signInWithPhoneAndPassword(String phoneNumber, String password) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(phoneNumber, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Авторизация через телефон и пароль была выполнена успешно
                            FirebaseUser user = task.getResult().getUser();
                            Toast.makeText(ClientPhoneAuthenticator.this, "Authentication successful", Toast.LENGTH_SHORT).show();
                        } else {
                            // Ошибка авторизации через телефон и пароль
                            Toast.makeText(ClientPhoneAuthenticator.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void showPhoneAndPasswordLoginDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Login with Phone and Password");

        final EditText phoneField = new EditText(this);
        phoneField.setInputType(InputType.TYPE_CLASS_PHONE);
        phoneField.setHint("Phone number");
        builder.setView(phoneField);

        final EditText passwordField = new EditText(this);
        passwordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passwordField.setHint("Password");
        builder.setView(passwordField);

        builder.setPositiveButton("Login", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String phoneNumber = phoneField.getText().toString();
                String password = passwordField.getText().toString();
                signInWithPhoneAndPassword(phoneNumber, password);
            }
        });

        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}