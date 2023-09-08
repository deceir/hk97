package net.hk.hk97.Commands.SlashCommands;

import net.hk.hk97.Commands.SlashCommands.Commands.*;
import net.hk.hk97.Models.calc.graphql.repositories.ResourceRepository;
import net.hk.hk97.Repositories.*;

import org.javacord.api.entity.message.MessageFlag;
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

    //    @Autowired
//    private LoanRepository loanRepository;

    @Autowired
    private TreasureRepository treasureRepository;

    @Autowired
    private NationRepository nationRepository;

    @Autowired
    private WarchestReqsRepository wcReqsRepository;

    @Autowired
    private BankStatusRepository bankStatusRepository;

    @Autowired
    private AllianceKeyRepository allianceKeyRepository;

    @Override
    public void onSlashCommandCreate(SlashCommandCreateEvent slashCommandCreateEvent) {

        SlashCommandInteraction interaction = slashCommandCreateEvent.getSlashCommandInteraction();

        switch (interaction.getCommandName()) {

            case "appraise":

                interaction.respondLater();
                AppraiseCommand.appraise(interaction, resourceDao, userRepository);

                break;


            case "apply":

                interaction.createImmediateResponder().setContent("Processing application. Please check your pings in this server or your DM's for further info. Thank you.").setFlags(MessageFlag.EPHEMERAL).respond();
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
                try {
                    AuditCommand.audit(interaction, userRepository);
                } catch (JSONException e) {
                interaction.createFollowupMessageBuilder().setContent("There was an error executing that command.\n" + e).send();
                }
                break;

            case "account":

                interaction.respondLater();
                AccountCommand.account(interaction, userRepository, bankDao);
                break;

            case "bank":

                interaction.respondLater();
                try {
                    BankCommand.bank(interaction, bankDao, userRepository, withdrawalRepository, allianceKeyRepository);
                } catch (Exception e) {
                    interaction.createFollowupMessageBuilder().setContent("There was an error executing your request.\n" + e).send();
                }
                BankButtonsCommand.getBankButtons(interaction);
                break;


            case "badmin":

                interaction.respondLater();
                try {
                    BadminCommand.badmin(interaction, bankDao, withdrawalRepository, userRepository, allianceKeyRepository);
                } catch (Exception e) {
                    interaction.createFollowupMessageBuilder().setContent("There was an error executing your request.\n" + e).send();
                }
                break;


            case "wc":
                interaction.respondLater();
                try {
                    WarchestCommand.wc(interaction, resourceDao, userRepository, bankDao, wcReqsRepository);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                break;

            case "treasure":
                interaction.respondLater();
                TreasureCommand.treasures(interaction, nationRepository, treasureRepository, userRepository);
                break;

            case "stats":
                interaction.respondLater();
                try {
                    StatsCommand.getStats(interaction, userRepository);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

            case "who":
                interaction.respondLater();
                try {
                    WhoCommand.getWho(interaction, nationRepository);

                } catch (Exception e) {
                    interaction.createFollowupMessageBuilder().setContent("There was an error executing your request.\n" + e).send();
                }
                break;

            case "wcaudit":
                interaction.respondLater();
                try {
                    WarchestAuditCommand.auditWarchests(interaction, bankDao);
                } catch (Exception e) {
                    interaction.createFollowupMessageBuilder().setContent("There was an error executing your request.\n" + e).send();
                    e.printStackTrace();
                }
                break;



                    // new case goes here

        }
    }
}
