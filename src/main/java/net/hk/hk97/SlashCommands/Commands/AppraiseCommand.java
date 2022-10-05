package net.hk.hk97.SlashCommands.Commands;

import net.hk.hk97.Models.calc.AppraiseCalc;
import net.hk.hk97.Models.calc.graphql.models.charts.MakeChart;
import net.hk.hk97.Models.calc.graphql.repositories.ResourceRepository;
import net.hk.hk97.Repositories.UserRepository;
import net.hk.hk97.Services.Util.MilUtil;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.json.JSONException;

import java.io.IOException;
import java.nio.channels.Channel;
import java.text.DecimalFormat;
import java.util.Optional;

public class AppraiseCommand {
    public static void appraise(SlashCommandInteraction interaction, ResourceRepository resourceDao, UserRepository userRepository) {
        String nation_name = "";
        Optional<TextChannel> channel = interaction.getChannel();


        if (channel.get().getIdAsString().equalsIgnoreCase("1016449238567223406")) {
            interaction.createImmediateResponder().setContent("You are not authorized to use this command here.").setFlags(MessageFlag.EPHEMERAL).respond();

        } else {

            AppraiseCalc appraiseCalc = new AppraiseCalc();


            try {
                if (interaction.getOptionByName("id").isPresent()) {

                    nation_name = MilUtil.getNationName(interaction.getOptionLongValueByName("id").get());

                    try {
                        appraiseCalc.generateAllValues(interaction.getOptionLongValueByName("id").get(), resourceDao);
                    } catch (JSONException | IOException e) {
                        throw new RuntimeException(e);
                    }

                } else if (userRepository.findById(interaction.getUser().getIdAsString()).isPresent()) {
                    appraiseCalc.generateAllValues(userRepository.findById(interaction.getUser().getIdAsString()).get().getNationid(), resourceDao);
                    nation_name = MilUtil.getNationName(userRepository.findById(interaction.getUser().getIdAsString()).get().getNationid());
                }

                DecimalFormat format = new DecimalFormat("##,##,##,##,##,##,##0");

                interaction.createFollowupMessageBuilder().setContent("Estimated total value: $" + format.format(appraiseCalc.totalvalue))
                        .send();

                interaction.createFollowupMessageBuilder().addAttachment(MakeChart.generatePieChart(nation_name + " est. value $" + format.format(appraiseCalc.totalvalue), appraiseCalc.getInfravalue(), appraiseCalc.getLandvalue(), appraiseCalc.getCitiesvalue(), appraiseCalc.getProjectsvalue()))
                        .send();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
