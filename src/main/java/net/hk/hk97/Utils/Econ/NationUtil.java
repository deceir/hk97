package net.hk.hk97.Utils.Econ;

import net.hk.hk97.Config;
import net.hk.hk97.Models.ActivityAudit;
import net.hk.hk97.Models.CityBuild.Revenue.CityRevenueBuilds.CityRevenueModel;
import net.hk.hk97.Models.Nation;
import net.hk.hk97.Models.NationWarchestAudited;
import net.hk.hk97.Models.Stats.NationAudited;
import net.hk.hk97.Models.calc.ConsumptionCity;
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
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;

public class NationUtil {


    public static List<NationAudited> getNationsAudited() throws JSONException {
        List<NationAudited> list = new ArrayList<>();


        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;

        client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://api.politicsandwar.com/graphql?api_key=" + Config.sniperKey);

        httpPost.addHeader("Content-Type", "application/json");
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("query", "{ nations (alliance_id: " + Config.aaId + ", tax_id:" + Config.growthProgramTaxId + ") { data { nation_name, leader_name, discord, id, food, uranium, iron, oil, bauxite, lead, coal, soldiers, iron_works, emergency_gasoline_reserve, arms_stockpile, bauxite_works, cities { date, infrastructure, oil_refinery, aluminum_refinery, munitions_factory, steel_mill, nuclear_power } wars { winner_id } nation_name } } }");

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

                        NationAudited nationAudited = new NationAudited();
                        List<ConsumptionCity> citiesList = new ArrayList<>();

                        boolean warStatus = false;

                        nationAudited.setId(object.optLong("id"));
                        nationAudited.setName(object.optString("nation_name"));
                        nationAudited.setLeader(object.optString("leader_name"));
                        nationAudited.setDiscord(object.optString("discord"));
                        nationAudited.setFood(object.optLong("food"));
                        nationAudited.setUranium(object.optLong("uranium"));
                        nationAudited.setIron(object.optLong("iron"));
                        nationAudited.setBauxite(object.optLong("bauxite"));
                        nationAudited.setOil(object.optLong("oil"));
                        nationAudited.setLead(object.optLong("lead"));
                        nationAudited.setCoal(object.optLong("coal"));
                        nationAudited.setSoldiers(object.optLong("soldiers"));

                        nationAudited.setIronworks(object.optBoolean("iron_works"));
                        nationAudited.setGasolineReserve(object.optBoolean("emergency_gasoline_reserve"));
                        nationAudited.setArmsStockpile(object.optBoolean("arms_stockpile"));
                        nationAudited.setBauxiteWorks(object.optBoolean("bauxite_works"));



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
                            citiesList.add(city);

                            nationAudited.setNuclearPowerPlants(nationAudited.getNuclearPowerPlants() + cityObj.optInt("nuclear_power"));
                            nationAudited.setOilRefineries(nationAudited.getOilRefineries() + cityObj.optInt("oil_refinery"));
                            nationAudited.setSteelMills(nationAudited.getSteelMills() + cityObj.optInt("steel_mill"));
                            nationAudited.setAluminumRefineries(nationAudited.getAluminumRefineries() + cityObj.optInt("aluminum_refinery"));
                            nationAudited.setMunitionsFactories(nationAudited.getMunitionsFactories() + cityObj.optInt("munitions_factory"));

                            nationAudited.setInfra(city.getInfra());


                        }

                        nationAudited.setFoodConsumption(Math.round(FoodConsumption.getFoodConsumption(citiesList, nationAudited.getSoldiers())));

                        list.add(nationAudited);


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

