

async function warroom(client,Discord,message,pnw,utilities,args,ci,sanitize, constants,formatMoney,MongoClient) {
    let fetch = require("node-fetch");
    let nationid = args[0]
    let nation = await pnw.nation(nationid)
    let acronym
    let leadername
    if(nation.leadername.length > 11){
        leadername = nation.leadername.substring(0,11);
    }else if(nation.leadername.length == 11){
        leadername = nation.leadername
    }else{
        let x = 11 - nation.leadername.length
        leadername = new Array(x+1).join(' ') + nation.leadername;
    }
    if(nation.allianceid == 0){
        acronym = " - "
    }else{
        let alliance = await pnw.alliance(nation.allianceid)

        acronym = alliance.acronym.substring(0,3);
        if(alliance.acronym == ""){
            let arr = alliance.name.split(" ")
            if(arr.length > 1){
                for(let ar of arr){
                    acronym += ar.substring(0,1);
                }
                acronym = acronym.substring(0,3);
            }else{
                acronym = alliance.name.substring(0,2);
            }

        }
    }
    console.log(acronym)
    let l = 3 - acronym.length
    acronym = new Array(l+1).join(' ') + acronym

    let cities
    let citiesdigits = Math.max(Math.floor(Math.log10(Math.abs(nation.cities))), 0) + 1
    if(citiesdigits == 2){
        cities = nation.cities
    }else{
        let x = 2 - citiesdigits
        cities = new Array(x+1).join(' ') + nation.cities
    }

    let score
    let rawscore = nation.score.split('.')[0]
    let scoredigits = Math.max(Math.floor(Math.log10(Math.abs(rawscore))), 0) + 1
    if(scoredigits == 5){
        score = rawscore
    }else{
        let x = 5 - scoredigits
        score = new Array(x+1).join(' ') +rawscore
    }


    let soldiers
    if(nation.soldiers.length == 9){
        soldiers = nation.soldiers
    }else{
        let x = 9 - nation.soldiers.length
        soldiers = new Array(x+1).join(' ') + nation.soldiers
    }
    let tanks
    if(nation.tanks.length == 6){
        tanks = nation.tanks
    }else{
        let x = 6 - nation.tanks.length
        tanks = new Array(x+1).join(' ') + nation.tanks
    }
    let aircraft
    if(nation.aircraft.length == 5){
        aircraft = nation.aircraft
    }else{
        let x = 5 - nation.aircraft.length
        aircraft = new Array(x+1).join(' ') + nation.aircraft
    }
    let ships
    console.log(nation.ships.length)
    if(nation.ships.length == 4){
        ships = nation.ships
    }else{
        let x = 4 - nation.ships.length
        ships = new Array(x+1).join(' ') + nation.ships
        console.log(ships)
        console.log(x)
    }
    let embed_off_string = ""
    let embed_def_string = ""
    let string = "```apache\n"+
        "     Leader  AA  #  Score  Soldier  Tank  Air Shp |  Resist  MAPS  GC AS NB turns | ID VDS  BTL\n" +
        leadername + " " + acronym + " " + cities + "  " + score + soldiers + tanks + aircraft + ships +
        "```"
    let w_info = await message.channel.send(string);
    if(nation.offensivewars > 0){
        let off_string  = ""
        for(let wid of nation.offensivewar_ids){
            let warraw = await fetch(`https://politicsandwar.com/api/war/${wid}&key=704dfff3e8ff76`)
            let war = await warraw.json()
            war = war.war[0]
            let nation = await pnw.nation(war.defender_id)
            let acronym
            if(nation.allianceid == 0){
                acronym = " - "
            }else{
                let alliance = await pnw.alliance(nation.allianceid)
                acronym = alliance.acronym.substring(0,3);
                if(alliance.acronym == ""){
                    let arr = alliance.name.split(" ")
                    if(arr.length > 1){
                        for(let ar of arr){
                            acronym += ar.substring(0,1);
                        }
                    }else{
                        acronym = alliance.name.substring(0,3);
                    }

                }
                if(acronym.length <= 3 && /^\p{Emoji}*$/u.test(acronym)){
                    acronym = alliance.name.substring(0,3);

                }else if(acronym.length <= 3){
                    let x = 3 - acronym.length
                    acronym = new Array(x+1).join(' ') + acronym ;
                }

            }
            let l = 3 - acronym.length

            acronym = new Array(l+1).join(' ') + acronym
            console.log(acronym)
            let leadername
            if(nation.leadername.length > 11){
                leadername = nation.leadername.substring(0,11);
            }else if(nation.leadername.length == 11){
                leadername = nation.leadername
            }else{
                let x = 11 - nation.leadername.length
                leadername = new Array(x+1).join(' ') + nation.leadername;
            }




            let cities
            let citiesdigits = Math.max(Math.floor(Math.log10(Math.abs(nation.cities))), 0) + 1
            if(citiesdigits == 2){
                cities = nation.cities
            }else{
                let x = 2 - citiesdigits
                cities = new Array(x+1).join(' ') + nation.cities
            }

            let score
            let rawscore = nation.score.split('.')[0]
            let scoredigits = Math.max(Math.floor(Math.log10(Math.abs(rawscore))), 0) + 1
            if(scoredigits == 5){
                score = rawscore
            }else{
                let x = 5 - scoredigits
                score = new Array(x+1).join(' ') +rawscore
            }

            let soldiers
            if(nation.soldiers.length == 9){
                soldiers = nation.soldiers
            }else{
                let x = 9 - nation.soldiers.length
                soldiers = new Array(x+1).join(' ') + nation.soldiers
            }
            let tanks
            if(nation.tanks.length == 6){
                tanks = nation.tanks
            }else{
                let x = 6 - nation.tanks.length
                tanks = new Array(x+1).join(' ') + nation.tanks
            }
            let aircraft
            if(nation.aircraft.length == 5){
                aircraft = nation.aircraft
            }else{
                let x = 5 - nation.aircraft.length
                aircraft = new Array(x+1).join(' ') + nation.aircraft
            }
            let ships
            if(nation.ships.length == 4){
                ships = nation.ships
            }else{
                let x = 4 - nation.ships.length
                ships = new Array(x+1).join(' ') + nation.ships
            }
            let res = war.aggressor_resistance + "  " + war.defender_resistance
            if(res.length == 8){
                res = res
            }else{
                let x = 8 - res.length
                res = res + new Array(x+1).join(' ')
            }

            let maps = war.aggressor_military_action_points + " " + war.defender_military_action_points
            if(maps.length == 5){
                maps = maps
            }else{
                let x = 5 - maps.length
                maps = new Array(x+1).join(' ') + maps
            }

            let gc = ""
            if(war.ground_control == war.aggressor_id){
                gc = "ag"
            }else if(war.ground_control == war.defender_id){
                gc = "de"
            }else{
                gc = "--"
            }
            let as = ""
            if(war.air_superiority == war.aggressor_id){
                as = "ag"
            }else if(war.air_superiority == war.defender_id){
                as = "de"
            }else{
                as = "--"
            }
            let nb = ""
            if(war.blockade == war.aggressor_id){
                nb = "ag"
            }else if(war.blockade == war.defender_id){
                nb = "de"
            }else{
                nb = "--"
            }

            let irondome = "  "
            if(nation.irondome == 1){
                irondome = "💥"
            }
            let vds = "  "
            if(nation.vitaldefsys == 1){
                vds = "🚀"
            }
            off_string += leadername + " " + acronym + " " + cities + "  " + score + soldiers + tanks + aircraft + ships + " | " + res + "" + maps +  "  " + gc + " " + as + " "  + nb + "   " + war.turns_left + " | " + irondome + " " + vds + "      " + nation.beige_turns_left + "\n"
            let activity = await utilities.activity(nation)
            embed_off_string += "[link](https://politicsandwar.com/nation/war/timeline/war=" + wid + ") = [" + nation.leadername + "](https://politicsandwar.com/nation/id=" + nation.nationid + "), " + nation.alliance + " -- "  + activity + "\n"
        }
        let string = "**OFFENSIVE WARS** (" + nation.offensivewars + "/5)\n" + "```apache\n" + off_string + "```"
        message.channel.send(string)

    }
    if(nation.defensivewars > 0){
        let def_string = ""
        for(let wid of nation.defensivewar_ids){
            let warraw = await fetch(`https://politicsandwar.com/api/war/${wid}&key=704dfff3e8ff76`)
            let war = await warraw.json()
            war = war.war[0]
            let nation = await pnw.nation(war.aggressor_id)
            let acronym
            if(nation.allianceid == 0){
                acronym = " - "
            }else{
                let alliance = await pnw.alliance(nation.allianceid)
                acronym = alliance.acronym.substring(0,3);
                if(alliance.acronym == ""){
                    let arr = alliance.name.split(" ")
                    if(arr.length > 1){
                        for(let ar of arr){
                            acronym += ar.substring(0,1);
                        }
                    }else{
                        acronym = alliance.name.substring(0,3);
                    }

                }
                if(acronym.length <= 3 && /^\p{Emoji}*$/u.test(acronym)){
                    acronym = alliance.name.substring(0,3);

                }else if(acronym.length <= 3){
                    let x = 3 - acronym.length
                    acronym = new Array(x+1).join(' ') + acronym ;
                }

            }

            let leadername
            if(nation.leadername.length > 11){
                leadername = nation.leadername.substring(0,11);
            }else if(nation.leadername.length == 11){
                leadername = nation.leadername
            }else{
                let x = 11 - nation.leadername.length
                leadername = new Array(x+1).join(' ') + nation.leadername;
            }
            let l = 3 - acronym.length
            console.log(l)
            console.log(acronym)
            acronym = new Array(l+1).join(' ') + acronym

            let cities
            let citiesdigits = Math.max(Math.floor(Math.log10(Math.abs(nation.cities))), 0) + 1
            if(citiesdigits == 2){
                cities = nation.cities
            }else{
                let x = 2 - citiesdigits
                cities = new Array(x+1).join(' ') + nation.cities
            }

            let score
            let rawscore = nation.score.split('.')[0]
            let scoredigits = Math.max(Math.floor(Math.log10(Math.abs(rawscore))), 0) + 1
            if(scoredigits == 5){
                score = rawscore
            }else{
                let x = 5 - scoredigits
                score = new Array(x+1).join(' ') +rawscore
            }


            let soldiers
            if(nation.soldiers.length == 9){
                soldiers = nation.soldiers
            }else{
                let x = 9 - nation.soldiers.length
                soldiers = new Array(x+1).join(' ') + nation.soldiers
            }
            let tanks
            if(nation.tanks.length == 6){
                tanks = nation.tanks
            }else{
                let x = 6 - nation.tanks.length
                tanks = new Array(x+1).join(' ') + nation.tanks
            }
            let aircraft
            if(nation.aircraft.length == 5){
                aircraft = nation.aircraft
            }else{
                let x = 5 - nation.aircraft.length
                aircraft = new Array(x+1).join(' ') + nation.aircraft
            }
            let ships
            if(nation.ships.length == 4){
                ships = nation.ships
            }else{
                let x = 4 - nation.ships.length
                ships = new Array(x+1).join(' ') + nation.ships
            }
            let res = war.aggressor_resistance + "  " + war.defender_resistance
            if(res.length == 8){
                res = res
            }else{
                let x = 8 - res.length
                res =  res + new Array(x+1).join(' ')
            }

            let maps = war.aggressor_military_action_points + " " + war.defender_military_action_points
            if(maps.length == 5){
                maps = maps
            }else{
                let x = 5 - maps.length
                maps = new Array(x+1).join(' ') + maps
            }

            let gc = ""
            if(war.ground_control == war.aggressor_id){
                gc = "ag"
            }else if(war.ground_control == war.defender_id){
                gc = "de"
            }else{
                gc = "--"
            }
            let as = ""
            if(war.air_superiority == war.aggressor_id){
                as = "ag"
            }else if(war.air_superiority == war.defender_id){
                as = "de"
            }else{
                as = "--"
            }
            let nb = ""
            if(war.blockade == war.aggressor_id){
                nb = "ag"
            }else if(war.blockade == war.defender_id){
                nb = "de"
            }else{
                nb = "--"
            }

            let irondome = "  "
            if(nation.irondome == 1){
                irondome = "💥"
            }
            let vds = "  "
            if(nation.vitaldefsys == 1){
                vds = "🚀"
            }
            console.log(acronym.length)
            def_string += leadername + " " + acronym + " " + cities + "  " + score + soldiers + tanks + aircraft + ships + " | " + res + "" + maps +  "  " + gc + " " + as + " "  + nb + "   " + war.turns_left + " | " + irondome + " " + vds + "      " + nation.beige_turns_left + "\n"
            let activity = await utilities.activity(nation)
            embed_def_string += "[link](https://politicsandwar.com/nation/war/timeline/war=" + wid + ") = [" + nation.leadername + "](https://politicsandwar.com/nation/id=" + nation.nationid + "), " + nation.alliance + " -- "  + activity + "\n"
        }
        let string = "**DEFENSIVE WARS** (" + nation.defensivewars + "/3)\n" + "```apache\n" + def_string + "```"
        message.channel.send(string)

    }

    let embed = new Discord.MessageEmbed()
        .setTitle(nation.leadername + ", " + nation.alliance, nation.flag)
        .setURL("https://politicsandwar.com/nation/id=" + nationid)
        .setColor(286722)
        .setDescription("\n**Offensive wars**\n" + embed_off_string + "**Defensive wars**\n" + embed_def_string)
    message.channel.send({embed})
}