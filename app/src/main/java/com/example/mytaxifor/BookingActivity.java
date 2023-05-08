package com.example.mytaxifor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BookingActivity extends AppCompatActivity {

    private String getType;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    private String name;
    private String phone;



    private Button bookButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);


        getType = getIntent().getStringExtra("type");
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(getType);

            // получаем переданные данные
            Intent intent = getIntent();
            String carColor = intent.getStringExtra("carColor");
            String time = intent.getStringExtra("time");
            String date = intent.getStringExtra("date");
            String sitNumber = intent.getStringExtra("sitNumber");
            String sitNumberBooking = intent.getStringExtra("sitNumberBooking");
            String whereFrom = intent.getStringExtra("whereFrom");
            String whereToGo = intent.getStringExtra("whereToGo");



            // отображаем данные в TextView
            TextView carColorTextView = findViewById(R.id.car_color_text_view);
            carColorTextView.setText(carColor);

            TextView timeTextView = findViewById(R.id.time_text_view);
            timeTextView.setText(time);

            TextView dateTextView = findViewById(R.id.date_text_view);
            dateTextView.setText(date);

            TextView sitNumberTextView = findViewById(R.id.sit_number_text_view);
            sitNumberTextView.setText(sitNumber);

            TextView sitNumberBookingTextView = findViewById(R.id.sit_number_booking_text_view);
            sitNumberBookingTextView.setText(sitNumberBooking);


        getUserInformation();





        bookButton = findViewById(R.id.book_button);
        bookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showBookingDialog();
            }
        });





    }

    private void showBookingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Вы хотите забронировать эту поездку?")
                .setPositiveButton("Бронировать", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Toast.makeText(BookingActivity.this, "Бронирование подвержденно", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Отменить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Ничего не делаем, просто закрываем диалог
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void getUserInformation() {
        databaseReference.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0)
                {
                    if (getType.equals("Customers")) {
                        name = dataSnapshot.child("name").getValue().toString();
                        phone = dataSnapshot.child("phone").getValue().toString();

                        // использование переменных name и phone
                        TextView nameTextView = findViewById(R.id.name_user);
                        nameTextView.setText(name);

                        TextView phoneTextView = findViewById(R.id.phone_user);
                        phoneTextView.setText(phone);


                    }

                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}

