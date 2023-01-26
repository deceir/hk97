package net.hk.hk97.Commands.SlashCommands.Commands;

import net.hk.hk97.Models.Bank.Bank;
import net.hk.hk97.Models.WarchestNation;
import net.hk.hk97.Models.WarchestRequirements;
import net.hk.hk97.Models.calc.graphql.repositories.ResourceRepository;
import net.hk.hk97.Repositories.BankRepository;
import net.hk.hk97.Repositories.UserRepository;
import net.hk.hk97.Repositories.WarchestReqsRepository;
import net.hk.hk97.Services.Util.MilUtil;
import net.hk.hk97.Services.Util.WarchestUtil;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class WarchestCommand {





    public static void wc(SlashCommandInteraction interaction, ResourceRepository resourceRepository, UserRepository userRepository, BankRepository bankRepository, WarchestReqsRepository wcReqsRepository) throws JSONException {

//        WarchestRequirements wc = wcReqsRepository.findById(10470L).get();

        long cash = 1000000;
        long gas = 2300;
        long munis = 2300;
        long alu = 1800;
        long steel = 2800;

        long onhandgas = 900;
        long onhandmunis = 900;
        long onhandalu = 800;
        long onhandsteel = 1000;

        NumberFormat n = NumberFormat.getCurrencyInstance(Locale.US);
        DecimalFormat d = new DecimalFormat("#,###");

        if (interaction.getOptionByName("reqs").isPresent()) {
            long cities;

            if (interaction.getOptionByName("reqs").get().getOptionLongValueByName("cities").isPresent()) {
                cities = interaction.getOptionByName("reqs").get().getOptionLongValueByName("cities").get();
            } else {
                cities = MilUtil.getCities(userRepository.findById(interaction.getUser().getIdAsString()).get().getNationid());
            }

            String baseStr = "**" + n.format(cash) + "** <:gasoline:1024144774602702868> " + d.format(gas) + " <:munitions:1024144775668051968> " + d.format(munis) + " <:steel:1024144776548847656> " + d.format(steel) + " <:aluminum:1024144777509347348>" + d.format(alu);

            String amountStr = "**" + n.format(cities * cash) + "** <:gasoline:1024144774602702868> " + d.format(cities * gas) + " <:munitions:1024144775668051968> " + d.format(cities * munis) + " <:steel:1024144776548847656> " + d.format(cities * steel) + " <:aluminum:1024144777509347348> " + d.format(cities * alu);

            String onHandAmountStr = "<:gasoline:1024144774602702868> " + d.format(cities * onhandgas) + " <:munitions:1024144775668051968> " + d.format(cities * onhandmunis) + " <:steel:1024144776548847656> " + d.format(cities * onhandsteel) + " <:aluminum:1024144777509347348> " + d.format(cities * onhandalu);


            onhandgas *= cities;
            onhandmunis *= cities;
            onhandalu *= cities;
            onhandsteel *= cities;

            WarchestNation nation = WarchestUtil.getNationHoldings(userRepository.findById(interaction.getUser().getIdAsString()).get().getNationid());

            long depmunis = 0;
            long depgas = 0;
            long depsteel = 0;
            long depalu = 0;

            if (nation.getMunitions() > onhandmunis) {
            depmunis = nation.getMunitions() - onhandmunis;
            }
            if (nation.getGas() > onhandgas) {
                depgas = nation.getGas() - onhandgas;
            }
            if (nation.getSteel() > onhandsteel) {
                depsteel = nation.getSteel() - onhandsteel;
            }
            if (nation.getAluminum() > onhandalu) {
                depalu = nation.getAluminum() - onhandalu;
            }


            String depString = "[Deposit Link](https://politicsandwar.com/alliance/id=10470&display=bank&d_gasoline=" + depgas + "&d_munitions=" + depmunis + "&d_steel=" + depsteel + "&d_aluminum=" + depalu + "&d_note=" + bankRepository.findByDiscordid(interaction.getUser().getIdAsString()).getDepositcode() + ")";

            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setAuthor(interaction.getUser())
                    .setTitle("Warchest Requirements")
                    .addField("Requirements:", baseStr)
                    .addField(cities + " City Requirements:", amountStr)
                    .addField("Amount to keep on nation:", onHandAmountStr + "\n" + depString + " -- **WARTIME FAST EXCESS DEPOSIT**");

            interaction.createFollowupMessageBuilder().addEmbed(embedBuilder).send();


        } else if (interaction.getOptionByName("calc").isPresent()) {

            long cities;

            if (interaction.getOptionByName("calc").get().getOptionLongValueByName("cities").isPresent()) {
                cities = interaction.getOptionByName("calc").get().getOptionLongValueByName("cities").get();
            } else {
                cities = MilUtil.getCities(userRepository.findById(interaction.getUser().getIdAsString()).get().getNationid());
            }

            long total = 0;
            long gasPrice = resourceRepository.findResourcesByName("GAS").getPrice();
            long muniPrice = resourceRepository.findResourcesByName("MUNIS").getPrice();
            long steelPrice = resourceRepository.findResourcesByName("STEEL").getPrice();
            long aluPrice = resourceRepository.findResourcesByName("ALU").getPrice();

            WarchestNation nation = WarchestUtil.getNationHoldings(userRepository.findById(interaction.getUser().getIdAsString()).get().getNationid());
            Bank bank = bankRepository.findByDiscordid(interaction.getUser().getIdAsString());
            nation.setCash(nation.getCash() + bank.getCash());
            nation.setGas(nation.getGas() + bank.getGasoline());
            nation.setMunitions(nation.getMunitions() + bank.getMunitions());
            nation.setSteel(nation.getSteel() + bank.getSteel());
            nation.setAluminum(nation.getAluminum() + bank.getAluminum());

            long gasReq = cities * gas;
            long steelReq = cities * steel;
            long muniReq = cities * munis;
            long aluReq = cities * alu;
            long cashReq = cities * cash;

            long neededGas = gasReq - nation.getGas();
            long neededCash = cashReq - nation.getCash();
            long neededMuni = muniReq - nation.getMunitions();
            long neededAlu = aluReq - nation.getAluminum();
            long neededSteel = steelReq - nation.getSteel();

            boolean gasB = neededGas > 0;
            boolean cashB = neededCash > 0;
            boolean muniB = neededMuni > 0;
            boolean aluB = neededAlu > 0;
            boolean steelB = neededSteel > 0;

            String neededAmountStr = "Missing: ";

            if (cashB) {
                neededAmountStr += n.format(neededCash) + " ";
                total += neededCash;
            }
            if (gasB) {
                neededAmountStr += "<:gasoline:1024144774602702868> " + d.format(neededGas) + " ";
                total += neededGas * gasPrice;
            }
            if (muniB) {
                neededAmountStr += "<:munitions:1024144775668051968> " + d.format(neededMuni) + " ";
                total += neededMuni * muniPrice;
            }
            if (aluB) {
                neededAmountStr += "<:aluminum:1024144777509347348> " + d.format(neededAlu) + " ";
                total += neededAlu * aluPrice;
            }
            if (steelB) {
                neededAmountStr += "<:steel:1024144776548847656> "  + d.format(neededSteel) + " ";
                total += neededSteel * steel;
            }


            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setTitle("Warchest Calculation")
                    .setDescription("Total cost of your missing warchest values.")
                    .setAuthor(interaction.getUser())
                    .addField(cities + " City Requirement:" , neededAmountStr)
                    .addField("Total:", n.format(total));

            interaction.createFollowupMessageBuilder().addEmbed(embedBuilder).send();



        }
    }
}
