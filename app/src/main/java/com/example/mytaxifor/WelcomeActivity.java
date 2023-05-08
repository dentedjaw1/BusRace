package com.example.mytaxifor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.dynamic.IFragmentWrapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WelcomeActivity extends AppCompatActivity {


    Button driverBtn, customerBtn;

    private FirebaseAuth mAuth;
    private FirebaseUser currrentUser;
    private DatabaseReference databaseReference;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        initialize();
        
        driverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent driverIntent = new Intent(WelcomeActivity.this, DriverAuthenticator.class);
                startActivity(driverIntent);
            }
        });

        customerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent customerIntent = new Intent(WelcomeActivity.this, CustomerAuthenticator.class);
                startActivity(customerIntent);
            }
        });

//        if (currrentUser != null)
//        {
//            openMap();
//        }

    }

//    private void openMap() {
//        databaseReference.child("Customers").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.child(mAuth.getCurrentUser().getUid()).exists())
//                {
//                    startActivity(new Intent(WelcomeActivity.this, CustomersMapActivity.class));
//
//                }
//                else {}
//            }
//
//
//
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

//        databaseReference.child("Drivers").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.child(mAuth.getCurrentUser().getUid()).exists())
//                {
//                    startActivity(new Intent(WelcomeActivity.this, DriversMapActivity.class));
//
//                }
//                else {}
//            }
//
//
//
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });



//    }

    private void initialize() {
        driverBtn = (Button)findViewById(R.id.driverBtn);
        customerBtn = (Button)findViewById(R.id.customerBtn);
        mAuth = FirebaseAuth.getInstance();
        currrentUser = mAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("Users");

    }
}