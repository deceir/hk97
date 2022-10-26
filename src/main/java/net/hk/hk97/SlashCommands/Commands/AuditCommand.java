package net.hk.hk97.SlashCommands.Commands;

import net.hk.hk97.Models.ActivityAudit;
import net.hk.hk97.Models.User;
import net.hk.hk97.Repositories.UserRepository;
import net.hk.hk97.Services.Util.AuditUtil;
import net.hk.hk97.Services.Util.MilUtil;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.*;
import java.util.List;

public class AuditCommand {

    public static void audit(SlashCommandInteraction interaction, UserRepository userRepository) throws JSONException {
        if (interaction.getOptionByName("activity").isPresent()) {

            try {
                List<ActivityAudit> audits = AuditUtil.getActivityAudit();

                EmbedBuilder activityEmbed = new EmbedBuilder()
                        .setAuthor(interaction.getUser())
                        .setColor(Color.CYAN);

                String inactiveUsers = "";

                for (ActivityAudit audit : audits) {
                    User user = userRepository.findUserByNationid(audit.getId());

                    inactiveUsers += "<@" + user.getDiscordid() + "> last active " + audit.getLastActive() + "hrs ago";
                    inactiveUsers += "\n";

                }

                if (inactiveUsers.length() == 0) {
                    inactiveUsers = "No inactive users.";
                }

                activityEmbed.addField("Inactive Nations:", inactiveUsers);

                interaction.createFollowupMessageBuilder().addEmbed(activityEmbed).send();


            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        } else if (interaction.getOptionByName("spies").isPresent()) {


            interaction.createFollowupMessageBuilder().setContent("This command has been deprecated. Please use `/audit nations` to get this information.").setFlags(MessageFlag.EPHEMERAL).send();

        } else if (interaction.getOptionByName("nations").isPresent()) {

            DiscordApi api = interaction.getApi();

            JSONObject nations = MilUtil.getSpies(10470);
            //pings for each
            String food = "";
            String uranium = "";
            String spies = "";


                List<ActivityAudit> audits = AuditUtil.getActivityAudit();


                String inactiveUsers = "";

                for (ActivityAudit audit : audits) {
                    User user = userRepository.findUserByNationid(audit.getId());

                    inactiveUsers += "<@" + user.getDiscordid() + "> last active " + audit.getLastActive() + "hrs ago";
                    inactiveUsers += "\n";

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
                    if (foodHeld <= 50000) {
                        food += "<@" + userRepository.findUserByNationid(id).getDiscordid() + "> ";
                    }
                    boolean hasIA = object.optBoolean("central_intelligence_agency");

                    int spyCount = object.optInt("spies");

                    if (hasIA && (spyCount < 60)) {
                        spies += "<@" + userRepository.findUserByNationid(id).getDiscordid() + "> ";
                    } else if (!hasIA && spyCount < 50) {
                        spies += "<@" + userRepository.findUserByNationid(id).getDiscordid() + "> ";
                    }
                    long uraHeld = object.optInt("uranium");
                    if (uraHeld < (cities * 3)) {
                        uranium += "<@" + userRepository.findUserByNationid(id).getDiscordid() + "> ";
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
                        .setTitle("Requiem Audit")
                        .setColor(Color.CYAN)
                        .setAuthor(interaction.getUser())
                        .addField("Inactive (48hrs)", inactiveUsers + " ")
                        .addField("Low Food (Less than 50k)", food + " ")
                        .addField("Low Uranium", uranium + " ")
                        .addField("Not Maxed On Spies", spies + " ")
                        .setFooter("Necron Internal Command", interaction.getApi().getYourself().getAvatar());


                interaction.createFollowupMessageBuilder().addEmbed(eb).send();


        }
    }
}
