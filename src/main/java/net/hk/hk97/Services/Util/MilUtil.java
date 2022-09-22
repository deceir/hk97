package net.hk.hk97.Services.Util;

import net.hk.hk97.Config;
import net.hk.hk97.Models.Military;
import net.hk.hk97.Models.War;
import net.hk.hk97.Models.WarInfo;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MilUtil {


    public static Military getNationMilitary(int id) throws JSONException {
        Military mil = new Military();
        mil.setId(id);


        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;

        client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://api.politicsandwar.com/graphql?api_key=" + Config.itachiPnwKey);

        httpPost.addHeader("Content-Type", "application/json");
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("query", "{ nations (id: " + id + ") { data { nation_name leader_name score last_active soldiers tanks aircraft ships missiles nukes cities } } }");


        try {
            StringEntity entity = new StringEntity(jsonObj.toString());

            httpPost.setEntity(entity);
            response = client.execute(httpPost);

            System.out.println(response);

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line = null;
            StringBuilder builder = new StringBuilder();
            while ((line = reader.readLine()) != null) {

                builder.append(line);

                JSONObject myObject = new JSONObject(builder.toString());

                JSONObject data = myObject.getJSONObject("data");

                JSONObject nations = data.getJSONObject("nations");


                try {

                    JSONArray array = nations.getJSONArray("data");

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(0);
                        mil.setLeader_name(object.optString("leader_name"));
                        mil.setScore(object.optDouble("score"));
                        mil.setLast_active(object.optString("last_active"));
                        mil.setSoldiers(object.optInt("soldiers"));
                        mil.setTanks(object.optInt("tanks"));
                        mil.setJets(object.optInt("aircraft"));
                        mil.setShips(object.optInt("ships"));
                        mil.setMissiles(object.optInt("missiles"));
                        mil.setNukes(object.optInt("nukes"));
                        mil.setCities(object.optInt("cities"));

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        return mil;
    }

    public static String getNationName(long id) throws JSONException {
        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;

        client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://api.politicsandwar.com/graphql?api_key=" + Config.itachiPnwKey);

        httpPost.addHeader("Content-Type", "application/json");
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("query", "{ nations (id: " + id + ") { data { nation_name } } }");

        String name = "";


        try {
            StringEntity entity = new StringEntity(jsonObj.toString());

            httpPost.setEntity(entity);
            response = client.execute(httpPost);

            System.out.println(response);

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line = null;
            StringBuilder builder = new StringBuilder();
            while ((line = reader.readLine()) != null) {

                builder.append(line);

                JSONObject myObject = new JSONObject(builder.toString());

                JSONObject data = myObject.getJSONObject("data");

                JSONObject nations = data.getJSONObject("nations");


                try {

                    JSONArray array = nations.getJSONArray("data");

                    JSONObject object = array.getJSONObject(0);
                    name = object.optString("nation_name");


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        return name;
    }


    public static String getLeaderName(long id) throws JSONException {
        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;

        client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://api.politicsandwar.com/graphql?api_key=" + Config.itachiPnwKey);

        httpPost.addHeader("Content-Type", "application/json");
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("query", "{ nations (id: " + id + ") { data { leader_name } } }");

        String name = "";


        try {
            StringEntity entity = new StringEntity(jsonObj.toString());

            httpPost.setEntity(entity);
            response = client.execute(httpPost);

            System.out.println(response);

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line = null;
            StringBuilder builder = new StringBuilder();
            while ((line = reader.readLine()) != null) {

                builder.append(line);

                JSONObject myObject = new JSONObject(builder.toString());

                JSONObject data = myObject.getJSONObject("data");

                JSONObject nations = data.getJSONObject("nations");


                try {

                    JSONArray array = nations.getJSONArray("data");

                    JSONObject object = array.getJSONObject(0);
                    name = object.optString("leader_name");


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        return name;
    }



}


