package com.mc.chi.mc_androidgps;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private boolean iniValue = true;
    private LocationManager locManager;
    private LocationListener locListener;
    private double minX, minY, minZ, maxX, maxY, maxZ;
    private TextView[] GPS_TextView = new TextView[4];
    String s = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GPS_TextView[0] = (TextView) findViewById(R.id.locLonValue);
        GPS_TextView[1] = (TextView) findViewById(R.id.locLatValue);
        GPS_TextView[2] = (TextView) findViewById(R.id.locAltValue);
        GPS_TextView[3] = (TextView) findViewById(R.id.locName);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locManager != null) {
            Log.d("Loc", "onCreate: has Loc Service");
            locListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location loc) {
                    if (loc != null) {
                        double locLon = loc.getLongitude();
                        double locLat = loc.getLatitude();
                        double locAlt = loc.getAltitude();
                        setPeak(locLon, locLat, locAlt);
                        Geocoder geoCoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                        StringBuilder builder = new StringBuilder();
                        List<Address> address = null;
                        try {
                            if (geoCoder.isPresent()){
                                address = geoCoder.getFromLocation(locLat, locLon, 3);
                                int maxLines = address.get(0).getMaxAddressLineIndex();
                                for (int i=0; i<maxLines; i++) {
                                    String addressStr = address.get(0).getAddressLine(i);
                                    builder.append(addressStr);
                                    builder.append(" ");
                                }
                                String finalAddress = builder.toString(); //This is the complete address.
                                GPS_TextView[3].setText("\n" + finalAddress); //display final address.
                            }else{
                                GPS_TextView[3].setText("\n!!GeoCoder not found!!"); //display final address.
                            }
                        } catch (IOException e) {
                            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT);
                        } catch (NullPointerException e) {
                            Toast.makeText(getApplicationContext(),  e.toString(), Toast.LENGTH_SHORT);
                        }
                    }
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(), "Please able GPS satellites location service", Toast.LENGTH_LONG).show();
                }
            };
        }
        else{
            Log.d("Loc", "onCreate: Loc Service NOT FOUND");
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (locManager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locManager.removeUpdates(locListener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (locManager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locListener);
        }
        else{
            Toast.makeText(getApplicationContext(), "Location service doesn't exist", Toast.LENGTH_SHORT);
        }

    }

    private void setPeak(double x, double y, double z) {
        if (iniValue) {
            maxX = x;minX = x;
            maxY = y;minY = y;
            maxZ = z;minZ = z;
            iniValue = false;
        }
        if (x > maxX) {maxX = x;}
        if (y > maxY) {maxY = y;}
        if (z > maxZ) {maxZ = z;}
        if (x < minX) {minX = x;}
        if (y < minY) {minY = y;}
        if (z < minZ) {minZ = z;}
        s = String.format("%s\nMin: %s\nMax: %s", Double.toString(x), Double.toString(minX), Double.toString(maxX));
        GPS_TextView[0].setText(s);
        s = String.format("%s\nMin: %s\nMax: %s", Double.toString(y), Double.toString(minY), Double.toString(maxY));
        GPS_TextView[1].setText(s);
        s = String.format("%s\nMin: %s\nMax: %s", Double.toString(z), Double.toString(minZ), Double.toString(maxZ));
        GPS_TextView[2].setText(s);
    }
}
