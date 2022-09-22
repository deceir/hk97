package net.hk.hk97.Services.Util;

import net.hk.hk97.Config;
import net.hk.hk97.Models.Military;
import net.hk.hk97.Models.Nation;
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

public class WarUtil {

    public static List<WarInfo> getWarInfo(long id) throws JSONException {
        List<WarInfo> list = new ArrayList<>();
        WarInfo war = new WarInfo();

        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;

        client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://api.politicsandwar.com/graphql?api_key=" + Config.itachiPnwKey);

        httpPost.addHeader("Content-Type", "application/json");
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("query", "{ wars (nation_id: " + id + ") { data { id reason war_type turns_left att_resistance def_resistance att_points def_points att_infra_destroyed_value def_infra_destroyed_value attacker { score color nation_name leader_name soldiers tanks aircraft ships missiles nukes } defender { score color nation_name leader_name soldiers tanks aircraft ships missiles nukes }} } }");


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

                JSONObject nations = data.getJSONObject("wars");


                try {

                    JSONArray array = nations.getJSONArray("data");


                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(0);
                        war.setId(object.optInt("id"));
                        //attacker
                        JSONObject attackerNation = object.getJSONObject("attacker");
                        Military attacker = new Military();
                        attacker.setScore(attackerNation.optInt("score"));
                        attacker.setColor(attackerNation.optString("color"));
                        attacker.setNation_name(attackerNation.optString("nation_name"));
                        attacker.setLeader_name(attackerNation.optString("leader_name"));
                        attacker.setSoldiers(attackerNation.optInt("soldiers"));
                        attacker.setTanks(attackerNation.optInt("tanks"));
                        attacker.setJets(attackerNation.optInt("aircraft"));
                        attacker.setShips(attackerNation.optInt("ships"));
                        attacker.setMissiles(attackerNation.optInt("missiles"));
                        attacker.setNukes(attackerNation.optInt("nukes"));
                        war.setAttackerNation(attacker);
                        //defender
                        Military defender = new Military();
                        JSONObject defenderNation = object.getJSONObject("defender");

                        defender.setScore(defenderNation.optInt("score"));
                        defender.setColor(defenderNation.optString("color"));
                        defender.setNation_name(defenderNation.optString("nation_name"));
                        defender.setLeader_name(defenderNation.optString("leader_name"));
                        defender.setSoldiers(defenderNation.optInt("soldiers"));
                        defender.setTanks(defenderNation.optInt("tanks"));
                        defender.setJets(defenderNation.optInt("aircraft"));
                        defender.setShips(defenderNation.optInt("ships"));
                        defender.setMissiles(defenderNation.optInt("missiles"));
                        defender.setNukes(defenderNation.optInt("nukes"));
                        war.setDefenderNation(defender);

                        war.setReason(object.optString("reason"));
                        war.setWarType(object.optString("war_type"));
                        war.setTurnsLeft(object.optInt("turns_left"));
                        war.setAttResistance(object.optInt("att_resistance"));
                        war.setDefResistance(object.optInt("def_resistance"));
                        war.setAttInfraDestroyed(object.optLong("att_infra_destroyed_value"));
                        war.setDefInfraDestroyed(object.optLong("def_infra_destroyed_value"));
                        war.setAttmaps(object.optInt("att_points"));
                        war.setDefmaps(object.optInt("def_points"));
                        list.add(war);
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

    public static List<List<WarInfo>> getNationWars(int id) throws JSONException {
        List<List<WarInfo>> list = new ArrayList<>();
        List<WarInfo> offensiveWars = new ArrayList<>();
        List<WarInfo> defensiveWars = new ArrayList<>();



        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;

        client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://api.politicsandwar.com/graphql?api_key=" + Config.itachiPnwKey);

        httpPost.addHeader("Content-Type", "application/json");
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("query", "{ wars (nation_id: " + id + ") { data { id reason ground_control air_superiority naval_blockade war_type turns_left att_resistance def_resistance att_points def_points att_infra_destroyed_value def_infra_destroyed_value attacker { alliance { acronym } id score color nation_name leader_name soldiers tanks aircraft ships missiles nukes last_active } defender { alliance { acronym } id score color nation_name leader_name soldiers tanks aircraft ships missiles nukes last_active }} } }");


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

                JSONObject nations = data.getJSONObject("wars");


                try {

                    JSONArray array = nations.getJSONArray("data");


                    for (int i = 0; i < array.length(); i++) {
                        WarInfo war = new WarInfo();
                        JSONObject object = array.getJSONObject(i);
                        war.setId(object.optInt("id"));
                        //attacker

                        JSONObject attackerNation = object.getJSONObject("attacker");
                        Military attacker = new Military();
                        try {
                            if (attackerNation.has("alliance")) {
                                JSONObject defAlliance = attackerNation.getJSONObject("alliance");
                                attacker.setAaAcronym(defAlliance.optString("acronym"));
                            }
                        } catch (JSONException e) {
                            attacker.setAaAcronym("");
                        }

                        attacker.setScore(attackerNation.optInt("score"));
                        attacker.setColor(attackerNation.optString("color"));
                        System.out.println("attacker nation name " + attackerNation.optString("nation_name"));
                        attacker.setNation_name(attackerNation.optString("nation_name"));
                        attacker.setLeader_name(attackerNation.optString("leader_name"));
                        attacker.setSoldiers(attackerNation.optInt("soldiers"));
                        attacker.setTanks(attackerNation.optInt("tanks"));
                        attacker.setJets(attackerNation.optInt("aircraft"));
                        attacker.setShips(attackerNation.optInt("ships"));
                        attacker.setMissiles(attackerNation.optInt("missiles"));
                        attacker.setNukes(attackerNation.optInt("nukes"));
                        attacker.setId(attackerNation.optInt("id"));
                        attacker.setLast_active(attackerNation.optString("last_active"));
                        war.setAttackerNation(attacker);

                        //defender
                        Military defender = new Military();
                        JSONObject defenderNation = object.getJSONObject("defender");
                        try {
                            if (defenderNation.has("alliance")) {
                                JSONObject defAlliance = defenderNation.getJSONObject("alliance");
                                defender.setAaAcronym(defAlliance.optString("acronym"));
                            }
                        } catch (JSONException e) {
                            defender.setAaAcronym("");
                        }

                        defender.setScore(defenderNation.optInt("score"));
                        defender.setColor(defenderNation.optString("color"));
                        defender.setNation_name(defenderNation.optString("nation_name"));
                        defender.setLeader_name(defenderNation.optString("leader_name"));
                        defender.setSoldiers(defenderNation.optInt("soldiers"));
                        defender.setTanks(defenderNation.optInt("tanks"));
                        defender.setJets(defenderNation.optInt("aircraft"));
                        defender.setShips(defenderNation.optInt("ships"));
                        defender.setMissiles(defenderNation.optInt("missiles"));
                        defender.setNukes(defenderNation.optInt("nukes"));
                        defender.setId(defenderNation.optInt("id"));
                        defender.setLast_active(defenderNation.optString("last_active"));
                        war.setDefenderNation(defender);

                        int groundControl = object.optInt("ground_control");
                        int airSuperiority = object.optInt("air_superiority");
                        int navalBlockade = object.optInt("naval_blockade");

                        if (groundControl == defender.getId()) {
                            defender.setGroundcontrol("✓");
                        } else if (groundControl == attacker.getId()) {
                            attacker.setGroundcontrol("✓");
                        }

                        if (airSuperiority == defender.getId()) {
                            defender.setAirsuperiority("✓");
                        } else if (airSuperiority == attacker.getId()) {
                            attacker.setAirsuperiority("✓");
                        }

                        if (navalBlockade == defender.getId()) {
                            defender.setNavalblockade("✓");
                        } else if (navalBlockade == attacker.getId()) {
                            attacker.setNavalblockade("✓");
                        }

                        war.setReason(object.optString("reason"));
                        war.setWarType(object.optString("war_type"));
                        war.setTurnsLeft(object.optInt("turns_left"));
                        war.setAttResistance(object.optInt("att_resistance"));
                        war.setDefResistance(object.optInt("def_resistance"));
                        war.setAttInfraDestroyed(object.optLong("att_infra_destroyed_value"));
                        war.setDefInfraDestroyed(object.optLong("def_infra_destroyed_value"));
                        war.setAttmaps(object.optInt("att_points"));
                        war.setDefmaps(object.optInt("def_points"));

                        if (war.getAttackerNation().getId() == id) {
                            offensiveWars.add(war);
                        } else if (war.getDefenderNation().getId() == id) {
                            defensiveWars.add(war);
                        }
                        System.out.println("attacker nation: " + attacker.getNation_name());
                        System.out.println("defender nation: " + defender.getNation_name());
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("list of defensive wars");
        for (WarInfo warInfo : defensiveWars) {
            System.out.println(warInfo.getDefenderNation().getNation_name());
        }



        list.add(offensiveWars);
        list.add(defensiveWars);
        return list;

    }

    public static Nation getNationInfo(int id) throws JSONException {
        Nation nation = new Nation();

        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;

        client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://api.politicsandwar.com/graphql?api_key=" + Config.itachiPnwKey);

        httpPost.addHeader("Content-Type", "application/json");
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("query", "{ nations (id: " + id + ") { data { nation_name score alliance { acronym name } soldiers tanks aircraft ships missiles nukes last_active } } }");

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
                    try {
                        JSONObject alliance = object.getJSONObject("alliance");
                        nation.setAcronym(alliance.optString("acronym"));
                        nation.setAlliance(alliance.optString("name"));

                    } catch (JSONException e) {
                        nation.setAlliance(" ");
                    }
                    nation.setScore(object.optInt("score"));
                    nation.setNation(object.optString("nation_name"));
                    nation.setSoldiers(object.optInt("soldiers"));
                    nation.setTanks(object.optInt("tanks"));
                    nation.setAircraft(object.optInt("aircraft"));
                    nation.setShips(object.optInt("ships"));
                    nation.setNukes(object.optInt("nukes"));
                    nation.setMissiles(object.optInt("missiles"));
                    nation.setLast_active(object.optString("last_active"));


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return nation;
    }

}
