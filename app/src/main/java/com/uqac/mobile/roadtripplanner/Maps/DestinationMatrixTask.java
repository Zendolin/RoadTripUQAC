package com.uqac.mobile.roadtripplanner.Maps;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DestinationMatrixTask extends AsyncTask<String, Void, String> {

    private static final String TAG = "DestinationMatrixTask";
    public static final String SEPARATOR = "&&";

    private ProgressDialog pd;
    Context mContext;
    private Double duration;
    private DestinationMatrixCallback dmc;

    //constructor is used to get the context.
    public DestinationMatrixTask(Context mContext) {
        this.mContext = mContext;
        dmc = (DestinationMatrixCallback) mContext;
    }

    //This function is executed before before "doInBackground(String...params)" is executed to dispaly the progress dialog
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pd = new ProgressDialog(mContext);
        pd.setMessage("Loading");
        pd.setCancelable(false);
        pd.show();
    }

    @Override
    protected void onPostExecute(String result) {

        Log.d(TAG, "onPostExecute: " + result);
        super.onPostExecute(result);
        if(result != null)
        {
            dmc.onTaskDone(result);
            pd.dismiss();
        }
        else
            Toast.makeText(mContext, "Error! Please Try Again wiht proper values", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            URL url = new URL(params[0]);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.connect();
            int statuscode = con.getResponseCode();
            if(statuscode == HttpURLConnection.HTTP_OK)
            {
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();
                while(line != null)
                {
                    sb.append(line);
                    line = br.readLine();
                }
                String json = sb.toString();
                Log.d("JSON", json);
                JSONObject root = new JSONObject(json);
                // addresses
                JSONArray array_destination_addresses = root.getJSONArray("destination_addresses");
                Log.d("JSON","array_destination_addresses:" + array_destination_addresses);
                String object_destination_addresses = array_destination_addresses.getString(0);
                Log.d("JSON","object_destination_addresses:" + object_destination_addresses);
                // origines
                JSONArray array_origin_addresses = root.getJSONArray("origin_addresses");
                Log.d("JSON","array_origin_addresses:" + array_origin_addresses);
                String object_origin_addresses = array_origin_addresses.getString(0);
                Log.d("JSON","object_origin_addresses:" + object_origin_addresses);
                // rows
                JSONArray array_rows = root.getJSONArray("rows");
                Log.d("JSON","array_rows:" + array_rows);
                JSONObject object_rows = array_rows.getJSONObject(0);
                Log.d("JSON","object_rows:" + object_rows);
                JSONArray array_elements = object_rows.getJSONArray("elements");
                Log.d("JSON","array_elements:" + array_elements);
                JSONObject  object_elements = array_elements.getJSONObject(0);
                Log.d("JSON","object_elements:" + object_elements);
                JSONObject object_duration = object_elements.getJSONObject("duration");
                JSONObject object_distance = object_elements.getJSONObject("distance");

                Log.d("JSON","object_duration:" + object_duration);
                return object_duration.getString("value")
                        + SEPARATOR + object_distance.getString("value")
                        + SEPARATOR + object_origin_addresses
                        + SEPARATOR + object_destination_addresses;

            }
        } catch (MalformedURLException e) {
            Log.d("error", "error1");
        } catch (IOException e) {
            Log.d("error", "error2");
        } catch (JSONException e) {
            Log.d("error","error3");
        }

        return null;
    }
}