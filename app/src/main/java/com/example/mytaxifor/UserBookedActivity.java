package com.example.mytaxifor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;

public class UserBookedActivity extends AppCompatActivity {

    private String getType;

    String name;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;


    private String phone;
    String sitNumber,sitNumberBooking;

//    String sitnumberNum;

//    String whereToGo,whereFrom,date, time,carNumber,carColor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_booked);

        getType = getIntent().getStringExtra("type");

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        getUserInformation();



        databaseReference.child("Users").child("Customers").child(mAuth.getCurrentUser().getUid()).child("Booking").addListenerForSingleValueEvent(new ValueEventListener() {
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

                    final String whereFrom = thirdSnapshot.child("WhereFrom").getValue(String.class);
                    final String whereToGo = thirdSnapshot.child("WhereToGo").getValue(String.class);

//                    final String booking = thirdSnapshot.child("Booking").getValue(String.class);

//                     carColor = thirdSnapshot.child("CarColor").getValue(String.class);
//                     carNumber = thirdSnapshot.child("CarNumber").getValue(String.class);
//                     time = thirdSnapshot.child("Time").getValue(String.class);
//                     date = thirdSnapshot.child("Date").getValue(String.class);
//
//                     whereFrom = thirdSnapshot.child("WhereFrom").getValue(String.class);
//                     whereToGo = thirdSnapshot.child("WhereToGo").getValue(String.class);



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

                    final String[] bookingStatus = new String[1];



//                    final String[] bookedStatus = new String[1];
//
//                    databaseReference.child("Race").child(whereFrom).child(whereToGo).child(date).child(time).child(phone).addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            bookedStatus[0] = dataSnapshot.child("SitNumberBooking").getValue(String.class);
//                            // сохраняем значение в переменную
//                            TextView cView = new TextView(getApplicationContext());
//                            cView.setText("Цвет машины: " + bookedStatus[0]);
//                            cView.setTextSize(16);
//                            infoBlock.addView(cView);
//
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError error) {
//                            // обработка ошибки
//                        }
//                    });






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
                    timeView.setText("Время отправки: "  + time);
                    timeView.setTextSize(16);
                    infoBlock.addView(timeView);

                    TextView colorView = new TextView(getApplicationContext());
                    colorView.setText("Цвет машины: " + carColor);
                    colorView.setTextSize(16);
                    infoBlock.addView(colorView);


                    TextView bookedView = new TextView(getApplicationContext());
                    bookedView.setText("Состояние " + name);
                    bookedView.setTextSize(16);
                    infoBlock.addView(bookedView);






//                    TextView cView = new TextView(getApplicationContext());
//                    cView.setText("Цвет машины: " + sitnumberNum);
//                    cView.setTextSize(16);
//                    infoBlock.addView(cView);

//

                    Button infoButton = new Button(getApplicationContext());
                    infoButton.setText("Отменить бронирование");
                    infoBlock.addView(infoButton);

                    // добавляем блок с информацией в контейнер
                    infoContainer.addView(infoBlock);



                            infoButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {


                                    AlertDialog.Builder builder = new AlertDialog.Builder(UserBookedActivity.this);
                                    builder.setMessage("Вы хотите ОТМЕНИТЬ бронирование на " + name+ "?")
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
                                                    rasessMap.put("WhereFrom", whereFrom.toString());//8
                                                    rasessMap.put("WhereToGo",whereToGo.toString()); //7
                                                    rasessMap.put("Date",date.toString()); //3
                                                    rasessMap.put("Time",time.toString());//6
                                                    rasessMap.put("CarColor",carColor.toString()); //1
                                                    rasessMap.put("CarNumber",carNumber.toString()); //2
//                                                    rasessMap.put("SitNumber",sitNumber.toString()); //4
                                                    rasessMap.put("SitNumberBooking",sitNumberBookingPlusString.toString()); //5



                                                    // Добавляем объект rasesMap в базу данных
                                                    databaseReference.child("Races").child(whereFrom).child(whereToGo).child(date).child(time).updateChildren(rasessMap);



                                                    databaseReference.child("Users").child("Customers").child(mAuth.getCurrentUser().getUid()).child("Booking").child(date).removeValue();
                                                    databaseReference.child("Race").child(whereFrom).child(whereToGo).child(date).child(time).child(phone).removeValue();

                                                    Toast.makeText(UserBookedActivity.this, "Бронирование отменено", Toast.LENGTH_SHORT).show();

                                                    Intent customerIntent = new Intent(UserBookedActivity.this, SerchActivity.class);
                                                    startActivity(customerIntent);
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


    public void getUserInformation() {
        databaseReference.child("Users").child("Customers").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    if (getType.equals("Customers")) {
                        name = dataSnapshot.child("name").getValue(String.class);

                        phone = dataSnapshot.child("phone").getValue(String.class);


                        // использование переменных name и phone
//                        TextView nameTextView = findViewById(R.id.name_user);
//                        nameTextView.setText(name);
//



                    }

                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
    }
