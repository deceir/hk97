package net.hk.hk97.Models.calc.graphql.services;


import io.github.adorableskullmaster.pw4j.PoliticsAndWar;
import io.github.adorableskullmaster.pw4j.domains.TradePrice;
import io.github.adorableskullmaster.pw4j.enums.ResourceType;
import net.hk.hk97.Config;
import net.hk.hk97.Models.calc.graphql.models.Resources;
import net.hk.hk97.Models.calc.graphql.repositories.ResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;

@Configuration
@EnableScheduling
public class ResourceService {

    @Autowired
    ResourceRepository resourceDao;

        @Scheduled(cron = "0 40 * * * *")
    public void getResourcePrices() {


        String apiKeys = Config.itachiPnwKey;

            PoliticsAndWar pnw = PoliticsAndWar.getDefaultInstance(apiKeys);

            try {

                TradePrice gasolinePrice = pnw.getTradeprice(ResourceType.GASONLINE);
                TradePrice aluminumPrice = pnw.getTradeprice(ResourceType.ALUMINUM);
                TradePrice steelPrice = pnw.getTradeprice(ResourceType.STEEL);
                TradePrice munitionsPrice = pnw.getTradeprice(ResourceType.MUNITIONS);
                TradePrice leadPrice = pnw.getTradeprice(ResourceType.LEAD);
                TradePrice oilPrice = pnw.getTradeprice(ResourceType.OIL);
                TradePrice foodPrice = pnw.getTradeprice(ResourceType.FOOD);
                TradePrice bauxitePrice = pnw.getTradeprice(ResourceType.BAUXITE);
                TradePrice coalPrice = pnw.getTradeprice(ResourceType.COAL);
                TradePrice ironPrice = pnw.getTradeprice(ResourceType.IRON);
                TradePrice uraPrice = pnw.getTradeprice(ResourceType.URANIUM);

                try {
                    Resources gasoline = new Resources();
                    gasoline.setName("GAS");
                    gasoline.setPrice(Long.parseLong(gasolinePrice.getAvgprice()));
                    gasoline.setLast_updated(LocalDateTime.now());
                    resourceDao.save(gasoline);
                } catch (Exception e) {
                    Resources gasoline = resourceDao.findResourcesByName("GAS");
                    gasoline.setPrice(Long.parseLong(gasolinePrice.getAvgprice()));
                    gasoline.setLast_updated(LocalDateTime.now());
                    resourceDao.save(gasoline);
                }
                try {
                    Resources aluminum = new Resources();
                    aluminum.setName("ALU");
                    aluminum.setPrice(Long.parseLong(aluminumPrice.getAvgprice()));
                    aluminum.setLast_updated(LocalDateTime.now());
                    resourceDao.save(aluminum);
                } catch (Exception e) {
                    Resources gasoline = resourceDao.findResourcesByName("ALU");
                    gasoline.setPrice(Long.parseLong(aluminumPrice.getAvgprice()));
                    gasoline.setLast_updated(LocalDateTime.now());
                    resourceDao.save(gasoline);
                }
                try {
                    Resources steel = new Resources();
                    steel.setName("STEEL");
                    steel.setPrice(Long.parseLong(steelPrice.getAvgprice()));
                    steel.setLast_updated(LocalDateTime.now());
                    resourceDao.save(steel);
                } catch (Exception e) {
                    Resources gasoline = resourceDao.findResourcesByName("STEEL");
                    gasoline.setPrice(Long.parseLong(steelPrice.getAvgprice()));
                    gasoline.setLast_updated(LocalDateTime.now());
                    resourceDao.save(gasoline);
                }
                try {
                    Resources munitions = new Resources();
                    munitions.setName("MUNIS");
                    munitions.setPrice(Long.parseLong(munitionsPrice.getAvgprice()));
                    munitions.setLast_updated(LocalDateTime.now());
                    resourceDao.save(munitions);
                } catch (Exception e) {
                    Resources gasoline = resourceDao.findResourcesByName("MUNIS");
                    gasoline.setPrice(Long.parseLong(munitionsPrice.getAvgprice()));
                    gasoline.setLast_updated(LocalDateTime.now());
                    resourceDao.save(gasoline);
                }
                try {
                    Resources lead = new Resources();
                    lead.setName("LEAD");
                    lead.setPrice(Long.parseLong(leadPrice.getAvgprice()));
                    lead.setLast_updated(LocalDateTime.now());
                    resourceDao.save(lead);
                } catch (Exception e) {
                    Resources lead = resourceDao.findResourcesByName("LEAD");
                    lead.setPrice(Long.parseLong(leadPrice.getAvgprice()));
                    lead.setLast_updated(LocalDateTime.now());
                    resourceDao.save(lead);
                }
                try {
                    Resources oil = new Resources();
                    oil.setName("OIL");
                    oil.setPrice(Long.parseLong(oilPrice.getAvgprice()));
                    oil.setLast_updated(LocalDateTime.now());
                    resourceDao.save(oil);
                } catch (Exception e) {
                    Resources gasoline = resourceDao.findResourcesByName("OIL");
                    gasoline.setPrice(Long.parseLong(oilPrice.getAvgprice()));
                    gasoline.setLast_updated(LocalDateTime.now());
                    resourceDao.save(gasoline);
                }
                try {
                    Resources gasoline = new Resources();
                    gasoline.setName("FOOD");
                    gasoline.setPrice(Long.parseLong(foodPrice.getAvgprice()));
                    gasoline.setLast_updated(LocalDateTime.now());
                    resourceDao.save(gasoline);
                } catch (Exception e) {
                    Resources gasoline = resourceDao.findResourcesByName("FOOD");
                    gasoline.setPrice(Long.parseLong(foodPrice.getAvgprice()));
                    gasoline.setLast_updated(LocalDateTime.now());
                    resourceDao.save(gasoline);
                }
                try {
                    Resources gasoline = new Resources();
                    gasoline.setName("BAUX");
                    gasoline.setPrice(Long.parseLong(bauxitePrice.getAvgprice()));
                    gasoline.setLast_updated(LocalDateTime.now());
                    resourceDao.save(gasoline);
                } catch (Exception e) {
                    Resources gasoline = resourceDao.findResourcesByName("BAUX");
                    gasoline.setPrice(Long.parseLong(bauxitePrice.getAvgprice()));
                    gasoline.setLast_updated(LocalDateTime.now());
                    resourceDao.save(gasoline);
                }
                try {
                    Resources gasoline = new Resources();
                    gasoline.setName("COAL");
                    gasoline.setPrice(Long.parseLong(coalPrice.getAvgprice()));
                    gasoline.setLast_updated(LocalDateTime.now());
                    resourceDao.save(gasoline);
                } catch (Exception e) {
                    Resources gasoline = resourceDao.findResourcesByName("COAL");
                    gasoline.setPrice(Long.parseLong(coalPrice.getAvgprice()));
                    gasoline.setLast_updated(LocalDateTime.now());
                    resourceDao.save(gasoline);
                }
                try {
                    Resources gasoline = new Resources();
                    gasoline.setName("IRON");
                    gasoline.setPrice(Long.parseLong(ironPrice.getAvgprice()));
                    gasoline.setLast_updated(LocalDateTime.now());
                    resourceDao.save(gasoline);
                } catch (Exception e) {
                    Resources gasoline = resourceDao.findResourcesByName("IRON");
                    gasoline.setPrice(Long.parseLong(ironPrice.getAvgprice()));
                    gasoline.setLast_updated(LocalDateTime.now());
                    resourceDao.save(gasoline);
                }
                try {
                    Resources gasoline = new Resources();
                    gasoline.setName("URA");
                    gasoline.setPrice(Long.parseLong(uraPrice.getAvgprice()));
                    gasoline.setLast_updated(LocalDateTime.now());
                    resourceDao.save(gasoline);
                } catch (Exception e) {
                    Resources gasoline = resourceDao.findResourcesByName("URA");
                    gasoline.setPrice(Long.parseLong(uraPrice.getAvgprice()));
                    gasoline.setLast_updated(LocalDateTime.now());
                    resourceDao.save(gasoline);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
    }
}
