package com.example.mytaxifor;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SerchActivity extends AppCompatActivity {

    private Spinner spinnerRace;
    private Spinner spinnerChild;
    private Spinner spinnerThird;

    private Button customerLogoutButton, settingsButton,customerRacesButton;
    private FirebaseAuth mAuth;

    String selectedRace;
    String selectedChild;
    String selectedDate;


    private DatabaseReference databaseReference;
    private ArrayAdapter<String> adapterRace;
    private ArrayAdapter<String> adapterChild;
    private ArrayAdapter<String> adapterThird;
//    private ArrayAdapter<String> adapterTime;

    private ArrayList<String> listRace = new ArrayList<>();
    private ArrayList<String> listChild = new ArrayList<>();
    private ArrayList<String> listThird = new ArrayList<>();
//    private ArrayList<String> listTime = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serch);


        customerLogoutButton = (Button)findViewById(R.id.customer_logout_button);
        settingsButton = (Button) findViewById(R.id.customer_settings_button);
        customerRacesButton = (Button) findViewById(R.id.customer_races_button);

        spinnerRace = findViewById(R.id.spinner_Race);
        spinnerChild = findViewById(R.id.spinner_Child);
        spinnerThird = findViewById(R.id.spinner_Third);
//        spinnerTime = findViewById(R.id.spinner_Time);

        adapterRace = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, listRace);
        adapterChild = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, listChild);
        adapterThird = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, listThird);
//        adapterTime = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, listTime);

        spinnerRace.setAdapter(adapterRace);
        spinnerChild.setAdapter(adapterChild);
        spinnerThird.setAdapter(adapterThird);
//        spinnerTime.setAdapter(adapterTime);

        databaseReference = FirebaseDatabase.getInstance().getReference();


        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SerchActivity.this, SettingsActivity.class);
                //чтобы приложение понимало, что зашло с экрана пользователя
                intent.putExtra("type", "Customers");
                startActivity(intent);
            }
        });

        customerRacesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SerchActivity.this, UserBookedActivity.class);
                //чтобы приложение понимало, что зашло с экрана пользователя
                intent.putExtra("type", "Customers");
                startActivity(intent);
            }
        });

        customerLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                LogoutCustomer();
            }
        });






        // добавляем слушатель изменений для первого списка
        spinnerRace.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                 selectedRace = listRace.get(position);

                // обновляем второй список на основе выбранного элемента из первого списка
                databaseReference.child("Races").child(selectedRace).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        listChild.clear();
                        for(DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                            String childName = childSnapshot.getKey();
                            listChild.add(childName);
                        }
                        adapterChild.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // обработка ошибок при чтении данных из базы Firebase
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // не реализовано
            }
        });




        // получаем ссылку на блок с информацией
        View infoBlock = findViewById(R.id.info_block);
// устанавливаем его видимость в значение View.GONE
        infoBlock.setVisibility(View.GONE);

        spinnerChild.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedChild = listChild.get(position);

                // обновляем третий список на основе выбранных элементов первого и второго списков
                databaseReference.child("Races").child(selectedRace).child(selectedChild).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        listThird.clear();
                        for(DataSnapshot thirdSnapshot: dataSnapshot.getChildren()) {
                            String thirdName = thirdSnapshot.getKey();
                            listThird.add(thirdName);
                        }
                        adapterThird.notifyDataSetChanged();
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // обработка ошибок при чтении данных из базы Firebase
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });





        spinnerThird.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedDate = listThird.get(position);

                // обновляем третий список на основе выбранных элементов первого и второго списков
                databaseReference.child("Races").child(selectedRace).child(selectedChild).child(selectedDate).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        // создаем блок с информацией
                        LinearLayout infoContainer = findViewById(R.id.info_container);
                        // Удаляем все дочерние элементы из контейнера
                        infoContainer.removeAllViews();

                        for(DataSnapshot thirdSnapshot: dataSnapshot.getChildren()) {



                            LinearLayout infoBlock = new LinearLayout(getApplicationContext());
                            infoBlock.setOrientation(LinearLayout.VERTICAL);
                            infoBlock.setPadding(16, 16, 16, 16);

                            // получаем информацию из дочернего узла
                            final String carColor = thirdSnapshot.child("CarColor").getValue(String.class);
                            final String carNumber = thirdSnapshot.child("CarNumber").getValue(String.class);
                            final String time = thirdSnapshot.child("Time").getValue(String.class);
                            final String date = thirdSnapshot.child("Date").getValue(String.class);
                            final String sitNumber = thirdSnapshot.child("SitNumber").getValue(String.class);
                            final String  sitNumberBooking = thirdSnapshot.child("SitNumberBooking").getValue(String.class);
                            final String whereFrom = thirdSnapshot.child("WhereFrom").getValue(String.class);
                            final String whereToGo = thirdSnapshot.child("WhereToGo").getValue(String.class);


                            // создаем элементы для отображения информации
                            TextView whereView = new TextView(getApplicationContext());
                            whereView.setText(whereFrom + " - " + whereToGo);
                            whereView.setTextSize(16);
                            infoBlock.addView(whereView);


                            TextView sitView = new TextView(getApplicationContext());
                            sitView.setText("Количество мест: " + sitNumber  + " Свободно: " + sitNumberBooking);
                            sitView.setTextSize(16);
                            infoBlock.addView(sitView);

                            TextView timeView = new TextView(getApplicationContext());
                            timeView.setText("Время отправки: "  + time);
                            timeView.setTextSize(16);
                            infoBlock.addView(timeView);

                            TextView colorView = new TextView(getApplicationContext());
                            colorView.setText("Цвет машины: " + carColor);
                            colorView.setTextSize(16);
                            infoBlock.addView(colorView);

                            Button infoButton = new Button(getApplicationContext());
                            infoButton.setText("Перейти");
                            infoBlock.addView(infoButton);

                            // добавляем блок с информацией в контейнер
                            infoContainer.addView(infoBlock);

                            infoButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getApplicationContext(), BookingActivity.class);
                                    intent.putExtra("carColor", carColor);
                                    intent.putExtra("carNumber", carNumber);
                                    intent.putExtra("time", time);
                                    intent.putExtra("date", date);
                                    intent.putExtra("sitNumber", sitNumber);
                                    intent.putExtra("sitNumberBooking", sitNumberBooking);
                                    intent.putExtra("whereFrom", whereFrom);
                                    intent.putExtra("whereToGo", whereToGo);
                                    intent.putExtra("type", "Customers");
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




                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // обработка ошибок при чтении данных из базы Firebase
                        }
                    });
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });








        // загружаем данные для первого списка из базы данных Firebase
        databaseReference.child("Races").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listRace.clear();
                for(DataSnapshot raceSnapshot: dataSnapshot.getChildren()) {
                    String raceName = raceSnapshot.getKey();
                    listRace.add(raceName);
                }
                adapterRace.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // обработка ошибок при чтении данных из базы Firebase
            }
        });


    }

    private void LogoutCustomer() {
        Intent welcomeIntent = new Intent(SerchActivity.this, WelcomeActivity.class);
        startActivity(welcomeIntent);
        finish();
    }
}

