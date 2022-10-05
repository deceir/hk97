package net.hk.hk97.SlashCommands;

import net.hk.hk97.Models.calc.graphql.repositories.ResourceRepository;
import net.hk.hk97.Repositories.BankRepository;
import net.hk.hk97.Repositories.InterviewRepository;
import net.hk.hk97.Repositories.UserRepository;
import net.hk.hk97.Repositories.WithdrawalRepository;
import net.hk.hk97.SlashCommands.Commands.*;
import org.javacord.api.entity.channel.*;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

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
