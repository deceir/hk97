package net.hk.hk97.Services;


import net.hk.hk97.Config;
import net.hk.hk97.Models.Treasure;
import net.hk.hk97.Repositories.TreasureRepository;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;


@Configuration
@EnableScheduling
public class TreasureService {


    @Autowired
    private TreasureRepository treasuresDao;


        @Scheduled(cron = "0 * */6 * * *", zone = "UTC")
//        @Scheduled(cron = "0 */5 * * * *", zone = "UTC")
    public void updateTreasures() throws JSONException {
        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;

        client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://api.politicsandwar.com/graphql?api_key=" + Config.itachiPnwKey);

        httpPost.addHeader("Content-Type", "application/json");
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("query", "{ treasures  { name color continent spawndate } }");


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


                try {

                    JSONArray array = data.getJSONArray("treasures");
                    DateFormat formatter = new SimpleDateFormat("dd-MMM-yy");

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        Treasure treasure = new Treasure();
                        treasure.setName(object.optString("name"));
                        treasure.setColor(object.optString("color"));
                        String cont = object.optString("continent");
                        if (cont.equals("n")) {
                            treasure.setContinent("any");
                        } else if (cont.equals("na")) {
                            treasure.setContinent("north america");
                        } else if (cont.equals("sa")) {
                            treasure.setContinent("south america");
                        } else if (cont.equals("eu")) {
                            treasure.setContinent("europe");
                        } else if (cont.equals("af")) {
                            treasure.setContinent("africa");
                        } else if (cont.equals("as")) {
                            treasure.setContinent("asia");
                        } else if (cont.equals("au")) {
                            treasure.setContinent("australia");
                        }
                        treasure.setSpawn_date(object.optString("spawndate"));
                        treasuresDao.save(treasure);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }



}
