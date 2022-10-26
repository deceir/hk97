package net.hk.hk97.Services.Util;

import net.hk.hk97.Config;
import net.hk.hk97.Models.Stats.NationRevenue;
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
import java.util.*;

public class StatsUtil {

    public static List<NationRevenue> getAllianceNationRevenue(long id) throws JSONException {

        List<NationRevenue> list = new ArrayList<>();


        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;

        client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://api.politicsandwar.com/graphql?api_key=" + Config.itachiPnwKey);

        httpPost.addHeader("Content-Type", "application/json");
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("query", "{ nations (alliance_id: " + id + ", vmode: false, first: 150) { data { nation_name leader_name num_cities gross_national_income } } }");


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
                System.out.println("my object:");
                System.out.println(myObject);

                JSONObject data = myObject.getJSONObject("data");

                JSONObject nations = data.getJSONObject("nations");


                try {

                    JSONArray array = nations.getJSONArray("data");

                    for (int i = 0; i < array.length(); i++) {

                        JSONObject object = array.getJSONObject(i);
                        NationRevenue nationRevenue = new NationRevenue();
                        nationRevenue.setName(object.optString("nation_name"));
                        nationRevenue.setLeader(object.optString("leader_name"));
                        nationRevenue.setCities(object.optInt("cities"));
                        nationRevenue.setRevenue(object.optLong("gross_national_income"));
                        list.add(nationRevenue);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        Collections.sort(list, Comparator.comparingLong(NationRevenue::getRevenue));
        return list;
    }

    public static String getAaName(long id) throws JSONException {
        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;

        client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://api.politicsandwar.com/graphql?api_key=" + Config.itachiPnwKey);

        httpPost.addHeader("Content-Type", "application/json");
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("query", "{ alliances (id: " + id + ") { data { name } } }");

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

                JSONObject nations = data.getJSONObject("alliances");


                try {

                    JSONArray array = nations.getJSONArray("data");

                    JSONObject object = array.getJSONObject(0);
                    name = object.optString("name");


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
