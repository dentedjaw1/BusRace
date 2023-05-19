package com.example.mytaxifor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.HasApiKey;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.mytaxifor.databinding.ActivityCustomersMapBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Driver;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomersMapActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;
       GoogleApiClient googleApiClient;
       Location lastLocation;
       LocationRequest locationRequest;
       Marker driverMarker, PickUpMarker;
       GeoQuery geoQuery;


        private Button customerLogoutButton, settingsButton;
        private  Button callTaxiButton;
        private  String customerID;
        private LatLng CustomerPosition;
        private  int radius = 1; // для расширения радиуса(если нет водителя поблизости)
        private  Boolean driverFound = false; //для поиска водителя
        private Boolean requestType = false;
        private  String driverFoundID;

        private FirebaseAuth mAuth;
        private FirebaseUser currentUser;
        private DatabaseReference CustomerDatabaseRef; //позиция клинета

        private  DatabaseReference DriversAvailableRef; //Свободный такстист
        private  DatabaseReference DriversRef; //cсылка на водителя
        private  DatabaseReference DriversLocationRef;//таксит работяга

        private ActivityCustomersMapBinding binding;
        private ValueEventListener DriverLocationRefListener;

        private TextView txtName, txtPhone, txtCarName;
        private CircleImageView driverPhoto;
        private RelativeLayout relativeLayout;
        private ImageView callDriver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

     binding = ActivityCustomersMapBinding.inflate(getLayoutInflater());
     setContentView(binding.getRoot());

        customerLogoutButton = (Button)findViewById(R.id.customer_logout_button);
        callTaxiButton = (Button) findViewById(R.id.customer_order_button);
        settingsButton = (Button) findViewById(R.id.customer_settings_button);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        //в этом стринге заложен униклаьный id клиента
        customerID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        CustomerDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Customers Requests");
        DriversAvailableRef = FirebaseDatabase.getInstance().getReference().child("Driver Available");
        DriversLocationRef = FirebaseDatabase.getInstance().getReference().child("Driver Working");

        txtName=(TextView) findViewById(R.id.driver_name);
        txtPhone=(TextView) findViewById(R.id.driver_phone_number);
        txtCarName=(TextView) findViewById(R.id.driver_car);
        callDriver = findViewById(R.id.call_to_driver);
        driverPhoto = (CircleImageView) findViewById(R.id.driver_photo);
        relativeLayout = findViewById(R.id.rel1);
        relativeLayout.setVisibility(View.INVISIBLE);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CustomersMapActivity.this, SettingsActivity.class);
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

        callTaxiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                if (requestType)
//                {
//                    requestType = false;
//                    GeoFire geofire = new GeoFire(CustomerDatabaseRef);
//                    geofire.removeLocation(customerID);
//
//                    if(PickUpMarker !=null)
//                    {
//                        PickUpMarker.remove();
//                    }
//                    if(driverMarker !=null)
//                    {
//                        driverMarker.remove();
//                    }
//
//                    callTaxiButton.setText("Вызвать такси");
//                    if (driverFound!=null)
//                    {
//                        DriversRef = FirebaseDatabase.getInstance().getReference()
//                                .child("Users").child("Drivers").child(driverFoundID)
//                                .child("CustomerRideID");
//
//                        DriversRef.removeValue();
//
//                        driverFoundID = null;
//                    }
//
//                    driverFound = false;
//                    radius = 1;
//
//
//                }
//                else {
//                    requestType = true;


                    GeoFire geofire = new GeoFire(CustomerDatabaseRef);
                    //долгота и ширина
                    geofire.setLocation(customerID, new GeoLocation(lastLocation.getLatitude(), lastLocation.getLongitude()));
                    //маркер пользователя
                    CustomerPosition = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
//                    PickUpMarker =
                    mMap.addMarker(new MarkerOptions().position(CustomerPosition).title("Я здесь")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.user)));

                    callTaxiButton.setText("Поиск водителя...");
                    getNearByDrivers();
                }
