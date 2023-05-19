package com.example.mytaxifor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class DriverSerchMain extends AppCompatActivity {


    private String getType;
    private Button LogoutDriverButton, SettingsDriverButton;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    private String name;
    private String phone;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_serch_main);

        getType = getIntent().getStringExtra("type");
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        LogoutDriverButton = (Button)findViewById(R.id.driver_logout_button);
        SettingsDriverButton = (Button)findViewById(R.id.driver_settings_button);


        SettingsDriverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DriverSerchMain.this, SettingsActivity.class);
                intent.putExtra("type", "Drivers");
                startActivity(intent);
            }
        });


        //слушетель
        LogoutDriverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                currentLogoutDriverStatus = true;
                mAuth.signOut();

                LogoutDriver();
//                DisconnectDriver();
            }
        });





        databaseReference.child("Users").child("Drivers").child(mAuth.getCurrentUser().getUid()).child("RacesOn").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // создаем блок с информацией
                LinearLayout infoContainer = findViewById(R.id.info_container);
                // Удаляем все дочерние элементы из контейнера
                infoContainer.removeAllViews();

                for (DataSnapshot thirdSnapshot : dataSnapshot.getChildren()) {


                    LinearLayout infoBlock = new LinearLayout(getApplicationContext());
                    infoBlock.setOrientation(LinearLayout.VERTICAL);
                    infoBlock.setPadding(16, 16, 16, 16);

                    // получаем информацию из дочернего узла
                    final String time = thirdSnapshot.child("Time").getValue(String.class);
                    final String date = thirdSnapshot.child("Date").getValue(String.class);
                    final String whereFrom = thirdSnapshot.child("WhereFrom").getValue(String.class);
                    final String whereToGo = thirdSnapshot.child("WhereToGo").getValue(String.class);
                    final String bookingStatus = thirdSnapshot.child("Booking").getValue(String.class);


                    final String[] sitnumberNum = new String[1];

                    getUserInformation();

                    // создаем элементы для отображения информации
                    TextView whereView = new TextView(getApplicationContext());
                    whereView.setText(whereFrom + " - " + whereToGo);
                    whereView.setTextSize(16);
                    infoBlock.addView(whereView);


                    TextView sitView = new TextView(getApplicationContext());
                    sitView.setText("Когда: " + date);
                    sitView.setTextSize(16);
                    infoBlock.addView(sitView);

                    TextView timeView = new TextView(getApplicationContext());
                    timeView.setText("Время отправки: " + time);
                    timeView.setTextSize(16);
                    infoBlock.addView(timeView);


                    Button infoButton = new Button(getApplicationContext());
                    infoButton.setText("Подробнее о рейсе");
                    infoBlock.addView(infoButton);
                    // добавляем блок с информацией в контейнер
                    infoContainer.addView(infoBlock);


                    infoButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(getApplicationContext(), DriverRaceInfo.class);
                            intent.putExtra("time", time);
                            intent.putExtra("date", date);
                            intent.putExtra("whereFrom", whereFrom);
                            intent.putExtra("whereToGo", whereToGo);
                            intent.putExtra("bookingStatus", bookingStatus);
                            intent.putExtra("type", "Drivers");
                            startActivity(intent);
                        }
                    });


                }
                // получаем ссылку на блок с информацией
                View infoBlock = findViewById(R.id.info_block);
                // устанавливаем его видимость в значение View.VISIBLE
                infoBlock.setVisibility(View.VISIBLE);

                // получаем ссылку на элемент заголовка блока с информацией
                TextView infoTitle = findViewById(R.id.info_title);
            }
//

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // обработка ошибок при чтении данных из базы Firebase
            }
        });
    }
//
    private void getUserInformation() {
        databaseReference.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    if (getType.equals("Drivers")) {
                        name = dataSnapshot.child("name").getValue().toString();
                        phone = dataSnapshot.child("phone").getValue().toString();

                        // использование переменных name и phone
//                        TextView nameTextView = findViewById(R.id.name_user);
//                        nameTextView.setText(name);
//
//                        TextView phoneTextView = findViewById(R.id.phone_user);
//                        phoneTextView.setText(phone);


                    }

                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void LogoutDriver() {
        Intent welcomeIntent = new Intent(DriverSerchMain.this, WelcomeActivity.class);
        startActivity(welcomeIntent);
        finish();
    }
}