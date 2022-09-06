package net.hk.hk97.SlashCommands;

import io.github.adorableskullmaster.pw4j.PoliticsAndWar;
import io.github.adorableskullmaster.pw4j.PoliticsAndWarBuilder;
import io.github.adorableskullmaster.pw4j.domains.Nation;
import net.hk.hk97.Config;
import net.hk.hk97.Interview;
import net.hk.hk97.Models.calc.AppraiseCalc;
import net.hk.hk97.Models.calc.graphql.models.charts.MakeChart;
import net.hk.hk97.Models.calc.graphql.repositories.ResourceRepository;
import net.hk.hk97.Repositories.InterviewRepository;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.*;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.PermissionState;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.permission.Permissions;
import org.javacord.api.entity.permission.PermissionsBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
public class SlashCommandHandler implements SlashCommandCreateListener {

    @Autowired
    ResourceRepository resourceDao;

    @Autowired
    InterviewRepository interviewRepository;

    @Override
    public void onSlashCommandCreate(SlashCommandCreateEvent slashCommandCreateEvent) {


        SlashCommandInteraction interaction = slashCommandCreateEvent.getSlashCommandInteraction();

        Optional<TextChannel> channel = interaction.getChannel();



        switch (interaction.getCommandName()) {

            case "appraise":

                System.out.println(interaction.getFirstOptionIntValue());

//                if (!channel.get().getIdAsString().equals("1004918961097424976") || !channel.get().getIdAsString().equals("1016460920970543235")) {
//                    interaction.createImmediateResponder().setContent("You are not allowed to use this command in this channel.").setFlags(MessageFlag.EPHEMERAL).respond();
//                    return;
//                }


                if (channel.get().getIdAsString().equalsIgnoreCase("1016449238567223406")) {
                    interaction.createImmediateResponder().setContent("You are not authorized to use this command here.").setFlags(MessageFlag.EPHEMERAL).respond();
                } else {

                    interaction.respondLater();

                    AppraiseCalc appraiseCalc = new AppraiseCalc();

                    try {

                        try {
                            appraiseCalc.generateAllValues(interaction.getFirstOptionIntValue().get(), resourceDao);
                        } catch (JSONException | IOException e) {
                            throw new RuntimeException(e);
                        }
                        DecimalFormat format = new DecimalFormat("##,##,##,##,##,##,##0");

                        interaction.createFollowupMessageBuilder().setContent("Estimated total value: $" + format.format(appraiseCalc.totalvalue))
                                .send();

                        interaction.createFollowupMessageBuilder().addAttachment(MakeChart.generatePieChart(interaction.getUser().getDiscriminatedName() + " est. value $" + format.format(appraiseCalc.totalvalue), appraiseCalc.getInfravalue(), appraiseCalc.getLandvalue(), appraiseCalc.getCitiesvalue(), appraiseCalc.getProjectsvalue()))
                                .send();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }

            case "apply":

                System.out.println(interaction.getFirstOptionIntValue());

                try {

                    if (channel.get().getIdAsString().equals("1016465107468947559") || channel.get().getIdAsString().equalsIgnoreCase("1016449238567223406")) {

                        if (interviewRepository.findById(interaction.getUser().getId()).isPresent()) {
                            interaction.createImmediateResponder().setContent("Error. You are only allowed a single interview room. Please check your existing room.").setFlags(MessageFlag.EPHEMERAL).respond();
                            return;
                        }

                        interaction.respondLater();

                        int nationId = interaction.getFirstOptionIntValue().get();
//                        System.out.println(nationId + " is the nation id.");

                    DiscordApi api = new DiscordApiBuilder().setToken(Config.discordToken)
                            .setAllNonPrivilegedIntents()
                            .login()
                            .join();
                    ChannelCategory interviewCategory = api.getChannelCategoryById(Config.applicationsChannelId).get();


                        Optional<Server> server = api.getServerById(Config.mainServerId);

                        PoliticsAndWar pnw = new PoliticsAndWarBuilder().setApiKey(Config.itachiPnwKey).build();


                        Nation nation = null;
                        try {
                            nation = pnw.getNation(nationId);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

//                        System.out.println("Nation name is " + nation.getName());


                        List<ServerTextChannel> listOfChannels = server.get().getTextChannelsByName(nation.getNationid() + "-" + nation.getName());

                        boolean channelExists = false;

                        for (ServerTextChannel channel1 : listOfChannels) {
                            if (channel1.getName().equals(nation.getNationid() + "-" + nation.getName())) {
                                channelExists = true;
                            }
                        }


                        Permissions permissions = new PermissionsBuilder().setState(PermissionType.READ_MESSAGES, PermissionState.ALLOWED).build();


                        Channel interview = new ServerTextChannelBuilder(server.get())
                                .setName(nation.getNationid() + "-" + nation.getName())
                                .addPermissionOverwrite(server.get().getEveryoneRole(), new PermissionsBuilder().setDenied(PermissionType.READ_MESSAGES).build())
                                .addPermissionOverwrite(interaction.getUser(), new PermissionsBuilder().setAllowed(PermissionType.READ_MESSAGES).build())
                                .addPermissionOverwrite(server.get().getRoleById("1016528192447717407").get(), new PermissionsBuilder().setAllowed(PermissionType.READ_MESSAGES).build())
                                .addPermissionOverwrite(server.get().getRoleById("1016448673825161327").get(), new PermissionsBuilder().setAllowed(PermissionType.READ_MESSAGES).build())
                                .setCategory(interviewCategory).create().join();

                        EmbedBuilder eb = new EmbedBuilder()
                                .setAuthor(interaction.getUser())
                                .setDescription("[" + nation.getName() + " led by  " + nation.getLeadername() + "](https://politicsandwar.com/nation/id=" + nation.getNationid() + ")")
                                .addInlineField("City Count", nation.getCities() + "")
                                .addInlineField("Score", nation.getScore() + "")
                                .addInlineField("Alliance", nation.getAlliance())
                                .addField("Militarization", ":military_helmet: " + nation.getSoldiers() + " :bus: " + nation.getTanks() + " :airplane: " + nation.getAircraft() + " :ship: " + nation.getShips())
                                .addField("WMD's", ":rocket: " + nation.getMissiles() + " :radioactive: " + nation.getNukes())

                                .setFooter("HK-97 Internal Command");

                        interview.asTextChannel().get().sendMessage(eb);

                        interaction.createFollowupMessageBuilder().setContent("Your application room has been created, <@" + interaction.getUser().getIdAsString() + ">.").send();

                        try {
                            TimeUnit.SECONDS.sleep(2);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }


                        interview.asTextChannel().get().sendMessage("Thank you for applying, <@" + interaction.getUser().getId() + ">! Please refrain from declaring any offensive wars at this time! Let us know when you are ready to begin!");

                        Interview interview1 = new Interview();
                        interview1.setId(interaction.getUser().getId());
                        interview1.setActive(true);
                        interview1.setChannelId(interview.getId());
                        interviewRepository.save(interview1);


                    } else {
                        interaction.createImmediateResponder().setContent("This command cannot be used in this channel.").setFlags(MessageFlag.EPHEMERAL).respond();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
    }
}
