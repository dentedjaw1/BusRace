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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class BookingActivity extends AppCompatActivity {

    private String getType;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    private String name;
    private String phone;

    private TextView carColorTextView,timeTextView, dateTextView, sitNumberTextView,sitNumberBookingTextView, whereFromView, whereToGoView;

    String carColor, time, date, sitNumber,sitNumberBooking,whereFrom,whereToGo,carNumber;
    String sitNumberBookingMinusString;
    int sitNumberBookingMinus;

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
            carColor = intent.getStringExtra("carColor");
            carNumber = intent.getStringExtra("carNumber");
            time = intent.getStringExtra("time");
            date = intent.getStringExtra("date");
            sitNumber = intent.getStringExtra("sitNumber");
            sitNumberBooking = intent.getStringExtra("sitNumberBooking");
            whereFrom = intent.getStringExtra("whereFrom");
            whereToGo = intent.getStringExtra("whereToGo");



            // отображаем данные в TextView
            carColorTextView = findViewById(R.id.car_color_text_view);
            carColorTextView.setText(carColor);

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
                        // Получаем ссылку на базу данных
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

                        // Добавляем ДАННЫЕ в RACE в базу данных
                        HashMap<String, Object> raceMap = new HashMap<>();
//                        userMap.put("uid",mAuth.getCurrentUser().getUid());
                        raceMap.put("WhereFrom", whereFrom.toString());
                        raceMap.put("WhereToGo",whereToGo.toString());
                        raceMap.put("Date",date.toString());
                        raceMap.put("Time",time.toString());
                        raceMap.put("Phone",phone.toString());
                        raceMap.put("Booking","Забронирован");


                        databaseReference.child("Race").child(whereFrom).child(whereToGo).child(date).child(time).child(mAuth.getCurrentUser().getUid()).updateChildren(raceMap);


                        // Добавляем ДАННЫЕ в Users в базу данных
                        HashMap<String, Object> userMap = new HashMap<>();
//                        userMap.put("uid",mAuth.getCurrentUser().getUid());
                        userMap.put("WhereFrom", whereFrom.toString());
                        userMap.put("WhereToGo",whereToGo.toString());
                        userMap.put("Date",date.toString());
                        userMap.put("Time",time.toString());
                        userMap.put("CarColor",carColor.toString());
                        userMap.put("CarNumber",carNumber.toString());


                        // Добавляем объект userMap в базу данных
                        databaseReference.child("Users").child(getType).child(mAuth.getCurrentUser().getUid()).child("Booking").child(date).updateChildren(userMap);

                        //отнимаем один, так как произошла бронь
                        sitNumberBookingMinus = Integer.parseInt(sitNumberBooking);
                        sitNumberBookingMinus--;

                        sitNumberBookingMinusString = Integer.toString(sitNumberBookingMinus);

                        // Добавляем ДАННЫЕ в RACES в базу данных
                        HashMap<String, Object> rasesMap = new HashMap<>();
//                        userMap.put("uid",mAuth.getCurrentUser().getUid());
                        rasesMap.put("WhereFrom", whereFrom.toString());//8
                        rasesMap.put("WhereToGo",whereToGo.toString()); //7
                        rasesMap.put("Date",date.toString()); //3
                        rasesMap.put("Time",time.toString());//6
                        rasesMap.put("CarColor",carColor.toString()); //1
                        rasesMap.put("CarNumber",carNumber.toString()); //2
                        rasesMap.put("SitNumber",sitNumber.toString()); //4
                        rasesMap.put("SitNumberBooking",sitNumberBookingMinusString.toString()); //5


                        // Добавляем объект rasesMap в базу данных
                        databaseReference.child("Races").child(whereFrom).child(whereToGo).child(date).child(time).updateChildren(rasesMap);


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

}

