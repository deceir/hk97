package net.hk.hk97.Controllers;

import net.hk.hk97.Models.Bank.Bank;
import net.hk.hk97.Models.User;
import net.hk.hk97.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class UserController {

    @Autowired
    UserRepository userDao;

    @GetMapping("/api/users")
    public @ResponseBody
    ResponseEntity<?> viewAllBanksInJSONFormat() {
        ResponseEntity<List<User>> responseEntity;
        responseEntity = new ResponseEntity<>(userDao.findAll(), HttpStatus.OK);
        return responseEntity;
    }

    @GetMapping("/api/users/{id}")
    public @ResponseBody
    ResponseEntity<?> viewUser(@PathVariable String id) {
        ResponseEntity<User> responseEntity;
        responseEntity = new ResponseEntity<>(userDao.getUserByDiscordid(id), HttpStatus.OK);
        return responseEntity;
    }


}
