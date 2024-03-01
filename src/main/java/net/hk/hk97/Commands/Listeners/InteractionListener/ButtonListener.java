package net.hk.hk97.Commands.Listeners.InteractionListener;

import net.hk.hk97.Commands.SlashCommands.Commands.BankButtonsCommand;
import net.hk.hk97.Models.Bank.Bank;
import net.hk.hk97.Models.Bank.Loan;
import net.hk.hk97.Models.User;
import net.hk.hk97.Models.calc.graphql.repositories.ResourceRepository;
import net.hk.hk97.Repositories.*;
import net.hk.hk97.Services.Util.BankUtil;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.TextInput;
import org.javacord.api.entity.message.component.TextInputStyle;
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

    @Autowired
    LoanRepository loanRepository;

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
                            .setTitle("TGH Treasury")
                            .setColor(Color.CYAN)
                            .setAuthor(interaction.getButtonInteraction().getUser())
                            .addField("Deposit Code: ", "`" + listOfAccounts.get(0).getDepositcode() + "`")
                            .addField("Totals:",
                                    n.format(b.getCash()) + " \n<:food:915071870636789792> " + d.format(b.getFood()) + " <:uranium:1024144769871523870> " + d.format(b.getUranium()) + " <:coal:1024144767858266222> " + d.format(b.getCoal()) + " <:oil:1024144768487391303> " + d.format(b.getOil()) + " <:lead:1024144770857177119> " + d.format(b.getLeadRss()) + " <:iron:1024144771884793918> " + d.format(b.getIron()) + " <:bauxite:1024144773075976243> " + d.format(b.getBauxite()) + " <:gasoline:1024144774602702868> " + d.format(b.getGasoline()) + " <:munitions:1024144775668051968> " + d.format(b.getMunitions()) + " <:steel:1024144776548847656> " + d.format(b.getSteel()) + " <:aluminum:1024144777509347348> " + d.format(b.getAluminum())
                            );

                    if (!loanRepository.getLoansByDiscordid(interaction.getButtonInteraction().getUser().getId()).isEmpty()) {
                        List<Loan> loans = loanRepository.getLoansByDiscordid(interaction.getButtonInteraction().getUser().getId());

                        for (Loan loan: loans) {
                            if (loan.getActive()) {
                                emb.addField("Loan ID: " + loan.getId(), "Amount Remaining: " + n.format(loan.getAmount()) + "\nOriginal Amount: " + n.format(loan.getOriginal_amount()) + " \nDue On: " + loan.getDateDue() + " \nDeposit Code: " + loan.getDepositcode() + "\nBanker: <@" + loan.getBanker() + ">");
                            }
                        }
                    }

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

            case "applicationButton":

                    interaction.getButtonInteraction().respondWithModal("applicationModal", "Application",
                            ActionRow.of((TextInput.create(TextInputStyle.SHORT, "nationId", "Enter your nation ID below."))));


                break;
            case "payloan":
                interaction.getButtonInteraction().respondLater();

                User user = userRepository.findById(interaction.getButtonInteraction().getUser().getIdAsString()).get();
                List<Loan> loans = loanRepository.getLoansByDiscordid(interaction.getButtonInteraction().getUser().getId());
                try {
                    for (Loan loan : loans) {
                        if (loan.getActive() == true) {

                            Bank deposits = BankUtil.getTransactions(user.getNationid(), loan.getDepositcode());

                            if (deposits.getTotals() == 0) {
                                interaction.getInteraction().createFollowupMessageBuilder().setContent("Loan Deposit code:").send();
                                interaction.getInteraction().getChannel().get().sendMessage(loan.getDepositcode());
                            } else {

                                loan.setAmount(loan.getAmount() - deposits.getCash());
                                if (loan.getAmount() <= 0) {
                                    loan.setActive(false);

                                    EmbedBuilder paidOff = new EmbedBuilder()
                                            .setAuthor(interaction.getInteraction().getUser())
                                            .setTitle("Loan Repayment In Full")
                                            .setDescription(interaction.getInteraction().getUser().getNicknameMentionTag() + " has repaid their loan (ID: " + loan.getId() + ")")
                                            .setFooter("HK-97 Banking Service");

                                    interaction.getApi().getTextChannelById("1128058377432477706").get().sendMessage(paidOff);
                                }
                                loan.updateDepositCode();
                                loanRepository.save(loan);
                                interaction.getInteraction().createFollowupMessageBuilder().setContent("Your loan payment has been received.").send();
                            }
                        }
                    }

                } catch (Exception e) {
                    interaction.getInteraction().createFollowupMessageBuilder().setContent("There was an error. " + e).send();
                }
                BankButtonsCommand.getBankButtons(interaction);
                break;
        }
    }
}
