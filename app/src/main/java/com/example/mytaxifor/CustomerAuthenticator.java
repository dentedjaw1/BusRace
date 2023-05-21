package com.example.mytaxifor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.net.Authenticator;

public class CustomerAuthenticator extends AppCompatActivity {

    TextView accountCreate, customerStatus;
    Button singInBtn, singUpBtn;
    EditText emailET, passwordET;

    FirebaseAuth mAuth;
    DatabaseReference CustomerDatabaseRef;
    String OnlineCustomerId;

    ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_authentification);

        customerStatus = (TextView) findViewById(R.id.statusCustomer);
        accountCreate = (TextView) findViewById(R.id.accountCreateCustomer);

        singInBtn = (Button) findViewById(R.id.signInCustomer);
        singUpBtn = (Button) findViewById(R.id.singUpCustomer);

        emailET = (EditText) findViewById(R.id.customerEmail);
        passwordET = (EditText) findViewById(R.id.customerPassword);

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
                customerStatus.setText("Регистрация клиента");
            }
        });

        singUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailET.getText().toString();
                String password = passwordET.getText().toString();

                RegisterCustomer(email, password);

            }
        });

        singInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailET.getText().toString();
                String password = passwordET.getText().toString();
                signInCustomer(email, password);
            }
        });

    }

    private void signInCustomer(String email, String password) {
        loadingBar.setTitle("Вход пользователя");
        loadingBar.setMessage("Загрузка");
        loadingBar.show();

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    Toast.makeText(CustomerAuthenticator.this, "Добро пожаловать!", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();

                    Intent customerIntent = new Intent(CustomerAuthenticator.this, SerchActivity.class);
                    startActivity(customerIntent);

                } else {
                    Toast.makeText(CustomerAuthenticator.this, "Ошибка регистрации", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }
        });
    }

    private void RegisterCustomer(String email, String password) {
        loadingBar.setTitle("Регистрация клиента");
        loadingBar.setMessage("Загрузка");
        loadingBar.show();

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    OnlineCustomerId = mAuth.getCurrentUser().getUid();
                    ///пользователя в дб
                    CustomerDatabaseRef = FirebaseDatabase.getInstance().getReference()
                            .child("Users").child("Customers").child(OnlineCustomerId);
                    CustomerDatabaseRef.setValue(true);//подверждение

                    CustomerDatabaseRef.child("bonusPoints").setValue("0");

                    Intent customerIntent = new Intent(CustomerAuthenticator.this, SerchActivity.class);
                    startActivity(customerIntent);

                    Toast.makeText(CustomerAuthenticator.this, "Регистрация прошла успешно", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();

                } else {
                    Toast.makeText(CustomerAuthenticator.this, "Ошибка регистрации", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }
        });
    }
}