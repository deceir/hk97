package net.hk.hk97.SlashCommands;

import io.github.adorableskullmaster.pw4j.PoliticsAndWar;
import io.github.adorableskullmaster.pw4j.PoliticsAndWarBuilder;
import io.github.adorableskullmaster.pw4j.domains.Nation;
import net.hk.hk97.Config;
import net.hk.hk97.Hk97Application;
import net.hk.hk97.Interview;
import net.hk.hk97.Models.ActivityAudit;
import net.hk.hk97.Models.User;
import net.hk.hk97.Models.calc.AppraiseCalc;
import net.hk.hk97.Models.calc.CityCalc;
import net.hk.hk97.Models.calc.InfraCalc;
import net.hk.hk97.Models.calc.LandCalc;
import net.hk.hk97.Models.calc.graphql.models.charts.MakeChart;
import net.hk.hk97.Models.calc.graphql.repositories.ResourceRepository;
import net.hk.hk97.Models.message.Messenger;
import net.hk.hk97.Repositories.InterviewRepository;
import net.hk.hk97.Repositories.UserRepository;
import net.hk.hk97.Services.Util.AuditUtil;
import net.hk.hk97.Services.Util.MilUtil;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.*;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.MessageSet;
import org.javacord.api.entity.message.embed.Embed;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.PermissionState;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.permission.Permissions;
import org.javacord.api.entity.permission.PermissionsBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Component
public class SlashCommandHandler implements SlashCommandCreateListener {

    @Autowired
    ResourceRepository resourceDao;

    @Autowired
    InterviewRepository interviewRepository;

    @Autowired
    UserRepository userRepository;




