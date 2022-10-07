package net.hk.hk97.Controllers;

import net.hk.hk97.Models.Bank;
import net.hk.hk97.Repositories.BankRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    ResponseEntity<?> viewAllBanksInJSONFormat() {
        ResponseEntity<List<Bank>> responseEntity;
        List<Bank> list = bankDao.findAll();
        responseEntity = new ResponseEntity<>(bankDao.findAll(), HttpStatus.OK);
        return responseEntity;
    }

}
