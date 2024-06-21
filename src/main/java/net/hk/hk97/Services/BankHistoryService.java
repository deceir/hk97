package net.hk.hk97.Services;

import net.hk.hk97.Config;
import net.hk.hk97.Models.Bank.AllianceBankHistory;
import net.hk.hk97.Models.Bank.Bank;
import net.hk.hk97.Repositories.AllianceKeyRepository;
import net.hk.hk97.Repositories.BankHistoryRepository;
import net.hk.hk97.Repositories.BankRepository;
import net.hk.hk97.Services.Util.BankUtil;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
@EnableScheduling
@Configuration
public class BankHistoryService {

    @Autowired
    BankHistoryRepository bankHistoryDao;

    @Autowired
    BankRepository bankDao;

    @Autowired
    AllianceKeyRepository allianceKeyRepository;

    @Scheduled(cron = "0 0 15 * * *")
    public void updateBankHistory() throws IOException, JSONException {

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

            b = BankUtil.getBankBalanceAlternate(Long.parseLong(Config.aaId), false);
            Bank c = BankUtil.getBankBalanceAlternate(Long.parseLong(Config.adamOffshoreId), true);

            b.setAluminum(b.getAluminum() + c.getAluminum());
            b.setBauxite(b.getBauxite() + c.getBauxite());
            b.setGasoline(b.getGasoline() + c.getGasoline());
            b.setFood(b.getFood() + c.getFood());
            b.setCoal(b.getCoal() + c.getCoal());
            b.setCash(b.getCash() + c.getCash());
            b.setIron(b.getIron() + c.getIron());
            b.setOil(b.getOil() + c.getOil());
            b.setLeadRss(b.getLeadRss() + c.getLeadRss());
            b.setSteel(b.getSteel() + c.getSteel());
            b.setMunitions(b.getMunitions() + c.getMunitions());
            b.setUranium(b.getUranium() + c.getUranium());

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

        }

        AllianceBankHistory bh = new AllianceBankHistory();

        bh.setDate(LocalDate.now());
        bh.setCash(b.getCash());
        bh.setFood(b.getFood());
        bh.setAluminum(b.getAluminum());
        bh.setCoal(b.getCoal());
        bh.setBauxite(b.getBauxite());
        bh.setGasoline(b.getGasoline());
        bh.setIron(b.getIron());
        bh.setOil(b.getOil());
        bh.setMunitions(b.getMunitions());
        bh.setSteel(b.getSteel());
        bh.setUranium(b.getUranium());
        bh.setLeadRss(b.getLeadRss());

        bankHistoryDao.save(bh);

    }

}
