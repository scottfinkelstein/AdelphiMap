package example.com.adelphimap;

import retrofit2.Call;
import retrofit2.http.GET;

public interface LocationsService {
    @GET("location.json")
    Call<Building[]> getAllBuildings();
}
