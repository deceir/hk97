package net.hk.hk97.Utils.Econ;

import net.hk.hk97.Models.calc.ConsumptionCity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class FoodConsumption {

    public static double getFoodConsumption(List<ConsumptionCity> cities, long soldiers) {


        LocalDate date = LocalDate.now();

        double cost = 0;

        for (ConsumptionCity city: cities) {

            LocalDate foundedDate = city.getFounded();
            double daysBetween = ChronoUnit.DAYS.between(foundedDate, date);

            cost += ((Math.pow((city.getInfra() * 100), 2) / 125_000_000) + (((city.getInfra() * 100) * (1 + Math.max(Math.log(daysBetween) / 15, 0)) - city.getInfra() * 100)) / 850);

        }

        if (cities.get(1).isAtWar()) {
            return cost  + (soldiers / 500 );
        } else {
            return cost + (soldiers / 750);
        }

    }

    public static double getFoodConsumption(ConsumptionCity city, long soldiers) {


        LocalDate date = LocalDate.now();

        double cost = 0;

            LocalDate foundedDate = city.getFounded();
            double daysBetween = ChronoUnit.DAYS.between(foundedDate, date);

            cost += ((Math.pow((city.getInfra() * 100), 2) / 125_000_000) + (((city.getInfra() * 100) * (1 + Math.max(Math.log(daysBetween) / 15, 0)) - city.getInfra() * 100)) / 850);


        if (city.isAtWar()) {
            return cost  + (soldiers / 500 );
        } else {
            return cost + (soldiers / 750);
        }

    }

}
