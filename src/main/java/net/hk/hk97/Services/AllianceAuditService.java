package net.hk.hk97.Services;

import net.hk.hk97.Config;
import net.hk.hk97.Models.ActivityAudit;
import net.hk.hk97.Models.User;
import net.hk.hk97.Repositories.UserRepository;
import net.hk.hk97.Services.Util.AuditUtil;
import net.hk.hk97.Services.Util.MilUtil;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;

@Service
@EnableScheduling
public class AllianceAuditService {

    @Autowired
    UserRepository userRepository;

    @Scheduled(cron = "0 30 0 * * *")
    public void runDailyAudit() {
        DiscordApi api = new DiscordApiBuilder()
                .setToken(Config.discordToken)
                .login().join();
        try {



//            DiscordApi api = interaction.getApi();

            JSONObject nations = MilUtil.getSpies(Long.parseLong(Config.aaId));
            //pings for each
            String food = "";
            String uranium = "";
            String spies = "";
            DecimalFormat d = new DecimalFormat("#,###");


            List<ActivityAudit> audits = AuditUtil.getActivityAudit();


            String inactiveUsers = "";

            for (ActivityAudit audit : audits) {
                try {

                    User user = userRepository.findUserByNationid(audit.getId());

                    inactiveUsers += "<@" + user.getDiscordid() + "> (" + user.getNationid() + ") last active " + audit.getLastActive() + "hrs ago";
                    inactiveUsers += "\n";
                } catch (Exception e) {
                    //probably applicant
                }

            }

            if (inactiveUsers.length() == 0) {
                inactiveUsers = "No inactive members.";
            }

            JSONArray array = nations.getJSONArray("data");

            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                try {


                    int cities = object.optInt("cities");
                    int id = object.optInt("id");
                    long foodHeld = object.optLong("food");
                    if (object.optInt("vacation_mode_turns") > 0) {
                        continue;
                    }
                    User user = userRepository.findUserByNationid(id);

                    if (foodHeld <= 50000 && cities >= 15) {
                        try {
                            food += "<@" + user.getDiscordid() + "> (" + user.getNationid() + ") " + d.format(foodHeld) + "\n";
                        } catch (Exception e) {
                            //probably applicant
                        }
                    } else if (foodHeld < 1000) {
                        food += "<@" + user.getDiscordid() + "> (" + user.getNationid() + ") " + d.format(foodHeld) + "\n";
                    }
                    boolean hasIA = object.optBoolean("central_intelligence_agency");

                    int spyCount = object.optInt("spies");

                    if (hasIA && (spyCount < 60)) {
                        try {
                            spies += "<@" + user.getDiscordid() + "> (" + user.getNationid() + ") " + d.format(spyCount) + "\n";
                        } catch (Exception e) {
                            //probably applicant
                        }
                    } else if (!hasIA && spyCount < 50) {
                        try {
                            spies += "<@" + user.getDiscordid() + "> (" + user.getNationid() + ") " + spyCount + "\n";
                        } catch (Exception e) {
                            //probably applicant
                        }
                    }
                    long uraHeld = object.optInt("uranium");
                    System.out.println("ura held: " + uraHeld);
                    if (uraHeld < 500 && cities >= 15) {
                        try {
                            uranium += "<@" + user.getDiscordid() + "> (" + user.getNationid() + ") " + d.format(uraHeld) + "\n";
                        } catch (Exception e) {
                            //probably applicant
                        }
                    } else if (uraHeld < 100) {
                        uranium += "<@" + user.getDiscordid() + "> (" + user.getNationid() + ") " + d.format(uraHeld) + "\n";
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (food.length() == 0) {
                food = "No low food members.";
            }

            if (uranium.length() == 0) {
                uranium = "No low uranium members.";
            }

            if (spies.length() == 0) {
                spies = "No members without max spies.";
            }
            EmbedBuilder eb = new EmbedBuilder()
                    .setTitle("TGH Audit")
                    .setColor(Color.CYAN)
                    .setDescription("Format is <ping> (id) value")
                    .addField("Inactive (48hrs+)", inactiveUsers + " ")
                    .addField("Low Food (Less than 50k)", food + " ")
                    .addField("Low Uranium (Less than 500)", uranium + " ")
                    .addField("Not Maxed On Spies", spies + " ")
                    .setFooter("HK-97 Internal Command");

            api.getTextChannelById("1129269346015907912").get().sendMessage("__**TGH Automated Audit**__");
            api.getTextChannelById("1129269346015907912").get().sendMessage(eb);



        } catch (Exception e) {
            api.getTextChannelById("1129269346015907912").get().sendMessage("There was an error retrieving the information for this daily audit.\n" + e);
            e.printStackTrace();
        }
    }


}
