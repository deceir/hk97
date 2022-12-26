package net.hk.hk97.Services;

import io.github.adorableskullmaster.pw4j.PoliticsAndWar;
import io.github.adorableskullmaster.pw4j.PoliticsAndWarBuilder;
import io.github.adorableskullmaster.pw4j.domains.subdomains.SNationContainer;
import net.hk.hk97.Config;
import net.hk.hk97.Models.Nation;
import net.hk.hk97.Repositories.NationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@EnableScheduling
@Configuration
public class NationService {

    @Autowired
    private NationRepository nationsDao;

    @Scheduled(cron = "0 * 12 * * *")
    public void updateNations() throws IOException {

        PoliticsAndWar pnw = new PoliticsAndWarBuilder().setApiKey(Config.itachiPnwKey).build();

        List<SNationContainer> nationList = pnw.getNations(false).getNationsContainer();

        for (SNationContainer nation : nationList) {
            Nation newNation = new Nation();
            newNation.setId(nation.getNationId());
            newNation.setNation(nation.getNation());
            newNation.setAlliance(nation.getAlliance());
            newNation.setAllianceid(nation.getAllianceid());
            newNation.setCities(nation.getCities());
            newNation.setColor(nation.getColor());
            newNation.setAllianceposition(nation.getAllianceposition());
            newNation.setContinent(nation.getContinent());
            newNation.setLeader(nation.getLeader());
            newNation.setDefensivewars(nation.getDefensivewars());
            newNation.setOffensivewars(nation.getOffensivewars());
            newNation.setMinutessinceactive(nation.getMinutessinceactive());
            newNation.setScore(nation.getScore());
            nationsDao.save(newNation);
        }
    }

}
