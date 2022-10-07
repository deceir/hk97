package net.hk.hk97.SlashCommands;

import net.hk.hk97.Models.calc.graphql.repositories.ResourceRepository;
import net.hk.hk97.Repositories.*;
import net.hk.hk97.SlashCommands.Commands.*;

import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


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

    @Autowired
    private TreasureRepository treasureRepository;

    @Autowired
    private NationRepository nationRepository;

    @Override
    public void onSlashCommandCreate(SlashCommandCreateEvent slashCommandCreateEvent) {

        SlashCommandInteraction interaction = slashCommandCreateEvent.getSlashCommandInteraction();

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

            case "treasure":
                interaction.respondLater();
                TreasureCommand.treasures(interaction,nationRepository, treasureRepository, userRepository);
                break;


                // new case goes here

        }
    }
}
