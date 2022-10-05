package net.hk.hk97.SlashCommands.Commands;

import net.hk.hk97.Models.Bank;
import net.hk.hk97.Models.Enums.WithdrawalTypes;
import net.hk.hk97.Models.User;
import net.hk.hk97.Models.Withdrawal;
import net.hk.hk97.Repositories.BankRepository;
import net.hk.hk97.Repositories.UserRepository;
import net.hk.hk97.Repositories.WithdrawalRepository;
import net.hk.hk97.Services.Util.BankUtil;
import net.hk.hk97.Services.Util.MilUtil;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;

public class BankCommand {

    public static void bank(SlashCommandInteraction interaction, BankRepository bankDao, UserRepository userRepository, WithdrawalRepository withdrawalRepository) {

        if (!userRepository.findById(interaction.getUser().getIdAsString()).isPresent()) {
            interaction.createFollowupMessageBuilder().setContent("You do not have an HK account. Please use `/account register` before trying to use the bank.").send();

        } else {

            if (interaction.getOptionByName("create").isPresent()) {

                List<Bank> listOfAccounts = bankDao.findBanksByDiscordid(interaction.getUser().getIdAsString());
                if (!(listOfAccounts.size() > 0)) {

                    Bank newAccount = new Bank();
                    newAccount.setDiscordid(interaction.getUser().getIdAsString());
                    newAccount.setName(interaction.getUser().getName());
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


                    interaction.getChannel().get().sendMessage("Deposit code:");
                    interaction.getChannel().get().sendMessage(b.getDepositcode());
                    interaction.createFollowupMessageBuilder().addEmbed(emb).send();


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


                            String withString = "[Requiem Bank](https://politicsandwar.com/alliance/id=10470&display=bank&w_money=" + cash + "&w_food=" + food + "&w_coal=" + coal + "&w_oil=" + oil + "&w_uranium=" + uranium + "&w_lead=" + lead + "&w_iron=" + iron + "&w_bauxite=" + bauxite + "&w_gasoline=" + gasoline + "&w_munitions=" + munitions + "&w_steel=" + steel + "&w_aluminum=" + aluminum + "&w_note=" + b.getDepositcode() + "&w_type=nation&w_recipient=" + fnation + ")";

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
    }
}
