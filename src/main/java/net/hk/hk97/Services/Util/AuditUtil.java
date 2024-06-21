package net.hk.hk97.Services.Util;

import net.hk.hk97.Config;
import net.hk.hk97.Models.ActivityAudit;
import net.hk.hk97.Models.User;
import net.hk.hk97.Models.calc.ConsumptionCity;
import net.hk.hk97.Repositories.UserRepository;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;

@Service
public class AuditUtil {

    @Autowired
    UserRepository userDao;

    public static List<ActivityAudit> getActivityAudit() throws JSONException {

        List<ActivityAudit> list = new ArrayList<>();


        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;

        client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://api.politicsandwar.com/graphql?api_key=" + Config.kastorKey);

        httpPost.addHeader("Content-Type", "application/json");
        JSONObject jsonObj = new JSONObject();
        String queryStr = "{ nations (alliance_id: " + Config.aaId + ", vmode: false) { paginatorInfo{ currentPage hasMorePages } data { last_active id alliance_id alliance_position leader_name nation_name } } }";
        jsonObj.put("query", queryStr);

        String name = "";

        boolean needsToRunAgain = false;
        int nextPage = 0;

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

            DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

            while ((line = reader.readLine()) != null) {

                builder.append(line);

                JSONObject myObject = new JSONObject(builder.toString());
                System.out.println(myObject);

                JSONObject data = myObject.getJSONObject("data");

                JSONObject nations = data.getJSONObject("nations");


                try {

                    JSONObject paginator = nations.getJSONObject("paginatorInfo");

                    if (paginator.optBoolean("hasMorePages")) {
                        needsToRunAgain = true;
                        nextPage = paginator.optInt("currentPage") + 1;

                    }

                    JSONArray array = nations.getJSONArray("data");

                    for (int i = 0; i <= array.length() - 1; i++) {
                        JSONObject object = array.getJSONObject(i);


                        int allianceId = object.optInt("alliance_id");
                        String alliancePosition = object.optString("alliance_position");
                        if (!alliancePosition.equalsIgnoreCase("applicant")) {
                            String activity = object.optString("last_active");
                            int id = object.optInt("id");

                            TemporalAccessor creationAccessor = DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(activity);
                            Instant instant = Instant.from(creationAccessor);
                            Duration duration = Duration.between(instant, Instant.now());

                            if (duration.toHours() >= 48) {

                                ActivityAudit audit = new ActivityAudit();
                                audit.setId(object.optInt("id"));
                                audit.setLastActive(duration.toHours());
                                list.add(audit);
                                System.out.println("id: " + audit.getId());

                            }

                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (needsToRunAgain) {
            List<ActivityAudit> additionalList = getActivityAuditAdditional(nextPage);
            list.addAll(additionalList);
        }

        return list;

    }

    public static List<ConsumptionCity> getConsumptionCities(long nationid) throws JSONException {

        List<ConsumptionCity> list = new ArrayList<>();


        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;

        client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://api.politicsandwar.com/graphql?api_key=" + Config.kastorKey);

        httpPost.addHeader("Content-Type", "application/json");
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("query", "{ nations (id: " + nationid + ") { data { nation_name cities { date, infrastructure, land } wars { winner_id } nation_name } } }");

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

            DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

            while ((line = reader.readLine()) != null) {

                builder.append(line);

                JSONObject myObject = new JSONObject(builder.toString());

                JSONObject data = myObject.getJSONObject("data");

                JSONObject nations = data.getJSONObject("nations");


                try {

                    JSONArray array = nations.getJSONArray("data");

                    for (int i = 0; i <= array.length() - 1; i++) {
                        JSONObject object = array.getJSONObject(i);

                        boolean warStatus = false;

                        JSONArray wars = object.getJSONArray("wars");
                        for (int j = 0; j <= wars.length() - 1; j++) {
                            JSONObject war = wars.getJSONObject(j);
                            if (war.optInt("id") != 0) {
                                warStatus = true;
                            }
                        }
                        JSONArray cities = object.getJSONArray("cities");
                        for (int x = 0; x <= cities.length() - 1; x++) {
                            JSONObject cityObj = cities.getJSONObject(x);
                            ConsumptionCity city = new ConsumptionCity();
                            city.setInfra(cityObj.optInt("infrastructure"));
                            city.setFounded(LocalDate.parse(cityObj.optString("date")));
                            city.setAtWar(warStatus);
                            list.add(city);
                        }


                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public static List<ActivityAudit> getActivityAuditAdditional(int page) throws JSONException {

        List<ActivityAudit> list = new ArrayList<>();


        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;

        client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://api.politicsandwar.com/graphql?api_key=" + Config.kastorKey);

        httpPost.addHeader("Content-Type", "application/json");
        JSONObject jsonObj = new JSONObject();
        String queryStr = "{ nations (alliance_id: " + Config.aaId + ", vmode: false, page: " + page + ") { paginatorInfo{ currentPage hasMorePages } data { last_active id alliance_id alliance_position leader_name nation_name } } }";
        jsonObj.put("query", queryStr);

        String name = "";

        boolean needsToRunAgain = false;

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

            DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

            while ((line = reader.readLine()) != null) {

                builder.append(line);

                JSONObject myObject = new JSONObject(builder.toString());
                System.out.println(myObject);

                JSONObject data = myObject.getJSONObject("data");

                JSONObject nations = data.getJSONObject("nations");


                try {

                    JSONObject paginator = nations.getJSONObject("paginatorInfo");

                    if (paginator.optBoolean("hasMorePages")) {
                        needsToRunAgain = true;
                    }

                    JSONArray array = nations.getJSONArray("data");

                    for (int i = 0; i <= array.length() - 1; i++) {
                        JSONObject object = array.getJSONObject(i);


                        int allianceId = object.optInt("alliance_id");
                        String alliancePosition = object.optString("alliance_position");
                        if (!alliancePosition.equalsIgnoreCase("applicant")) {
                            String activity = object.optString("last_active");
                            int id = object.optInt("id");

                            TemporalAccessor creationAccessor = DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(activity);
                            Instant instant = Instant.from(creationAccessor);
                            Duration duration = Duration.between(instant, Instant.now());

                            if (duration.toHours() >= 48) {

                                ActivityAudit audit = new ActivityAudit();
                                audit.setId(object.optInt("id"));
                                audit.setLastActive(duration.toHours());
                                list.add(audit);
                                System.out.println("id: " + audit.getId());

                            }

                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (needsToRunAgain) {
            List<ActivityAudit> additionalList = getActivityAuditAdditional((page+1));
            list.addAll(additionalList);
        }

        return list;

    }
}
