package net.hk.hk97.Services;

import net.hk.hk97.Models.User;
import net.hk.hk97.Repositories.UserRepository;
import net.hk.hk97.Services.Util.MilUtil;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@EnableScheduling
@Configuration
public class UserNationNameService {

    @Autowired
    public UserRepository userDao;

    @Scheduled(cron = "0 0 12 * * *")
    public void updateLeaderNames() throws JSONException {

        List<User> list = userDao.findAll();

        for (User user : list) {

            user.setLeadername(MilUtil.getLeaderName(user.getNationid()));
            userDao.save(user);
        }

    }
//
//    @Scheduled(cron = "0 0 10 * * *")
//    public void updateNationNames() throws JSONException {
//
//        List<User> list = userDao.findAll();
//
//        for (User user : list) {
//
//            user.setNation(MilUtil.getNationName(user.getNationid()));
//            userDao.save(user);
//        }
//
//    }
}
