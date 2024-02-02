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

public class BankUtil {

    public static Bank getTransactions(long id, String code) throws JSONException {
        Bank bank = new Bank();

        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;

        client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://api.politicsandwar.com/graphql?api_key=" + Config.itachiPnwKey);

        httpPost.addHeader("Content-Type", "application/json");
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("query", "{ nations (id: " + id + ") { data { bankrecs { sender_id note money coal oil uranium iron bauxite lead gasoline munitions steel aluminum food  } } } }");

        long money = 0;
        long coal = 0;
        long oil = 0;
        long uranium = 0;
        long iron = 0;
        long bauxite = 0;
        long lead = 0;
        long gasoline = 0;
        long munitions = 0;
        long steel = 0;
        long aluminum = 0;
        long food = 0;


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
                    JSONArray newArray = object.getJSONArray("bankrecs");

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject bankreqs = newArray.getJSONObject(i);
                        String note = bankreqs.optString("note");
                        long sender = bankreqs.optLong("sender_id");
                        if (note.trim().equals(code) && (sender == id)) {
                            money += bankreqs.optDouble("money");
                            coal += bankreqs.optDouble("coal");
                            oil += bankreqs.optDouble("oil");
                            uranium += bankreqs.optDouble("uranium");
                            iron += bankreqs.optDouble("iron");
                            bauxite += bankreqs.optDouble("bauxite");
                            lead += bankreqs.optDouble("lead");
                            gasoline += bankreqs.optDouble("gasoline");
                            munitions += bankreqs.optDouble("munitions");
                            steel += bankreqs.optDouble("steel");
                            aluminum += bankreqs.optDouble("aluminum");
                            food += bankreqs.optDouble("food");
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        bank.setCash(money);
        bank.setCoal(coal);
        bank.setFood(food);
        bank.setIron(iron);
        bank.setOil(oil);
        bank.setLeadRss(lead);
        bank.setBauxite(bauxite);
        bank.setUranium(uranium);
        bank.setGasoline(gasoline);
        bank.setMunitions(munitions);
        bank.setSteel(steel);
        bank.setAluminum(aluminum);

        return bank;
    }

    public static Bank getReceivedTransactions(long id, String code) throws JSONException {
        Bank bank = new Bank();

        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;

        client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://api.politicsandwar.com/graphql?api_key=" + Config.itachiPnwKey);

        httpPost.addHeader("Content-Type", "application/json");
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("query", "{ nations (id: " + id + ") { data { bankrecs { receiver_id note money coal oil uranium iron bauxite lead gasoline munitions steel aluminum food  } } } }");

        long money = 0;
        long coal = 0;
        long oil = 0;
        long uranium = 0;
        long iron = 0;
        long bauxite = 0;
        long lead = 0;
        long gasoline = 0;
        long munitions = 0;
        long steel = 0;
        long aluminum = 0;
        long food = 0;


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
                    JSONArray newArray = object.getJSONArray("bankrecs");

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject bankreqs = newArray.getJSONObject(i);
                        String note = bankreqs.optString("note");
                        long receiver = bankreqs.optLong("receiver_id");
                        if (note.equals(code) && (receiver == id)) {
                            money += bankreqs.optDouble("money");
                            coal += bankreqs.optDouble("coal");
                            oil += bankreqs.optDouble("oil");
                            uranium += bankreqs.optDouble("uranium");
                            iron += bankreqs.optDouble("iron");
                            bauxite += bankreqs.optDouble("bauxite");
                            lead += bankreqs.optDouble("lead");
                            gasoline += bankreqs.optDouble("gasoline");
                            munitions += bankreqs.optDouble("munitions");
                            steel += bankreqs.optDouble("steel");
                            aluminum += bankreqs.optDouble("aluminum");
                            food += bankreqs.optDouble("food");
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        bank.setCash(money);
        bank.setCoal(coal);
        bank.setFood(food);
        bank.setIron(iron);
        bank.setOil(oil);
        bank.setLeadRss(lead);
        bank.setBauxite(bauxite);
        bank.setUranium(uranium);
        bank.setGasoline(gasoline);
        bank.setMunitions(munitions);
        bank.setSteel(steel);
        bank.setAluminum(aluminum);

        return bank;
    }

    public static Bank getBankBalance(long id) throws JSONException {
        Bank bank = new Bank();

        String keyToUse = "";

        keyToUse = Config.itachiPnwKey;

        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;

        client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://api.politicsandwar.com/graphql?api_key=" + keyToUse);

        httpPost.addHeader("Content-Type", "application/json");
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("query", "{ alliances (id: " + id + ") { data { name money coal oil uranium iron bauxite lead gasoline munitions steel aluminum food } } }");

        long money = 0;
        long coal = 0;
        long oil = 0;
        long uranium = 0;
        long iron = 0;
        long bauxite = 0;
        long lead = 0;
        long gasoline = 0;
        long munitions = 0;
        long steel = 0;
        long aluminum = 0;
        long food = 0;


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





                    JSONArray newArray = nations.getJSONArray("data");

                    for (int i = 0; i < 1; i++) {
                        JSONObject bankreqs = newArray.getJSONObject(i);


                            money += bankreqs.optDouble("money");
                            coal += bankreqs.optDouble("coal");
                            oil += bankreqs.optDouble("oil");
                            uranium += bankreqs.optDouble("uranium");
                            iron += bankreqs.optDouble("iron");
                            bauxite += bankreqs.optDouble("bauxite");
                            lead += bankreqs.optDouble("lead");
                            gasoline += bankreqs.optDouble("gasoline");
                            munitions += bankreqs.optDouble("munitions");
                            steel += bankreqs.optDouble("steel");
                            aluminum += bankreqs.optDouble("aluminum");
                            food += bankreqs.optDouble("food");


                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

        } catch (Exception e) {
            e.printStackTrace();
        }



        bank.setCash(money);
        bank.setCoal(coal);
        bank.setFood(food);
        bank.setIron(iron);
        bank.setOil(oil);
        bank.setLeadRss(lead);
        bank.setBauxite(bauxite);
        bank.setUranium(uranium);
        bank.setGasoline(gasoline);
        bank.setMunitions(munitions);
        bank.setSteel(steel);
        bank.setAluminum(aluminum);

        return bank;
    }

    public static Bank getBankBalanceAlternate(long id) throws JSONException {
        Bank bank = new Bank();

        String keyToUse = Config.adamApiKey;

        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;

        client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://api.politicsandwar.com/graphql?api_key=" + keyToUse);

        httpPost.addHeader("Content-Type", "application/json");
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("query", "{ alliances (id: " + id + ") { data { name money coal oil uranium iron bauxite lead gasoline munitions steel aluminum food } } }");

        long money = 0;
        long coal = 0;
        long oil = 0;
        long uranium = 0;
        long iron = 0;
        long bauxite = 0;
        long lead = 0;
        long gasoline = 0;
        long munitions = 0;
        long steel = 0;
        long aluminum = 0;
        long food = 0;


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





                    JSONArray newArray = nations.getJSONArray("data");

                    for (int i = 0; i < 1; i++) {
                        JSONObject bankreqs = newArray.getJSONObject(i);


                        money += bankreqs.optDouble("money");
                        coal += bankreqs.optDouble("coal");
                        oil += bankreqs.optDouble("oil");
                        uranium += bankreqs.optDouble("uranium");
                        iron += bankreqs.optDouble("iron");
                        bauxite += bankreqs.optDouble("bauxite");
                        lead += bankreqs.optDouble("lead");
                        gasoline += bankreqs.optDouble("gasoline");
                        munitions += bankreqs.optDouble("munitions");
                        steel += bankreqs.optDouble("steel");
                        aluminum += bankreqs.optDouble("aluminum");
                        food += bankreqs.optDouble("food");


                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

        } catch (Exception e) {
            e.printStackTrace();
        }



        bank.setCash(money);
        bank.setCoal(coal);
        bank.setFood(food);
        bank.setIron(iron);
        bank.setOil(oil);
        bank.setLeadRss(lead);
        bank.setBauxite(bauxite);
        bank.setUranium(uranium);
        bank.setGasoline(gasoline);
        bank.setMunitions(munitions);
        bank.setSteel(steel);
        bank.setAluminum(aluminum);

        return bank;
    }
}
