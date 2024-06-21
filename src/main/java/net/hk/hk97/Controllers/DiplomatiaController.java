package net.hk.hk97.Controllers;

import net.hk.hk97.Models.Diplomatia.DiplomatiaSignup;
import net.hk.hk97.Repositories.DiplomatiaSignupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class DiplomatiaController {

    @Autowired
    private DiplomatiaSignupRepository diplomatiaSignupRepository;


    @PostMapping("/api/diplomatia/signup")
    public ResponseEntity<HttpStatus> acceptSignups(@RequestBody DiplomatiaSignup signup) {
        DiplomatiaSignup newSignup = new DiplomatiaSignup();
        newSignup.setEmailAddress(signup.getEmailAddress());
        diplomatiaSignupRepository.save(newSignup);

        ResponseEntity<HttpStatus> response = new ResponseEntity<>(HttpStatus.ACCEPTED);


        return response;
    }
}
