package net.hk.hk97.Commands.SlashCommands.Commands;

import net.hk.hk97.Config;
import net.hk.hk97.Models.Bank.Bank;
import net.hk.hk97.Models.Bank.Loan;
import net.hk.hk97.Models.Enums.WithdrawalTypes;
import net.hk.hk97.Models.User;
import net.hk.hk97.Models.Bank.Withdrawal;
import net.hk.hk97.Repositories.*;
//import net.hk.hk97.Repositories.LoanRepository;

import net.hk.hk97.Services.Util.BankUtil;
import net.hk.hk97.Services.Util.MilUtil;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.Embed;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.json.JSONException;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class BankCommand {

    public static void bank(SlashCommandInteraction interaction, BankRepository bankDao, UserRepository userRepository, WithdrawalRepository withdrawalRepository, AllianceKeyRepository allianceKeyRepository, LoanRepository loanRepository) throws JSONException {

        if (!userRepository.findById(interaction.getUser().getIdAsString()).isPresent()) {
            interaction.createFollowupMessageBuilder().setContent("You do not have a HK-97 account. Please use `/account register` before trying to use the bank.").send();

        } else {

//            BankStatus bankStatus = bankStatusRepository.findById(1).get();
//            String with_status = bank.getWithdrawalStatus();
//            String dep_status = bank.getDepositStatus();

            if (interaction.getOptionByName("create").isPresent()) {

                List<Bank> listOfAccounts = bankDao.findBanksByDiscordid(interaction.getUser().getIdAsString());
                if (!(listOfAccounts.size() > 0)) {

                    Bank newAccount = new Bank();
                    newAccount.setDiscordid(interaction.getUser().getIdAsString());
                    newAccount.setNationid(userRepository.findById(interaction.getUser().getIdAsString()).get().getNationid());
                    newAccount.setName(interaction.getUser().getName());
                    bankDao.save(newAccount);
                    interaction.createFollowupMessageBuilder().setContent("Account created.").send();

                } else {
                    interaction.createFollowupMessageBuilder().setContent("You already have an account. You cannot create another.").send();
                }

            } else if (interaction.getOptionByName("info").isPresent()) {

                try {

                    List<Bank> listOfAccounts = bankDao.findBanksByDiscordid(interaction.getUser().getIdAsString());
//                    User user = userRepository.findById(interaction.getUser().getIdAsString()).get();
//                    List<Loan> loans = loanRepository.getLoansByDiscordidAAndActive(interaction.getUser().getId(), true);


                    NumberFormat n = NumberFormat.getCurrencyInstance(Locale.US);
                    DecimalFormat d = new DecimalFormat("#,###");

                    Bank b = listOfAccounts.get(0);

                    EmbedBuilder emb = new EmbedBuilder()
                            .setTitle("TGH Treasury")
                            .setColor(Color.CYAN)
                            .setAuthor(interaction.getUser())
                            .addField("Deposit Code: ", "`" + listOfAccounts.get(0).getDepositcode() + "`")
                            .addField("Totals:",
                                    n.format(b.getCash()) + " \n<:food:915071870636789792> " + d.format(b.getFood()) + " <:uranium:1024144769871523870> " + d.format(b.getUranium()) + " <:coal:1024144767858266222> " + d.format(b.getCoal()) + " <:oil:1024144768487391303> " + d.format(b.getOil()) + " <:lead:1024144770857177119> " + d.format(b.getLeadRss()) + " <:iron:1024144771884793918> " + d.format(b.getIron()) + " <:bauxite:1024144773075976243> " + d.format(b.getBauxite()) + " <:gasoline:1024144774602702868> " + d.format(b.getGasoline()) + " <:munitions:1024144775668051968> " + d.format(b.getMunitions()) + " <:steel:1024144776548847656> " + d.format(b.getSteel()) + " <:aluminum:1024144777509347348> " + d.format(b.getAluminum())
                            );

                    if (!loanRepository.getLoansByDiscordid(interaction.getUser().getId()).isEmpty()) {
                        List<Loan> loans = loanRepository.getLoansByDiscordid(interaction.getUser().getId());

                        for (Loan loan: loans) {
                            if (loan.getActive()) {
                                emb.addField("Loan ID: " + loan.getId(), "Amount Remaining: " + n.format(loan.getAmount()) + "\nOriginal Amount: " + n.format(loan.getOriginal_amount()) + " \nDue On: " + loan.getDateDue() + " \nDeposit Code: " + loan.getDepositcode() + "\nBanker: <@" + loan.getBanker() + ">");
                            }
                        }
                    }

                    interaction.createFollowupMessageBuilder().addEmbed(emb).send();
                    TimeUnit.SECONDS.sleep(1);
                    interaction.getChannel().get().sendMessage("Deposit code:");
                    interaction.getChannel().get().sendMessage(b.getDepositcode());

//                    if (loans.size() > 0) {
//                        TimeUnit.SECONDS.sleep(1);
//                        interaction.getChannel().get().sendMessage("Loan code:");
//                        interaction.getChannel().get().sendMessage(loans.get(0).getDepositcode());
//                    }

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
                        interaction.getChannel().get().sendMessage(bank.getDepositcode());
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
                        interaction.createFollowupMessageBuilder().setContent("Deposit recorded successfully.").send();

                    }

                } catch (Exception e) {
                    interaction.createFollowupMessageBuilder().setContent("There was an error. " + e).send();
                }

            } else if(interaction.getOptionByName("payloan").isPresent()) {
                User user = userRepository.findById(interaction.getUser().getIdAsString()).get();
                List<Loan> loans = loanRepository.getLoansByDiscordid(interaction.getUser().getId());
                try {

                        for (Loan loan : loans) {
                            if (loan.getActive() == true) {

                                Bank deposits = BankUtil.getTransactions(user.getNationid(), loan.getDepositcode());

                                if (deposits.getTotals() == 0) {
                                    interaction.createFollowupMessageBuilder().setContent("Loan Deposit code:").send();
                                    interaction.getChannel().get().sendMessage(loan.getDepositcode());
                                } else {

                                    loan.setAmount(loan.getAmount() - deposits.getCash());
                                    if (loan.getAmount() <= 0) {
                                        loan.setActive(false);

                                        EmbedBuilder paidOff = new EmbedBuilder()
                                                .setAuthor(interaction.getUser())
                                                .setTitle("Loan Repayment In Full")
                                                .setDescription(interaction.getUser().getNicknameMentionTag() + " has repaid their loan (ID: " + loan.getId() + ")")
                                                .setFooter("HK-97 Banking Service");

                                        interaction.getApi().getTextChannelById("1128058377432477706").get().sendMessage(paidOff);
                                    }
                                    loan.updateDepositCode();
                                    loanRepository.save(loan);
                                    interaction.createFollowupMessageBuilder().setContent("Your loan payment has been received.").send();
                                }
                            }
                        }


                } catch (Exception e) {
                    interaction.createFollowupMessageBuilder().setContent("There was an error. " + e).send();
                }





            } else if (interaction.getOptionByName("withdraw").isPresent()) {

                if (!withdrawalRepository.findById(interaction.getUser().getIdAsString()).isPresent()) {

                    SlashCommandInteractionOption option = interaction.getOptionByName("withdraw").get();

                    try {
                        long cash = 0;
                        boolean withCash = false;
                        long food = 0;
                        boolean withFood = false;
                        long oil = 0;
                        boolean withOil = false;
                        long uranium = 0;
                        boolean withUra = false;
                        long lead = 0;
                        boolean withLead = false;
                        long iron = 0;
                        boolean withIron = false;
                        long bauxite = 0;
                        boolean withBauxite = false;
                        long gasoline = 0;
                        boolean withGas = false;
                        long munitions = 0;
                        boolean withMunis = false;
                        long steel = 0;
                        boolean withSteel = false;
                        long aluminum = 0;
                        boolean withAlu = false;
                        long coal = 0;
                        boolean withCoal = false;

                        if (option.getOptionLongValueByName("cash").isPresent()) {
                            cash = option.getOptionLongValueByName("cash").get();
                            withCash = true;
                        }
                        if (option.getOptionLongValueByName("food").isPresent()) {
                            food = option.getOptionLongValueByName("food").get();
                            withFood = true;
                        }
                        if (option.getOptionLongValueByName("oil").isPresent()) {
                            oil = option.getOptionLongValueByName("oil").get();
                            withOil = true;
                        }
                        if (option.getOptionLongValueByName("uranium").isPresent()) {
                            uranium = option.getOptionLongValueByName("uranium").get();
                            withUra = true;
                        }
                        if (option.getOptionLongValueByName("lead").isPresent()) {
                            lead = option.getOptionLongValueByName("lead").get();
                            withLead = true;
                        }
                        if (option.getOptionLongValueByName("iron").isPresent()) {
                            iron = option.getOptionLongValueByName("iron").get();
                            withIron = true;
                        }
                        if (option.getOptionLongValueByName("bauxite").isPresent()) {
                            bauxite = option.getOptionLongValueByName("bauxite").get();
                            withBauxite = true;
                        }
                        if (option.getOptionLongValueByName("gasoline").isPresent()) {
                            gasoline = option.getOptionLongValueByName("gasoline").get();
                            withGas = true;
                        }
                        if (option.getOptionLongValueByName("munitions").isPresent()) {
                            munitions = option.getOptionLongValueByName("munitions").get();
                            withMunis = true;
                        }
                        if (option.getOptionLongValueByName("steel").isPresent()) {
                            steel = option.getOptionLongValueByName("steel").get();
                            withSteel = true;
                        }
                        if (option.getOptionLongValueByName("aluminum").isPresent()) {
                            aluminum = option.getOptionLongValueByName("aluminum").get();
                            withAlu = true;
                        }
                        if (option.getOptionLongValueByName("coal").isPresent()) {
                            coal = option.getOptionLongValueByName("coal").get();
                            withCoal = true;
                        }

                        String errorLog = "";

                        Bank bank = bankDao.findByDiscordid(interaction.getUser().getIdAsString());

                        if (cash > bank.getCash() && withCash) {
                            errorLog += "You do not have enough cash for this withdrawal. \n";
                        }
                        if (food > bank.getFood() && withFood) {
                            errorLog += "You do not have enough food for this withdrawal. \n";
                        }
                        if (aluminum > bank.getAluminum() && withAlu) {
                            errorLog += "You do not have enough aluminum for this withdrawal. \n";
                        }
                        if (bauxite > bank.getBauxite() && withBauxite) {
                            errorLog += "You do not have enough bauxite for this withdrawal. \n";
                        }
                        if (iron > bank.getIron() && withIron) {
                            errorLog += "You do not have enough iron for this withdrawal. \n";
                        }
                        if (coal > bank.getCoal() && withCoal) {
                            errorLog += "You do not have enough coal for this withdrawal. \n";
                        }
                        if (oil > bank.getOil() && withOil) {
                            errorLog += "You do not have enough oil for this withdrawal. \n";
                        }
                        if (gasoline > bank.getGasoline() && withGas) {
                            errorLog += "You do not have enough gasoline for this withdrawal. \n";
                        }
                        if (steel > bank.getSteel() && withSteel) {
                            errorLog += "You do not have enough steel for this withdrawal. \n";
                        }
                        if (uranium > bank.getUranium() && withUra) {
                            errorLog += "You do not have enough uranium for this withdrawal. \n";
                        }
                        if (lead > bank.getLeadRss() && withLead) {
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

                        long total = 0;
                        total += cash;
                        total += food;
                        total += oil;
                        total += uranium;
                        total += lead;
                        total += iron;
                        total += bauxite;
                        total += gasoline;
                        total += munitions;
                        total += steel;
                        total += aluminum;
                        total += coal;

                        if (total <= 0) {
                            interaction.createFollowupMessageBuilder().setContent("Withdrawal amount cannot be zero or negative.").send();
                        } else if (errorLog.length() > 0) {
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
                            b.setMunitions(munitions);
                            b.setSteel(steel);
                            b.setUranium(uranium);
                            b.setLeadRss(lead);

                            b.setDiscordid(interaction.getUser().getIdAsString());
                            b.setWithdrawalType(WithdrawalTypes.WITHDRAWAL);

                            withdrawalRepository.save(b);

                            //creating embed
                            NumberFormat n = NumberFormat.getCurrencyInstance(Locale.US);
                            DecimalFormat d = new DecimalFormat("#,###");

                            String nationName = MilUtil.getNationName(userRepository.findById(interaction.getUser().getIdAsString()).get().getNationid());
                            String fnation = nationName.replaceAll(" ", "+");


                            String withString = "[TGH Bank](https://politicsandwar.com/alliance/id=4567&display=bank&w_money=" + cash + "&w_food=" + food + "&w_coal=" + coal + "&w_oil=" + oil + "&w_uranium=" + uranium + "&w_lead=" + lead + "&w_iron=" + iron + "&w_bauxite=" + bauxite + "&w_gasoline=" + gasoline + "&w_munitions=" + munitions + "&w_steel=" + steel + "&w_aluminum=" + aluminum + "&w_note=" + b.getDepositcode() + "&w_type=nation&w_recipient=" + fnation + ")";

                            String offshoreWithString = "[Offshore](https://politicsandwar.com/alliance/id=" + allianceKeyRepository.findAllianceKeysByAaName("offshore").getId() + "&display=bank&w_money=" + cash + "&w_food=" + food + "&w_coal=" + coal + "&w_oil=" + oil + "&w_uranium=" + uranium + "&w_lead=" + lead + "&w_iron=" + iron + "&w_bauxite=" + bauxite + "&w_gasoline=" + gasoline + "&w_munitions=" + munitions + "&w_steel=" + steel + "&w_aluminum=" + aluminum + "&w_note=" + b.getDepositcode() + "&w_type=nation&w_recipient=" + fnation + ")";;
                            EmbedBuilder emb = new EmbedBuilder()
                                    .setTitle("TGH Treasury")
                                    .setDescription("Withdrawal from " + interaction.getUser().getNicknameMentionTag() + " at " + LocalTime.now())
                                    .setColor(Color.CYAN)
                                    .setAuthor(interaction.getUser())
                                    .addField("Totals:",
                                            n.format(b.getCash()) + " \n<:food:915071870636789792> " + d.format(b.getFood()) + " <:uranium:1024144769871523870> " + d.format(b.getUranium()) + " <:coal:1024144767858266222> " + d.format(b.getCoal()) + " <:oil:1024144768487391303> " + d.format(b.getOil()) + " <:lead:1024144770857177119> " + d.format(b.getLeadRss()) + " <:iron:1024144771884793918> " + d.format(b.getIron()) + " <:bauxite:1024144773075976243> " + d.format(b.getBauxite()) + " <:gasoline:1024144774602702868> " + d.format(b.getGasoline()) + " <:munitions:1024144775668051968> " + d.format(b.getMunitions()) + " <:steel:1024144776548847656> " + d.format(b.getSteel()) + " <:aluminum:1024144777509347348> " + d.format(b.getAluminum()))
                                    .addField("Bank", withString + " " + offshoreWithString);

                            Role role = interaction.getApi().getRoleById(Config.mainServerBankAdminId).get();
                            ServerTextChannel channel1 = interaction.getApi().getServerTextChannelById("1128058377432477706").get();
                            channel1.sendMessage("Withdrawal request: ");
                            channel1.sendMessage(b.getDepositcode());
                            channel1.sendMessage(emb);

                                Message msg = channel1.sendMessage(role.getMentionTag()).get();



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
    }
}
