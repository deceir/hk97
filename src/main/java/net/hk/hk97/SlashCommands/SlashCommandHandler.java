package net.hk.hk97.SlashCommands;

import io.github.adorableskullmaster.pw4j.PoliticsAndWar;
import io.github.adorableskullmaster.pw4j.PoliticsAndWarBuilder;
import io.github.adorableskullmaster.pw4j.domains.Nation;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.hk.hk97.Config;
import net.hk.hk97.Hk97Application;
import net.hk.hk97.Interview;
import net.hk.hk97.Models.ActivityAudit;
import net.hk.hk97.Models.Bank;
import net.hk.hk97.Models.User;
import net.hk.hk97.Models.Withdrawal;
import net.hk.hk97.Models.calc.AppraiseCalc;
import net.hk.hk97.Models.calc.CityCalc;
import net.hk.hk97.Models.calc.InfraCalc;
import net.hk.hk97.Models.calc.LandCalc;
import net.hk.hk97.Models.calc.graphql.models.charts.MakeChart;
import net.hk.hk97.Models.calc.graphql.repositories.ResourceRepository;
import net.hk.hk97.Models.message.Messenger;
import net.hk.hk97.Repositories.BankRepository;
import net.hk.hk97.Repositories.InterviewRepository;
import net.hk.hk97.Repositories.UserRepository;
import net.hk.hk97.Repositories.WithdrawalRepository;
import net.hk.hk97.Services.Util.AuditUtil;
import net.hk.hk97.Services.Util.BankUtil;
import net.hk.hk97.Services.Util.MilUtil;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.*;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.MessageSet;
import org.javacord.api.entity.message.embed.Embed;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.*;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;
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

    @Autowired
    BankRepository bankDao;

    @Autowired
    WithdrawalRepository withdrawalRepository;


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
                    break;
                }


            case "apply":

                System.out.println(interaction.getOptionLongValueByName("id").get());

                try {

                    if (channel.get().getIdAsString().equals("1016465107468947559") || channel.get().getIdAsString().equalsIgnoreCase("1016449238567223406")) {

                        if (interviewRepository.findById(interaction.getUser().getId()).isPresent()) {
                            interaction.createImmediateResponder().setContent("Error. You are only allowed a single interview room. Please check your existing room.").setFlags(MessageFlag.EPHEMERAL).respond();
                            return;
                        }

                        interaction.respondLater();

                        int nationId = Integer.parseInt(interaction.getOptionLongValueByName("id").get() + "");


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
                                .addPermissionOverwrite(server.get().getEveryoneRole(), new PermissionsBuilder().setDenied(PermissionType.VIEW_CHANNEL).build())
                                .addPermissionOverwrite(interaction.getUser(), new PermissionsBuilder().setAllowed(PermissionType.VIEW_CHANNEL).build())
                                .addPermissionOverwrite(server.get().getRoleById("1016528192447717407").get(), new PermissionsBuilder().setAllowed(PermissionType.VIEW_CHANNEL).build())
                                .addPermissionOverwrite(server.get().getRoleById("1016448673825161327").get(), new PermissionsBuilder().setAllowed(PermissionType.VIEW_CHANNEL).build())
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
                break;


            case "verify":

                interaction.respondLater();

                userRepository.findById(interaction.getUser().getIdAsString());
                if (!userRepository.findById(interaction.getUser().getIdAsString()).get().isRegistered()) {

                    User user = userRepository.findById(interaction.getUser().getIdAsString()).get();

                    if ((interaction.getOptionByName("").get().equals(user.getVerification()))) {
                        user.setRegistered(true);
                        userRepository.save(user);
                        interaction.createFollowupMessageBuilder().setContent("Your account has been registered.").send();

                    } else {
                        interaction.createFollowupMessageBuilder().setContent("You have entered the incorrect verification code.").send();
                    }

                }
                break;


            case "calc":

                interaction.respondLater();


                EmbedBuilder eb = new EmbedBuilder();

                if (interaction.getOptionByName("infra").isPresent()) {
                    InfraCalc calc = new InfraCalc();


                    if (interaction.getOptionByName("infra").get().getOptionByName("cities").isPresent()) {

                        System.out.println("cities is present");


                        long starting_infra = interaction.getOptionByName("infra").get().getOptionByName("start").get().getLongValue().get();
                        System.out.println(starting_infra + " starting infra");
                        long stopping_infra = interaction.getOptionByName("infra").get().getOptionByName("end").get().getLongValue().get();
                        long cities = interaction.getOptionByName("infra").get().getOptionByName("cities").get().getLongValue().get();
                        System.out.println("cities " + cities);
                        calc.calculateInfra((int) starting_infra, (int) stopping_infra, (int) cities);
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
                        long starting_infra = interaction.getOptionByName("infra").get().getOptionByName("start").get().getLongValue().get();
                        System.out.println(starting_infra + " starting infra");
                        long stopping_infra = interaction.getOptionByName("infra").get().getOptionByName("end").get().getLongValue().get();
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


                        long starting_infra = interaction.getOptionByName("land").get().getOptionByName("start").get().getLongValue().get();
                        long stopping_infra = interaction.getOptionByName("land").get().getOptionByName("end").get().getLongValue().get();
                        long cities = interaction.getOptionByName("land").get().getOptionByName("cities").get().getLongValue().get();
                        System.out.println("cities " + cities);
                        calc.calculateLand((int) starting_infra, (int) stopping_infra, (int) cities);
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
                        long starting_infra = interaction.getOptionByName("land").get().getOptionByName("start").get().getLongValue().get();
                        long stopping_infra = interaction.getOptionByName("land").get().getOptionByName("end").get().getLongValue().get();

                        calc.calculateLand((int) starting_infra, (int) stopping_infra);
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


                        long start = interaction.getOptionByName("cities").get().getOptionByName("start").get().getLongValue().get();
                        long end = interaction.getOptionByName("cities").get().getOptionByName("end").get().getLongValue().get();

                        if (start > end) {
                            interaction.createFollowupMessageBuilder().setContent("You have formatted the command improperly. Your start city should be your current city, your end city should be the city you are buying up to.").send();
                        } else {
                            calc.calculateCity((int) start, (int) end);
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
                        long start = interaction.getOptionByName("cities").get().getOptionByName("start").get().getLongValue().get();

                        calc.calculateCity((int) start);
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

                break;

            case "audit":

                interaction.createImmediateResponder().setFlags(MessageFlag.EPHEMERAL).respond();

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

                break;

            case "account":
                interaction.respondLater();
                if (interaction.getOptionByName("register").isPresent()) {
                    //initial registration

                    SlashCommandInteractionOption option = interaction.getOptionByName("register").get();

                    if (userRepository.findById(interaction.getUser().getIdAsString()).isPresent()) {
                        interaction.createFollowupMessageBuilder().setContent("An account with your discord id already exists. Verify using `/account verify` if you have not already, otherwise your account is completed!").send();
                    } else {

                        try {
                            String leaderName = MilUtil.getLeaderName(option.getOptionLongValueByName("id").get());
                            User user = new User();
                            user.setName(interaction.getUser().getName());
                            user.setDiscordid(interaction.getUser().getIdAsString());
                            user.setNationid(option.getOptionLongValueByName("id").get());
                            userRepository.save(user);

                            Messenger.sendMessage(option.getOptionLongValueByName("id").get(), "HK-97 Meatbag Verification Protocol", ("Your HK-97 verification code is: " + user.getVerification()));

                            interaction.createFollowupMessageBuilder().setContent("Your account has been created. \nA DM with your verification code has been sent in-game. Use that token with `/account verify` to complete your registration.").send();
                        } catch (Exception e) {
                            interaction.createFollowupMessageBuilder().setContent("The id you have entered has returned invalid. Please try again with the correct id.").send();
                        }
                    }
                } else if (interaction.getOptionByName("verify").isPresent()) {
                    //using token
                    SlashCommandInteractionOption option = interaction.getOptionByName("verify").get();
                    User user = userRepository.findById(interaction.getUser().getIdAsString()).get();
                    if (option.getOptionLongValueByName("token").get() == user.getVerification()) {
                        user.setRegistered(true);
                        userRepository.save(user);
                        interaction.createFollowupMessageBuilder().setContent("You have successfully verified your account.").send();
                    } else {
                        interaction.createFollowupMessageBuilder().setContent("Your token is incorrect.").send();
                    }

                }

            case "bank":
                interaction.respondLater();

                if (!userRepository.findById(interaction.getUser().getIdAsString()).isPresent()) {
                    interaction.createFollowupMessageBuilder().setContent("You do not have an HK account. Please use `/account register` before trying to use the bank.").send();

                } else {

                    if (interaction.getOptionByName("create").isPresent()) {

                        List<Bank> listOfAccounts = bankDao.findBanksByDiscordid(interaction.getUser().getIdAsString());
                        if (!(listOfAccounts.size() > 0)) {

                            Bank newAccount = new Bank();
                            newAccount.setDiscordid(interaction.getUser().getIdAsString());
                            bankDao.save(newAccount);
                            interaction.createFollowupMessageBuilder().setContent("Account created.").send();

                        } else {
                            interaction.createFollowupMessageBuilder().setContent("You already have an account. You cannot create another.").send();
                        }

                    } else if (interaction.getOptionByName("info").isPresent()) {
                        try {

                            List<Bank> listOfAccounts = bankDao.findBanksByDiscordid(interaction.getUser().getIdAsString());
                            User user = userRepository.findById(interaction.getUser().getIdAsString()).get();


                            NumberFormat n = NumberFormat.getCurrencyInstance(Locale.US);
                            DecimalFormat d = new DecimalFormat("#,###");

                            Bank b = listOfAccounts.get(0);

                            EmbedBuilder emb = new EmbedBuilder()
                                    .setTitle("Requiem Strongbox Services")
                                    .setColor(Color.black)
                                    .setAuthor(interaction.getUser())
                                    .addField("Deposit Code: ", "`" + listOfAccounts.get(0).getDepositcode() + "`")
                                    .addField("Totals:",
                                            n.format(b.getCash()) + " \n<:food:915071870636789792> " + d.format(b.getFood()) + " <:uranium:1024144769871523870> " + d.format(b.getUranium()) + " <:coal:1024144767858266222> " + d.format(b.getCoal()) + " <:oil:1024144768487391303> " + d.format(b.getOil()) + " <:lead:1024144770857177119> " + d.format(b.getLeadRss()) + " <:iron:1024144771884793918> " + d.format(b.getIron()) + " <:bauxite:1024144773075976243> " + d.format(b.getBauxite()) + " <:gasoline:1024144774602702868> " + d.format(b.getGasoline()) + " <:munitions:1024144775668051968> " + d.format(b.getMunitions()) + " <:steel:1024144776548847656> " + d.format(b.getSteel()) + " <:aluminum:1024144777509347348> " + d.format(b.getAluminum())
                                    );


                            interaction.createFollowupMessageBuilder().addEmbed(emb).send();
                            interaction.createFollowupMessageBuilder().setContent("Deposit code:").send();
                            interaction.createFollowupMessageBuilder().setContent(b.getDepositcode()).send();

                        } catch (Exception e) {
                            interaction.createFollowupMessageBuilder().setContent("There was an error. " + e).send();
                        }
                    } else if (interaction.getOptionByName("deposit").isPresent()) {
                        try {
                            User user = userRepository.findById(interaction.getUser().getIdAsString()).get();
                            Bank bank = bankDao.findByDiscordid(interaction.getUser().getIdAsString());
                            Bank deposits = BankUtil.getTransactions(user.getNationid(), bank.getDepositcode());

                            if (deposits.getTotals() == 0) {
                                interaction.createFollowupMessageBuilder().setContent("Deposit code:").send();
                                interaction.createFollowupMessageBuilder().setContent(bank.getDepositcode());
                            } else {
                                bank.setCash(bank.getCash() + deposits.getCash());
                                bank.setFood(bank.getFood() + deposits.getFood());
                                bank.setIron(bank.getIron() + deposits.getIron());
                                bank.setOil(bank.getOil() + deposits.getOil());
                                bank.setUranium(bank.getUranium() + deposits.getUranium());
                                bank.setLeadRss(bank.getLeadRss() + deposits.getLeadRss());
                                bank.setBauxite(bank.getBauxite() + deposits.getBauxite());
                                bank.setGasoline(bank.getGasoline() + deposits.getGasoline());
                                bank.setMunitions(bank.getMunitions() + deposits.getMunitions());
                                bank.setSteel(bank.getSteel() + deposits.getSteel());
                                bank.setAluminum(bank.getAluminum() + deposits.getAluminum());
                                bank.updateDepositCode();
                                bankDao.save(bank);
                                interaction.createFollowupMessageBuilder().setContent("Deposit recorded successfully.").send();
                            }


                        } catch (Exception e) {
                            interaction.createFollowupMessageBuilder().setContent("There was an error. " + e).send();
                            e.printStackTrace();
                        }
                    } else if (interaction.getOptionByName("withdraw").isPresent()) {

                        if (!withdrawalRepository.findById(interaction.getUser().getIdAsString()).isPresent()) {

                            SlashCommandInteractionOption option = interaction.getOptionByName("withdraw").get();

                            try {
                                long cash = 0;
                                long food = 0;
                                long oil = 0;
                                long uranium = 0;
                                long lead = 0;
                                long iron = 0;
                                long bauxite = 0;
                                long gasoline = 0;
                                long munitions = 0;
                                long steel = 0;
                                long aluminum = 0;
                                long coal = 0;

                                if (option.getOptionLongValueByName("cash").isPresent()) {
                                    cash = option.getOptionLongValueByName("cash").get();
                                }
                                if (option.getOptionLongValueByName("food").isPresent()) {
                                    food = option.getOptionLongValueByName("food").get();
                                }
                                if (option.getOptionLongValueByName("oil").isPresent()) {
                                    oil = option.getOptionLongValueByName("oil").get();
                                }
                                if (option.getOptionLongValueByName("uranium").isPresent()) {
                                    uranium = option.getOptionLongValueByName("uranium").get();
                                }
                                if (option.getOptionLongValueByName("lead").isPresent()) {
                                    lead = option.getOptionLongValueByName("lead").get();
                                }
                                if (option.getOptionLongValueByName("iron").isPresent()) {
                                    iron = option.getOptionLongValueByName("iron").get();
                                }
                                if (option.getOptionLongValueByName("bauxite").isPresent()) {
                                    bauxite = option.getOptionLongValueByName("bauxite").get();
                                }
                                if (option.getOptionLongValueByName("gasoline").isPresent()) {
                                    gasoline = option.getOptionLongValueByName("gasoline").get();
                                }
                                if (option.getOptionLongValueByName("munitions").isPresent()) {
                                    munitions = option.getOptionLongValueByName("munitions").get();
                                }
                                if (option.getOptionLongValueByName("steel").isPresent()) {
                                    steel = option.getOptionLongValueByName("steel").get();
                                }
                                if (option.getOptionLongValueByName("aluminum").isPresent()) {
                                    aluminum = option.getOptionLongValueByName("aluminum").get();
                                }
                                if (option.getOptionLongValueByName("coal").isPresent()) {
                                    coal = option.getOptionLongValueByName("coal").get();
                                }

                                String errorLog = "";

                                Bank bank = bankDao.findByDiscordid(interaction.getUser().getIdAsString());

                                if (cash > bank.getCash()) {
                                    errorLog += "You do not have enough cash for this withdrawal. \n";
                                }
                                if (food > bank.getFood()) {
                                    errorLog += "You do not have enough food for this withdrawal. \n";
                                }
                                if (aluminum > bank.getAluminum()) {
                                    errorLog += "You do not have enough aluminum for this withdrawal. \n";
                                }
                                if (bauxite > bank.getBauxite()) {
                                    errorLog += "You do not have enough bauxite for this withdrawal. \n";
                                }
                                if (iron > bank.getIron()) {
                                    errorLog += "You do not have enough iron for this withdrawal. \n";
                                }
                                if (coal > bank.getCoal()) {
                                    errorLog += "You do not have enough coal for this withdrawal. \n";
                                }
                                if (oil > bank.getOil()) {
                                    errorLog += "You do not have enough oil for this withdrawal. \n";
                                }
                                if (gasoline > bank.getGasoline()) {
                                    errorLog += "You do not have enough gasoline for this withdrawal. \n";
                                }
                                if (steel > bank.getSteel()) {
                                    errorLog += "You do not have enough steel for this withdrawal. \n";
                                }
                                if (uranium > bank.getUranium()) {
                                    errorLog += "You do not have enough uranium for this withdrawal. \n";
                                }
                                if (lead > bank.getLeadRss()) {
                                    errorLog += "You do not have enough lead for this withdrawal. \n";
                                }

                                if (cash < 0) {
                                    errorLog += "Withdrawal amount cannot be zero or negative. \n";
                                }
                                if (food < 0) {
                                    errorLog += "Withdrawal amount cannot be zero or negative. \n";
                                }
                                if (aluminum < 0) {
                                    errorLog += "Withdrawal amount cannot be zero or negative. \n";
                                }
                                if (bauxite < 0) {
                                    errorLog += "Withdrawal amount cannot be zero or negative. \n";
                                }
                                if (iron < 0) {
                                    errorLog += "Withdrawal amount cannot be zero or negative. \n";
                                }
                                if (coal < 0) {
                                    errorLog += "Withdrawal amount cannot be zero or negative. \n";
                                }
                                if (oil < 0) {
                                    errorLog += "Withdrawal amount cannot be zero or negative. \n";
                                }
                                if (gasoline < 0) {
                                    errorLog += "Withdrawal amount cannot be zero or negative. \n";
                                }
                                if (steel < 0) {
                                    errorLog += "Withdrawal amount cannot be zero or negative. \n";
                                }
                                if (uranium < 0) {
                                    errorLog += "Withdrawal amount cannot be zero or negative. \n";
                                }
                                if (lead < 0) {
                                    errorLog += "Withdrawal amount cannot be zero or negative. \n";
                                }

                                if (errorLog.length() > 0) {
                                    interaction.createFollowupMessageBuilder().setContent(errorLog).send();
                                } else {

                                    Withdrawal b = new Withdrawal();
                                    b.setCash(cash);
                                    b.setFood(food);
                                    b.setAluminum(aluminum);
                                    b.setBauxite(bauxite);
                                    b.setIron(iron);
                                    b.setCoal(coal);
                                    b.setOil(oil);
                                    b.setGasoline(gasoline);
                                    b.setSteel(steel);
                                    b.setUranium(uranium);
                                    b.setLeadRss(lead);

                                    b.setDiscordid(interaction.getUser().getIdAsString());

                                    withdrawalRepository.save(b);

                                    //creating embed
                                    NumberFormat n = NumberFormat.getCurrencyInstance(Locale.US);
                                    DecimalFormat d = new DecimalFormat("#,###");

                                    String nationName = MilUtil.getNationName(userRepository.findById(interaction.getUser().getIdAsString()).get().getNationid());

                                    String withString = "[Requiem Bank](https://politicsandwar.com/alliance/id=10470&display=bank&w_money=" + cash + "&w_food=" + food + "&w_coal=" + coal + "&w_oil=" + oil + "&w_uranium=" + uranium + "&w_lead=" + lead + "&w_iron=" + iron + "&w_bauxite=" + bauxite + "&w_gasoline=" + gasoline + "&w_munitions=" + munitions + "&w_steel=" + steel + "&w_aluminum=" + aluminum + "&w_note=" + b.getDepositcode() + "&w_type=nation&w_recipient=" + nationName + ")";

                                    EmbedBuilder emb = new EmbedBuilder()
                                            .setTitle("Requiem Strongbox Services")
                                            .setDescription("Withdrawal from " + interaction.getUser().getNicknameMentionTag() + " at " + LocalTime.now())
                                            .setColor(Color.black)
                                            .setAuthor(interaction.getUser())
                                            .addField("Totals:",
                                                    n.format(b.getCash()) + " \n<:food:915071870636789792> " + d.format(b.getFood()) + " <:uranium:1024144769871523870> " + d.format(b.getUranium()) + " <:coal:1024144767858266222> " + d.format(b.getCoal()) + " <:oil:1024144768487391303> " + d.format(b.getOil()) + " <:lead:1024144770857177119> " + d.format(b.getLeadRss()) + " <:iron:1024144771884793918> " + d.format(b.getIron()) + " <:bauxite:1024144773075976243> " + d.format(b.getBauxite()) + " <:gasoline:1024144774602702868> " + d.format(b.getGasoline()) + " <:munitions:1024144775668051968> " + d.format(b.getMunitions()) + " <:steel:1024144776548847656> " + d.format(b.getSteel()) + " <:aluminum:1024144777509347348> " + d.format(b.getAluminum()))
                                            .addField("Bank", withString);

                                    Role role = interaction.getApi().getRoleById("1024324511962763347").get();
                                    ServerTextChannel channel1 = interaction.getApi().getServerTextChannelById("1016460984103219281").get();
                                    channel1.sendMessage("Withdrawal request: ");
                                    channel1.sendMessage(b.getDepositcode());
                                    channel1.sendMessage(emb);
                                    channel1.sendMessage(role.getMentionTag());


                                    interaction.createFollowupMessageBuilder().setContent("Withdrawal request submitted. Please wait for the Econ Department to process your request.").send();


                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            interaction.createFollowupMessageBuilder().setContent("You must wait for your previous withdrawal to be completed before submitting another.").send();
                        }
                    }

                }


            case "badmin":

                interaction.respondLater();

                org.javacord.api.entity.user.User user = interaction.getUser();
                List<Role> roles = user.getRoles(interaction.getApi().getServerById(Config.mainServerId).get());
                boolean isAdmin = false;
                for (Role role : roles) {
                    if (role.getIdAsString().equals("1024324511962763347")) {
                        isAdmin = true;
                    }
                }

                if (interaction.getCommandName().equals("badmin") && !isAdmin) {
                    interaction.createFollowupMessageBuilder().setContent("You are not authorized to use this command.").send();
                } else {


                    if (interaction.getOptionByName("pending").isPresent()) {


                        List<Withdrawal> pending = withdrawalRepository.findAll();
                        EmbedBuilder embedBuilder = new EmbedBuilder()
                                .setTitle("Pending Withdrawals")
                                .setColor(Color.ORANGE)
                                .setAuthor(interaction.getUser());

                        if (pending.size() == 0) {
                            embedBuilder.addField("Pending:", "No pending withdrawals.");
                        }

                        for (Withdrawal b : pending) {

                            NumberFormat n = NumberFormat.getCurrencyInstance(Locale.US);
                            DecimalFormat d = new DecimalFormat("#,###");
                            String id = b.getDiscordid();



                            embedBuilder.addField(  b.getDepositcode() + " Withdrawal:",
                                    "<@" + id + ">" + "\n" + n.format(b.getCash()) + " \n<:food:915071870636789792> " + d.format(b.getFood()) + " <:uranium:1024144769871523870> " + d.format(b.getUranium()) + " <:coal:1024144767858266222> " + d.format(b.getCoal()) + " <:oil:1024144768487391303> " + d.format(b.getOil()) + " <:lead:1024144770857177119> " + d.format(b.getLeadRss()) + " <:iron:1024144771884793918> " + d.format(b.getIron()) + " <:bauxite:1024144773075976243> " + d.format(b.getBauxite()) + " <:gasoline:1024144774602702868> " + d.format(b.getGasoline()) + " <:munitions:1024144775668051968> " + d.format(b.getMunitions()) + " <:steel:1024144776548847656> " + d.format(b.getSteel()) + " <:aluminum:1024144777509347348> " + d.format(b.getAluminum())
                            );


                        }

                        interaction.createFollowupMessageBuilder().addEmbed(embedBuilder).send();

                    } else if (interaction.getOptionByName("update").isPresent()) {
                        String code = interaction.getOptionByName("update").get().getOptionStringValueByName("code").get();
                        try {
                            Withdrawal withdrawal = withdrawalRepository.findWithdrawalByDepositcode(code);
                            Bank userBank = bankDao.findByDiscordid(withdrawal.getDiscordid());
                            User bankingUser = userRepository.findById(userBank.getDiscordid()).get();
                            Bank updateAmount = BankUtil.getReceivedTransactions(bankingUser.getNationid(), code);

                            if (updateAmount.getTotals() == 0) {
                                interaction.createFollowupMessageBuilder().setContent("No withdrawal found.").send();
                            } else {

                                userBank.setCash(userBank.getCash() - updateAmount.getCash());
                                userBank.setFood(userBank.getFood() - updateAmount.getFood());
                                userBank.setIron(userBank.getIron() - updateAmount.getIron());
                                userBank.setOil(userBank.getOil() - updateAmount.getOil());
                                userBank.setUranium(userBank.getUranium() - updateAmount.getUranium());
                                userBank.setLeadRss(userBank.getLeadRss() - updateAmount.getLeadRss());
                                userBank.setBauxite(userBank.getBauxite() - updateAmount.getBauxite());
                                userBank.setGasoline(userBank.getGasoline() - updateAmount.getGasoline());
                                userBank.setMunitions(userBank.getMunitions() - updateAmount.getMunitions());
                                userBank.setSteel(userBank.getSteel() - updateAmount.getSteel());
                                userBank.setAluminum(userBank.getAluminum() - updateAmount.getAluminum());
                                userBank.updateDepositCode();
                                bankDao.save(userBank);
                                withdrawalRepository.delete(withdrawal);
                                interaction.createFollowupMessageBuilder().setContent("Withdrawal recorded successfully.").send();

                            }

                        } catch (Exception e) {
                            interaction.createFollowupMessageBuilder().setContent("There was an error fetching the withdrawal. It may be an invalid code.").send();
                            e.printStackTrace();
                        }

                    }


                }


                // new case goes here

        }
    }
}
