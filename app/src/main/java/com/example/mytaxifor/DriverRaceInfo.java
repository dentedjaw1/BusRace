package com.example.mytaxifor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
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


public class DriverRaceInfo extends AppCompatActivity {

    private String getType;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    private Button driverMapButton,endRaceButton;
    private TextView carColorTextView,timeTextView, dateTextView, sitNumberTextView,sitNumberBookingTextView, whereFromView, whereToGoView;

    private String name;
    private String phone;
    private String bookingStatus;


    String carColor, time, date, sitNumber,sitNumberBooking,whereFrom,whereToGo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_race_info);

        getType = getIntent().getStringExtra("type");
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        driverMapButton = (Button) findViewById(R.id.driver_map);
        endRaceButton = (Button) findViewById(R.id.end_race_button);

        // получаем переданные данные
        Intent intent = getIntent();
        time = intent.getStringExtra("time");
        date = intent.getStringExtra("date");
        whereFrom = intent.getStringExtra("whereFrom");
        whereToGo = intent.getStringExtra("whereToGo");



        // отображаем данные в TextView
//        carColorTextView = findViewById(R.id.car_color_text_view);
//        carColorTextView.setText(carColor);

        timeTextView = findViewById(R.id.time_text_view);
        timeTextView.setText(time);

        dateTextView = findViewById(R.id.date_text_view);
        dateTextView.setText(date);