    public static List<NationAudited> getNationsAuditedOffshore() throws JSONException {
        List<NationAudited> list = new ArrayList<>();


        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;

        client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://api.politicsandwar.com/graphql?api_key=" + Config.itachiPnwKey);

        httpPost.addHeader("Content-Type", "application/json");
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("query", "{ nations (alliance_id: " + Config.offshoreAaId + ", tax_id:" + 23104 + ") { data { nation_name, leader_name, discord, id, food, uranium, iron, oil, bauxite, lead, coal, soldiers, iron_works, emergency_gasoline_reserve, arms_stockpile, bauxite_works, cities { date, infrastructure, oil_refinery, aluminum_refinery, munitions_factory, steel_mill, nuclear_power } wars { winner_id } nation_name } } }");

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

                        NationAudited nationAudited = new NationAudited();
                        List<ConsumptionCity> citiesList = new ArrayList<>();

                        boolean warStatus = false;

                        nationAudited.setId(object.optLong("id"));
                        nationAudited.setName(object.optString("nation_name"));
                        nationAudited.setLeader(object.optString("leader_name"));
                        nationAudited.setDiscord(object.optString("discord"));
                        nationAudited.setFood(object.optLong("food"));
                        nationAudited.setUranium(object.optLong("uranium"));
                        nationAudited.setIron(object.optLong("iron"));
                        nationAudited.setBauxite(object.optLong("bauxite"));
                        nationAudited.setOil(object.optLong("oil"));
                        nationAudited.setLead(object.optLong("lead"));
                        nationAudited.setCoal(object.optLong("coal"));
                        nationAudited.setSoldiers(object.optLong("soldiers"));

                        nationAudited.setIronworks(object.optBoolean("iron_works"));
                        nationAudited.setGasolineReserve(object.optBoolean("emergency_gasoline_reserve"));
                        nationAudited.setArmsStockpile(object.optBoolean("arms_stockpile"));
                        nationAudited.setBauxiteWorks(object.optBoolean("bauxite_works"));



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
                            citiesList.add(city);

                            nationAudited.setNuclearPowerPlants(nationAudited.getNuclearPowerPlants() + cityObj.optInt("nuclear_power"));
                            nationAudited.setOilRefineries(nationAudited.getOilRefineries() + cityObj.optInt("oil_refinery"));
                            nationAudited.setSteelMills(nationAudited.getSteelMills() + cityObj.optInt("steel_mill"));
                            nationAudited.setAluminumRefineries(nationAudited.getAluminumRefineries() + cityObj.optInt("aluminum_refinery"));
                            nationAudited.setMunitionsFactories(nationAudited.getMunitionsFactories() + cityObj.optInt("munitions_factory"));

                            nationAudited.setInfra(city.getInfra());


                        }

                        nationAudited.setFoodConsumption(Math.round(FoodConsumption.getFoodConsumption(citiesList, nationAudited.getSoldiers())));

                        list.add(nationAudited);


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

    public static List<NationAudited> getUsageNationsList(List<NationAudited> nations, long days) {

        for (NationAudited nation: nations) {

            double ironUsed = 0;
            double oilUsed = 0;
            double coalUsed = 0;
            double leadUsed = 0;
            double bauxUsed = 0;
            double uraniumUsed = 0;

            int timesDivisible = 0;


            //ura
            if (nation.getNuclearPowerPlants() > 0) {
                int i = nation.getInfra();
                while (i > 0) {
                    timesDivisible++;
                    i = i - 1000;
                }
                uraniumUsed += ((2.4 * timesDivisible) * nation.getNuclearPowerPlants() * days);
//                uraniumUsed += ((2.4 * timesDivisible) * days);

            }

            //lead
            if (nation.isArmsStockpile() && nation.getMunitionsFactories() > 0) {
                leadUsed += (12.064 * nation.getMunitionsFactories()) * days;
            } else if (nation.getMunitionsFactories() > 0) {
                leadUsed += (6 * nation.getMunitionsFactories()) * days;
            }

            //oil
            if (nation.isGasolineReserve() && nation.getOilRefineries() > 0) {
                oilUsed += (9 * nation.getOilRefineries()) * days;
            } else if (nation.getOilRefineries() > 0) {
                oilUsed += (3 * nation.getOilRefineries()) * days;
            }

            //bauxite
            if (nation.isBauxiteWorks() && nation.getAluminumRefineries() > 0) {
                bauxUsed += (6.12 * nation.getAluminumRefineries()) * days;
            } else if (nation.getAluminumRefineries() > 0) {
                bauxUsed += (3 * nation.getAluminumRefineries()) * days;
            }

            //coal and iron
            if (nation.isIronworks() && nation.getSteelMills() > 0) {
                coalUsed += (6.12 * nation.getSteelMills()) * days;
                ironUsed += (6.12 * nation.getSteelMills()) * days;
            } else if (nation.getSteelMills() > 0) {
                coalUsed += (3 * nation.getSteelMills()) * days;
                ironUsed += (3 * nation.getSteelMills()) * days;
            }

            nation.setIronUsed(Math.round(ironUsed));
            nation.setBauxUsed(Math.round(bauxUsed));
            nation.setUraniumUsed(Math.round(uraniumUsed));
            nation.setLeadUsed(Math.round(leadUsed));
            nation.setOilUsed(Math.round(oilUsed));
            nation.setCoalUsed(Math.round(coalUsed));
        }

        return nations;
    }



