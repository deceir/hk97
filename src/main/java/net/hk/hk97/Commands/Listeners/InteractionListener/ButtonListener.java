package net.hk.hk97.Commands.Listeners.InteractionListener;

import net.hk.hk97.Commands.SlashCommands.Commands.BankButtonsCommand;
import net.hk.hk97.Models.Bank.Bank;
import net.hk.hk97.Models.User;
import net.hk.hk97.Models.calc.graphql.repositories.ResourceRepository;
import net.hk.hk97.Repositories.BankRepository;
import net.hk.hk97.Repositories.InterviewRepository;
import net.hk.hk97.Repositories.UserRepository;
import net.hk.hk97.Repositories.WithdrawalRepository;
import net.hk.hk97.Services.Util.BankUtil;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.interaction.ButtonClickEvent;
import org.javacord.api.listener.interaction.ButtonClickListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@Component
public class ButtonListener implements ButtonClickListener {

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
    public void onButtonClick(ButtonClickEvent interaction) {
        String buttonId = interaction.getButtonInteraction().getCustomId();


        switch (buttonId) {
            case "bankBalance":

                interaction.getButtonInteraction().respondLater();

                // bank balance button
                try {

                    List<Bank> listOfAccounts = bankDao.findBanksByDiscordid(interaction.getButtonInteraction().getUser().getIdAsString());
//                    User user = userRepository.findById(interaction.getUser().getIdAsString()).get();
//                    List<Loan> loans = loanRepository.getLoansByDiscordidAAndActive(interaction.getUser().getId(), true);


                    NumberFormat n = NumberFormat.getCurrencyInstance(Locale.US);
                    DecimalFormat d = new DecimalFormat("#,###");

                    Bank b = listOfAccounts.get(0);

                    EmbedBuilder emb = new EmbedBuilder()
                            .setTitle("Requiem Strongbox Services")
                            .setColor(Color.CYAN)
                            .setAuthor(interaction.getButtonInteraction().getUser())
                            .addField("Deposit Code: ", "`" + listOfAccounts.get(0).getDepositcode() + "`")
                            .addField("Totals:",
                                    n.format(b.getCash()) + " \n<:food:915071870636789792> " + d.format(b.getFood()) + " <:uranium:1024144769871523870> " + d.format(b.getUranium()) + " <:coal:1024144767858266222> " + d.format(b.getCoal()) + " <:oil:1024144768487391303> " + d.format(b.getOil()) + " <:lead:1024144770857177119> " + d.format(b.getLeadRss()) + " <:iron:1024144771884793918> " + d.format(b.getIron()) + " <:bauxite:1024144773075976243> " + d.format(b.getBauxite()) + " <:gasoline:1024144774602702868> " + d.format(b.getGasoline()) + " <:munitions:1024144775668051968> " + d.format(b.getMunitions()) + " <:steel:1024144776548847656> " + d.format(b.getSteel()) + " <:aluminum:1024144777509347348> " + d.format(b.getAluminum())
                            );


                    interaction.getButtonInteraction().createFollowupMessageBuilder().addEmbed(emb).send();
                    TimeUnit.SECONDS.sleep(1);
                    System.out.println("Deposit code sent.");
                    interaction.getInteraction().getChannel().get().sendMessage("Deposit code:");
                    interaction.getInteraction().getChannel().get().sendMessage(b.getDepositcode());


                } catch (Exception e) {
                    interaction.getInteraction().getChannel().get().sendMessage("There was an error. " + e);
                }
                BankButtonsCommand.getBankButtons(interaction);
                break;


            case "bankDeposit":
                interaction.getButtonInteraction().respondLater();
                try {
                    User user = userRepository.findById(interaction.getInteraction().getUser().getIdAsString()).get();
                    Bank bank = bankDao.findByDiscordid(interaction.getInteraction().getUser().getIdAsString());
                    Bank deposits = BankUtil.getTransactions(user.getNationid(), bank.getDepositcode());

                    if (deposits.getTotals() == 0) {
                        interaction.getInteraction().createFollowupMessageBuilder().setContent("**" + interaction.getInteraction().getUser().getDiscriminatedName() + "**\nDeposit code:").send();
                        interaction.getInteraction().getChannel().get().sendMessage(bank.getDepositcode());
                    } else {
                        bank.setCash(bank.getCash() + deposits.getCash());
                        bank.setFood(bank.getFood() + deposits.getFood());
                        bank.setIron(bank.getIron() + deposits.getIron());
                        bank.setOil(bank.getOil() + deposits.getOil());
                        bank.setCoal(bank.getCoal() + deposits.getCoal());
                        bank.setUranium(bank.getUranium() + deposits.getUranium());
                        bank.setLeadRss(bank.getLeadRss() + deposits.getLeadRss());
                        bank.setBauxite(bank.getBauxite() + deposits.getBauxite());
                        bank.setGasoline(bank.getGasoline() + deposits.getGasoline());
                        bank.setMunitions(bank.getMunitions() + deposits.getMunitions());
                        bank.setSteel(bank.getSteel() + deposits.getSteel());
                        bank.setAluminum(bank.getAluminum() + deposits.getAluminum());
                        bank.updateDepositCode();
                        bankDao.save(bank);
                        interaction.getInteraction().createFollowupMessageBuilder().setContent("**" + interaction.getInteraction().getUser().getDiscriminatedName() + "**\nDeposit recorded successfully.").send();


                    }


                } catch (Exception e) {
                    interaction.getInteraction().createFollowupMessageBuilder().setContent("There was an error. " + e).send();
                }
                BankButtonsCommand.getBankButtons(interaction);
                break;
        }
    }
}
