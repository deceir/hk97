package net.hk.hk97.Commands.SlashCommands.Commands;

import net.hk.hk97.Models.Nation;
import net.hk.hk97.Repositories.NationRepository;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;

import java.awt.*;
import java.util.List;

public class WhoCommand {

    public static void getWho(SlashCommandInteraction interaction, NationRepository nationRepository) {

        if (interaction.getOptionByName("leader").isPresent()) {

            List<Nation> list = nationRepository.findNationByLeaderContainingIgnoreCaseAndAlliance(interaction.getOptionByName("leader").get().getOptionStringValueByName("name").get(), "Requiem");

            EmbedBuilder eb = new EmbedBuilder()
                    .setTitle("Leader Search")
                    .setColor(Color.CYAN);

            String names = "";


            System.out.println(names);

            for (Nation nation : list) {
                names += "[" + nation.getLeader() + " of " + nation.getNation() + "](https://politicsandwar.com/nation/id=" + nation.getId() + ") ("  + nation.getId() +")\n";
            }
            if (names.length() == 0) {
                names = "No matching names.";
            }
            eb.addField("Requiem Nations", names);
            interaction.createFollowupMessageBuilder().addEmbed(eb).send();

        }
    }
}
