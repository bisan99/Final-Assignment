package bisan.android.final_assignment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;


public class MainActivity1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);
        GetAllRegions();
    }
    public void GetAllRegions() {

        String url = "http://192.168.1.109:80/android/regions.php";
        //check if there is a permission or not . if there is no permission send permission request
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET},
                    123);

        }
        else{
            MainActivity1.DownloadTextTask runner = new MainActivity1.DownloadTextTask();
            runner.execute(url);
        }

    }
    private InputStream OpenHttpConnection(String urlString) throws IOException
    {
        InputStream in = null;
        int response = -1;

        URL url = new URL(urlString);
        URLConnection conn = url.openConnection();

        if (!(conn instanceof HttpURLConnection))
            throw new IOException("Not an HTTP connection");
        try{
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setAllowUserInteraction(false);
            httpConn.setInstanceFollowRedirects(true);
            //GET since the request is of type get
            httpConn.setRequestMethod("GET");
            httpConn.connect();
            response = httpConn.getResponseCode();
            if (response == HttpURLConnection.HTTP_OK) {
                in = httpConn.getInputStream();
            }
        }
        catch (Exception ex)
        {
            Log.d("Networking", ex.getLocalizedMessage());
            throw new IOException("Error connecting");
        }

        return in;
    }
    //This method talk ro web service using android API
    private String DownloadText(String URL)
    {
        int BUFFER_SIZE = 2000;
        InputStream in = null;
        try {
            in = OpenHttpConnection(URL);
        } catch (IOException e) {
            Log.d("Networking", e.getLocalizedMessage());
            return "";
        }
//loop to read the returned data
        InputStreamReader isr = new InputStreamReader(in);
        int charRead;
        String str = "";
        char[] inputBuffer = new char[BUFFER_SIZE];
        try {
            while ((charRead = isr.read(inputBuffer))>0) {
                //---convert the chars to a String---
                String readString =
                        String.copyValueOf(inputBuffer, 0, charRead);
                str += readString;
                inputBuffer = new char[BUFFER_SIZE];
            }
            in.close();
        } catch (IOException e) {
            Log.d("Networking", e.getLocalizedMessage());
            return "";
        }

        return str;
    }


    //inner class for threads take 3 parameters(data type,progress bar or not, returned data type)
    private class DownloadTextTask extends AsyncTask<String, Void, String> {
        @Override
        //this method take the first parameter and we put the method which take a longtime inside it.
        protected String doInBackground(String... urls) {

            return DownloadText(urls[0]);

        }
        @Override
        protected void onPostExecute(String result) {
            String[] values = result.split(";");
            String []name=new String[values.length];
            String []longit=new String[values.length];
            String []lat=new String[values.length];
            String []pop=new String[values.length];
            int []image_id=new int[values.length];
            for(int i=0;i<values.length;i++){
                String [] newData=values[i].split(",");
                for(int j=0;j<=0;j++){
                    name[i]=newData[j+1];
                    int resourceId = MainActivity1.this.getResources().getIdentifier(newData[j+2], "drawable", MainActivity1.this.getPackageName());
                    image_id[i]=resourceId;
                    longit[i]=newData[j+3];
                    lat[i]=newData[j+4];
                    pop[i]=newData[j+5];
                }


            }
            RecyclerView recycler = (RecyclerView)findViewById(R.id.regions_recycler);
            recycler.setLayoutManager(new LinearLayoutManager(MainActivity1.this));
            CaptionedImageAdapter adapter = new CaptionedImageAdapter(name, image_id,longit,lat,pop);
            recycler.setAdapter(adapter);

        }
    }
}