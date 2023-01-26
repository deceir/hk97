package net.hk.hk97.Commands.SlashCommands.Commands;

import net.hk.hk97.Models.Bank.Bank;
import net.hk.hk97.Repositories.BankRepository;
import net.hk.hk97.Services.Util.MilUtil;
import net.hk.hk97.Services.Util.WarUtil;
import net.hk.hk97.Services.Util.WcUtil;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.json.JSONException;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WarchestAuditCommand {

    public static void auditWarchests(SlashCommandInteraction interaction, BankRepository bankRepository) throws JSONException {

        List<String> auditStr = new ArrayList<>();




        List<Bank> onhandList = WcUtil.getMemberBanksInMap();
//        List<Bank> bankList = bankRepository.findAll();

        System.out.println("starting initial loop");
        for (Bank bank : onhandList) {

            try {

                String str = "";

                long totalCash = 0;
                long totalGas = 0;
                long totalMunis = 0;
                long totalAlu = 0;
                long totalSteel = 0;

                Bank depBank = bankRepository.findBankByNationid(bank.getNationid());

                System.out.println("trying for nation : " + bank.getName());
                long cities = MilUtil.getCities(bank.getNationid());

                long cash = 1000000;
                long gas = 2300;
                long munis = 2300;
                long alu = 1800;
                long steel = 2800;

                cash *= cities;
                gas *= cities;
                munis *= cities;
                alu *= cities;
                steel *= cities;

                DecimalFormat d = new DecimalFormat("#,###");


                System.out.println("onhand cash: " + depBank.getCash());
                totalCash += bank.getCash() + depBank.getCash();
                totalGas += bank.getGasoline() + depBank.getGasoline();
                totalMunis += bank.getMunitions() + depBank.getMunitions();
                totalAlu += bank.getAluminum() + depBank.getAluminum();
                totalSteel += bank.getSteel() + depBank.getSteel();

                cash -= totalCash;
                System.out.println("cash: " + cash);
                gas -= totalGas;
                System.out.println("gas: " + gas);
                munis -= totalMunis;
                alu -= totalAlu;
                steel -= totalSteel;

                if (cash > 0) {
                    str += " " + d.format(cash) + " cash";
                }
                if (gas > 0) {
                    str += " " + d.format(gas) + " gasoline";
                }
                if (munis > 0) {
                    str += " " + d.format(munis) + " munitions";
                }
                if (alu > 0) {
                    str += " " + d.format(alu) + " aluminum";
                }
                if (steel > 0) {
                    str += " " + d.format(steel) + " steel";
                }

                if (str.length() > 0) {
                    str = bank.getName() + "("+ depBank.getNationid() +") is missing:" + str + "\n";
                    auditStr.add(str);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        if (auditStr.isEmpty()) {
            interaction.createFollowupMessageBuilder().setContent("All nations currently meet warchest requirements!").send();
        } else {
            String message = "```\n";
            message += "Requiem Warchest Audit \n--- \n";
            for (String str : auditStr) {
                message += str;
            }
            message += "```";
            interaction.createFollowupMessageBuilder().setContent(message).send();
        }
    }
}
