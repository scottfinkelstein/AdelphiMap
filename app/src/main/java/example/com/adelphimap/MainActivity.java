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

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.newLatLng(scienceBuilding));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16));


        mMap.addMarker(new MarkerOptions().title("Science Building").position(scienceBuilding));
        mMap.addMarker(new MarkerOptions().title("University Center").position(uniCenter));
        mMap.addMarker(new MarkerOptions().title("Post Hall").position(postHall));


    }


}
