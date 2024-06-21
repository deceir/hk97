package net.hk.hk97.Services.Util.Mutations;


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
import java.util.Arrays;
public class WithdrawalMutationService {



    public static JSONObject bankWithdrawal(long receiver, long money, long food, long uranium, long coal, long oil, long iron, long bauxite, long lead, long gasoline, long munitions, long steel, long aluminum, String note) throws JSONException {


        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;

        JSONObject myObject = null;

        client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://api.politicsandwar.com/graphql?api_key=" + Config.itachiPnwKey);

        httpPost.addHeader("Content-Type", "application/json");
        httpPost.addHeader("X-Bot-Key", Config.xboxKeySniper);
        httpPost.addHeader("X-Api-Key", Config.sniperKey);
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("query", "mutation { bankWithdraw (receiver:" + receiver + ", receiver_type: 1, money:" + money + ", food:" + food + ", uranium:" + uranium + ", coal:" + coal + ",oil: " + oil + ", iron:" + iron +  ", bauxite:" + bauxite +", lead:" + lead + ", gasoline:" + gasoline + ", munitions:" + munitions + ", steel:" + steel + ", aluminum:" + aluminum + ", note:\"" + note + "\") { money, note } }");
        System.out.println(jsonObj);



        try {
            StringEntity entity = new StringEntity(jsonObj.toString());

            httpPost.setEntity(entity);
            response = client.execute(httpPost);

            System.out.println(response);

        } catch (
                IOException e) {
            e.printStackTrace();
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line = null;
            StringBuilder builder = new StringBuilder();
            while ((line = reader.readLine()) != null) {

                builder.append(line);

                myObject = new JSONObject(builder.toString());

                System.out.println(myObject);



            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return myObject;

    }

    public static JSONObject bankWithdrawal(long receiver, long money, double food, double uranium, double coal, double oil, double iron, double bauxite, double lead, double gasoline, double munitions, double steel, double aluminum, String note) throws JSONException {


        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;

        JSONObject myObject = null;

        client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://api.politicsandwar.com/graphql?api_key=" + Config.itachiPnwKey);

        httpPost.addHeader("Content-Type", "application/json");
        httpPost.addHeader("X-Bot-Key", Config.xboxKeySniper);
        httpPost.addHeader("X-Api-Key", Config.sniperKey);
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("query", "mutation { bankWithdraw (receiver:" + receiver + ", receiver_type: 1, money:" + money + ", food:" + food + ", uranium:" + uranium + ", coal:" + coal + ",oil: " + oil + ", iron:" + iron +  ", bauxite:" + bauxite +", lead:" + lead + ", gasoline:" + gasoline + ", munitions:" + munitions + ", steel:" + steel + ", aluminum:" + aluminum + ", note:\"" + note + "\") { money, note } }");
        System.out.println(jsonObj);



        try {
            StringEntity entity = new StringEntity(jsonObj.toString());

            httpPost.setEntity(entity);
            response = client.execute(httpPost);

            System.out.println(response);

        } catch (
                IOException e) {
            e.printStackTrace();
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line = null;
            StringBuilder builder = new StringBuilder();
            while ((line = reader.readLine()) != null) {

                builder.append(line);

                myObject = new JSONObject(builder.toString());

                System.out.println(myObject);



            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return myObject;

    }



}