//
//            sitNumberTextView = findViewById(R.id.sit_number_text_view);
//            sitNumberTextView.setText(sitNumber);
//
//            sitNumberBookingTextView = findViewById(R.id.sit_number_booking_text_view);
//            sitNumberBookingTextView.setText(sitNumberBooking);

        whereFromView = findViewById(R.id.where_from_text_view);
        whereFromView.setText(whereFrom);

        whereToGoView = findViewById(R.id.where_to_go_text_view);
        whereToGoView.setText(whereToGo);

        driverMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DriverRaceInfo.this, DriversMapActivity.class);
                //чтобы приложение понимало, что зашло с экрана пользователя
                intent.putExtra("type", "Driver");
                intent.putExtra("geoDot", whereToGo);
                startActivity(intent);
            }
        });


        endRaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                AlertDialog.Builder builder = new AlertDialog.Builder(DriverRaceInfo.this);
                builder.setMessage("Вы хотите ЗАКОНЧИТЬ рейс? ")
                        .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                databaseReference.child("Race").child(whereFrom).child(whereToGo).child(date).child(time).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        databaseReference.child("Race").child(whereFrom).child(whereToGo).child(date).child(time).removeValue();
                                        databaseReference.child("Races").child(whereFrom).child(whereToGo).child(date).child(time).removeValue();
                                        databaseReference.child("Users").child("Drivers").child(mAuth.getCurrentUser().getUid()).child("RacesOn").child(date+" Time: "+time).removeValue();


                                        for (DataSnapshot thirdSnapshot : dataSnapshot.getChildren()) {
                                            String uidCustomer = thirdSnapshot.getKey();

                                            final String[] userBonusST = new String[1];


                                            databaseReference.child("Users").child("Customers").child(uidCustomer).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    userBonusST[0] = dataSnapshot.child("bonusPoints").getValue(String.class);

                                                    Integer bonusPointsPlus = Integer.parseInt(userBonusST[0]);
                                                    bonusPointsPlus = bonusPointsPlus + 45;
                                                    String bonusPointsPlusString = Integer.toString(bonusPointsPlus);

                                                    HashMap<String, Object> usersMap = new HashMap<>();
                                                    usersMap.put("bonusPoints", bonusPointsPlusString);//8
                                                    databaseReference.child("Users").child("Customers").child(uidCustomer).updateChildren(usersMap);
                                                    return;
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError error) {
                                                    // обработка ошибки
                                                }
                                            });

                                            databaseReference.child("Users").child("Customers").child(uidCustomer).child("Booking").child(date).removeValue();




                                        }


                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                                startActivity(new Intent(DriverRaceInfo.this, DriverSerchMain.class));
                            }
                        })
                        .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Ничего не делаем, просто закрываем диалог

                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });





        databaseReference.child("Race").child(whereFrom).child(whereToGo).child(date).child(time).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot){
                                            // создаем блок с информацией
                                            LinearLayout infoContainer = findViewById(R.id.info_container);
                                            // Удаляем все дочерние элементы из контейнера
//                infoContainer.removeAllViews();

                for (DataSnapshot thirdSnapshot : dataSnapshot.getChildren()) {

                    //String phone = thirdSnapshot.child("phone").getValue().toString();
//                    final String phone = thirdSnapshot.child("Phone").getValue(String.class);
                    String childName = thirdSnapshot.child("Phone").getValue(String.class);
                    bookingStatus = thirdSnapshot.child("Booking").getValue(String.class);
                    String uidCustomer = thirdSnapshot.getKey();

                    LinearLayout infoBlock = new LinearLayout(getApplicationContext());
                    infoBlock.setOrientation(LinearLayout.VERTICAL);
                    infoBlock.setPadding(16, 16, 16, 16);

//                    getUserInformation();


                    TextView timeView = new TextView(getApplicationContext());
                    timeView.setText("Номер телефона клиента: " + childName);
                    timeView.setTextSize(16);
                    infoBlock.addView(timeView);



//                    Button infoButton = new Button(getApplicationContext());
//                    infoButton.setText("Подвердить");
//                    infoBlock.addView(infoButton);



                    LinearLayout buttonsLayout = new LinearLayout(getApplicationContext());
                    buttonsLayout.setOrientation(LinearLayout.HORIZONTAL);


                    Button callButton = new Button(getApplicationContext());
                    callButton.setText("Позвонить");
                    callButton.setBackground(getResources().getDrawable(R.color.callColor)); // устанавливаем зеленый фон
                    LinearLayout.LayoutParams lp3 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f); // ширина равна 1/2 экрана
                    lp3.gravity = Gravity.CENTER_VERTICAL; // выравниваем по вертикали
                    lp3.height = 130; // задаем высоту кнопки в пикселях
                    callButton.setLayoutParams(lp3);
                    buttonsLayout.addView(callButton);

                    Button infoButton = new Button(getApplicationContext());
                    infoButton.setText("Подвердить");
                    infoButton.setBackground(getResources().getDrawable(R.color.green)); // устанавливаем зеленый фон
                    LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f); // ширина равна 1/2 экрана
                    lp1.gravity = Gravity.CENTER_VERTICAL; // выравниваем по вертикали
                    lp1.height = 130; // задаем высоту кнопки в пикселях
                    infoButton.setLayoutParams(lp1);
                    buttonsLayout.addView(infoButton);


                    Button delButton = new Button(getApplicationContext());
                    delButton.setText("Удалить");
                    delButton.setBackground(getResources().getDrawable(R.color.red)); // устанавливаем красный фон
                    LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f); // ширина равна 1/2 экрана
                    lp2.gravity = Gravity.CENTER_VERTICAL; // выравниваем по вертикали
                    lp2.height = 130; // задаем высоту кнопки в пикселях
                    delButton.setLayoutParams(lp2);
                    buttonsLayout.addView(delButton);


                    if (bookingStatus.equals("Подтвержден")){

                        infoButton.setText("Подтверждено");
                        timeView.setBackground(getResources().getDrawable(R.color.green));

                    }

//
                    final String[] sitnumberNum = new String[1];

                    databaseReference.child("Races").child(whereFrom).child(whereToGo).child(date).child(time).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            sitnumberNum[0] = dataSnapshot.child("SitNumberBooking").getValue(String.class);
                            // сохраняем значение в переменную
//                            TextView cView = new TextView(getApplicationContext());
//                            cView.setText("Цвет машины: " + sitnumberNum[0]);
//                            cView.setTextSize(16);
//                            infoBlock.addView(cView);

                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            // обработка ошибки
                        }
                    });


