package net.hk.hk97.Services.Util;

import net.hk.hk97.Config;
import net.hk.hk97.Models.Bank.Bank;
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
import java.util.HashMap;
import java.util.List;

public class WcUtil {

    public static List<Bank> getMemberBanksInMap() throws JSONException {
        List<Bank> map = new ArrayList<>();

        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;

        client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://api.politicsandwar.com/graphql?api_key=" + Config.adamApiKey);

        httpPost.addHeader("Content-Type", "application/json");
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("query", "{ nations (alliance_id: " + Config.aaId + ") { data { id leader_name nation_name num_cities steel munitions aluminum gasoline money } } }");


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

                    for (int i = 0; i <= array.length() -1 ; i++) {

                        try {

                        JSONObject bankreqs = array.getJSONObject(i);

                            Bank bank = new Bank();

                            bank.setCash((long) bankreqs.optDouble("money"));

                            bank.setGasoline((long) bankreqs.optDouble("gasoline"));
                            bank.setMunitions((long) bankreqs.optDouble("munitions"));
                            bank.setSteel((long) bankreqs.optDouble("steel"));
                            bank.setAluminum((long) bankreqs.optDouble("aluminum"));
                            bank.setCities(bankreqs.optLong("num_cities"));
                            bank.setNationid(bankreqs.optLong("id"));
                            bank.setName(bankreqs.optString("leader_name") + " of " + bankreqs.optString("nation_name"));
                            System.out.println("city count is " + bank.getCities());
                            map.add(bank);
                        } catch (Exception er) {
                            er.printStackTrace();
                        }


                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    System.out.println("it failed");
                }


            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }
}
