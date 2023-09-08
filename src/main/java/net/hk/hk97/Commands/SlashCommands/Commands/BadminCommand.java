package net.hk.hk97.Commands.SlashCommands.Commands;

import net.hk.hk97.Config;
import net.hk.hk97.Models.Bank.Bank;
import net.hk.hk97.Models.Bank.Loan;
import net.hk.hk97.Models.Enums.WithdrawalTypes;
import net.hk.hk97.Models.User;
import net.hk.hk97.Models.Bank.Withdrawal;
import net.hk.hk97.Repositories.AllianceKeyRepository;
import net.hk.hk97.Repositories.BankRepository;
//import net.hk.hk97.Repositories.LoanRepository;
import net.hk.hk97.Repositories.UserRepository;
import net.hk.hk97.Repositories.WithdrawalRepository;
import net.hk.hk97.Services.Util.BankUtil;
import net.hk.hk97.Services.Util.MilUtil;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.json.JSONException;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class BadminCommand {

    public static void badmin(SlashCommandInteraction interaction, BankRepository bankDao, WithdrawalRepository withdrawalRepository, UserRepository userRepository, AllianceKeyRepository allianceKeyRepository) throws JSONException {


        org.javacord.api.entity.user.User user = interaction.getUser();
        List<Role> roles = user.getRoles(interaction.getApi().getServerById(Config.mainServerId).get());
        boolean isAdmin = false;
        for (Role role : roles) {
            if (role.getIdAsString().equals(Config.mainServerBankAdminId) || role.getIdAsString().equals(Config.mainServerGovId) || role.getIdAsString().equals("404940752691396608") || role.getIdAsString().equals("404941145966116874")) {
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
                        .setColor(Color.CYAN)
                        .setAuthor(interaction.getUser());

                if (pending.size() == 0) {
                    embedBuilder.addField("Pending:", "No pending withdrawals.");
                }

                for (Withdrawal b : pending) {

                    NumberFormat n = NumberFormat.getCurrencyInstance(Locale.US);
                    DecimalFormat d = new DecimalFormat("#,###");
                    String id = b.getDiscordid();


                    embedBuilder.addField(b.getDepositcode() + " " + b.getWithdrawalType() + " :",
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

                    if (withdrawal.getWithdrawalType().equals(WithdrawalTypes.WITHDRAWAL)) {

                        Bank updateAmount = BankUtil.getReceivedTransactions(bankingUser.getNationid(), code);

                        if (updateAmount.getTotals() == 0) {
                            interaction.createFollowupMessageBuilder().setContent("No withdrawal found.").send();
                        } else {

                            userBank.setCash(userBank.getCash() - updateAmount.getCash());
                            userBank.setFood(userBank.getFood() - updateAmount.getFood());
                            userBank.setCoal(userBank.getCoal() - updateAmount.getCoal());
                            userBank.setIron(userBank.getIron() - updateAmount.getIron());
                            userBank.setOil(userBank.getOil() - updateAmount.getOil());
                            userBank.setUranium(userBank.getUranium() - updateAmount.getUranium());
                            userBank.setLeadRss(userBank.getLeadRss() - updateAmount.getLeadRss());
                            userBank.setBauxite(userBank.getBauxite() - updateAmount.getBauxite());
                            userBank.setGasoline(userBank.getGasoline() - updateAmount.getGasoline());
                            userBank.setMunitions(userBank.getMunitions() - updateAmount.getMunitions());
                            userBank.setSteel(userBank.getSteel() - updateAmount.getSteel());
                            userBank.setAluminum(userBank.getAluminum() - updateAmount.getAluminum());
                            bankDao.save(userBank);
                            withdrawalRepository.delete(withdrawal);
                            interaction.createFollowupMessageBuilder().setContent("Withdrawal recorded successfully.").send();

                            LocalDate date = LocalDate.now();
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M / d / u");
                            String text = date.format(formatter);
//                                    LocalDate parsedDate = LocalDate.parse(text, formatter);
                            DiscordApi api = interaction.getApi();

                            EmbedBuilder msgEmbed = new EmbedBuilder()
                                    .setAuthor(interaction.getUser())
                                    .setTitle("TGH Treasury")
                                    .addField("Your withdrawal was successfully processed by " + interaction.getUser().getDiscriminatedName(), "Proccessed on: " + text)
                                    .setColor(Color.CYAN)
                                    .setFooter("Necron Banking Command", api.getYourself().getAvatar());

                            org.javacord.api.entity.user.User member = interaction.getApi().getUserById(bankingUser.getDiscordid()).get();
                            member.sendMessage(msgEmbed);
                        }
                    } else if (withdrawal.getWithdrawalType().equals(WithdrawalTypes.MODIFICATION)) {


                        userBank.setCash(userBank.getCash() + withdrawal.getCash());
                        userBank.setFood(userBank.getFood() + withdrawal.getFood());
                        userBank.setCoal(userBank.getCoal() + withdrawal.getCoal());
                        userBank.setIron(userBank.getIron() + withdrawal.getIron());
                        userBank.setOil(userBank.getOil() + withdrawal.getOil());
                        userBank.setUranium(userBank.getUranium() + withdrawal.getUranium());
                        userBank.setLeadRss(userBank.getLeadRss() + withdrawal.getLeadRss());
                        userBank.setBauxite(userBank.getBauxite() + withdrawal.getBauxite());
                        userBank.setGasoline(userBank.getGasoline() + withdrawal.getGasoline());
                        userBank.setMunitions(userBank.getMunitions() + withdrawal.getMunitions());
                        userBank.setSteel(userBank.getSteel() + withdrawal.getSteel());
                        userBank.setAluminum(userBank.getAluminum() + withdrawal.getAluminum());
                        userBank.updateDepositCode();
                        bankDao.save(userBank);
                        withdrawalRepository.delete(withdrawal);
                        interaction.createFollowupMessageBuilder().setContent("Modification recorded successfully.").send();

                    }

                } catch (Exception e) {
                    interaction.createFollowupMessageBuilder().setContent("There was an error fetching the withdrawal. It may be an invalid code.").send();
                    e.printStackTrace();
                }

            } else if (interaction.getOptionByName("modify").isPresent()) {

                SlashCommandInteractionOption option = interaction.getOptionByName("modify").get();
                org.javacord.api.entity.user.User moddedUser = option.getOptionUserValueByName("member").get();

                if (bankDao.findByDiscordid(moddedUser.getIdAsString()) == null) {

                    interaction.createFollowupMessageBuilder().setContent("That user does not have a bank account created.").send();

                } else {

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


                    EmbedBuilder modEmbed = new EmbedBuilder()
                            .setAuthor(interaction.getUser())
                            .setTitle("Bank Modification")
                            .addField("This process is not reversible. Please carefully confirm the modification amounts before continuing.", interaction.getUser().getMentionTag() + " is attempting to modify the bank account of " + moddedUser.getMentionTag());

                    NumberFormat n = NumberFormat.getCurrencyInstance(Locale.US);
                    DecimalFormat d = new DecimalFormat("#,###");

                    String modString = n.format(cash) + "\n<:food:915071870636789792> " + d.format(food) + " <:uranium:1024144769871523870> " + d.format(uranium) + " <:coal:1024144767858266222> " + d.format(coal) + " <:oil:1024144768487391303> " + d.format(oil) + " <:lead:1024144770857177119> " + d.format(lead) + " <:iron:1024144771884793918> " + d.format(iron) + " <:bauxite:1024144773075976243> " + d.format(bauxite) + " <:gasoline:1024144774602702868> " + d.format(gasoline) + " <:munitions:1024144775668051968> " + d.format(munitions) + " <:steel:1024144776548847656> " + d.format(steel) + " <:aluminum:1024144777509347348> " + d.format(aluminum);

                    modEmbed.addField("Modification Amounts", modString)
                            .setFooter("NECRON BADMIN ACCOUNT MODIFICATION", interaction.getApi().getYourself().getAvatar());


                    Withdrawal b = new Withdrawal();
                    b.setCash(cash);
                    b.setFood(food);
                    b.setAluminum(aluminum);
                    b.setBauxite(bauxite);
                    b.setIron(iron);
                    b.setCoal(coal);
                    b.setOil(oil);
                    b.setMunitions(munitions);
                    b.setGasoline(gasoline);
                    b.setSteel(steel);
                    b.setUranium(uranium);
                    b.setLeadRss(lead);

                    b.setDiscordid(option.getOptionUserValueByName("member").get().getIdAsString());
                    b.setWithdrawalType(WithdrawalTypes.MODIFICATION);
                    withdrawalRepository.save(b);

                    interaction.createFollowupMessageBuilder().addEmbed(modEmbed).send();
                    interaction.getChannel().get().sendMessage("Update code:");
                    interaction.getChannel().get().sendMessage(b.getDepositcode());


                }

            } else if (interaction.getOptionByName("view").isPresent()) {

                org.javacord.api.entity.user.User viewUser = interaction.getOptionByName("view").get().getOptionUserValueByName("member").get();
//                        User member = userRepository.findById(interaction.getUser().getIdAsString()).get();


                NumberFormat n = NumberFormat.getCurrencyInstance(Locale.US);
                DecimalFormat d = new DecimalFormat("#,###");

                Bank b = bankDao.findByDiscordid(viewUser.getIdAsString());

                EmbedBuilder emb = new EmbedBuilder()
                        .setTitle("TGH Treasury")
                        .setColor(Color.cyan)
                        .setAuthor(viewUser)
                        .addField("Deposit Code: ", "`" + b.getDepositcode() + "`")
                        .addField("Totals:",
                                n.format(b.getCash()) + " \n<:food:915071870636789792> " + d.format(b.getFood()) + " <:uranium:1024144769871523870> " + d.format(b.getUranium()) + " <:coal:1024144767858266222> " + d.format(b.getCoal()) + " <:oil:1024144768487391303> " + d.format(b.getOil()) + " <:lead:1024144770857177119> " + d.format(b.getLeadRss()) + " <:iron:1024144771884793918> " + d.format(b.getIron()) + " <:bauxite:1024144773075976243> " + d.format(b.getBauxite()) + " <:gasoline:1024144774602702868> " + d.format(b.getGasoline()) + " <:munitions:1024144775668051968> " + d.format(b.getMunitions()) + " <:steel:1024144776548847656> " + d.format(b.getSteel()) + " <:aluminum:1024144777509347348> " + d.format(b.getAluminum())
                        );

                interaction.createFollowupMessageBuilder().addEmbed(emb).send();


            } else if (interaction.getOptionByName("remove").isPresent()) {

                String code = interaction.getOptionByName("remove").get().getOptionStringValueByName("code").get();

                withdrawalRepository.delete(withdrawalRepository.findWithdrawalByDepositcode(code));
                interaction.createFollowupMessageBuilder().setContent("Successfully removed withdrawal/modification.").send();

            } else if (interaction.getOptionByName("generate").isPresent()) {

                org.javacord.api.entity.user.User member = null;

                if (interaction.getOptionByName("generate").get().getOptionUserValueByName("member").isPresent()) {

                    member = interaction.getOptionByName("generate").get().getOptionUserValueByName("member").get();
                    System.out.println("User value specified.");

                } else {
                    member = interaction.getUser();
                }
                Withdrawal b = new Withdrawal();
                b.setDiscordid(member.getIdAsString());
                b.setWithdrawalType(WithdrawalTypes.WITHDRAWAL);
                withdrawalRepository.save(b);


                String nationName = null;

                try {

                    nationName = MilUtil.getNationName(userRepository.findById(member.getIdAsString()).get().getNationid());
                    String fnation = nationName.replaceAll(" ", "+");

                    String withString = "[TGH Bank](https://politicsandwar.com/alliance/id=4567&display=bank&w_money=" + b.getCash() + "&w_food=" + b.getFood() + "&w_coal=" + b.getCoal() + "&w_oil=" + b.getOil() + "&w_uranium=" + b.getUranium() + "&w_lead=" + b.getLeadRss() + "&w_iron=" + b.getIron() + "&w_bauxite=" + b.getBauxite() + "&w_gasoline=" + b.getGasoline() + "&w_munitions=" + b.getMunitions() + "&w_steel=" + b.getSteel() + "&w_aluminum=" + b.getAluminum() + "&w_note=" + b.getDepositcode() + "&w_type=nation&w_recipient=" + fnation + ")";
                    EmbedBuilder embedBuilder = new EmbedBuilder()
                            .setAuthor(interaction.getUser())
                            .addField("Link:", withString);
                    interaction.createFollowupMessageBuilder().setContent("Generated withdrawal code:").send();
                    interaction.getChannel().get().sendMessage(b.getDepositcode());
                    interaction.getChannel().get().sendMessage(embedBuilder);
                } catch (JSONException e) {
                    interaction.createFollowupMessageBuilder().setContent("There was an error generating the withdrawal. \n" + e).send();
                }


            } else if (interaction.getOptionByName("member_deposits").isPresent()) {
                List<Bank> list = bankDao.findAll();
                Bank b = new Bank();
                for (Bank bank : list) {
                    b.setAluminum(b.getAluminum() + bank.getAluminum());
                    b.setBauxite(b.getBauxite() + bank.getBauxite());
                    b.setGasoline(b.getGasoline() + bank.getGasoline());
                    b.setFood(b.getFood() + bank.getFood());
                    b.setCoal(b.getCoal() + bank.getCoal());
                    b.setCash(b.getCash() + bank.getCash());
                    b.setIron(b.getIron() + bank.getIron());
                    b.setOil(b.getOil() + bank.getOil());
                    b.setLeadRss(b.getLeadRss() + bank.getLeadRss());
                    b.setSteel(b.getSteel() + bank.getSteel());
                    b.setMunitions(b.getMunitions() + bank.getMunitions());
                    b.setUranium(b.getUranium() + bank.getUranium());
                }


                NumberFormat n = NumberFormat.getCurrencyInstance(Locale.US);
                DecimalFormat d = new DecimalFormat("#,###");

                EmbedBuilder emb = new EmbedBuilder()
                        .setTitle("TGH Treasury")
                        .setDescription("Combined totals of all member account deposits.")
                        .setColor(Color.cyan)
                        .setAuthor(interaction.getUser())
                        .addField("Totals:",
                                n.format(b.getCash()) + " \n<:food:915071870636789792> " + d.format(b.getFood()) + " <:uranium:1024144769871523870> " + d.format(b.getUranium()) + " <:coal:1024144767858266222> " + d.format(b.getCoal()) + " <:oil:1024144768487391303> " + d.format(b.getOil()) + " <:lead:1024144770857177119> " + d.format(b.getLeadRss()) + " <:iron:1024144771884793918> " + d.format(b.getIron()) + " <:bauxite:1024144773075976243> " + d.format(b.getBauxite()) + " <:gasoline:1024144774602702868> " + d.format(b.getGasoline()) + " <:munitions:1024144775668051968> " + d.format(b.getMunitions()) + " <:steel:1024144776548847656> " + d.format(b.getSteel()) + " <:aluminum:1024144777509347348> " + d.format(b.getAluminum())
                        );

                interaction.createFollowupMessageBuilder().addEmbed(emb).send();

            } else if (interaction.getOptionByName("bankbalance").isPresent()) {

                Bank b = BankUtil.getBankBalance(Long.parseLong(Config.aaId));
                                List<Bank> banks = bankDao.findAll();

                for (Bank bank : banks) {

                    b.setAluminum(b.getAluminum() - bank.getAluminum());
                    b.setBauxite(b.getBauxite() - bank.getBauxite());
                    b.setGasoline(b.getGasoline() - bank.getGasoline());
                    b.setFood(b.getFood() - bank.getFood());
                    b.setCoal(b.getCoal() - bank.getCoal());
                    b.setCash(b.getCash() - bank.getCash());
                    b.setIron(b.getIron() - bank.getIron());
                    b.setOil(b.getOil() - bank.getOil());
                    b.setLeadRss(b.getLeadRss() - bank.getLeadRss());
                    b.setSteel(b.getSteel() - bank.getSteel());
                    b.setMunitions(b.getMunitions() - bank.getMunitions());
                    b.setUranium(b.getUranium() - bank.getUranium());
                }

                if (b.getTotals() < 0) {

                        Bank a = BankUtil.getBankBalance(allianceKeyRepository.findAllianceKeysByAaName("offshore").getId());

                        b.setAluminum(b.getAluminum() + a.getAluminum());
                        b.setBauxite(b.getBauxite() + a.getBauxite());
                        b.setGasoline(b.getGasoline() + a.getGasoline());
                        b.setFood(b.getFood() + a.getFood());
                        b.setCoal(b.getCoal() + a.getCoal());
                        b.setCash(b.getCash() + a.getCash());
                        b.setIron(b.getIron() + a.getIron());
                        b.setOil(b.getOil() + a.getOil());
                        b.setLeadRss(b.getLeadRss() + a.getLeadRss());
                        b.setSteel(b.getSteel() + a.getSteel());
                        b.setMunitions(b.getMunitions() + a.getMunitions());
                        b.setUranium(b.getUranium() + a.getUranium());

                }

                NumberFormat n = NumberFormat.getCurrencyInstance(Locale.US);
                DecimalFormat d = new DecimalFormat("#,###");
                EmbedBuilder emb = new EmbedBuilder()
                        .setTitle("TGH Treasury")
                        .setDescription("Bank balance with all member deposits subtracted.")
                        .setColor(Color.cyan)
                        .setAuthor(interaction.getUser())
                        .addField("Totals:",
                                n.format(b.getCash()) + " \n<:food:915071870636789792> " + d.format(b.getFood()) + " <:uranium:1024144769871523870> " + d.format(b.getUranium()) + " <:coal:1024144767858266222> " + d.format(b.getCoal()) + " <:oil:1024144768487391303> " + d.format(b.getOil()) + " <:lead:1024144770857177119> " + d.format(b.getLeadRss()) + " <:iron:1024144771884793918> " + d.format(b.getIron()) + " <:bauxite:1024144773075976243> " + d.format(b.getBauxite()) + " <:gasoline:1024144774602702868> " + d.format(b.getGasoline()) + " <:munitions:1024144775668051968> " + d.format(b.getMunitions()) + " <:steel:1024144776548847656> " + d.format(b.getSteel()) + " <:aluminum:1024144777509347348> " + d.format(b.getAluminum())
                        );

                interaction.createFollowupMessageBuilder().addEmbed(emb).send();

            } else if (interaction.getOptionByName("bank_audit").isPresent()) {

                List<Bank> list = bankDao.findAll();

                Channel channel = interaction.getChannel().get();


                NumberFormat n = NumberFormat.getCurrencyInstance(Locale.US);
                DecimalFormat d = new DecimalFormat("#,###");

                for (Bank b : list) {

                    try {
                        EmbedBuilder emb = new EmbedBuilder()
                                .setTitle(b.getName() + " ---" + b.getDiscordid())
                                .setColor(Color.cyan)
                                .setAuthor(interaction.getUser())
                                .addField("Deposit Code: ", "`" + b.getDepositcode() + "`")
                                .addField("Totals:",
                                        n.format(b.getCash()) + " \n<:food:915071870636789792> " + d.format(b.getFood()) + " <:uranium:1024144769871523870> " + d.format(b.getUranium()) + " <:coal:1024144767858266222> " + d.format(b.getCoal()) + " <:oil:1024144768487391303> " + d.format(b.getOil()) + " <:lead:1024144770857177119> " + d.format(b.getLeadRss()) + " <:iron:1024144771884793918> " + d.format(b.getIron()) + " <:bauxite:1024144773075976243> " + d.format(b.getBauxite()) + " <:gasoline:1024144774602702868> " + d.format(b.getGasoline()) + " <:munitions:1024144775668051968> " + d.format(b.getMunitions()) + " <:steel:1024144776548847656> " + d.format(b.getSteel()) + " <:aluminum:1024144777509347348> " + d.format(b.getAluminum())
                                );
                        channel.asTextChannel().get().sendMessage(emb);
                    } catch (Exception e) {
                        channel.asTextChannel().get().sendMessage("Error with this account.");
                    }

                }

                interaction.createFollowupMessageBuilder().setContent("Bank audit complete.").send();

            }
//            else if (interaction.getOptionByName("loan").isPresent()) {
//
//                if (interaction.getOptionByName("loan").get().getOptionByName("create").isPresent()) {
//                    org.javacord.api.entity.user.User member = interaction.getOptionByName("loan").get().getOptionByName("create").get().getOptionUserValueByName("member").get();
//
//                    LocalDate date = LocalDate.now().plusDays(interaction.getOptionByName("loan").get().getOptionByName("create").get().getOptionLongValueByName("days").get());
//                    long amount = interaction.getOptionByName("loan").get().getOptionByName("create").get().getOptionLongValueByName("amount").get();
//                    Loan loan = new Loan();
//                    loan.setDiscordid(member.getId());
//                    loan.setActive(true);
//                    loan.setDateDue(date);
//                    loan.setAmount(interaction.getOptionByName("loan").get().getOptionByName("create").get().getOptionLongValueByName("amount").get());
//                    loan.setBanker(interaction.getUser().getIdAsString());
//                    loanRepository.save(loan);
//
//                    NumberFormat n = NumberFormat.getCurrencyInstance(Locale.US);
//
//
//                    EmbedBuilder eb = new EmbedBuilder()
//                            .setTitle("Loan Created for " + member.getDiscriminatedName())
//                            .setAuthor(member)
//                            .addField("Loan Amount: " + n.format(amount), "Due Date: " + date + "\nBanker: " + interaction.getUser().getMentionTag() + "\nLoan ID: " + loan.getId())
//                            .setFooter("Necron Banking Command", interaction.getApi().getYourself().getAvatar());
//
//
//                    interaction.createFollowupMessageBuilder().addEmbed(eb).send();
//
//                }
//
//            } else if (interaction.getOptionByName("loan").get().getOptionByName("remove").isPresent()) {
//
//                Loan loan = loanRepository.findById(interaction.getOptionByName("loan").get().getOptionByName("remove").get().getOptionLongValueByName("loan").get()).get();
//                loanRepository.delete(loan);
//
//                EmbedBuilder eb = new EmbedBuilder()
//                        .setTitle("Loan Removed")
//                        .addField("Loan Removed", "Loan ID: " + loan.getId())
//                        .setFooter("Necron Banking Command", interaction.getApi().getYourself().getAvatar());
//
//                interaction.createFollowupMessageBuilder().addEmbed(eb).send();
//            }


        }

    }
}

