package net.hk.hk97.Commands.SlashCommands.Commands;

import net.hk.hk97.Models.NAudit;
import net.hk.hk97.Models.Nation;
import net.hk.hk97.Models.User;
import net.hk.hk97.Repositories.NationRepository;
import net.hk.hk97.Repositories.UserRepository;
import net.hk.hk97.Services.Util.MilUtil;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.json.JSONException;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class WhoCommand {

    public static void getWho(SlashCommandInteraction interaction, NationRepository nationRepository, UserRepository userRepository) throws JSONException {

        if (interaction.getOptionByName("leader").isPresent()) {

            List<Nation> list = nationRepository.findNationByLeaderContainingIgnoreCaseAndAlliance(interaction.getOptionByName("leader").get().getOptionStringValueByName("name").get(), "The Golden Horde");

            EmbedBuilder eb = new EmbedBuilder()
                    .setTitle("Leader Search")
                    .setColor(Color.CYAN);

            String names = "";


            System.out.println(names);

            for (Nation nation : list) {
                names += "[" + nation.getLeader() + " of " + nation.getNation() + "](https://politicsandwar.com/nation/id=" + nation.getId() + ") (" + nation.getId() + ")\n";
            }
            if (names.length() == 0) {
                names = "No matching names.";
            }
            eb.addField("TGH Nations", names);
            interaction.createFollowupMessageBuilder().addEmbed(eb).send();

        } else if (interaction.getOptionByName("nation").isPresent()) {

            NAudit nAudit = MilUtil.getNAudit(interaction.getOptionByName("nation").get().getOptionLongValueByName("id").get());

            //formats
            NumberFormat n = NumberFormat.getCurrencyInstance(Locale.US);
            DecimalFormat d = new DecimalFormat("#,###");

            String discord = userRepository.findUserByNationid(interaction.getOptionByName("nation").get().getOptionLongValueByName("id").get()).getDiscordid();
            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setAuthor(interaction.getUser())
                    .setTitle(nAudit.getName() + " led by " + nAudit.getLeader())
                    .setDescription("Cities: " + nAudit.getCities() + "\n" + nAudit.getAlliance())
                    .addField("Discord", "<@" + discord + ">");

            if (nAudit.getAlliance().equalsIgnoreCase("The Golden Horde")) {
                embedBuilder.addField("Resources", n.format(nAudit.getCash()) + " <:gasoline:1024144774602702868> " + d.format(nAudit.getGas()) + " <:munitions:1024144775668051968> " + d.format(nAudit.getMunitions()) + " <:steel:1024144776548847656> " + d.format(nAudit.getSteel()) + " <:aluminum:1024144777509347348> " + d.format(nAudit.getAluminum()));
            } else {
            }
            embedBuilder.addField("Military", ":military_helmet: " + d.format(nAudit.getSoldiers()) + " :bus: " + d.format(nAudit.getTanks()) + " :airplane: " + d.format(nAudit.getJets()) + " :ship: " + d.format(nAudit.getShips()) + "\n :rocket: " + d.format(nAudit.getMissiles()) + " :radioactive: " + d.format(nAudit.getNukes()))
                    .addField("Nation Link:", "[" + nAudit.getLeader() + " of " + nAudit.getName() + "](https://politicsandwar.com/nation/id=" + nAudit.getId() + ")")
                    .setColor(Color.CYAN)
                    .setFooter("HK-97 Internal Command", interaction.getApi().getYourself().getAvatar());

            interaction.createFollowupMessageBuilder().addEmbed(embedBuilder).send();
        } else if (interaction.getOptionByName("user").isPresent()) {

            interaction.getOptionByName("user").get().getOptionUserValueByName("usertosearch").get();
            System.out.println(interaction.getOptionByName("user").get().getOptionUserValueByName("usertosearch").get());
            User user = userRepository.getUserByDiscordid(interaction.getOptionByName("user").get().getOptionUserValueByName("usertosearch").get().getIdAsString());

            String nation = MilUtil.getNationName(user.getNationid());
            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setTitle("User Search")
                    .addField("" + user.getName(), "[" + nation + "](https://politicsandwar.com/nation/id=" + user.getNationid() + ") (" + user.getNationid() + ")");

            interaction.createFollowupMessageBuilder().addEmbed(embedBuilder).send();
        }
    }
}
