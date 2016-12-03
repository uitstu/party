package com.uitstu.party.supports;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.uitstu.party.presenter.PartyFirebase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GetDirection extends AsyncTask<String, String,String> {
    private ProgressDialog dialog;      //waiting dialog
    private ArrayList<LatLng> points;   //MARKER points
    private ArrayList<LatLng> pontos;   //points to draw
    private GoogleMap googleMap;        //current google map
    private Context context;            //activity
    private Polyline line;

    LatLng place1;
    LatLng place2;


    public GetDirection(GoogleMap googleMap, Context context, LatLng place1, LatLng place2)
    {

        this.points = new ArrayList<LatLng>();
        this.place1 = place1;
        this.place2 = place2;

        points.add(place1);
        points.add(place2);

        pontos = new ArrayList<>();
        this.googleMap = googleMap;
        this.context = context;
        this.execute();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        /*
        dialog = new ProgressDialog(this.context);
        dialog.setMessage("Waiting...");
        dialog.setIndeterminate(false);
        dialog.setCancelable(false);
        dialog.show();
        */
    }

    protected String doInBackground(String... args) {
        StringBuilder stringUrl = new StringBuilder();
        StringBuilder response = new StringBuilder();
        try {
            Log.d("cuong","points.length= "+points.size());
            for (int i=0; i<points.size()-1; i++) {
                if (PartyFirebase.user != null && PartyFirebase.user.vehicle != null && PartyFirebase.user.vehicle.equals("car")) {
                    stringUrl.append("https://maps.googleapis.com/maps/api/directions/json?")
                            .append("origin=").append(points.get(i).latitude).append(",").append(points.get(i).longitude)
                            .append("&destination=").append(points.get(i + 1).latitude).append(",").append(points.get(i + 1).longitude)
                            .append("&sensor=false&mode=driving");
                }
                else {
                    stringUrl.append("https://maps.googleapis.com/maps/api/directions/json?")
                            .append("origin=").append(points.get(i).latitude).append(",").append(points.get(i).longitude)
                            .append("&destination=").append(points.get(i + 1).latitude).append(",").append(points.get(i + 1).longitude)
                            .append("&sensor=false&mode=biking");
                }


                URL url = new URL(stringUrl.toString());
                HttpURLConnection httpconn = (HttpURLConnection) url
                        .openConnection();
                if (httpconn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader input = new BufferedReader(
                            new InputStreamReader(httpconn.getInputStream()),
                            8192);
                    String strLine = null;

                    while ((strLine = input.readLine()) != null) {
                        response.append(strLine);
                    }
                    input.close();
                }

                String jsonOutput = response.toString();

                JSONObject jsonObject = new JSONObject(jsonOutput);

                // routesArray contains ALL routes
                JSONArray routesArray = jsonObject.getJSONArray("routes");
                // Grab the first route
                JSONObject route = routesArray.getJSONObject(0);

                JSONObject poly = route.getJSONObject("overview_polyline");
                String polyline = poly.getString("points");
                decodePoly(polyline);

                //reset properties
                stringUrl = new StringBuilder();
                response = new StringBuilder();
            }

        } catch (Exception e) {

        }

        return null;

    }

    protected void onPostExecute(String result) {

        for (int i = 0; i < pontos.size() - 1; i++) {

            LatLng src = pontos.get(i);
            LatLng dest = pontos.get(i + 1);
            try{
                //here is where it will draw the polyline in your map
               line =  googleMap.addPolyline(new PolylineOptions()
                        .width(3).color(Color.BLUE)
                        .add(new LatLng(src.latitude, src.longitude),
                                new LatLng(dest.latitude, dest.longitude))
                        .geodesic(true));


            }catch(NullPointerException e){
                Log.e("Error", "NullPointerException onPostExecute: " + e.toString());
            }catch (Exception e2) {
                Log.e("Error", "Exception onPostExecute: " + e2.toString());
            }

        }
        //dialog.dismiss();

    }
    private void decodePoly(String encoded) {

        ArrayList<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        pontos.add(place1);

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            pontos.add(p);
        }
        pontos.add(place2);
    }

    public void removeLine() {
        if(line != null)
            line.remove();
    }
}