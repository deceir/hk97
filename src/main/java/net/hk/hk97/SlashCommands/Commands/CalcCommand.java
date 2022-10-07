package net.hk.hk97.SlashCommands.Commands;

import net.hk.hk97.Models.calc.CityCalc;
import net.hk.hk97.Models.calc.InfraCalc;
import net.hk.hk97.Models.calc.LandCalc;
import net.hk.hk97.Models.calc.graphql.repositories.ResourceRepository;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;

import java.awt.*;

public class CalcCommand {

    public static void calc(SlashCommandInteraction interaction) {


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
                        .setColor(Color.orange);

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
                        .setColor(Color.orange)
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
                            .setColor(Color.ORANGE)
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
                        .setColor(Color.orange)
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

    }
}
