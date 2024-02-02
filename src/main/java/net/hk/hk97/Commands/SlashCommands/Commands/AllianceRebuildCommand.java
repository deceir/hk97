package net.hk.hk97.Commands.SlashCommands.Commands;


import net.dv8tion.jda.api.entities.MessageEmbed;
import net.hk.hk97.Config;
import net.hk.hk97.Models.calc.InfraCalc;
import net.hk.hk97.Repositories.AllianceKeyRepository;
import net.hk.hk97.Repositories.UserRepository;
import org.apache.http.client.ClientProtocolException;
import org.javacord.api.entity.message.embed.Embed;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.Array;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class AllianceRebuildCommand {

    public static void getAllianceRebuild(SlashCommandInteraction interaction, UserRepository userRepository, AllianceKeyRepository allianceKeyRepository) {



                EmbedBuilder badArg = new EmbedBuilder()
                        .setTitle("Incorrect format.")
                        .setColor(Color.CYAN)
                        .setDescription("There was an error running this command. You should not be here. Goodnight.")
                        .addField("Quick Alliance ID Reference:", "TGH- 4567", false)
                        .setFooter("Necron", "https://i.imgur.com/qCq0TQe.png");

                    try {


                        SlashCommandInteractionOption option = interaction.getOptionByName("rebuild").get();

                        Long totalCost = 0L;
                        DecimalFormat format = new DecimalFormat("##,##,##,##,##,##,##0");

                        // graphql api call
                        CloseableHttpClient client = null;
                        CloseableHttpResponse response = null;

                        client = HttpClients.createDefault();
                        HttpPost httpPost = new HttpPost("https://api.politicsandwar.com/graphql?api_key=" + Config.itachiPnwKey);

                        httpPost.addHeader("Content-Type", "application/json");
                        JSONObject jsonObj = new JSONObject();
                        jsonObj.put("query", "{ nations (first: 100, alliance_id: " + option.getOptionByName("allianceid").get().getLongValue().get() + ", vmode:false, min_cities: " + option.getOptionByName("mincity").get().getLongValue().get() + ", max_cities: " + option.getOptionByName("maxcity").get().getLongValue().get() + ") { data { id } } }");


                        List<Long> listOfIds = new ArrayList<>();

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


                                    for (int index = 0; index < array.length(); index++) {
                                        JSONObject object = array.getJSONObject(index);

                                        listOfIds.add(object.optLong("id"));

                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }

                            for (Long id : listOfIds) {

                                client = null;
                                response = null;

                                client = HttpClients.createDefault();
                                httpPost = new HttpPost("https://api.politicsandwar.com/graphql?api_key=" + Config.kastorKey);

                                httpPost.addHeader("Content-Type", "application/json");
                                jsonObj = new JSONObject();
                                jsonObj.put("query", "{ nations (first: 1, id: " + id + "){ data { nation_name adv_engineering_corps cities { infrastructure } } } }");


                                try {
                                    StringEntity entity = new StringEntity(jsonObj.toString());

                                    httpPost.setEntity(entity);
                                    response = client.execute(httpPost);

                                    System.out.println(response);

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                try {
                                    reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                                    line = null;
                                    builder = new StringBuilder();
                                    long infrastructure = 0;
                                    long cost = 0;
                                    while ((line = reader.readLine()) != null) {

                                        builder.append(line);

                                        JSONObject myObject = new JSONObject(builder.toString());

                                        JSONObject data = myObject.getJSONObject("data");

                                        JSONObject nations = data.getJSONObject("nations");

                                        try {

                                            JSONArray array = nations.getJSONArray("data");
                                            Boolean aec = false;


                                            for (int index = 0; index < array.length(); index++) {
                                                JSONObject object = array.getJSONObject(index);
                                                String nation = object.optString("nation_name");
                                                aec = object.optBoolean("adv_engineering_corps");
                                                String fnation = nation.replaceAll(" ", "+");

                                                JSONArray imprArray = object.getJSONArray("cities");
                                                for (int y = 0; y < imprArray.length(); y++) {
                                                    JSONObject imprObj = imprArray.getJSONObject(y);
                                                    infrastructure = imprObj.optLong("infrastructure");

                                                    InfraCalc infraCalc = new InfraCalc();
                                                    infraCalc.calculateInfra(infrastructure, option.getOptionByName("infra").get().getLongValue().get());
                                                    if (aec) {
                                                        cost += infraCalc.getAec_cost();
                                                    } else {
                                                        cost += infraCalc.getTwo_cost();
                                                    }

                                                }
                                                System.out.println("cost= " + cost);
                                                String formattedCost = "$" + format.format(cost);


                                                if (cost > 0) {
                                                    EmbedBuilder rebuildEmbed = new EmbedBuilder()
                                                            .setDescription("[" + nation + "](https://politicsandwar.com/nation/id=" + id + ")")
                                                            .addField("Rebuild Cost For " + nation + " | AA ID " + option.getOptionByName("allianceid").get().getLongValue().get() + ":", formattedCost, true)
                                                            .addField("AEC:", aec + "", true)
                                                            .addField("Links:", "[Offshore Bank](https://politicsandwar.com/alliance/id=" + allianceKeyRepository.findAllianceKeysByAaName("offshore").getId() + "&display=bank&w_money=" + cost + "&w_note=Rebuild&w_type=nation&w_recipient=" + fnation + ")", true)
                                                            .setColor(Color.CYAN);

                                                    totalCost += cost;
                                                    interaction.getChannel().get().sendMessage(rebuildEmbed);
                                                }
                                            }


                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }
                            String formattedTotalCost = "$" + format.format(totalCost);
                            interaction.getChannel().get().sendMessage("The total cost for this rebuild is: " + formattedTotalCost);

                        } catch (Exception e) {
                            interaction.createFollowupMessageBuilder().addEmbed(badArg).send();
                            e.printStackTrace();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }




    }
}
