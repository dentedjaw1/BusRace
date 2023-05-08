package com.example.mytaxifor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

public class UserBookedActivity extends AppCompatActivity {

    private String getType;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_booked);

        getType = getIntent().getStringExtra("type");

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();



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
//                    final String sitNumber = thirdSnapshot.child("SitNumber").getValue(String.class);
//                    final String  sitNumberBooking = thirdSnapshot.child("SitNumberBooking").getValue(String.class);
                    final String whereFrom = thirdSnapshot.child("WhereFrom").getValue(String.class);
                    final String whereToGo = thirdSnapshot.child("WhereToGo").getValue(String.class);


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

//                    Button infoButton = new Button(getApplicationContext());
//                    infoButton.setText("Перейти");
//                    infoBlock.addView(infoButton);

                    // добавляем блок с информацией в контейнер
                    infoContainer.addView(infoBlock);
//
//                    infoButton.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Intent intent = new Intent(getApplicationContext(), BookingActivity.class);
//                            intent.putExtra("carColor", carColor);
//                            intent.putExtra("carNumber", carNumber);
//                            intent.putExtra("time", time);
//                            intent.putExtra("date", date);
////                            intent.putExtra("sitNumber", sitNumber);
////                            intent.putExtra("sitNumberBooking", sitNumberBooking);
//                            intent.putExtra("whereFrom", whereFrom);
//                            intent.putExtra("whereToGo", whereToGo);
//                            intent.putExtra("type", "Customers");
//                            startActivity(intent);
//                        }
//                    });


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



}
