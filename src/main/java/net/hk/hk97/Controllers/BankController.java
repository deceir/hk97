package net.hk.hk97.Controllers;

import net.hk.hk97.Models.Bank;
import net.hk.hk97.Repositories.BankRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class BankController {


    @Autowired
    private BankRepository bankDao;

    @GetMapping("/banks")
    public @ResponseBody
    List<Bank> viewAllBanksInJSONFormat() {
        return bankDao.findAll();
    }

}
