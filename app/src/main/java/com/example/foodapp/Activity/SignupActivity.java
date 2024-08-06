package com.example.foodapp.Activity;


import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationRequest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.foodapp.Domain.User;
import com.example.foodapp.Listener.FragmentListener;
import com.example.foodapp.Listener.NavigateListener;
import com.example.foodapp.Utils.GPSEnabler;
import com.example.foodapp.Fragment.NotifyDialogFragment;
import com.example.foodapp.R;
import com.example.foodapp.Fragment.UserInfoFragment;
import com.example.foodapp.databinding.ActivitySignupBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.List;
import java.util.Locale;


public class SignupActivity extends BaseActivity implements NavigateListener, FragmentListener {
    ActivitySignupBinding binding;
    private final static int REQUEST_CODE = 100;
    FusedLocationProviderClient fusedLocationProviderClient;

    private String address = "";

    private LocationRequest locationRequest;
    public final static int REQUEST_CHECK_SETTINGS = 1001;
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        GPSEnabler gpsEnabler = new GPSEnabler(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(SignupActivity.this);


        if (!gpsEnabler.checkGPSStatus()){
            gpsEnabler.showGPSDisabledAlertToUser(this);
        }

        if (gpsEnabler.checkGPSStatus()) getLastLocation();
        setVariable();
    }




    private void setVariable() {



        binding.signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = binding.userEdit.getText().toString();
                String password = binding.passEdit.getText().toString();
                String password2 = binding.passEdit2.getText().toString();



                if (!isValidEmail(email) || email.isEmpty()) {
                    NotifyDialogFragment notifyDialogFragment = new NotifyDialogFragment("Email error", "Your email is not valid");
                    notifyDialogFragment.show(getSupportFragmentManager(), "email");

                    return;
                }

                if (password.length() < 6) {
                    NotifyDialogFragment notifyDialogFragment = new NotifyDialogFragment("Password error", "Your password must be longer than 6 characters");
                    notifyDialogFragment.show(getSupportFragmentManager(), "email");

                    return;
                }

                if (!password2.equals(password)) {
                    NotifyDialogFragment notifyDialogFragment = new NotifyDialogFragment("Password error", "Passwords do not match");
                    notifyDialogFragment.show(getSupportFragmentManager(), "email");

                    return;
                }


                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignupActivity.this, task -> {
                    if (task.isSuccessful()) {

                        FirebaseUser Fuser = mAuth.getCurrentUser();

                        if (Fuser != null) {
                            String uid = mAuth.getCurrentUser().getUid();
                            User user = new User();
                            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                                @Override
                                public void onComplete(@NonNull Task<String> task) {
                                    String token = task.getResult();
                                    user.setToken(token);
                                    FirebaseDatabase.getInstance().getReference("User").child(uid).setValue(user);
                                    replaceFragment(new UserInfoFragment(address));
                                }
                            });

                        }
                        else Log.i(TAG, "Can't get current user");


                    } else {
                        Log.i(TAG, "failure: " + task.getException());
                        Toast.makeText(SignupActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });

        binding.loginNavigateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            }
        });

    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onClick() {
        startActivity(new Intent(SignupActivity.this, IntroActivity.class));
    }

    private void getLastLocation(){

        if (ContextCompat.checkSelfPermission(SignupActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                Geocoder geocoder = new Geocoder(SignupActivity.this, Locale.getDefault());
                                List<Address> addresses = null;
                                try {
                                    addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                    address = addresses.get(0).getAddressLine(0);
                                } catch (Exception e){
                                    e.printStackTrace();
                                }

                            }
                            else {
                                Toast.makeText(SignupActivity.this, "Cannot get current location!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            askPermission();
        }
    }

    private void askPermission() {
        ActivityCompat.requestPermissions(SignupActivity.this, new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION
        }, REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLastLocation();
            }
            else {
                Toast.makeText(SignupActivity.this, "Required permission!", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onClick1() {
        finish();
    }

    @Override
    public void onClick2() {

    }

    @Override
    public void onClick3() {

    }
}