    public static List<NationWarchestAudited> getWarchestAudits() throws JSONException {
        List<NationWarchestAudited> list = new ArrayList<>();


        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;

        client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://api.politicsandwar.com/graphql?api_key=" + Config.sniperKey);

        httpPost.addHeader("Content-Type", "application/json");
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("query", "{ nations (alliance_id: " + Config.aaId + ", tax_id:" + Config.growthProgramTaxId + ") { data { nation_name, leader_name, discord, id, aluminum, steel, munitions, gasoline, num_cities } } }");

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

                        NationWarchestAudited nationAudited = new NationWarchestAudited();
                        List<ConsumptionCity> citiesList = new ArrayList<>();

                        nationAudited.setCities(object.optInt("num_cities"));
                        nationAudited.setId(object.optLong("id"));
                        nationAudited.setName(object.optString("nation_name"));
                        nationAudited.setLeader(object.optString("leader_name"));
                        nationAudited.setDiscord(object.optString("discord"));
                        nationAudited.setMunitions(object.optDouble("munitions"));
                        nationAudited.setGasoline(object.optDouble("gasoline"));
                        nationAudited.setSteel(object.optDouble("steel"));
                        nationAudited.setAluminum(object.optDouble("aluminum"));

                        list.add(nationAudited);


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

    public static List<CityRevenueModel> getCitiesForRevenue(long id) throws JSONException {
        List<CityRevenueModel> list = new ArrayList<>();


        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;

        client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://api.politicsandwar.com/graphql?api_key=" + Config.itachiPnwKey);

        httpPost.addHeader("Content-Type", "application/json");
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("query", "{ nations (id: " + id + ") { data { international_trade_center,telecommunications_satellite,recycling_initiative, green_technologies,government_support_agency,arms_stockpile, continent,emergency_gasoline_reserve,bauxite_works,iron_works,mass_irrigation,uranium_enrichment_program cities { id, infrastructure, land, date, oil_power, wind_power, coal_power, nuclear_power, coal_mine, oil_well, uranium_mine, farm, police_station, hospital, recycling_center, subway, supermarket, bank, shopping_mall, stadium, lead_mine, iron_mine, bauxite_mine, oil_refinery, aluminum_refinery, steel_mill, munitions_factory }  } } }");


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

                    JSONArray array = nations.getJSONArray("data");

                    for (int i = 0; i <= array.length() - 1; i++) {
                        JSONObject object = array.getJSONObject(i);

                        boolean itc = object.optBoolean("international_trade_center");
                        boolean telecom = object.optBoolean("telecommunications_satellite");
                        boolean recycling_initiative = object.optBoolean("recycling_initiative");
                        boolean government_support_agency = object.optBoolean("government_support_agency");
                        boolean arms_stockpile = object.optBoolean("arms_stockpile");
                        boolean emergency_gasoline_reserve = object.optBoolean("emergency_gasoline_reserve");
                        boolean bauxite_works = object.optBoolean("bauxite_works");
                        boolean iron_works = object.optBoolean("iron_works");
                        boolean mass_irrigation = object.optBoolean("mass_irrigation");
                        boolean uranium_enrichment_program = object.optBoolean("uranium_enrichment_program");
                        boolean green_technologies = object.optBoolean("green_technologies");

                        String cont = object.optString("continent");
                        if (cont.equals("na")) {
                            cont = "north_america";
                        } else if (cont.equals("sa")) {
                            cont = "south_america";
                        } else if (cont.equals("eu")) {
                            cont = "europe";
                        } else if (cont.equals("af")) {
                            cont = "africa";
                        } else if (cont.equals("as")) {
                            cont = "asia";
                        } else if (cont.equals("au")) {
                            cont = "australia";
                        }

                        String domesticPolicy = object.optString("domestic_policy");

                        boolean openMarkets = false;
                        if (domesticPolicy.equalsIgnoreCase("OPEN_MARKETS")) {
                            openMarkets = true;
                        }


                        JSONArray wars = object.getJSONArray("cities");
                        for (int j = 0; j <= wars.length() - 1; j++) {
                            JSONObject city = wars.getJSONObject(j);

                            CityRevenueModel cityRevenueModel = new CityRevenueModel();

                            cityRevenueModel.setDate(city.getString("date"));
                            cityRevenueModel.setCoalPower(city.optInt("coal_power"));
                            cityRevenueModel.setOilPower(city.optInt("oil_power"));
                            cityRevenueModel.setWindPower(city.optInt("wind_power"));
                            cityRevenueModel.setNuclearPower(city.optInt("nuclear_power"));
                            cityRevenueModel.setCoalMine(city.optInt("coal_mine"));
                            cityRevenueModel.setIronMine(city.optInt("iron_mine"));
                            cityRevenueModel.setBauxMine(city.optInt("baux_mine"));
                            cityRevenueModel.setLeadMine(city.optInt("lead_mine"));
                            cityRevenueModel.setFarm(city.optInt("farm"));
                            cityRevenueModel.setGasRefinery(city.optInt("oil_refinery"));
                            cityRevenueModel.setSteelFactory(city.optInt("steel_mill"));
                            cityRevenueModel.setAluRefinery(city.optInt("aluminum_refinery"));
                            cityRevenueModel.setMuniFactory(city.optInt("munitions_factory"));
                            cityRevenueModel.setPoliceStation(city.optInt("police_station"));
                            cityRevenueModel.setHospital(city.optInt("hospital"));
                            cityRevenueModel.setRecyclingCenter(city.optInt("recycling_center"));
                            cityRevenueModel.setSubway(city.optInt("subway"));
                            cityRevenueModel.setSupermarket(city.optInt("supermarket"));
                            cityRevenueModel.setBank(city.optInt("bank"));
                            cityRevenueModel.setMall(city.optInt("shopping_mall"));
                            cityRevenueModel.setStadium(city.optInt("stadium"));

                            cityRevenueModel.setItc(itc);
                            cityRevenueModel.setTelecomSat(telecom);
                            cityRevenueModel.setRecylcyingInitiative(recycling_initiative);
                            cityRevenueModel.setGSA(government_support_agency);
                            cityRevenueModel.setArmsStockpile(arms_stockpile);
                            cityRevenueModel.setGasolineReserve(emergency_gasoline_reserve);
                            cityRevenueModel.setBauxworks(bauxite_works);
                            cityRevenueModel.setIronworks(iron_works);
                            cityRevenueModel.setIrrigation(mass_irrigation);
                            cityRevenueModel.setUraniumEnrichment(uranium_enrichment_program);
                            cityRevenueModel.setGreenTech(green_technologies);
                            cityRevenueModel.setOpenMarkets(openMarkets);
                            cityRevenueModel.setContinent(cont);
                            list.add(cityRevenueModel);
                        }



                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("number of cities: " + list.size());
        return list;
    }

}
