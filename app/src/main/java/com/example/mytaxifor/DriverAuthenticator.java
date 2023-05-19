package com.example.mytaxifor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DriverAuthenticator extends AppCompatActivity {

    TextView accountCreate, driverStatus;
    Button singInBtn, singUpBtn;
    EditText emailET, passwordET;

    FirebaseAuth mAuth;
    DatabaseReference DriverDatabaseRef;
    String OnlineDriverId;

    ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_authentification);

        driverStatus = (TextView) findViewById(R.id.statusDriver);
        accountCreate = (TextView) findViewById(R.id.accountCreate);

        singInBtn = (Button) findViewById(R.id.signInDriver);
        singUpBtn = (Button) findViewById(R.id.singUpDriver);

        emailET = (EditText) findViewById(R.id.driverEmail);
        passwordET = (EditText) findViewById(R.id.driverPassword);

        singUpBtn.setVisibility(View.INVISIBLE);
        singUpBtn.setEnabled(false);

        mAuth = FirebaseAuth.getInstance();
        loadingBar = new ProgressDialog(this);

        accountCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                singUpBtn.setVisibility(View.VISIBLE);
                accountCreate.setVisibility(View.INVISIBLE);
                singInBtn.setVisibility(View.INVISIBLE);
                singUpBtn.setEnabled(true);
                driverStatus.setText("Регистрация водителя");
            }
        });

        singUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailET.getText().toString();
                String password = passwordET.getText().toString();

                RegisterDriver(email, password);

            }
        });

        singInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailET.getText().toString();
                String password = passwordET.getText().toString();
                signInDriver(email, password);
            }
        });
    }

    private void signInDriver(String email, String password) {
        loadingBar.setTitle("Вход водителя");
        loadingBar.setMessage("Загрузка");
        loadingBar.show();

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(DriverAuthenticator.this, "Добро пожаловать!", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Intent driverIntent = new Intent(DriverAuthenticator.this, DriverSerchMain.class);
                    startActivity(driverIntent);
                } else {
                    Toast.makeText(DriverAuthenticator.this, "Ошибка регистрации", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }
        });

    }

    private void RegisterDriver(String email, String password) {
        loadingBar.setTitle("Регистрация водителя");
        loadingBar.setMessage("Загрузка");
        loadingBar.show();

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    OnlineDriverId = mAuth.getCurrentUser().getUid();
                    ///пользователя в дб
                    DriverDatabaseRef = FirebaseDatabase.getInstance().getReference()
                            .child("Users").child("Drivers").child(OnlineDriverId);
                    DriverDatabaseRef.setValue(true);//подверждение

                    Intent driverIntent = new Intent(DriverAuthenticator.this, DriverSerchMain.class);
                    startActivity(driverIntent);

                    Toast.makeText(DriverAuthenticator.this, "Регистрация прошла успешно", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();

                } else {
                    Toast.makeText(DriverAuthenticator.this, "Ошибка регистрации", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }
        });

    }
}

