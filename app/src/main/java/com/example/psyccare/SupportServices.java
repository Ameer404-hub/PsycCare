package com.example.psyccare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class SupportServices extends AppCompatActivity {

    String number;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support_services);
    }

    public void searchNearbyClinics(View view) {
        String uri = "http://maps.google.com/maps?&daddr=" + "Psychiatric Clinics";
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(mapIntent);
        try {
            startActivity(mapIntent);
        } catch (ActivityNotFoundException ex) {
            try {
                Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(unrestrictedIntent);
            } catch (ActivityNotFoundException innerEx) {
                Toast.makeText(this, "Google Maps not installed on your device", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void callAmbulance(View view) {
        number = "1122";
        if (Build.VERSION.SDK_INT < 23) {
            phoneCall(number);
        } else {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                phoneCall(number);
            } else {
                final String[] PERMISSIONS_STORAGE = {Manifest.permission.CALL_PHONE};
                //Asking request Permissions
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, 9);
            }
        }
    }

    public void callFireBrigade(View view) {
        number = "16";
        if (Build.VERSION.SDK_INT < 23) {
            phoneCall(number);
        } else {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                phoneCall(number);
            } else {
                final String[] PERMISSIONS_STORAGE = {Manifest.permission.CALL_PHONE};
                //Asking request Permissions
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, 9);
            }
        }
    }

    public void callPolice(View view) {
        number = "15";
        if (Build.VERSION.SDK_INT < 23) {
            phoneCall(number);
        } else {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                phoneCall(number);
            } else {
                final String[] PERMISSIONS_STORAGE = {Manifest.permission.CALL_PHONE};
                //Asking request Permissions
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, 9);
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean permissionGranted = false;
        switch (requestCode) {
            case 9:
                permissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (permissionGranted) {
            phoneCall(number);
        } else {
            Toast.makeText(this, "Permission denied!.", Toast.LENGTH_SHORT).show();
        }
    }

    private void phoneCall(String number) {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:"+number));
            this.startActivity(callIntent);
        } else {
            Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
        }
    }
}