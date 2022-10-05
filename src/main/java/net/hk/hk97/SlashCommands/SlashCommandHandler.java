package net.hk.hk97.SlashCommands;

import io.github.adorableskullmaster.pw4j.PoliticsAndWar;
import io.github.adorableskullmaster.pw4j.PoliticsAndWarBuilder;
import io.github.adorableskullmaster.pw4j.domains.Nation;
import net.hk.hk97.Config;
import net.hk.hk97.Interview;
import net.hk.hk97.Models.ActivityAudit;
import net.hk.hk97.Models.Bank;
import net.hk.hk97.Models.Enums.WithdrawalTypes;
import net.hk.hk97.Models.User;
import net.hk.hk97.Models.Withdrawal;
import net.hk.hk97.Models.calc.AppraiseCalc;
import net.hk.hk97.Models.calc.CityCalc;
import net.hk.hk97.Models.calc.InfraCalc;
import net.hk.hk97.Models.calc.LandCalc;
import net.hk.hk97.Models.calc.graphql.models.charts.MakeChart;
import net.hk.hk97.Models.calc.graphql.repositories.ResourceRepository;
import net.hk.hk97.Models.Message.Messenger;
import net.hk.hk97.Repositories.BankRepository;
import net.hk.hk97.Repositories.InterviewRepository;
import net.hk.hk97.Repositories.UserRepository;
import net.hk.hk97.Repositories.WithdrawalRepository;
import net.hk.hk97.Services.Util.AuditUtil;
import net.hk.hk97.Services.Util.BankUtil;
import net.hk.hk97.Services.Util.MilUtil;
import net.hk.hk97.SlashCommands.Commands.*;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.*;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.MessageSet;
import org.javacord.api.entity.message.embed.Embed;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.*;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
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
                AppraiseCommand.appraise(interaction, resourceDao, userRepository);

                break;


            case "apply":
                interaction.respondLater();
                try {
                    ApplicationCommand.application(interaction, interviewRepository);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;


            //deprecated verification
//            case "verify":
//
//                interaction.respondLater();
//
//                userRepository.findById(interaction.getUser().getIdAsString());
//                if (!userRepository.findById(interaction.getUser().getIdAsString()).get().isRegistered()) {
//
//                    User user = userRepository.findById(interaction.getUser().getIdAsString()).get();
//
//                    if ((interaction.getOptionByName("").get().equals(user.getVerification()))) {
//                        user.setRegistered(true);
//                        userRepository.save(user);
//                        interaction.createFollowupMessageBuilder().setContent("Your account has been registered.").send();
//
//                    } else {
//                        interaction.createFollowupMessageBuilder().setContent("You have entered the incorrect verification code.").send();
//                    }
//
//                }
//                break;


            case "calc":

                interaction.respondLater();
                CalcCommand.calc(interaction);
                break;

            case "audit":


                interaction.respondLater();
                AuditCommand.audit(interaction, userRepository);
                break;

            case "account":
                interaction.respondLater();
                AccountCommand.account(interaction, userRepository);
                break;

            case "bank":
                interaction.respondLater();
                BankCommand.bank(interaction, bankDao, userRepository, withdrawalRepository);
                break;


            case "badmin":

                interaction.respondLater();
                BadminCommand.badmin(interaction, bankDao, withdrawalRepository, userRepository);
                break;


            case "wc" :
                interaction.respondLater();
                try {
                    WarchestCommand.wc(interaction, resourceDao, userRepository, bankDao);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                break;



                // new case goes here

        }
    }
}
