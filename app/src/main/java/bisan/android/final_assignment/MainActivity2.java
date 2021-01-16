package bisan.android.final_assignment;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MainActivity2 extends AppCompatActivity {
    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Intent intent = getIntent();
        getRegionId((String)intent.getExtras().get("region_name"));
        TextView pop= findViewById(R.id.edtpop);
        TextView name= findViewById(R.id.edtName);
        TextView lat= findViewById(R.id.edtlat);
        TextView longi = findViewById(R.id.edtlong);
        name.setText((String)intent.getExtras().get("region_name"));
        pop.setText((String)intent.getExtras().get("region_pop"));
        longi.setText((String)intent.getExtras().get("region_long"));
        lat.setText((String)intent.getExtras().get("region_lat"));

        determineLocation((String)intent.getExtras().get("region_lat"),(String)intent.getExtras().get("region_long"));

    }

    public void getRegionId(String name) {

        String url ="https://www.metaweather.com/api/location/search/?query=" + name;


        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest
                (Request.Method.GET, url,
                        null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        String cityID = "";
                        try {//take the first object in ithe returned array
                            JSONObject obj = response.getJSONObject(0);
                            cityID = obj.getString("woeid");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        getRegionWeather(cityID);

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error

                    }
                });
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);

    }
    public void getRegionWeather(String Rid) {
        String url = "https://www.metaweather.com/api/location/" +Rid;


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url,
                        null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        String[] days;
                        try {
                            JSONArray array = response.getJSONArray("consolidated_weather");
                            days = new String[array.length()];
                            for(int i = 0; i<array.length(); i++){
                                JSONObject obj = array.getJSONObject(i);
                                String weatherDay = "";
                                weatherDay = "Day: "+(i+1)+
                                        "\nState: " + obj.getString("weather_state_name") +
                                        "\nDate: " + obj.getString("applicable_date") +
                                        "\nMin: " + obj.getString("min_temp") +
                                        "\nMax: " + obj.getString("max_temp");
                                days[i] = weatherDay;
                            }

                            ListView lst = findViewById(R.id.lst);
                            ArrayAdapter<String> itemsAdapter =
                                    new ArrayAdapter<String>(MainActivity2.this, android.R.layout.simple_list_item_1,
                                            days);
                            lst.setAdapter(itemsAdapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error

                    }
                });
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    public void determineLocation(String lat,String lang){
        TextView loaction= findViewById(R.id.edtLocation);
        Geocoder geocoder = new Geocoder(this);
        try {
            List<Address> address = geocoder.getFromLocation(Double.parseDouble(lat),
                    Double.parseDouble(lang), 1);
            loaction.setText(address.get(0).getAddressLine(0));
        } catch (Exception e) {
            loaction.setText("Cannot get Street Address!");
        }
    }
}
