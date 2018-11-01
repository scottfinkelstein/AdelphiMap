package example.com.adelphimap;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback {
    LatLng scienceBuilding = new LatLng(40.7204395, -73.6519642);
    LatLng uniCenter = new LatLng(40.7222207, -73.6522762);
    LatLng postHall = new LatLng(40.7209603, -73.6531237);

    private GoogleMap mMap;
    private static final int MY_LOCATION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MainActivity.this);


    }

    private void getLocationOnPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, MY_LOCATION_REQUEST_CODE);
        }

        mMap.setMyLocationEnabled(true);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.newLatLng(scienceBuilding));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16));


        /*mMap.addMarker(new MarkerOptions().title("Science Building").position(scienceBuilding));
        mMap.addMarker(new MarkerOptions().title("University Center").position(uniCenter));
        mMap.addMarker(new MarkerOptions().title("Post Hall").position(postHall));*/

        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://home.adelphi.edu/~fi17067/").addConverterFactory(GsonConverterFactory.create()).build();
        LocationsService service = retrofit.create(LocationsService.class);
        Call<Building[]> call = service.getAllBuildings();
        call.enqueue(new Callback<Building[]>() {
            @Override
            public void onResponse(Call<Building[]> call, Response<Building[]> response) {
                Building[] buildings = response.body();
                for (int i = 0; i < buildings.length; i++) {
                    mMap.addMarker(new MarkerOptions().title(buildings[i].name).position(new LatLng(buildings[i].lat, buildings[i].lng)));
                }
            }

            @Override
            public void onFailure(Call<Building[]> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Uh Oh!", Toast.LENGTH_SHORT).show();
            }
        });

        getLocationOnPermission();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        getLocationOnPermission();
    }


}
