package net.hk.hk97.Models.calc;


import net.hk.hk97.Models.calc.graphql.repositories.ResourceRepository;
import lombok.Getter;
import lombok.Setter;
import net.hk.hk97.Config;
import net.hk.hk97.Models.calc.graphql.repositories.ResourceRepository;
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

public class AppraiseCalc {


    @Getter
    @Setter
    public long totalvalue;

    @Getter
    @Setter
    public long infravalue;

    @Getter
    @Setter
    public long landvalue;

    @Getter
    @Setter
    public long citiesvalue;

    @Getter
    @Setter
    public long projectsvalue = 0;

    @Getter
    @Setter
    public int cities;

    public void generateCityValue(long id) throws JSONException {

        this.cities = 0;

        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;

        client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://api.politicsandwar.com/graphql?api_key=" + Config.itachiPnwKey);

        httpPost.addHeader("Content-Type", "application/json");
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("query", "{ nations (first: 1, id: " + id + ") { data { num_cities } } }");


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
                    this.cities = object.optInt("num_cities");


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        CityCalc cityCalc = new CityCalc();
        cityCalc.calculateCity(1, cities);
        this.citiesvalue = cityCalc.getBase_cost();

    }

    public void generateInfraValue(long id) throws JSONException, IOException {

        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;

        client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://api.politicsandwar.com/graphql?api_key=" + Config.itachiPnwKey);

        httpPost.addHeader("Content-Type", "application/json");
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("query", "{ nations (first: 1, id: " + id + ") { data { cities { infrastructure land } } } }");

        InfraCalc infraCalc = new InfraCalc();
        LandCalc landCalc = new LandCalc();

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

                        JSONArray imprArray = object.getJSONArray("cities");

                            JSONObject imprObj = imprArray.getJSONObject(0);
                            long infra = imprObj.optLong("infrastructure");
                            long land = imprObj.optLong("land");
                            infraCalc.calculateInfra(0, (int) infra, this.cities);
                            this.infravalue += infraCalc.getBase_cost();
                            landCalc.calculateLand(250, (int) land, this.cities);
                            this.landvalue = landCalc.getBase_cost();






                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void generateProjectValue(long id, ResourceRepository resourceDao) throws JSONException {


        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;

        client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://api.politicsandwar.com/graphql?api_key=" + Config.itachiPnwKey);

        httpPost.addHeader("Content-Type", "application/json");
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("query", "{ nations (first: 1, id: " + id + ") { data { ironw " +
                "bauxitew " +
                "armss " +
                "egr " +
                "massirr " +
                "itc " +
                "mlp " +
                "nrf " +
                "irond " +
                "vds " +
                "cia " +
                "cfce " +
                "propb " +
                "uap " +
                "city_planning " +
                "adv_city_planning " +
                "space_program " +
                "spy_satellite " +
                "moon_landing " +
                "pirate_economy " +
                "recycling_initiative " +
                "telecom_satellite " +
                "green_tech " +
                "arable_land_agency " +
                "clinical_research_center " +
                "specialized_police_training " +
                "adv_engineering_corps } } }");

        long gas = resourceDao.findResourcesByName("GAS").getPrice();
        long alu = resourceDao.findResourcesByName("ALU").getPrice();
        long coal = resourceDao.findResourcesByName("COAL").getPrice();
        long food = resourceDao.findResourcesByName("FOOD").getPrice();
        long iron = resourceDao.findResourcesByName("IRON").getPrice();
        long lead = resourceDao.findResourcesByName("LEAD").getPrice();
        long munis = resourceDao.findResourcesByName("MUNIS").getPrice();
        long oil = resourceDao.findResourcesByName("OIL").getPrice();
        long steel = resourceDao.findResourcesByName("STEEL").getPrice();
        long ura = resourceDao.findResourcesByName("URA").getPrice();
        long baux = resourceDao.findResourcesByName("BAUX").getPrice();


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

                boolean aup = false;
                boolean aec = false;
                boolean ala = false;
                boolean armss = false;
                boolean bauxw = false;
                boolean cce = false;
                boolean crc = false;
                boolean egr = false;
                boolean greentech = false;
                boolean ia = false;
                boolean itc = false;
                boolean irond = false;
                boolean iw = false;
                boolean massir = false;
                boolean mlp = false;
                boolean moonland = false;
                boolean nrf = false;
                boolean pirate = false;
                boolean propaganda = false;
                boolean recycle = false;
                boolean spaceprog = false;
                boolean specializedpolice = false;
                boolean spysat = false;
                boolean telecomm = false;
                boolean uep = false;
                boolean up = false;
                boolean vds = false;

                try {

                    JSONArray array = nations.getJSONArray("data");


                    JSONObject object = array.getJSONObject(0);
                    aup = object.optBoolean("adv_city_planning");
                    aec = object.optBoolean("adv_engineering_corps");
                    ala = object.optBoolean("arable_land_agency");
                    armss = object.optBoolean("armss");
                    bauxw = object.optBoolean("bauxitew");
                    cce = object.optBoolean("cfce");
                    crc = object.optBoolean("clinical_research_center");
                    egr = object.optBoolean("egr");
                    greentech = object.optBoolean("green_tech");
                    ia = object.optBoolean("cia");
                    itc = object.optBoolean("itc");
                    irond = object.optBoolean("irond");
                    iw = object.optBoolean("ironw");
                    massir = object.optBoolean("massirr");
                    mlp = object.optBoolean("mlp");
                    moonland = object.optBoolean("moon_landing");
                    nrf = object.optBoolean("nrf");
                    pirate = object.optBoolean("pirate_economy");
                    propaganda = object.optBoolean("propb");
                    recycle = object.optBoolean("recycling_initiative");
                    spaceprog = object.optBoolean("space_program");
                    specializedpolice = object.optBoolean("specialized_police_training");
                    spysat = object.optBoolean("spy_satellite");
                    telecomm = object.optBoolean("telecom_satellite");
                    uep = object.optBoolean("uap");
                    up = object.optBoolean("city_planning");
                    vds = object.optBoolean("vds");


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                long cost = 0;
                if (aup) {
                    cost = ((ura * 10000) + (alu * 40000) + (steel * 20000) + (food * 2500000));
                    this.projectsvalue += cost;
                }
                if (aec) {
                    cost = ((ura * 1000) + (munis * 10000) + (gas * 10000) + 50000000);
                    this.projectsvalue += cost;
                }
                if (ala) {
                    cost = ((coal * 1500) + (lead * 1500) + 3000000);
                    this.projectsvalue += cost;
                }
                if (armss) {
                    cost = ((alu * 125) + (steel * 125) + 4000000);
                    this.projectsvalue += cost;
                }
                if (bauxw) {
                    cost = ((baux * 750) + (gas * 1500) + 5000000);
                    this.projectsvalue += cost;
                }
                if (cce) {
                    cost = ((oil * 1000) + (iron * 1000) + (baux * 1000) + 3000000);
                    this.projectsvalue += cost;
                }
                if (crc) {
                    cost = ((food * 100000) + 10000000);
                    this.projectsvalue += cost;
                }
                if (egr) {
                    cost = ((alu * 125) + (steel * 125) + 4000000);
                    this.projectsvalue += cost;
                }
                if (greentech) {
                    cost = ((iron * 10000) + (steel * 10000) + (alu * 10000) + (food * 250000) + 100000000);
                    this.projectsvalue += cost;
                }
                if (ia) {
                    cost = ((steel * 500) + (gas * 500) + 5000000);
                    this.projectsvalue += cost;
                }
                if (itc) {
                    cost = ((alu * 2500) + (steel * 2500) + (gas * 5000) + 45000000);
                    this.projectsvalue += cost;
                }
                if (irond) {
                    cost = ((alu * 500) + (steel * 1250) + (gas * 500) + 6000000);
                    this.projectsvalue += cost;
                }
                if (iw) {
                    cost = ((alu * 750) + (gas * 1500) + 5000000);
                    this.projectsvalue += cost;
                }
                if (massir) {
                    cost = ((alu * 500) + (steel * 500) + 3000000);
                    this.projectsvalue += cost;
                }
                if (mlp) {
                    cost = ((steel * 1000) + (gas * 350) + 8000000);
                    this.projectsvalue += cost;
                }
                if (moonland) {
                    cost = ((oil * 5000) + (munis * 5000) + (gas * 5000) + (steel * 5000) + (alu * 5000) + (ura * 10000) + 50000);
                    this.projectsvalue += cost;
                }
                if (nrf) {
                    cost = ((steel * 5000) + (gas * 7500) + 50000000);
                    this.projectsvalue += cost;
                }
                if (pirate) {
                    cost = ((alu * 10000) + (munis * 10000) + (gas * 10000) + (steel * 10000) + 25000000);
                    this.projectsvalue += cost;
                }
                if (propaganda) {
                    cost = ((alu * 1500) + 15000000);
                    this.projectsvalue += cost;
                }
                if (recycle) {
                    cost = ((food * 100000) + 10000000);
                    this.projectsvalue += cost;
                }
                if (spaceprog) {
                    cost = ((ura * 20000) + (oil * 20000) + (iron * 10000) + (gas * 5000) + (steel * 1000) + (alu * 1000) + 40000000);
                    this.projectsvalue += cost;
                }
                if (specializedpolice) {
                    cost = ((food * 100000) + 10000000);
                    this.projectsvalue += cost;
                }
                if (spysat) {
                    cost = ((oil * 10000) + (iron * 10000) + (lead * 10000) + (baux * 10000) + (ura * 10000) + 20000000);
                    this.projectsvalue += cost;
                }
                if (telecomm) {
                    cost = ((ura * 10000) + (iron * 10000) + (oil * 10000) + (alu * 10000) + 300000000);
                    this.projectsvalue += cost;
                }
                if (uep) {
                    cost = ((alu * 100) + (gas * 1000) + (ura * 500) + 21000000);
                    this.projectsvalue += cost;
                }
                if (up) {
                    cost = ((coal * 10000) + (oil * 10000) + (alu * 20000) + (munis * 10000) + (gas * 10000) + (food * 1000000));
                    this.projectsvalue += cost;
                }
                if (vds) {
                    cost = ((alu * 3000) + (steel * 6500) + (gas * 5000) + 40000000);
                    this.projectsvalue += cost;
                }


            }

        } catch (Exception e) {
            e.printStackTrace();
        }



    }

    public void generateAllValues(long id, ResourceRepository resourceDao) throws JSONException, IOException {
        this.generateCityValue(id);
        this.generateInfraValue(id);
        this.generateProjectValue(id, resourceDao);
        this.totalvalue = this.citiesvalue + this.infravalue + this.projectsvalue + this.landvalue;
    }
}

