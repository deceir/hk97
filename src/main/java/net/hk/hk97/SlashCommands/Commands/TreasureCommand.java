package net.hk.hk97.SlashCommands.Commands;

import net.hk.hk97.Config;
import net.hk.hk97.Models.Nation;
import net.hk.hk97.Models.Treasure;
import net.hk.hk97.Repositories.NationRepository;
import net.hk.hk97.Repositories.TreasureRepository;
import net.hk.hk97.Repositories.UserRepository;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TreasureCommand {


    public static void treasures(SlashCommandInteraction interaction, NationRepository nationRepository, TreasureRepository treasureRepository, UserRepository userRepository) {


        if (interaction.getOptionByName("maxscore").isPresent()) {
            List<Nation> maxScore = nationRepository.findAll(Sort.by(Sort.Direction.DESC, "score"));
            double topScore = maxScore.get(0).getScore();

            interaction.createFollowupMessageBuilder().setContent("Max Score is " + topScore).send();

        } else if (interaction.getOptionByName("range").isPresent()) {
            List<Nation> maxScore = nationRepository.findAll(Sort.by(Sort.Direction.DESC, "score"));
            double topScore = maxScore.get(0).getScore();

            double minTrScore = (topScore * .15);
            double maxTrScore = (topScore * .65);

            interaction.createFollowupMessageBuilder().setContent("Treasure range is " + minTrScore + " to " + maxTrScore + ".").send();

        } else if (interaction.getOptionByName("hunt").isPresent()) {

            if (!interaction.getOptionByName("hunt").get().getOptionByName("continent").isPresent()) {

                //no continent
                List<Nation> maxScore = nationRepository.findAll(Sort.by(Sort.Direction.DESC, "score"));
                double topScore = maxScore.get(0).getScore();

                double minTrScore = (topScore * .15);
                double maxTrScore = (topScore * .65);

                String color = interaction.getOptionByName("hunt").get().getOptionStringValueByName("color").get();
                List<Nation> nationsOnColor = nationRepository.getNationsByColor(color);


                //filtering based on min and max score
                nationsOnColor.removeIf(nation -> nation.getScore() < minTrScore || nation.getScore() > maxTrScore);
                int colorcount = nationsOnColor.size();

                try {
                    List<String> reqNations = new ArrayList<>();

                    String qColor = null;
                    double qScore = 0;
                    long qId = 0;
                    long reqCounter = 0;

                    CloseableHttpClient client = null;
                    CloseableHttpResponse response = null;

                    client = HttpClients.createDefault();
                    HttpPost httpPost = new HttpPost("https://api.politicsandwar.com/graphql?api_key=" + Config.itachiPnwKey);

                    httpPost.addHeader("Content-Type", "application/json");
                    JSONObject jsonObj = new JSONObject();
                    jsonObj.put("query", "{ nations (first: 500, alliance_id: 10470) { data { id color score continent vmode } } }");


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
                                    JSONObject object = array.getJSONObject(i);
                                    qColor = object.optString("color");
                                    qScore = object.optDouble("score");
                                    qId = object.optLong("id");
                                    int vm = object.optInt("vmode");

                                    if (qScore > minTrScore && qScore < maxTrScore) {
                                        reqCounter++;
                                    }

                                    if (qScore > minTrScore && qScore < maxTrScore && !qColor.equals(color) && vm != 1) {
                                        try {
                                            reqNations.add("<@" + userRepository.findUserByNationid(qId).getDiscordid() + ">");
                                        } catch (Exception e) {
                                            e.printStackTrace();
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


                    double percentreq = ((double) (reqCounter) / (double) (reqCounter + colorcount)) * 100;


                    EmbedBuilder huntEmbed = new EmbedBuilder()
                            .setTitle("Treasure Time")
                            .setAuthor(interaction.getUser())
                            .setDescription("There are currently " + colorcount + " nations on " + color + " and in range.")
                            .addInlineField("Requiem", reqCounter + " nations in range.\n" + Math.round(percentreq) + "% chance if all nations swap.")
                            .setColor(Color.CYAN)
                            .setFooter("Necron Treasure Service", interaction.getApi().getYourself().getAvatar());

                    //making list for users to ping

                    EmbedBuilder leaderEmbed = new EmbedBuilder();


                    if (colorcount == 0) {
                        interaction.createFollowupMessageBuilder().setContent("There was an issue with what you submitted.");
                    } else {
                        interaction.createFollowupMessageBuilder().addEmbed(huntEmbed).send();
                        //making embed for leader pings copy
                        interaction.getChannel().get().sendMessage("Players in range and not on " + color + ":");
                        String leadersString = "";

                        for (int i = 0; i < reqNations.size(); i++) {
                            leadersString += reqNations.get(i) + " ";
                        }
                        leaderEmbed.addField("Requiem", leadersString);
                        interaction.getChannel().get().sendMessage(leaderEmbed);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }


            } else {

                //continent duplicate code here

                List<Nation> maxScore = nationRepository.findAll(Sort.by(Sort.Direction.DESC, "score"));
                double topScore = maxScore.get(0).getScore();

                double minTrScore = (topScore * .15);
                double maxTrScore = (topScore * .65);

                String color = interaction.getOptionByName("hunt").get().getOptionStringValueByName("color").get();
                String continent = interaction.getOptionByName("hunt").get().getOptionStringValueByName("continent").get();

                List<Nation> nationsOnColor = nationRepository.getNationsByColorAndContinentLike(color, continent);


                //filtering based on min and max score
                nationsOnColor.removeIf(nation -> nation.getScore() < minTrScore || nation.getScore() > maxTrScore);
                nationsOnColor.removeIf(nation -> !(nation.getContinent().equalsIgnoreCase(continent)));
                int colorcount = nationsOnColor.size();

                try {
                    List<String> reqNations = new ArrayList<>();

                    String qColor = null;
                    String qContinent = null;
                    double qScore = 0;
                    long qId = 0;
                    long reqCounter = 0;

                    String ContSearch = null;

                    if (continent.equalsIgnoreCase("africa")) {
                        ContSearch = "af";
                    } else if (continent.equalsIgnoreCase("asia")) {
                        ContSearch = "as";
                    } else if (continent.equalsIgnoreCase("north america")) {
                        ContSearch = "na";
                    } else if (continent.equalsIgnoreCase("south america")) {
                        ContSearch = "sa";
                    } else if (continent.equalsIgnoreCase("europe")) {
                        ContSearch = "eu";
                    } else if (continent.equalsIgnoreCase("australia")) {
                        ContSearch = "au";
                    }

                    CloseableHttpClient client = null;
                    CloseableHttpResponse response = null;

                    client = HttpClients.createDefault();
                    HttpPost httpPost = new HttpPost("https://api.politicsandwar.com/graphql?api_key=" + Config.itachiPnwKey);

                    httpPost.addHeader("Content-Type", "application/json");
                    JSONObject jsonObj = new JSONObject();
                    jsonObj.put("query", "{ nations (first: 500, alliance_id: 10470) { data { id color score continent vmode } } }");


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
                                    JSONObject object = array.getJSONObject(i);
                                    qColor = object.optString("color");
                                    qScore = object.optDouble("score");
                                    qId = object.optLong("id");
                                    qContinent = object.optString("continent");
                                    int vm = object.optInt("vmode");

                                    if (qScore > minTrScore && qScore < maxTrScore && qContinent.equalsIgnoreCase(ContSearch)) {
                                        reqCounter++;
                                    }

                                    if (qScore > minTrScore && qScore < maxTrScore && !qColor.equals(color) && vm != 1 && qContinent.equalsIgnoreCase(ContSearch)) {
                                        try {
                                            reqNations.add("<@" + userRepository.findUserByNationid(qId).getDiscordid() + ">");
                                        } catch (Exception e) {
                                            e.printStackTrace();
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


                    double percentreq = ((double) (reqCounter) / (double) (reqCounter + colorcount)) * 100;

                    EmbedBuilder huntEmbed = new EmbedBuilder()
                            .setTitle("Treasure Time")
                            .setAuthor(interaction.getUser())
                            .setDescription("There are currently " + colorcount + " nations on " + color + " and in range on " + continent + ".")
                            .addInlineField("Requiem", reqCounter + " nations in range.\n" + Math.round(percentreq) + "% chance if all nations swap.")
                            .setColor(Color.CYAN)
                            .setFooter("Necron Treasure Service", interaction.getApi().getYourself().getAvatar());

                    //making list for users to ping

                    EmbedBuilder leaderEmbed = new EmbedBuilder();


                    if (colorcount == 0) {
                        interaction.createFollowupMessageBuilder().setContent("There was an issue with what you submitted.");
                    } else {
                        interaction.createFollowupMessageBuilder().addEmbed(huntEmbed).send();
                        //making embed for leader pings copy
                        interaction.getChannel().get().sendMessage("Players in range and not on " + color + ":");
                        String leadersString = "";

                        for (int i = 0; i < reqNations.size(); i++) {
                            leadersString += reqNations.get(i) + " ";

                        }
                        leaderEmbed.addField("Requiem", leadersString);
                        interaction.getChannel().get().sendMessage(leaderEmbed);
                    }


                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        } else if (interaction.getOptionByName("list").isPresent()) {

            try {
                List<Treasure> treasures = treasureRepository.findAll();
                Collections.sort(treasures);
                System.out.println(treasures);

                List<Treasure> sortedTreasure = new ArrayList<>();

                for (int i = 0; i < 5; i++) {
                    sortedTreasure.add(treasures.get(i));
                }

                Collections.sort(sortedTreasure);
                EmbedBuilder treasuresEmbed = new EmbedBuilder()
                        .setTitle("Treasures Dates")
                        .setAuthor(interaction.getUser())
                        .setDescription("All current treasure spawn dates sorted by closest to spawning within the next 5 spawns.")
                        .setColor(Color.CYAN);
                System.out.println("embed made");
                for (Treasure treasure : sortedTreasure) {
                    treasuresEmbed.addInlineField(treasure.getName(), "Color: " + treasure.getColor() + "\nContinent: " + treasure.getContinent() + "\nSpawns: " + treasure.getNextSpawn());
                }
                treasuresEmbed.setFooter("Necron Treasure Service", interaction.getApi().getYourself().getAvatar());

                System.out.println("embed ready to send");
                interaction.createFollowupMessageBuilder().addEmbed(treasuresEmbed).send();
            } catch (Exception e) {
                interaction.getChannel().get().sendMessage("Error.");
                e.printStackTrace();
            }

        }
    }
}
