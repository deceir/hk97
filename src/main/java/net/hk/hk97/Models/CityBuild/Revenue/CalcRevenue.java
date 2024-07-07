package net.hk.hk97.Models.CityBuild.Revenue;

import net.hk.hk97.Models.calc.ConsumptionCity;
import net.hk.hk97.Utils.Econ.FoodConsumption;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class CalcRevenue {

    public static CityRevenue CalcRevenue(int coalPower, int oilPower, int windPower, int nuclearPower, int coalMine, int oilWell, int uraMine, int leadMine, int ironMine, int bauxMine, int farm, int gasRefinery, int aluRefinery, int muniFactory, int steelFactory, int policeStation, int hospital, int recyclingCenter, int subway, int supermarket, int bank, int mall, int stadium, int infrastructure, int land, boolean itc, boolean telecomSat, boolean greenTech, boolean recylcyingInitiative, boolean openMarkets, boolean GSA, String date, boolean armsStockpile, boolean gasolineReserve, boolean bauxworks, boolean ironworks, boolean irrigation, double continentRadiation, double globalRadiation, boolean uraniumEnrichment) {


        CityRevenue cityRevenue = new CityRevenue();

        //calc commerce
        double commerce = 0;
        commerce += (supermarket * 3);
        commerce += (bank * 5);
        commerce += (mall * 9);
        commerce += (stadium * 12);
        commerce += (subway * 8);
        if(commerce > 100 && telecomSat) {
            commerce += 2;
            if (commerce > 125) {
                commerce = 125;
            }
        } else if (commerce > 100 && itc) {
            if (commerce > 115) {
                commerce = 115;
            }
        } else if (commerce > 100) {
            commerce = 100;
        }
        //pollution
        double pollutionModifier = 0;
        pollutionModifier += (coalPower * 8);
        pollutionModifier += (oilPower * 6);
        pollutionModifier += (coalMine * 12);
        pollutionModifier += (bauxMine * 12);
        pollutionModifier += (leadMine * 12);
        pollutionModifier += (policeStation);
        pollutionModifier += (hospital * 4);
        if (greenTech) {
            pollutionModifier += ((farm * 2) * 0.5);
            pollutionModifier += ((gasRefinery * 32) * 0.75);
            pollutionModifier += ((steelFactory * 40) * 0.75);
            pollutionModifier += ((aluRefinery * 40) * 0.75);
            pollutionModifier += ((muniFactory * 32) * 0.75);

            pollutionModifier -= (subway * 45);
        } else {
            pollutionModifier += ((farm * 2));
            pollutionModifier += ((gasRefinery * 32));
            pollutionModifier += ((steelFactory * 40));
            pollutionModifier += ((aluRefinery * 40));
            pollutionModifier += ((muniFactory * 32));
            pollutionModifier -= (subway * 20);

        }
    if (recylcyingInitiative) {
        pollutionModifier -= (recyclingCenter * 75);
    } else {
        pollutionModifier -= (recyclingCenter * 70);
    }
    if (pollutionModifier < 0) {
        pollutionModifier = 0;
    }

    //crime
        double policeModifier = (policeStation * 2.5);
        double crimePercent = ((Math.pow((103 - commerce), 2) + (infrastructure * 100)) / 111111) - policeModifier;

        double basePopulation = infrastructure * 100;
        double popDensity = basePopulation / land;
        double hospitalModifier = hospital * 2.5;

        //disease
        double disease = ((((Math.pow(popDensity, 2) * 0.01) -25) / 100) + (basePopulation / 100000) + pollutionModifier - hospitalModifier);

        double income = CalcIncome(infrastructure, disease, crimePercent, openMarkets, GSA, commerce, date);

        double expenses = 0;

        //power upkeep
        expenses += (coalPower * 1200) + (oilPower * 1800) + (nuclearPower * 10500) + (windPower * 500);
        //raw upkeep
        if (greenTech) {
            expenses += ((coalMine * 400) + (ironMine *1600) + (uraMine * 5000) + (leadMine * 1500) + (oilWell * 600) + (bauxMine * 1600) + (farm * 300) * 0.9);
        } else {
            expenses += ((coalMine * 400) + (ironMine *1600) + (uraMine * 5000) + (leadMine * 1500) + (oilWell * 600) + (bauxMine * 1600) + (farm * 300));
        }
        //manu upkeep
        expenses += ((gasRefinery * 4000) + (steelFactory * 4000) + (aluRefinery * 2500) + (muniFactory * 2500));
        //civil upkeep
        expenses += ((policeStation * 750) + (hospital * 1000) + (recyclingCenter * 2500) + (subway * 3250));
        //commerce upkeep
        expenses += ((supermarket * 600) + (mall * 5400) + (stadium * 12150) + (bank *1800));

        //raw usage
        double ironUsed = 0;
        double oilUsed = 0;
        double coalUsed = 0;
        double leadUsed = 0;
        double bauxUsed = 0;
        double uraniumUsed = 0;


        int timesDivisible = 0;


        //ura
        if (nuclearPower > 0) {
            int i = infrastructure;
            while (i > 0) {
                timesDivisible++;
                i = i - 1000;
            }
            uraniumUsed += ((2.4 * timesDivisible) * nuclearPower);

        }

        //lead
        if (armsStockpile && muniFactory > 0) {
            leadUsed += (12.064 * muniFactory);
        } else if (muniFactory > 0) {
            leadUsed += (6 * muniFactory);
        }

        //oil
        if (gasolineReserve && gasRefinery > 0) {
            oilUsed += (9 * gasRefinery);
        } else if (gasRefinery > 0) {
            oilUsed += (3 * gasRefinery);
        }

        //bauxite
        if (bauxworks && aluRefinery > 0) {
            bauxUsed += (6.12 * aluRefinery);
        } else if (aluRefinery > 0) {
            bauxUsed += (3 * aluRefinery);
        }

        //coal and iron
        if (ironworks && steelFactory > 0) {
            coalUsed += (6.12 * steelFactory);
            ironUsed += (6.12 * steelFactory);
        } else if (steelFactory > 0) {
            coalUsed += (3 * steelFactory);
            ironUsed += (3 * steelFactory);
        }


        //food production
        double foodProduction = 0;

        if (irrigation) {
            foodProduction = (farm * (land / 400)) * 1 + (0.5 * (farm - 1)) / (20 - 1);

        } else {
            foodProduction = (farm * (land / 500)) * 1 + (0.5 * (farm - 1)) / (20 - 1);


        }
        foodProduction = foodProduction * (1 - (continentRadiation + globalRadiation) / 1000);

        //bonus = 1 + (0.5 * (current_count - 1)) / (max_count - 1);
        //manu rss production
        double munitions = 0;
        if (armsStockpile) {
//            munitions = muniFactory*(6+6*2.01)*(1+0.125*(5-1));
            munitions = (muniFactory * 2.01) * 1 + (0.5 * (muniFactory - 1)) / (5 - 1);
        } else {
//            munitions = muniFactory*(6+6*1.5)*(1+0.125*(5-1));
            munitions = (muniFactory * 1.5) * 1 + (0.5 * (muniFactory - 1)) / (5 - 1);
        }
        double aluminum = 0;
        if (bauxworks) {
//            aluminum = aluRefinery*(6+6*1.02)*(1+0.125*(5-1));
            aluminum = (aluRefinery * 1.02) * 1 + (0.5 * (aluRefinery - 1)) / (5 - 1);
        } else {
//            aluminum = aluRefinery*(6+6*0.75)*(1+0.125*(5-1));
            aluminum = (aluRefinery * 0.75) * 1 + (0.5 * (aluRefinery - 1)) / (5 - 1);
        }
        double steel = 0;
        if (ironworks) {
//            steel = steelFactory*(6+6*1.02)*(1+0.125*(5-1));
            steel = (steelFactory * 1.02) * 1 + (0.5 * (steelFactory - 1)) / (5 - 1);
        } else {
//            steel = steelFactory*(6+6*0.75)*(1+0.125*(5-1));
            steel = (steelFactory * 0.75) * 1 + (0.5 * (steelFactory - 1)) / (5 - 1);
        }
        double gasoline = 0;
        if (gasolineReserve) {
//            gasoline = gasRefinery*(6+6*1)*(1+0.125*(5-1));
            gasoline = (gasRefinery) * 1 + (0.5 * (gasRefinery - 1)) / (5 - 1);
        } else {
//            gasoline = gasRefinery*(6+6*0.5)*(1+0.125*(5-1));
            gasoline = (gasRefinery * 0.5) * 1 + (0.5 * (gasRefinery - 1)) / (5 - 1);
        }

        // producing_building*building_production_base*(1+(0.5/building_max_quantity)*producing_building-1
        //bonus = 1 + (0.5 * (current_count - 1)) / (max_count - 1);


        //bonus = 1 + (0.5 * (current_count - 1)) / (max_count - 1);
        //raws
        double uranium = 0;
        if (uraniumEnrichment) {
            uranium = uraMine * 3 * (6 + 1) * (1 + (0.5 * (uraMine - 1)) / 5 - 1);
        } else {
            uranium = uraMine * 3 * (3 + 1) * (1 + (0.5 * (uraMine - 1)) / 5 - 1);
        }

        double coal = coalMine*3*(1+(0.5/10)*10-1);
        double bauxite = bauxMine*3*(1+(0.5/10)*10-1);
        double oil = oilWell*3*(1+(0.5/10)*10-1);
        double iron = ironMine*3*(1+(0.5/10)*10-1);
        double lead = leadMine*3*(1+(0.5/10)*10-1);

        ConsumptionCity city = new ConsumptionCity();
        city.setInfra(infrastructure);
        city.setLand(land);
        city.setAtWar(false);
        city.setFounded(LocalDate.parse(date));

        double foodConsumed = FoodConsumption.getFoodConsumption(city, 0);
        cityRevenue.revenue = income;
        cityRevenue.expenses = expenses;
        cityRevenue.foodConsumed = foodConsumed;
        cityRevenue.oilConsumed = oilUsed;
        cityRevenue.bauxiteConsumed = bauxUsed;
        cityRevenue.uraniumConsumed = uraniumUsed;
        cityRevenue.leadConsumed = leadUsed;
        cityRevenue.coalConsumed = coalUsed;
        cityRevenue.ironConsumed = ironUsed;
        cityRevenue.gasoline = gasoline;
        cityRevenue.steel = steel;
        cityRevenue.aluminum = aluminum;
        cityRevenue.munitions = munitions;
        cityRevenue.coal = coal;
        cityRevenue.bauxite = bauxite;
        cityRevenue.oil = oil;
        cityRevenue.iron = iron;
        cityRevenue.lead = lead;
        cityRevenue.uranium = uranium;
        cityRevenue.food = foodProduction;
        cityRevenue.profit = income - expenses;

        return cityRevenue;
    }

    public static double CalcIncome(int infra, double disease, double crime, boolean openMarkets, boolean GSA, double commerce, String founded) {
        LocalDate now = LocalDate.now();
        LocalDate foundedDate = LocalDate.parse(founded);
        double daysBetween = ChronoUnit.DAYS.between(foundedDate, now);

        double population = ((infra * 100) - ((disease * 100 * infra) / 100) - Math.max((crime / 10) * (100 * infra) - 25, 0)) * (1 + Math.log(daysBetween) / 15);

        double multiplier = 0;
        if (openMarkets && GSA) {
            multiplier = 1.015;
        } else if (openMarkets) {
            multiplier = 1.01;
        }

        return (((((commerce / 50) * 0.725) + 0.725) * population) * multiplier);

    }
}
