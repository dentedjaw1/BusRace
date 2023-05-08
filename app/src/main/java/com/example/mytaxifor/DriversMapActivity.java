package com.example.mytaxifor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.LocationRequest;

import android.os.Bundle;
import android.text.BoringLayout;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.mytaxifor.databinding.ActivityDriversMapBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;


public class DriversMapActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;
    GoogleApiClient googleApiClient;
    Location lastLocation;
    LocationRequest locationRequest;
    Marker  customerMarker, PickUpMarker;


    private LatLng driverPosition;


    private ActivityDriversMapBinding binding;

    private Button LogoutDriverButton, SettingsDriverButton;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private Boolean currentLogoutDriverStatus = false;;
//            = false;
    private DatabaseReference assignedCustomerRef, AssignedCustomerPositionRef;
    private String driverID, customerID = "";

    private ValueEventListener AssignedCustomerPositionListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDriversMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        driverID = mAuth.getCurrentUser().getUid();//id текущего водителя

        LogoutDriverButton = (Button)findViewById(R.id.driver_logout_button);
        SettingsDriverButton = (Button)findViewById(R.id.driver_settings_button);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        SettingsDriverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DriversMapActivity.this, SettingsActivity.class);
                intent.putExtra("type", "Drivers");
                startActivity(intent);
            }
        });


        //слушетель
        LogoutDriverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentLogoutDriverStatus = true;
                mAuth.signOut();

                LogoutDriver();
                DisconnectDriver();
            }
        });

        getAssignedCustomerRequest();
    }

    private void getAssignedCustomerRequest() {
        assignedCustomerRef = FirebaseDatabase.getInstance().getReference()
                .child("Users").child("Drivers").child(driverID).child("CustomerRideID");

        assignedCustomerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    //Получение
                    customerID = snapshot.getValue().toString();

                    //получение местанахождения клиента
                    getAssignedCustomerPosition();
                }
//                else
//                {
//                    customerID = "";

//                    if (PickUpMarker != null){
//                        PickUpMarker.remove();
//                    }

//                    if (AssignedCustomerPositionListener != null){
//                        AssignedCustomerPositionRef.removeEventListener(AssignedCustomerPositionListener);
//
//                    }
//                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getAssignedCustomerPosition() {
        AssignedCustomerPositionRef = FirebaseDatabase.getInstance().getReference().child("Customers Requests")
                .child(customerID).child("l");

        AssignedCustomerPositionListener = AssignedCustomerPositionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists())
                {
                    List<Object> customerPositionMap = (List<Object>) dataSnapshot.getValue();
                    double locationLat = Double.parseDouble(customerPositionMap.get(0).toString());
                    double locationLng = Double.parseDouble(customerPositionMap.get(1).toString());

                    LatLng DriverLatLng = new LatLng(locationLat, locationLng);
                    PickUpMarker = mMap.addMarker(new MarkerOptions().position(DriverLatLng).title("Забрать клиента тут").icon(BitmapDescriptorFactory.fromResource(R.drawable.user)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(DriverLatLng));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

//    @SuppressLint("SuspiciousIndentation")
//    private void getAssignedCustomerPosition() {
//
//
//

//        //позиция пользователя назначепнго водителю
//        AssignedCustomerPositionRef = FirebaseDatabase.getInstance().getReference()
//                .child("Customer Requests").child(customerID).child("l");
//
//                AssignedCustomerPositionRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists())
//                {
//                    List<Object> customerPositionMap = (List<Object>) snapshot.getValue();
//                    double locationLat = 0;
//                    double locationLng = 0;
//
//                    if (customerPositionMap.get(0) != null)
//                    {
//                        locationLat = Double.parseDouble(customerPositionMap.get(0).toString());
//                    }
//
//                    if (customerPositionMap.get(1) != null)
//                    {
//                        locationLng = Double.parseDouble(customerPositionMap.get(1).toString());
//                    }
//                    LatLng CustomerLatLng = new LatLng(locationLat, locationLng);
//                    if(customerMarker != null)
//                    {
//                        customerMarker.remove();
//                    }
//                                        //показывает точки клиента
//                    customerMarker = mMap.addMarker(new MarkerOptions().position(CustomerLatLng).title("Забрать клиента тут")
//                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.user)));
//                    mMap.moveCamera(CameraUpdateFactory.newLatLng(CustomerLatLng));
//                    mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
//
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }

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
      if (getApplicationContext() != null)
        {
            lastLocation = location;

            //шируны и долготу находим
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            //зум
            mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
            //Залогиненый пользователь
            String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

            //передаем данные в путь
            DatabaseReference DriverAvalablityRef = FirebaseDatabase.getInstance().getReference().child(("Driver Available"));
            GeoFire geoFireAvailablity = new GeoFire(DriverAvalablityRef);
            //получаем данные
            geoFireAvailablity.setLocation(userID, new GeoLocation(location.getLatitude(), location.getLongitude()));


            DatabaseReference DriverWorkingRef = FirebaseDatabase.getInstance().getReference().child(("Driver Working"));
            //yrtrhtrhtrh
            GeoFire geoFireWorking = new GeoFire(DriverWorkingRef);
            //получаем данные
            geoFireWorking.setLocation(userID, new GeoLocation(location.getLatitude(), location.getLongitude()));

            switch (customerID)
            {
                case "":
                    geoFireWorking.removeLocation(userID);
                    geoFireAvailablity.setLocation(userID, new GeoLocation(location.getLatitude(), location.getLongitude()));
                    break;
                default:
                    geoFireAvailablity.removeLocation(userID);
                    geoFireWorking.setLocation(userID, new GeoLocation(location.getLatitude(), location.getLongitude()));
                    break;

            }
        }
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

        if (!currentLogoutDriverStatus) {
            DisconnectDriver();
        }
    }

    private void DisconnectDriver()
    {
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference DriverAvalablityRef = FirebaseDatabase.getInstance().getReference().child("Driver Available");

        GeoFire geoFire = new GeoFire(DriverAvalablityRef);
        geoFire.removeLocation(userID);
    }



    private void LogoutDriver() {
        Intent welcomeIntent = new Intent(DriversMapActivity.this, WelcomeActivity.class);
        startActivity(welcomeIntent);
        finish();
    }
}