//            }
        });
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        buildGoogleApiClient();
        //проверка
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(locationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        lastLocation = location;
        //шируны и долготу находим
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        //зум
        mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
        //Залогиненый пользователь

    }
    protected synchronized  void buildGoogleApiClient(){
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        }

    private void LogoutCustomer() {
        Intent welcomeIntent = new Intent(CustomersMapActivity.this, WelcomeActivity.class);
        startActivity(welcomeIntent);
        finish();
    }

    //поиск водителя
    private void getNearByDrivers() {
        GeoFire geoFire = new GeoFire(DriversAvailableRef); //местоположение водителя
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(CustomerPosition.latitude, CustomerPosition.longitude), radius);
        geoQuery.removeAllListeners();

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if(!driverFound){
                    //водитель найден(key = ключ водителя)
                    driverFound = true;
                     driverFoundID = key;

                    DriversRef = FirebaseDatabase.getInstance().getReference().child("Users")
                            .child("Drivers").child(driverFoundID);
                    HashMap driverMap = new HashMap();
                    driverMap.put("CustomerRideID", customerID);
                    DriversRef.updateChildren(driverMap);//обновляем информацию

                    GetDriverLocation();
                }
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if (!driverFound){
                    //водитель не найден ++
                    radius++;
                    getNearByDrivers();
                }

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    private void GetDriverLocation() {
//        DriverLocationRefListener =
      DriversLocationRef.child(driverFoundID).child("l").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            List<Object> driverLocationMap = (List<Object>) dataSnapshot.getValue();
                            double LocationLat = 0;
                            double LocationLng = 0;
                            callTaxiButton.setText("Водитель найден");
                            //получаем инфу о водителе
                            getDriverInformation();
                            relativeLayout.setVisibility(View.VISIBLE);

                            if (driverLocationMap.get(0) != null)
                            {
                                LocationLat = Double.parseDouble(driverLocationMap.get(0).toString());
                            }

                            if (driverLocationMap.get(1) != null)
                            {
                                LocationLng = Double.parseDouble(driverLocationMap.get(1).toString());
                            }
                            LatLng DriverLatLng = new LatLng(LocationLat, LocationLng);

                            if(driverMarker != null)
                            {
                                driverMarker.remove();
                            }

                            //точка клиента (для рассчетов)
                            Location location1 = new Location("");
                            location1.setLatitude(CustomerPosition.latitude);
                            location1.setLongitude(CustomerPosition.longitude);

                            //точка водителя (для рассчетов)
                            Location location2 = new Location("");
                            location2.setLatitude(DriverLatLng.latitude);
                            location2.setLongitude(DriverLatLng.longitude);

                            // расстояние между
                            float Distance = location1.distanceTo(location2);

                            if(Distance < 150){
                                callTaxiButton.setText("Такси подъезжает");
                            }
                            else
                            {
                                callTaxiButton.setText("Расстояние до такси" + String.valueOf((Distance)));
                            }

                            driverMarker = mMap.addMarker(new MarkerOptions().position(DriverLatLng).title("Ваше такси тут")
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.car)));


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    //показывает информацию о водителе
    private void getDriverInformation()
    {
        //берём информацию (id) водителя
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Users").child("Drivers").child(driverFoundID);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists() && snapshot.getChildrenCount()>0)
                {


                        String carname = snapshot.child("carname").getValue().toString();
                        String name = snapshot.child("name").getValue().toString();
                        String phone = snapshot.child("phone").getValue().toString();
                        txtName.setText(name);
                        txtPhone.setText(phone);
                        txtCarName.setText(carname);



                    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    //не звони сюда больше
                        callDriver.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                int permissionCheck = ContextCompat.checkSelfPermission(CustomersMapActivity.this, Manifest.permission.CALL_PHONE);

                                if (permissionCheck != PackageManager.PERMISSION_GRANTED)
                                {
                                    ActivityCompat.requestPermissions(
                                            CustomersMapActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 123);
                                }
                                else
                                {
                                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel: " + phone));
                                    startActivity(intent);
                                }
                            }
                        });
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//                        String carname = dataSnapshot.child("carname").getValue().toString();
//                        carET.setText(carname);

//                    if (dataSnapshot.hasChild("image")) {
//                        String image = dataSnapshot.child("image").getValue().toString();
//                        Picasso.get().load(image).into(circleImageView);
//                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

}
