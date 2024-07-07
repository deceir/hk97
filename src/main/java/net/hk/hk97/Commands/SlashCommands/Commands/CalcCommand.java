package net.hk.hk97.Commands.SlashCommands.Commands;

import com.google.gdata.model.gd.City;
import net.hk.hk97.Models.CityBuild.Revenue.CalcRevenue;
import net.hk.hk97.Models.CityBuild.Revenue.CityRevenue;
import net.hk.hk97.Models.CityBuild.Revenue.CityRevenueBuilds.CityRevenueModel;
import net.hk.hk97.Models.calc.CityCalc;
import net.hk.hk97.Models.calc.InfraCalc;
import net.hk.hk97.Models.calc.LandCalc;
import net.hk.hk97.Models.calc.graphql.repositories.ResourceRepository;
import net.hk.hk97.Repositories.RadiationRepository;
import net.hk.hk97.Utils.Econ.NationUtil;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.json.JSONException;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CalcCommand {

    public static void calc(SlashCommandInteraction interaction, RadiationRepository radiationRepository, ResourceRepository resourceRepository) throws JSONException {


        EmbedBuilder eb = new EmbedBuilder();

        if (interaction.getOptionByName("infra").isPresent()) {
            InfraCalc calc = new InfraCalc();


            if (interaction.getOptionByName("infra").get().getOptionByName("cities").isPresent()) {

                System.out.println("cities is present");


                long starting_infra = interaction.getOptionByName("infra").get().getOptionByName("start").get().getLongValue().get();
                System.out.println(starting_infra + " starting infra");
                long stopping_infra = interaction.getOptionByName("infra").get().getOptionByName("end").get().getLongValue().get();
                long cities = interaction.getOptionByName("infra").get().getOptionByName("cities").get().getLongValue().get();
                System.out.println("cities " + cities);
                calc.calculateInfra((int) starting_infra, (int) stopping_infra, (int) cities);
                calc.formatCost();


                eb.setAuthor(interaction.getUser())
                        .setTitle("Infra cost for " + starting_infra + " to " + stopping_infra + " in " + cities + " cities")
                        .setAuthor(interaction.getUser())
                        .addField("Base Cost", calc.getBase_cost_f(), true)
                        .addInlineField("1 Cost", calc.getOne_cost_f() + "\n saving: " + calc.getOne_cost_saved_f())
                        .addInlineField("2 Cost", calc.getTwo_cost_f() + "\n saving: " + calc.getTwo_cost_saved_f())
                        .addInlineField("AEC Cost", calc.getAec_cost_f() + "\n saving: " + calc.getAec_cost_saved_f())
                        .addInlineField("GSA Cost", calc.getGsa_cost_f() + "\n saving: " + calc.getGsa_cost_saved_f())
                        .setColor(Color.CYAN);
                interaction.createFollowupMessageBuilder().addEmbed(eb).send();


            } else {
                long starting_infra = interaction.getOptionByName("infra").get().getOptionByName("start").get().getLongValue().get();
                System.out.println(starting_infra + " starting infra");
                long stopping_infra = interaction.getOptionByName("infra").get().getOptionByName("end").get().getLongValue().get();
                System.out.println(stopping_infra + " ending infra");

                calc.calculateInfra(starting_infra, stopping_infra);
                calc.formatCost();


                interaction.createFollowupMessageBuilder().addEmbed(new EmbedBuilder()
                        .setTitle("Infra cost for " + starting_infra + " to " + stopping_infra)
                        .addField("Base Cost", calc.getBase_cost_f(), true)
                        .setAuthor(interaction.getUser())
                        .addInlineField("1 Cost", calc.getOne_cost_f() + "\n saving: " + calc.getOne_cost_saved_f())
                        .addInlineField("2 Cost", calc.getTwo_cost_f() + "\n saving: " + calc.getTwo_cost_saved_f())
                        .addInlineField("AEC Cost", calc.getAec_cost_f() + "\n saving: " + calc.getAec_cost_saved_f())
                        .addInlineField("GSA Cost", calc.getGsa_cost_f() + "\n saving: " + calc.getGsa_cost_saved_f())
                        .setColor(Color.CYAN)
                ).send();
                System.out.println("Embed sent.");


            }

        } else if (interaction.getOptionByName("land").isPresent()) {

            LandCalc calc = new LandCalc();

            if (interaction.getOptionByName("land").get().getOptionByName("cities").isPresent()) {


                long starting_infra = interaction.getOptionByName("land").get().getOptionByName("start").get().getLongValue().get();
                long stopping_infra = interaction.getOptionByName("land").get().getOptionByName("end").get().getLongValue().get();
                long cities = interaction.getOptionByName("land").get().getOptionByName("cities").get().getLongValue().get();
                System.out.println("cities " + cities);
                calc.calculateLand((int) starting_infra, (int) stopping_infra, (int) cities);
                calc.formatCost();


                eb.setAuthor(interaction.getUser())
                        .setTitle("Land cost for " + starting_infra + " to " + stopping_infra + " in " + cities + " cities")
                        .setAuthor(interaction.getUser())
                        .addField("Base Cost", calc.getBase_cost_f(), true)
                        .addInlineField("RE/ALA Cost", calc.getOne_cost_f() + "\n saving: " + calc.getOne_cost_saved_f())
                        .addInlineField("RE+ALA Cost", calc.getTwo_cost_f() + "\n saving: " + calc.getTwo_cost_saved_f())
                        .addInlineField("AEC Cost", calc.getAec_cost_f() + "\n saving: " + calc.getAec_cost_saved_f())
                        .addInlineField("GSA Cost", calc.getGsa_cost_f() + "\n saving: " + calc.getGsa_cost_saved_f())
                        .setColor(Color.CYAN);

                interaction.createFollowupMessageBuilder().addEmbed(eb).send();


            } else {
                long starting_infra = interaction.getOptionByName("land").get().getOptionByName("start").get().getLongValue().get();
                long stopping_infra = interaction.getOptionByName("land").get().getOptionByName("end").get().getLongValue().get();

                calc.calculateLand((int) starting_infra, (int) stopping_infra);
                calc.formatCost();


                interaction.createFollowupMessageBuilder().addEmbed(new EmbedBuilder()
                        .setTitle("Land cost for " + starting_infra + " to " + stopping_infra)
                        .addField("Base Cost", calc.getBase_cost_f(), true)
                        .setAuthor(interaction.getUser())
                        .addField("Base Cost", calc.getBase_cost_f(), true)
                        .addInlineField("RE/ALA Cost", calc.getOne_cost_f() + "\n saving: " + calc.getOne_cost_saved_f())
                        .addInlineField("RE+ALA Cost", calc.getTwo_cost_f() + "\n saving: " + calc.getTwo_cost_saved_f())
                        .addInlineField("AEC Cost", calc.getAec_cost_f() + "\n saving: " + calc.getAec_cost_saved_f())
                        .addInlineField("GSA Cost", calc.getGsa_cost_f() + "\n saving: " + calc.getGsa_cost_saved_f())
                        .setColor(Color.CYAN)
                ).send();
                System.out.println("Embed sent.");
            }

        } else if (interaction.getOptionByName("cities").isPresent()) {
            System.out.println("Cities calc invoked.");

            CityCalc calc = new CityCalc();
            if (interaction.getOptionByName("cities").get().getOptionByName("end").isPresent()) {


                long start = interaction.getOptionByName("cities").get().getOptionByName("start").get().getLongValue().get();
                long end = interaction.getOptionByName("cities").get().getOptionByName("end").get().getLongValue().get();

                if (start > end) {
                    interaction.createFollowupMessageBuilder().setContent("You have formatted the command improperly. Your start city should be your current city, your end city should be the city you are buying up to.").send();
                } else {
                    calc.calculateCity((int) start, (int) end);
                    calc.formatCost();

                    eb
                            .setTitle("The cost to get city " + start + " to city " + end)
                            .setColor(Color.CYAN)
                            .setAuthor(interaction.getUser())
                            .addField("Base Cost", calc.getBase_cost_formatted())
                            .addInlineField("MD Cost", calc.getMd_cost_formatted() + "\n saving: " + calc.getMd_cost_saved_f())
                            .addInlineField("UP + MD Cost", calc.getUp_cost_f() + "\n saving: " + calc.getUp_md_saved_f())
                            .addInlineField("AUP + UP + MD Cost", calc.getAup_md_cost_f() + "\n saving: " + calc.getAup_md_saved_f())
                            .addInlineField("GSA MD Cost", calc.getGsa_cost_f() + "\n saving: " + calc.getGsa_saved_f())
                            .addInlineField("GSA UP + MD Cost", calc.getGsa_up_cost_f() + "\n saving: " + calc.getGsa_up_saved_f())
                            .addInlineField("GSA AUP + UP + MD Cost", calc.getGsa_aup_cost_f() + "\n saving: " + calc.getGsa_aup_saved_f())
                            .addField("Min. Cost (MP + all other reductions)", calc.getMin_cost_f() + "\n saving: " + calc.getMin_cost_saved_f());

                    interaction.createFollowupMessageBuilder().addEmbed(eb).send();

                }
            } else {
                // single city cost
                long start = interaction.getOptionByName("cities").get().getOptionByName("start").get().getLongValue().get();

                calc.calculateCity((int) start);
                calc.formatCost();

                eb
                        .setTitle("The cost to get city " + start + " has been calculated.")
                        .setAuthor(interaction.getUser())
                        .setColor(Color.CYAN)
                        .addField("Base Cost", calc.getBase_cost_formatted())
                        .addInlineField("MD Cost", calc.getMd_cost_formatted() + "\n saving: " + calc.getMd_cost_saved_f())
                        .addInlineField("UP + MD Cost", calc.getUp_cost_f() + "\n saving: " + calc.getUp_md_saved_f())
                        .addInlineField("AUP + UP + MD Cost", calc.getAup_md_cost_f() + "\n saving: " + calc.getAup_md_saved_f())
                        .addInlineField("GSA MD Cost", calc.getGsa_cost_f() + "\n saving: " + calc.getGsa_saved_f())
                        .addInlineField("GSA UP + MD Cost", calc.getGsa_up_cost_f() + "\n saving: " + calc.getGsa_up_saved_f())
                        .addInlineField("GSA AUP + UP + MD Cost", calc.getGsa_aup_cost_f() + "\n saving: " + calc.getGsa_aup_saved_f())
                        .addField("Min. Cost (MP + all other reductions)", calc.getMin_cost_f() + "\n saving: " + calc.getMin_cost_saved_f());

                interaction.createFollowupMessageBuilder().addEmbed(eb).send();

            }

        }
//        else if (interaction.getOptionByName("revenue").isPresent()) {
//            System.out.println("revnue command entered");
//            long id = interaction.getOptionByName("revenue").get().getOptionByName("id").get().getLongValue().get();
//
//            List<CityRevenueModel> cities = NationUtil.getCitiesForRevenue(id);
//
//            List<CityRevenue> revenues = new ArrayList<>();
//
//            double globalRadiation = radiationRepository.findRadiationById("global").getRadiationLevel();
//            double contRadiation = radiationRepository.findRadiationById(cities.get(0).getContinent()).getRadiationLevel();
//
//            System.out.println("global radiation level: " + globalRadiation + " cont radiation: " + contRadiation);
//            long foodPrice = resourceRepository.findResourcesByName("FOOD").getPrice();
//            long uraPrice = resourceRepository.findResourcesByName("URA").getPrice();
//            long aluPrice = resourceRepository.findResourcesByName("ALU").getPrice();
//            long coalPrice = resourceRepository.findResourcesByName("COAL").getPrice();
//            long bauxPrice = resourceRepository.findResourcesByName("BAUX").getPrice();
//            long oilPrice = resourceRepository.findResourcesByName("OIL").getPrice();
//            long gasPrice = resourceRepository.findResourcesByName("GAS").getPrice();
//            long steelPrice = resourceRepository.findResourcesByName("STEEL").getPrice();
//            long ironPrice = resourceRepository.findResourcesByName("IRON").getPrice();
//            long munisPrice = resourceRepository.findResourcesByName("MUNIS").getPrice();
//            long leadPrice = resourceRepository.findResourcesByName("LEAD").getPrice();
//
//
//            CityRevenue nationTotal = new CityRevenue();
//            for (CityRevenueModel c : cities) {
//                CityRevenue cityRevenue = CalcRevenue.CalcRevenue(c.getCoalPower(), c.getOilPower(), c.getWindPower(), c.getNuclearPower(), c.getCoalMine(), c.getOilWell(), c.getUraMine(), c.getLeadMine(), c.getIronMine(), c.getBauxMine(), c.getFarm(), c.getGasRefinery(), c.getAluRefinery(), c.getMuniFactory(), c.getSteelFactory(), c.getPoliceStation(), c.getHospital(), c.getRecyclingCenter(), c.getSubway(), c.getSupermarket(), c.getBank(), c.getMall(), c.getStadium(), c.getInfrastructure(), c.getLand(), c.isItc(), c.isTelecomSat(), c.isGreenTech(), c.isRecylcyingInitiative(), c.isOpenMarkets(), c.isGSA(), c.getDate(), c.isArmsStockpile(), c.isGasolineReserve(), c.isBauxworks(), c.isIronworks(), c.isIrrigation(), contRadiation, globalRadiation, c.isUraniumEnrichment());
//                cityRevenue.setNetProfitValue(foodPrice, coalPrice,ironPrice, leadPrice,oilPrice,uraPrice,bauxPrice,munisPrice,gasPrice,steelPrice,aluPrice);
//                revenues.add(cityRevenue);
//
//                System.out.println(cityRevenue.getRevenue());
//
//                nationTotal.setRevenue(nationTotal.getRevenue() + cityRevenue.getRevenue());
//                nationTotal.setExpenses(nationTotal.getExpenses() + cityRevenue.getExpenses());
//                nationTotal.setProfit(nationTotal.getProfit() + cityRevenue.getProfit());
//                nationTotal.setAluminum(nationTotal.getAluminum() + cityRevenue.getAluminum());
//                nationTotal.setBauxite(nationTotal.getBauxite() + cityRevenue.getBauxite());
//                nationTotal.setCoal(nationTotal.getCoal() + cityRevenue.getCoal());
//                nationTotal.setFood(nationTotal.getFood() + cityRevenue.getFood());
//                nationTotal.setIron(nationTotal.getIron() + cityRevenue.getIron());
//                nationTotal.setFoodConsumed(nationTotal.getFoodConsumed() + cityRevenue.getFoodConsumed());
//                nationTotal.setOilConsumed(nationTotal.getOilConsumed() + cityRevenue.getOilConsumed());
//                nationTotal.setBauxiteConsumed(nationTotal.getBauxiteConsumed() + cityRevenue.getBauxiteConsumed());
//                nationTotal.setUraniumConsumed(nationTotal.getUraniumConsumed() + cityRevenue.getUraniumConsumed());
//                nationTotal.setLeadConsumed(nationTotal.getLeadConsumed() + cityRevenue.getLeadConsumed());
//                nationTotal.setCoalConsumed(nationTotal.getCoalConsumed() + cityRevenue.getCoalConsumed());
//                nationTotal.setIronConsumed(nationTotal.getIronConsumed() + cityRevenue.getIronConsumed());
//                nationTotal.setGasoline(nationTotal.getGasoline() + cityRevenue.getGasoline());
//                nationTotal.setSteel(nationTotal.getSteel() + cityRevenue.getSteel());
//                nationTotal.setMunitions(nationTotal.getMunitions() + cityRevenue.getMunitions());
//                nationTotal.setOil(nationTotal.getOil() + cityRevenue.getOil());
//                nationTotal.setLead(nationTotal.getLead() + cityRevenue.getLead());
//                nationTotal.setUranium(nationTotal.getUranium() + cityRevenue.getUranium());
//                nationTotal.setNetProfit(nationTotal.getNetProfit() + cityRevenue.getNetProfit());
//            }
//
//            //whats left? get resource value on a per city basis and format revenue response
//            NumberFormat n = NumberFormat.getCurrencyInstance(Locale.US);
//            DecimalFormat d = new DecimalFormat("#,###");
//
//            EmbedBuilder embedBuilder = new EmbedBuilder()
//                    .setTitle("Nation Revenue for ID: " + id)
//                    .setAuthor(interaction.getUser())
//                    .addField("Resources", "Cash: " + n.format(nationTotal.getProfit()) + "\n<:food:915071870636789792> " + d.format(nationTotal.getFood()) + " <:uranium:1024144769871523870> " + d.format(nationTotal.getUranium()) + " <:coal:1024144767858266222> " + d.format(nationTotal.getCoal()) + " <:oil:1024144768487391303> " + d.format(nationTotal.getOil()) + " <:lead:1024144770857177119> " + d.format(nationTotal.getLead()) + " <:iron:1024144771884793918> " + d.format(nationTotal.getIron()) + " <:bauxite:1024144773075976243> " + d.format(nationTotal.getBauxite()) + " <:gasoline:1024144774602702868> " + d.format(nationTotal.getGasoline()) + " <:munitions:1024144775668051968> " + d.format(nationTotal.getMunitions()) + " <:steel:1024144776548847656> " + d.format(nationTotal.getSteel()) + " <:aluminum:1024144777509347348> " + d.format(nationTotal.getAluminum()))
//                    .addField("Resources Consumed", "<:food:915071870636789792> " + d.format(nationTotal.getFoodConsumed()) + " <:uranium:1024144769871523870> " + d.format(nationTotal.getUraniumConsumed()) + " <:coal:1024144767858266222> " + d.format(nationTotal.getCoalConsumed()) + " <:oil:1024144768487391303> " + d.format(nationTotal.getOilConsumed()) + " <:lead:1024144770857177119> " + d.format(nationTotal.getLeadConsumed()) + " <:iron:1024144771884793918> " + d.format(nationTotal.getIronConsumed()) + " <:bauxite:1024144773075976243> " + d.format(nationTotal.getBauxiteConsumed()))
//                    .addField("Net Profit:", n.format(nationTotal.getNetProfit()))
//                    .setFooter("HK-97 Revenue Service beta");
//
//            interaction.createFollowupMessageBuilder().addEmbed(embedBuilder).send();
//
//        }

    }
}
