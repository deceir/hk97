package net.hk.hk97.Commands.SlashCommands.Commands;

import net.hk.hk97.Config;
import net.hk.hk97.Models.Bank.BankLogs;
import net.hk.hk97.Models.NationWarchestAudited;
import net.hk.hk97.Models.Stats.NationAudited;
import net.hk.hk97.Repositories.BankLogsRepository;
import net.hk.hk97.Repositories.UserRepository;
import net.hk.hk97.Services.Util.Mutations.WithdrawalMutationService;
import net.hk.hk97.Utils.Econ.NationUtil;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.channel.ChannelType;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class SBadminCommand {

    public static void getSuperBadminCommands(SlashCommandInteraction interaction, UserRepository userDao, BankLogsRepository bankLogsDao) throws JSONException, ExecutionException, InterruptedException {

        if (interaction.getOptionByName("grant").isPresent()) {
            SlashCommandInteractionOption option = interaction.getOptionByName("grant").get();

            org.javacord.api.entity.user.User receivingUser = option.getOptionUserValueByName("member").get();


            if (userDao.getUserByDiscordid(receivingUser.getIdAsString()) == null) {

                interaction.createFollowupMessageBuilder().setContent("That user is not registered. Please get them to register if they need to be sent a grant through HK-97.").send();

            } else {

                long nationid = userDao.getUserByDiscordid(receivingUser.getIdAsString()).getNationid();

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

                String note = option.getOptionStringValueByName("note").get();

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

                //security catch
                if (cash >= 1500000000 || food >= 1000000 || oil > 25000 || uranium > 37000 || lead > 25000 || iron > 25000 || bauxite > 25000 || gasoline > 25000 || munitions > 25000 || steel > 25000 || aluminum > 25000 || coal > 25000) {
                    interaction.createFollowupMessageBuilder().setContent("You have entered a number higher than my security protocols will allow. If you believe this is a mistake, please direct all feedback to Itachi.").send();
                } else {
                    BankLogs log = new BankLogs();
                    log.setBanker(interaction.getUser().getIdAsString());
                    log.setBankerName(interaction.getUser().getName());
                    log.setReceiver(receivingUser.getId());
                    log.setCash(cash);
                    log.setBauxite(bauxite);
                    log.setCoal(coal);
                    log.setFood(food);
                    log.setIron(iron);
                    log.setAluminum(aluminum);
                    log.setGasoline(gasoline);
                    log.setOil(oil);
                    log.setSteel(steel);
                    log.setMunitions(munitions);
                    log.setUranium(uranium);
                    log.setLeadRss(lead);
//                    bankLogsDao.save(log);

                    note = "HK-97::" + interaction.getUser().getName() + "::" + note;

                    JSONObject response = WithdrawalMutationService.bankWithdrawal(nationid, cash, food, uranium, coal, oil, iron, bauxite, lead, gasoline, munitions, steel, aluminum, note);

                    if (response.toString().contains("error")) {
                        interaction.createFollowupMessageBuilder().setContent("I have attempted your request. It might have failed, error in response.\n" + response).send();

                        ServerTextChannel loggingChannel = interaction.getApi().getServerTextChannelById(Config.loggingChannelId).get();
                        String msg = "**Grant Failed To Send!**\nSent By: " + interaction.getUser().getName() + " Receiver ID: " + nationid +  "Note: " + note +  " \n";
                        if (cash > 0) {
                            msg += "CASH: $" + cash;
                        }
                        if (food > 0) {
                            msg += " FOOD: " + food;
                        }
                        if (uranium > 0) {
                            msg += " URANIUM: " + uranium;
                        }
                        if (coal > 0) {
                            msg += " COAL: " + coal;
                        }
                        if (oil > 0) {
                            msg += " OIL: " + oil;
                        }
                        if (iron > 0) {
                            msg += " IRON: " + iron;
                        }
                        if (bauxite > 0) {
                            msg += " BAUXITE: " + bauxite;
                        }
                        if (lead > 0) {
                            msg += " LEAD: " + lead;
                        }
                        if (gasoline > 0) {
                            msg += " GASOLINE: " + gasoline;
                        }
                        if (munitions > 0) {
                            msg += " MUNITIONS: " + munitions;
                        }
                        if (steel > 0) {
                            msg += " STEEL: " + steel;
                        }
                        if (aluminum > 0) {
                            msg += " ALUMINUM: " + aluminum;
                        }

                        loggingChannel.sendMessage(msg+ "\n" + response);
                    } else {
                        interaction.createFollowupMessageBuilder().setContent("I have attempted your request. It was probably successful, no error detected in response.").send();
                        ServerTextChannel loggingChannel = interaction.getApi().getServerTextChannelById(Config.loggingChannelId).get();
                        String msg = "**Grant Successfully Sent!**\nSent By: " + interaction.getUser().getName() + " Receiver ID: " + nationid + "Note: " + note +  " \n";
                        if (cash > 0) {
                            msg += "CASH: $" + cash;
                        }
                        if (food > 0) {
                            msg += " FOOD: " + food;
                        }
                        if (uranium > 0) {
                            msg += " URANIUM: " + uranium;
                        }
                        if (coal > 0) {
                            msg += " COAL: " + coal;
                        }
                        if (oil > 0) {
                            msg += " OIL: " + oil;
                        }
                        if (iron > 0) {
                            msg += " IRON: " + iron;
                        }
                        if (bauxite > 0) {
                            msg += " BAUXITE: " + bauxite;
                        }
                        if (lead > 0) {
                            msg += " LEAD: " + lead;
                        }
                        if (gasoline > 0) {
                            msg += " GASOLINE: " + gasoline;
                        }
                        if (munitions > 0) {
                            msg += " MUNITIONS: " + munitions;
                        }
                        if (steel > 0) {
                            msg += " STEEL: " + steel;
                        }
                        if (aluminum > 0) {
                            msg += " ALUMINUM: " + aluminum;
                        }

                        loggingChannel.sendMessage(msg);
                    }


                }

            }

        } else if (interaction.getOptionByName("resource_disperse").isPresent()) {

            LocalDateTime start = LocalDateTime.now();
            //rss stuff

            long days = interaction.getOptionByName("resource_disperse").get().getOptionLongValueByName("days").get();

            if (days > 7) {
                interaction.createFollowupMessageBuilder().setContent("You set the days too high. Cannot be more than 7 days.").send();
            } else {

                List<NationAudited> nationsList = NationUtil.getNationsAudited();
                nationsList.addAll(NationUtil.getNationsAuditedOffshore());
                nationsList = NationUtil.getUsageNationsList(nationsList, days);

                double totalFood = 0;
                double totalUranium = 0;
                double totalOil = 0;
                double totalLead = 0;
                double totalIron = 0;
                double totalCoal = 0;
                double totalBauxite = 0;

                double totalFoodUsed = 0;
                double totalUraniumUsed = 0;
                double totalOilUsed = 0;
                double totalLeadUsed = 0;
                double totalIronUsed = 0;
                double totalCoalUsed = 0;
                double totalBauxiteUsed = 0;


                Channel channel = interaction.getChannel().get().asServerTextChannel().get().createThread(ChannelType.SERVER_PUBLIC_THREAD, "Resource Dispersement - " + LocalDate.now(), 60).get();
                List<NationAudited> nationsInNeed = new ArrayList<>();

                for (NationAudited nation : nationsList) {

                    String msg = "***" + nation.getName() + "*** " + nation.getDiscord() + " " + nation.getId();
                    msg += "\nfood consumption:" + nation.getFoodConsumption();
                    double foodNeeded = ((nation.getFoodConsumption() * days) - nation.getFood());
                    if (foodNeeded < 0) {
                        foodNeeded = 0;
                    }
                    msg += "\nfood needed: " + foodNeeded;
                    double uraNeeded = nation.getUraniumUsed() - nation.getUranium();
                    if (uraNeeded < 0) {
                        uraNeeded = 0;
                    }
                    msg += "\nura needed: " + uraNeeded;
                    double oilNeeded = nation.getOilUsed() - nation.getOil();
                    if (oilNeeded < 0) {
                        oilNeeded = 0;
                    }
                    msg += "\noil needed: " + oilNeeded;
                    double leadNeeded = nation.getLeadUsed() - nation.getLead();
                    if (leadNeeded < 0) {
                        leadNeeded = 0;
                    }
                    msg += "\nlead needed: " + leadNeeded;
                    double ironNeeded = nation.getIronUsed() - nation.getIron();
                    if (ironNeeded < 0) {
                        ironNeeded = 0;
                    }
                    msg += "\n iron needed: " + ironNeeded;
                    double coalNeeded = nation.getCoalUsed() - nation.getCoal();
                    if (coalNeeded < 0) {
                        coalNeeded = 0;
                    }
                    msg += "\ncoal needed: " + coalNeeded;
                    double bauxNeeded = nation.getBauxUsed() - nation.getBauxite();
                    if (bauxNeeded < 0) {
                        bauxNeeded = 0;
                    }
                    msg += "\nbaux needed: " + bauxNeeded;

                    totalFood += foodNeeded;
                    totalUranium += uraNeeded;
                    totalOil += oilNeeded;
                    totalLead += leadNeeded;
                    totalIron += ironNeeded;
                    totalCoal += coalNeeded;
                    totalBauxite += bauxNeeded;

                    totalFoodUsed += (nation.getFoodConsumption() * days);
                    totalUraniumUsed += nation.getUraniumUsed();
                    totalOilUsed += nation.getOilUsed();
                    totalLeadUsed += nation.getLeadUsed();
                    totalIronUsed += nation.getIronUsed();
                    totalCoalUsed += nation.getCoalUsed();
                    totalBauxiteUsed += nation.getBauxUsed();

                    if ((foodNeeded + uraNeeded + coalNeeded + oilNeeded + ironNeeded + bauxNeeded + leadNeeded) > 0) {
                        channel.asTextChannel().get().sendMessage(msg);
                        nationsInNeed.add(nation);

                    }
                }



                DecimalFormat n = new DecimalFormat("#,###");

                String endMsg = "Total Food Needed: " + n.format(totalFood);
                endMsg += "\nTotal Uranium Needed: " + n.format(totalUranium);
                endMsg += "\nTotal Oil Needed: " + n.format(totalOil);
                endMsg += "\nTotal Lead Needed: " + n.format(totalLead);
                endMsg += "\nTotal Iron Needed: " + n.format(totalIron);
                endMsg += "\nTotal Coal Needed: " + n.format(totalCoal);
                endMsg += "\nTotal Bauxite Needed: " + n.format(totalBauxite);
                endMsg += "\nThis is for: " + days + "day(s)";
                String usedMsg = "Total Food USED: " + n.format(totalFoodUsed);
                usedMsg += "\nTotal Uranium USED: " + n.format(totalUraniumUsed);
                usedMsg += "\nTotal Oil USED: " + n.format(totalOilUsed);
                usedMsg += "\nTotal Lead USED: " + n.format(totalLeadUsed);
                usedMsg += "\nTotal Iron USED: " + n.format(totalIronUsed);
                usedMsg += "\nTotal Coal USED: " + n.format(totalCoalUsed);
                usedMsg += "\nTotal Bauxite USED: " + n.format(totalBauxiteUsed);
                usedMsg += "\nThis is for: " + days + "day(s)";
                interaction.createFollowupMessageBuilder().setContent("Completing your request.").send();
                interaction.createFollowupMessageBuilder().setContent(endMsg).send();
                interaction.createFollowupMessageBuilder().setContent(usedMsg).send();

                new MessageBuilder()
                        .setContent("Automatic Resource Dispersement Protocols")
                        .addComponents(
                                ActionRow.of(Button.danger("sendresourcesauto", "Send Resources"))

                        ).send(interaction.getChannel().get());


                interaction.getApi().addButtonClickListener(buttonClickEvent -> {
                    String buttonId = buttonClickEvent.getButtonInteraction().getCustomId();


                    buttonClickEvent.getInteraction().respondLater();
                    switch (buttonId) {

                        case "sendresourcesauto":
                            if (buttonClickEvent.getInteraction().getUser().getIdAsString().equals(interaction.getUser().getIdAsString())) {
                                buttonClickEvent.getButtonInteraction().createFollowupMessageBuilder().setContent("Sending resources now...").send();
                                ServerTextChannel serverTextChannel = interaction.getApi().getServerTextChannelById("1213894840635625562").get();

                                serverTextChannel.sendMessage("__***Attention Growth Program Members:***__\nResources have been sent to everyone for the next " + days + "day(s). If you are currently blockaded, you will not have received any resources. Contact Econ in this case as soon as your blockade has been cleared.\n*Banker: " + interaction.getUser().getName() + "*");
                                buttonClickEvent.getButtonInteraction().getMessage().delete();

                                //sending
                                for (NationAudited nation : nationsInNeed) {


                                    double foodNeeded = ((nation.getFoodConsumption() * days) - nation.getFood());
                                    if (foodNeeded < 0) {
                                        foodNeeded = 0;
                                    }

                                    double uraNeeded = nation.getUraniumUsed() - nation.getUranium();
                                    if (uraNeeded < 0) {
                                        uraNeeded = 0;
                                    }

                                    double oilNeeded = nation.getOilUsed() - nation.getOil();
                                    if (oilNeeded < 0) {
                                        oilNeeded = 0;
                                    }

                                    double leadNeeded = nation.getLeadUsed() - nation.getLead();
                                    if (leadNeeded < 0) {
                                        leadNeeded = 0;
                                    }

                                    double ironNeeded = nation.getIronUsed() - nation.getIron();
                                    if (ironNeeded < 0) {
                                        ironNeeded = 0;
                                    }

                                    double coalNeeded = nation.getCoalUsed() - nation.getCoal();
                                    if (coalNeeded < 0) {
                                        coalNeeded = 0;
                                    }
                                    double bauxNeeded = nation.getBauxUsed() - nation.getBauxite();
                                    if (bauxNeeded < 0) {
                                        bauxNeeded = 0;
                                    }
                                    if ((foodNeeded + uraNeeded + coalNeeded + oilNeeded + ironNeeded + bauxNeeded + leadNeeded) > 0) {

                                        try {
                                            JSONObject response = WithdrawalMutationService.bankWithdrawal(nation.getId(), 0, foodNeeded, uraNeeded, coalNeeded, oilNeeded, ironNeeded, bauxNeeded, leadNeeded, 0, 0, 0, 0, "HK-97::" + "RESOURCE DISPERSEMENT SENT BY::" + interaction.getUser().getName());

                                            if (response.toString().contains("error")) {
                                                channel.asTextChannel().get().sendMessage("There was likely an error sending the resources to: " + nation.getLeader() + " / " + nation.getName() + " / " + nation.getDiscord());
                                            } else {
                                                String sentMsg = "\nFood: " + foodNeeded + " Uranium: " + uraNeeded + " Coal: " + coalNeeded + " Oil: " + oilNeeded + " Iron: " + ironNeeded + " Bauxite: " + bauxNeeded + " Lead: " + leadNeeded;
                                                channel.asTextChannel().get().sendMessage("The following has been sent to: " + nation.getLeader() + " / " + nation.getName() + " / " + nation.getDiscord() + sentMsg);
//                                                User user = interaction.getApi().getUserById(userDao.findUserByNationid(nation.getId()).getDiscordid()).get();
//                                                user.sendMessage("The following has been sent to: " + nation.getLeader() + " / " + nation.getName() + " / " + nation.getDiscord() + sentMsg);

                                                //logging channel
                                                ServerTextChannel loggingChannel = interaction.getApi().getServerTextChannelById(Config.loggingChannelId).get();
                                                String msg = "**Resource Dispersement!**\nSent By: " + interaction.getUser().getName() + " Receiver ID: " + nation.getId() +  " | ";

                                                if (foodNeeded > 0) {
                                                    msg += " FOOD: " + foodNeeded;
                                                }
                                                if (uraNeeded > 0) {
                                                    msg += " URANIUM: " + uraNeeded;
                                                }
                                                if (coalNeeded > 0) {
                                                    msg += " COAL: " + coalNeeded;
                                                }
                                                if (oilNeeded > 0) {
                                                    msg += " OIL: " + oilNeeded;
                                                }
                                                if (ironNeeded > 0) {
                                                    msg += " IRON: " + ironNeeded;
                                                }
                                                if (bauxNeeded > 0) {
                                                    msg += " BAUXITE: " + bauxNeeded;
                                                }
                                                if (leadNeeded > 0) {
                                                    msg += " LEAD: " + leadNeeded;
                                                }
                                                loggingChannel.sendMessage(msg);
                                            }
                                        } catch (Exception e) {
                                            interaction.createFollowupMessageBuilder().setContent("There was an issue sending. " + nation.getName() + " " + nation.getDiscord() + " " + nation.getLeader() + "\n" + e).send();
                                        }

                                    }
                                }


                            } else {
                                buttonClickEvent.getInteraction().createFollowupMessageBuilder().setContent("<@" + buttonClickEvent.getInteraction().getUser().getIdAsString() + "> this is not a button for you!").send();
                            }

                    }


                }).removeAfter(3, TimeUnit.MINUTES);

            }

        } else if (interaction.getOptionByName("onhand_wc").isPresent()) {
            //give wc amounts 900 gas 1k munitions 1.2k steel 700 aluminium

            List<NationWarchestAudited> warchestAuditeds = NationUtil.getWarchestAudits();

            double steelTotal = 0;
            double gasTotal = 0;
            double aluTotal = 0;
            double muniTotal = 0;

            Channel channel = interaction.getChannel().get().asServerTextChannel().get().createThread(ChannelType.SERVER_PUBLIC_THREAD, "Warchest Dispersement - " + LocalDate.now(), 60).get();
            List<NationWarchestAudited> nationsInNeed = new ArrayList<>();

            for (NationWarchestAudited nationWarchestAudited : warchestAuditeds) {
                double steelReq = 1200 * nationWarchestAudited.getCities();
                double gasReq = 900 * nationWarchestAudited.getCities();
                double aluReq = 700 * nationWarchestAudited.getCities();
                double muniReq = 1000 * nationWarchestAudited.getCities();

                double steelToSend = Math.round(steelReq - nationWarchestAudited.getSteel());
                double gasToSend = Math.round(gasReq - nationWarchestAudited.getGasoline());
                double aluToSend = Math.round(aluReq - nationWarchestAudited.getAluminum());
                double muniToSend = Math.round(muniReq - nationWarchestAudited.getMunitions());

                if (steelToSend < 0) {
                    steelToSend = 0;
                }
                if (gasToSend < 0) {
                    gasToSend = 0;
                }
                if (aluToSend < 0) {
                    aluToSend = 0;
                }
                if (muniToSend < 0) {
                    muniToSend = 0;
                }

                steelTotal += steelToSend;
                gasTotal += gasToSend;
                aluTotal += aluToSend;
                muniTotal += muniToSend;

                nationWarchestAudited.setSteelToSend(steelToSend);
                nationWarchestAudited.setGasolineToSend(gasToSend);
                nationWarchestAudited.setAluminumToSend(aluToSend);
                nationWarchestAudited.setMunitionsToSend(muniToSend);

                if (steelToSend > 0 || gasToSend > 0 || aluToSend > 0 || muniToSend > 0) {
                    nationsInNeed.add(nationWarchestAudited);

                    String msg = "***" + nationWarchestAudited.getName() + "*** " + nationWarchestAudited.getDiscord() + " " + nationWarchestAudited.getId();

                    if (steelToSend > 0) {
                        msg += "\nSteel Needed: " + steelToSend;
                    }
                    if (gasToSend > 0) {
                        msg += "\nGas Needed: " + gasToSend;
                    }
                    if (aluToSend > 0) {
                        msg += "\nAlu Needed: " + aluToSend;
                    }
                    if (muniToSend > 0) {
                        msg += "\nMunis Needed: " + muniToSend;
                    }

                    channel.asTextChannel().get().sendMessage(msg);

                }



            }

            //send totals message
            DecimalFormat n = new DecimalFormat("#,###");

            String endMsg = "Total Steel Needed: " + n.format(steelTotal);
            endMsg += "\nTotal Gasoline Needed: " + n.format(gasTotal);
            endMsg += "\nTotal Aluminum Needed: " + n.format(aluTotal);
            endMsg += "\nTotal Munitions Needed: " + n.format(muniTotal);


            interaction.createFollowupMessageBuilder().setContent("Completing your request.").send();
            interaction.createFollowupMessageBuilder().setContent(endMsg).send();


            new MessageBuilder()
                    .setContent("Automatic Warchest On-Hand Dispersement Protocols")
                    .addComponents(
                            ActionRow.of(Button.danger("sendwarchest", "Send WC Resources"))

                    ).send(interaction.getChannel().get());


            interaction.getApi().addButtonClickListener(buttonClickEvent -> {
                String buttonId = buttonClickEvent.getButtonInteraction().getCustomId();


                buttonClickEvent.getInteraction().respondLater();
                switch (buttonId) {

                    case "sendwarchest":
                        buttonClickEvent.getButtonInteraction().getMessage().delete();

                        for(NationWarchestAudited nation : nationsInNeed) {



                            try {
                                JSONObject response = WithdrawalMutationService.bankWithdrawal(nation.getId(), 0, 0, 0, 0, 0, 0, 0, 0, nation.getGasolineToSend(), nation.getMunitionsToSend(), nation.getSteelToSend(), nation.getAluminumToSend(), "HK-97::" + "WARCHEST ON-HAND RSS SENT BY " + interaction.getUser().getName());

                                if (response.toString().contains("error")) {
                                    channel.asTextChannel().get().sendMessage("There was likely an error sending the resources to: " + nation.getLeader() + " / " + nation.getName() + " / " + nation.getDiscord());
                                } else {
                                    String sentMsg = "\nSteel: " + nation.getSteelToSend() + " Gasoline: " + nation.getGasolineToSend() + " Aluminum: " + nation.getAluminumToSend() + " Munitions: " + nation.getMunitionsToSend();
                                    channel.asTextChannel().get().sendMessage("The following has been sent to: " + nation.getLeader() + " / " + nation.getName() + " / " + nation.getDiscord() + sentMsg);
//                                                User user = interaction.getApi().getUserById(userDao.findUserByNationid(nation.getId()).getDiscordid()).get();
//                                                user.sendMessage("The following has been sent to: " + nation.getLeader() + " / " + nation.getName() + " / " + nation.getDiscord() + sentMsg);

                                    //logging channel
                                    ServerTextChannel loggingChannel = interaction.getApi().getServerTextChannelById(Config.loggingChannelId).get();
                                    String msg = "**Resource Dispersement!**\nSent By: " + interaction.getUser().getName() + " Receiver ID: " + nation.getId() +  " | ";

                                    if (nation.getSteelToSend() > 0) {
                                        msg += " STEEL: " + nation.getSteelToSend();
                                    }
                                    if (nation.getGasolineToSend() > 0) {
                                        msg += " GASOLINE: " + nation.getGasolineToSend();
                                    }
                                    if (nation.getAluminumToSend() > 0) {
                                        msg += " ALUMINUM: " + nation.getAluminumToSend();
                                    }
                                    if (nation.getMunitionsToSend() > 0) {
                                        msg += " OIL: " + nation.getMunitionsToSend();
                                    }
                                    loggingChannel.sendMessage(msg);
                                }
                            } catch (Exception e) {
                                interaction.createFollowupMessageBuilder().setContent("There was an issue sending. " + nation.getName() + " " + nation.getDiscord() + " " + nation.getLeader() + "\n" + e).send();
                            }

                        }

                }
            }).removeAfter(3, TimeUnit.MINUTES);


        }
    }
}
