package net.hk.hk97.SlashCommands.Commands;

import net.hk.hk97.Models.ActivityAudit;
import net.hk.hk97.Models.User;
import net.hk.hk97.Repositories.UserRepository;
import net.hk.hk97.Services.Util.AuditUtil;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.json.JSONException;

import java.awt.*;
import java.util.List;

public class AuditCommand {

    public static void audit(SlashCommandInteraction interaction, UserRepository userRepository) {
        if (interaction.getOptionByName("activity").isPresent()) {

            try {
                List<ActivityAudit> audits = AuditUtil.getActivityAudit();

                EmbedBuilder activityEmbed = new EmbedBuilder()
                        .setAuthor(interaction.getUser())
                        .setColor(Color.ORANGE);

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


            interaction.createFollowupMessageBuilder().setContent("This feature has not been released! Please check back later!").setFlags(MessageFlag.EPHEMERAL).send();

        }
    }
}
