package example.com.wearmap;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.os.Bundle;
import android.support.wear.widget.SwipeDismissFrameLayout;
import android.support.wearable.activity.WearableActivity;
import android.view.Gravity;
import android.view.View;
import android.view.WindowInsets;
import android.widget.FrameLayout;
import android.widget.Toast;

import example.com.shared.Building;
import example.com.shared.LocationsService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapsActivity extends WearableActivity implements OnMapReadyCallback {

    /**
     * Map is initialized when it's fully loaded and ready to be used.
     *
     * @see #onMapReady(com.google.android.gms.maps.GoogleMap)
     */
    private GoogleMap mMap;

    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        // Enables always on.
        setAmbientEnabled();

        setContentView(R.layout.activity_maps);

        final SwipeDismissFrameLayout swipeDismissRootFrameLayout =
                (SwipeDismissFrameLayout) findViewById(R.id.swipe_dismiss_root_container);
        final FrameLayout mapFrameLayout = (FrameLayout) findViewById(R.id.map_container);

        // Enables the Swipe-To-Dismiss Gesture via the root layout (SwipeDismissFrameLayout).
        // Swipe-To-Dismiss is a standard pattern in Wear for closing an app and needs to be
        // manually enabled for any Google Maps Activity. For more information, review our docs:
        // https://developer.android.com/training/wearables/ui/exit.html
        swipeDismissRootFrameLayout.addCallback(new SwipeDismissFrameLayout.Callback() {
            @Override
            public void onDismissed(SwipeDismissFrameLayout layout) {
                // Hides view before exit to avoid stutter.
                layout.setVisibility(View.GONE);
                finish();
            }
        });

        // Adjusts margins to account for the system window insets when they become available.
        swipeDismissRootFrameLayout.setOnApplyWindowInsetsListener(
                new View.OnApplyWindowInsetsListener() {
                    @Override
                    public WindowInsets onApplyWindowInsets(View view, WindowInsets insets) {
                        insets = swipeDismissRootFrameLayout.onApplyWindowInsets(insets);

                        FrameLayout.LayoutParams params =
                                (FrameLayout.LayoutParams) mapFrameLayout.getLayoutParams();

                        // Sets Wearable insets to FrameLayout container holding map as margins
                        params.setMargins(
                                insets.getSystemWindowInsetLeft(),
                                insets.getSystemWindowInsetTop(),
                                insets.getSystemWindowInsetRight(),
                                insets.getSystemWindowInsetBottom());
                        mapFrameLayout.setLayoutParams(params);

                        return insets;
                    }
                });

        // Obtain the MapFragment and set the async listener to be notified when the map is ready.
        MapFragment mapFragment =
                (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Map is ready to be used.
        mMap = googleMap;

        // Inform user how to close app (Swipe-To-Close).
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(getApplicationContext(), R.string.intro_text, duration);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();

        LatLng scienceBuilding = new LatLng(40.7204395, -73.6519642);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(scienceBuilding));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

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

            }
        });
    }
}