    @Override
    public void onSlashCommandCreate(SlashCommandCreateEvent slashCommandCreateEvent) {



        SlashCommandInteraction interaction = slashCommandCreateEvent.getSlashCommandInteraction();

        Optional<TextChannel> channel = interaction.getChannel();


        switch (interaction.getCommandName()) {

            case "appraise":

                interaction.respondLater();
                String nation_name = "";


                if (channel.get().getIdAsString().equalsIgnoreCase("1016449238567223406")) {
                    interaction.createImmediateResponder().setContent("You are not authorized to use this command here.").setFlags(MessageFlag.EPHEMERAL).respond();

                } else {

                    AppraiseCalc appraiseCalc = new AppraiseCalc();



                    try {
                        if (interaction.getFirstOptionIntValue().isPresent()) {

                            nation_name = MilUtil.getNationName(interaction.getFirstOptionIntValue().get());

                            try {
                                appraiseCalc.generateAllValues(interaction.getFirstOptionIntValue().get(), resourceDao);
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


                        DiscordApi api = slashCommandCreateEvent.getApi();

                        ChannelCategory interviewCategory = api.getChannelCategoryById(Config.applicationsChannelId).get();


                        Optional<Server> server = api.getServerById(Config.mainServerId);

                        PoliticsAndWar pnw = new PoliticsAndWarBuilder().setApiKey(Config.itachiPnwKey).build();


                        Nation nation = null;

                        try {
                            nation = pnw.getNation(nationId);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }


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

                        interview.asTextChannel().get().sendMessage(eb).get().pin();
                        MessageSet list = interview.asServerTextChannel().get().getMessages(0).get();
                        list.getOldestMessage().get().pin();

                        interaction.createFollowupMessageBuilder().setContent("Your application room has been created, <@" + interaction.getUser().getIdAsString() + ">. <#" + interview.getIdAsString() + ">").setFlags(MessageFlag.EPHEMERAL).send();

                        try {
                            TimeUnit.SECONDS.sleep(2);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }


                        interview.asTextChannel().get().sendMessage("Hello and thank you for applying, <@" + interaction.getUser().getId() + ">! Please refrain from declaring any offensive wars at this time. Let us know when you are ready to begin the interview.");

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

            case "verify":

                interaction.respondLater();

                userRepository.findById(interaction.getUser().getIdAsString());
                if (!userRepository.findById(interaction.getUser().getIdAsString()).get().isRegistered()) {

                    User user = userRepository.findById(interaction.getUser().getIdAsString()).get();

                    if ((interaction.getFirstOptionIntValue().get() == user.getVerification())) {
                        user.setRegistered(true);
                        userRepository.save(user);
                        interaction.createFollowupMessageBuilder().setContent("Your account has been registered.").send();

                    } else {
                        interaction.createFollowupMessageBuilder().setContent("You have entered the incorrect verification code.").send();
                    }

                }

            case "calc":

                interaction.respondLater();


                EmbedBuilder eb = new EmbedBuilder();

                if (interaction.getOptionByName("infra").isPresent()) {
                    InfraCalc calc = new InfraCalc();


                    if (interaction.getOptionByName("infra").get().getOptionByName("cities").isPresent()) {

                        System.out.println("cities is present");


                        int starting_infra = interaction.getOptionByName("infra").get().getOptionByName("start").get().getIntValue().get();
                        System.out.println(starting_infra + " starting infra");
                        int stopping_infra = interaction.getOptionByName("infra").get().getOptionByName("end").get().getIntValue().get();
                        int cities = interaction.getOptionByName("infra").get().getOptionByName("cities").get().getIntValue().get();
                        System.out.println("cities " + cities);
                        calc.calculateInfra(starting_infra, stopping_infra, cities);
                        calc.formatCost();


                        eb.setAuthor(interaction.getUser())
                                .setTitle("Infra cost for " + starting_infra + " to " + stopping_infra + " in " + cities + " cities")
                                .setAuthor(interaction.getUser())
                                .addField("Base Cost", calc.getBase_cost_f(), true)
                                .addInlineField("1 Cost", calc.getOne_cost_f() + "\n saving: " + calc.getOne_cost_saved_f())
                                .addInlineField("2 Cost", calc.getTwo_cost_f() + "\n saving: " + calc.getTwo_cost_saved_f())
                                .addInlineField("AEC Cost", calc.getAec_cost_f() + "\n saving: " + calc.getAec_cost_saved_f())
                                .addInlineField("GSA Cost", calc.getGsa_cost_f() + "\n saving: " + calc.getGsa_cost_saved_f())
                                .setColor(Color.orange);

                        interaction.createFollowupMessageBuilder().addEmbed(eb).send();


                    } else {
                        int starting_infra = interaction.getOptionByName("infra").get().getOptionByName("start").get().getIntValue().get();
                        System.out.println(starting_infra + " starting infra");
                        int stopping_infra = interaction.getOptionByName("infra").get().getOptionByName("end").get().getIntValue().get();
                        System.out.println(stopping_infra + " ending infra");

                        calc.calculateInfra(starting_infra, stopping_infra);
                        calc.formatCost();



                        interaction.createFollowupMessageBuilder().addEmbed(new EmbedBuilder()
                                .setTitle("Infra cost for " + starting_infra + " to " + stopping_infra)
                                .addField("Base Cost", calc.getBase_cost_f(), true)
                                .setAuthor(interaction.getUser())
                                .addInlineField("1 Cost", calc.getOne_cost_f() + "\n saving: " + calc.getOne_cost_saved_f())
                                .addInlineField("2 Cost", calc.getTwo_cost_f() + "\n saving: " + calc.getTwo_cost_saved_f())
                                .addInlineField("AEC Cost", calc.getAec_cost_f() + "\n saving: " + calc.getAec_cost_saved_f())
                                .addInlineField("GSA Cost", calc.getGsa_cost_f() + "\n saving: " + calc.getGsa_cost_saved_f())
                                .setColor(Color.orange)
                        ).send();
                        System.out.println("Embed sent.");


                    }

                } else if (interaction.getOptionByName("land").isPresent()) {

                    LandCalc calc = new LandCalc();

                    if (interaction.getOptionByName("land").get().getOptionByName("cities").isPresent()) {




                        int starting_infra = interaction.getOptionByName("land").get().getOptionByName("start").get().getIntValue().get();
                        int stopping_infra = interaction.getOptionByName("land").get().getOptionByName("end").get().getIntValue().get();
                        int cities = interaction.getOptionByName("land").get().getOptionByName("cities").get().getIntValue().get();
                        System.out.println("cities " + cities);
                        calc.calculateLand(starting_infra, stopping_infra, cities);
                        calc.formatCost();


                        eb.setAuthor(interaction.getUser())
                                .setTitle("Land cost for " + starting_infra + " to " + stopping_infra + " in " + cities + " cities")
                                .setAuthor(interaction.getUser())
                                .addField("Base Cost", calc.getBase_cost_f(), true)
                                .addInlineField("RE/ALA Cost", calc.getOne_cost_f() + "\n saving: " + calc.getOne_cost_saved_f())
                                .addInlineField("RE+ALA Cost", calc.getTwo_cost_f() + "\n saving: " + calc.getTwo_cost_saved_f())
                                .addInlineField("AEC Cost", calc.getAec_cost_f() + "\n saving: " + calc.getAec_cost_saved_f())
                                .addInlineField("GSA Cost", calc.getGsa_cost_f() + "\n saving: " + calc.getGsa_cost_saved_f())
                                .setColor(Color.orange);

                        interaction.createFollowupMessageBuilder().addEmbed(eb).send();


                    } else {
                        int starting_infra = interaction.getOptionByName("land").get().getOptionByName("start").get().getIntValue().get();
                        int stopping_infra = interaction.getOptionByName("land").get().getOptionByName("end").get().getIntValue().get();

                        calc.calculateLand(starting_infra, stopping_infra);
                        calc.formatCost();


                        interaction.createFollowupMessageBuilder().addEmbed(new EmbedBuilder()
                                .setTitle("Land cost for " + starting_infra + " to " + stopping_infra)
                                .addField("Base Cost", calc.getBase_cost_f(), true)
                                .setAuthor(interaction.getUser())
                                .addField("Base Cost", calc.getBase_cost_f(), true)
                                .addInlineField("RE/ALA Cost", calc.getOne_cost_f() + "\n saving: " + calc.getOne_cost_saved_f())
                                .addInlineField("RE+ALA Cost", calc.getTwo_cost_f() + "\n saving: " + calc.getTwo_cost_saved_f())
                                .addInlineField("AEC Cost", calc.getAec_cost_f() + "\n saving: " + calc.getAec_cost_saved_f())
                                .addInlineField("GSA Cost", calc.getGsa_cost_f() + "\n saving: " + calc.getGsa_cost_saved_f())
                                .setColor(Color.orange)
                        ).send();
                        System.out.println("Embed sent.");
                    }

                } else if (interaction.getOptionByName("cities").isPresent()) {
                    System.out.println("Cities calc invoked.");

                    CityCalc calc = new CityCalc();
                    if (interaction.getOptionByName("cities").get().getOptionByName("end").isPresent()) {


                        int start = interaction.getOptionByName("cities").get().getOptionByName("start").get().getIntValue().get();
                        int end = interaction.getOptionByName("cities").get().getOptionByName("end").get().getIntValue().get();

                        if (start > end) {
                            interaction.createFollowupMessageBuilder().setContent("You have formatted the command improperly. Your start city should be your current city, your end city should be the city you are buying up to.").send();
                        } else {
                            calc.calculateCity(start, end);
                            calc.formatCost();

                            eb
                                    .setTitle("The cost to get city " + start + " to city " + end)
                                    .setColor(Color.ORANGE)
                                    .setAuthor(interaction.getUser())
                                    .addField("Base Cost", calc.getBase_cost_formatted())
                                    .addInlineField("MD Cost", calc.getMd_cost_formatted() + "\n saving: " + calc.getMd_cost_saved_f())
                                    .addInlineField("UP + MD Cost", calc.getUp_cost_f() + "\n saving: " + calc.getUp_md_saved_f())
                                    .addInlineField("AUP + UP + MD Cost", calc.getAup_md_cost_f() + "\n saving: " + calc.getAup_md_saved_f())
                                    .addInlineField("GSA MD Cost", calc.getGsa_cost_f() + "\n saving: " + calc.getGsa_saved_f())
                                    .addInlineField("GSA UP + MD Cost", calc.getGsa_up_cost_f() + "\n saving: " + calc.getGsa_up_saved_f())
                                    .addInlineField("GSA AUP + UP + MD Cost", calc.getGsa_aup_cost_f() + "\n saving: " + calc.getGsa_aup_saved_f())
                                    .addField("Min. Cost (MP + all other reductions)", calc.getMin_cost_f() + "\n saving: " + calc.getMin_cost_saved_f());

                            interaction.createFollowupMessageBuilder().addEmbed(eb).send();

                        }
                    } else {
                        // single city cost
                        int start = interaction.getOptionByName("cities").get().getOptionByName("start").get().getIntValue().get();

                        calc.calculateCity(start);
                        calc.formatCost();

                        eb
                                .setTitle("The cost to get city " + start + " has been calculated.")
                                .setAuthor(interaction.getUser())
                                .setColor(Color.orange)
                                .addField("Base Cost", calc.getBase_cost_formatted())
                                .addInlineField("MD Cost", calc.getMd_cost_formatted() + "\n saving: " + calc.getMd_cost_saved_f())
                                .addInlineField("UP + MD Cost", calc.getUp_cost_f() + "\n saving: " + calc.getUp_md_saved_f())
                                .addInlineField("AUP + UP + MD Cost", calc.getAup_md_cost_f() + "\n saving: " + calc.getAup_md_saved_f())
                                .addInlineField("GSA MD Cost", calc.getGsa_cost_f() + "\n saving: " + calc.getGsa_saved_f())
                                .addInlineField("GSA UP + MD Cost", calc.getGsa_up_cost_f() + "\n saving: " + calc.getGsa_up_saved_f())
                                .addInlineField("GSA AUP + UP + MD Cost", calc.getGsa_aup_cost_f() + "\n saving: " + calc.getGsa_aup_saved_f())
                                .addField("Min. Cost (MP + all other reductions)", calc.getMin_cost_f() + "\n saving: " + calc.getMin_cost_saved_f());

                        interaction.createFollowupMessageBuilder().addEmbed(eb).send();

                    }

                }


            case "audit" :

                interaction.respondLater();

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

                // new case goes here

        }
    }
}