//                    Кнопка подтверждения
                    infoButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            infoButton.setText("Подтверждено");
                            timeView.setBackground(getResources().getDrawable(R.color.green));

                            HashMap<String, Object> raceMap = new HashMap<>();
                            raceMap.put("Booking","Подтвержден");


                            databaseReference.child("Race").child(whereFrom).child(whereToGo).child(date).child(time).child(uidCustomer).updateChildren(raceMap);


                        }
                    });


                    // Кнопка удаления
                    delButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            AlertDialog.Builder builder = new AlertDialog.Builder(DriverRaceInfo.this);
                            builder.setMessage("Вы хотите ОТМЕНИТЬ бронирование на " + childName+ "?")
                                    .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // Получаем ссылку на базу данных



                                            //прибавляем один, так как произошла отменили бронирование
                                            int  sitNumberBookingPlus = Integer.parseInt(sitnumberNum[0]);
                                            sitNumberBookingPlus =  sitNumberBookingPlus + 1;
                                            String sitNumberBookingPlusString = Integer.toString(sitNumberBookingPlus);

//                                                    TextView numView = new TextView(getApplicationContext());
//                                                        numView.setText("Цвет машины: " + sitNumberBooking);
//                                                        numView.setTextSize(16);
//                                                        infoBlock.addView(numView);

                                            // Добавляем ДАННЫЕ в RACES в базу данных
                                            HashMap<String, Object> rasessMap = new HashMap<>();
//                        userMap.put("uid",mAuth.getCurrentUser().getUid());
//                                            rasessMap.put("WhereFrom", whereFrom.toString());//8
//                                            rasessMap.put("WhereToGo",whereToGo.toString()); //7
//                                            rasessMap.put("Date",date.toString()); //3
//                                            rasessMap.put("Time",time.toString());//6
//                                            rasessMap.put("CarColor",carColor.toString()); //1
//                                            rasessMap.put("CarNumber",carNumber.toString()); //2
//                                                    rasessMap.put("SitNumber",sitNumber.toString()); //4
                                            rasessMap.put("SitNumberBooking",sitNumberBookingPlusString.toString()); //5



                                            // Добавляем объект rasesMap в базу данных
                                            databaseReference.child("Races").child(whereFrom).child(whereToGo).child(date).child(time).updateChildren(rasessMap);



                                            databaseReference.child("Users").child("Customers").child(uidCustomer).child("Booking").child(date).removeValue();
                                            databaseReference.child("Race").child(whereFrom).child(whereToGo).child(date).child(time).child(uidCustomer).removeValue();

                                            Toast.makeText(DriverRaceInfo.this, "Бронирование отменено", Toast.LENGTH_SHORT).show();


                                            Intent intent = getIntent();
                                            finish();
                                            startActivity(intent);

                                        }
                                    })
                                    .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // Ничего не делаем, просто закрываем диалог

                                        }
                                    });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    });


                    // Кнопка звонка
                    callButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int permissionCheck = ContextCompat.checkSelfPermission(DriverRaceInfo.this, Manifest.permission.CALL_PHONE);

                            if (permissionCheck != PackageManager.PERMISSION_GRANTED)
                            {
                                ActivityCompat.requestPermissions(
                                        DriverRaceInfo.this, new String[]{Manifest.permission.CALL_PHONE}, 123);
                            }
                            else
                            {
                                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel: " + childName));
                                startActivity(intent);
                            }
                        }
                    });



                    //                    // добавляем блок с информацией в контейнер
                    infoContainer.addView(infoBlock);
                    infoBlock.addView(buttonsLayout);



//                    Button delButton = new Button(getApplicationContext());
//                    delButton.setText("Удалить");
//                    infoBlock.addView(delButton);
//                    // добавляем блок с информацией в контейнер
//                    infoContainer.addView(infoBlock);




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





//    private void getUserInformation() {
//        databaseReference.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
//                    if (getType.equals("Drivers")) {
//                        name = dataSnapshot.child("name").getValue().toString();
//                        phone = dataSnapshot.child("phone").getValue().toString();
//
//                        // использование переменных name и phone
////                        TextView nameTextView = findViewById(R.id.name_user);
////                        nameTextView.setText(name);
////
////                        TextView phoneTextView = findViewById(R.id.phone_user);
////                        phoneTextView.setText(phone);
//
//
//                    }
//
//                }
//            }
//
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//
//    }
//

}