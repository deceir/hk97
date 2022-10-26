//package net.hk.hk97.Models.template;
//
//import net.hk.hk97.Models.calc.graphql.repositories.ResourceRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//
//public class TemplateFormulas {
//
//    @Autowired
//    ResourceRepository resourceRepository;
//
//    int taxes;
//
//    TemplateCity city;
//
//    int cumulative = 1;
//
//    double coal_production = 0.25 * taxes;
//    double iron_production = 0.25 * taxes;
//    double oil_production = 0.25 * taxes;
//    double bauxite_production = 0.25 * taxes;
//    double lead_production = 0.25 * taxes;
//    double uranium_production = 0.25 * taxes;
//    double gasoline_production = 0.5 * taxes;
//    double steel_production = 0.75 * taxes;
//    double aluminum_production = 0.75 * taxes;
//    double munitions_production =1.5 * taxes;
//
//    int land = city.getLand();
//
//    double food_production = (land / 500) * taxes;
//
//    /*
//        CAP OF IMPROVEMENTS PER RESOURCE
//     */
//    int coal_cap = 10;
//    int oil_cap = 10;
//    int iron_cap = 10;
//    int bauxite_cap = 10;
//    int lead_cap = 10;
//    int uranium_cap = 5;
//    int gasoline_cap = 5;
//    int steel_cap = 5;
//    int aluminum_cap = 5;
//    int munitions_cap = 5;
//    int food_cap = 20;
//
//
//    double population = (city.getBasePop() - (0 * city.getInfra()) - 0) * (1 + (Math.log(city.getAge())/15));
//
//    double stadium_profit = (((((12*5)/100))*.29*population*cumulative/12)-1013-(((((cu_commerce*5)/100)+2.5)*.29*(city["infrastructure"]*0.25)*cumulative)/12))*npb*taxesm;
//
//    double mall_profit = (((((9*5)/100))*.29*population*cumulative/12)-450-(((((cu_commerce*5)/100)+2.5)*.29*(city["infrastructure"]*0.1)*cumulative)/12))*npb*taxesm;
//
//    double bank_profit = (((((5*5)/100))*.29*population*cumulative/12)-150)*npb*taxesm;
//
//    double supermarket_profit = (((((3*5)/100))*.29*population*cumulative/12)-50)*npb*taxesm;
//
//    let food_profit
//		if(nation["massirrigation"] == 0){
//        food_profit = await this.bonusCalc(imp_left,food_cap)*(city["land"]/500)*taxes*food_price*foodmodifier - 25 - ((((cu_commerce*5)/100)+2.5)*.29*(city["infrastructure"]*0.1*f_pollution_modifier)*cumulative)/12;
//    }else{
//        food_profit = await this.bonusCalc(imp_left,food_cap)*(city["land"]/400)*taxes*food_price*foodmodifier - 25 - ((((cu_commerce*5)/100)+2.5)*.29*(city["infrastructure"]*0.1*f_pollution_modifier)*cumulative)/12;
//    }
//    let food_profit_normal
//		if(nation["massirrigation"] == 0){
//        food_profit_normal = await this.bonusCalc(imp_left,food_cap)*(city["land"]/500)*taxes*food_price*normalfoodmod - 25 - ((((cu_commerce*5)/100)+2.5)*.29*(city["infrastructure"]*0.1*f_pollution_modifier)*cumulative)/12;
//    }else{
//        food_profit_normal = await this.bonusCalc(imp_left,food_cap)*(city["land"]/400)*taxes*food_price*normalfoodmod - 25 - ((((cu_commerce*5)/100)+2.5)*.29*(city["infrastructure"]*0.1*f_pollution_modifier)*cumulative)/12;
//    }
//
//
//    let coal_profit = await this.bonusCalc(imp_left,coal_cap)*coal_production*coal_price - 34*upkeep_modifier - ((((cu_commerce*5)/100)+2.5)*.29*(city["infrastructure"]*0.6)*cumulative)/12;
//    let oil_profit = await this.bonusCalc(imp_left,oil_cap)*oil_production*oil_price - 50*upkeep_modifier - ((((cu_commerce*5)/100)+2.5)*.29*(city["infrastructure"]*0.6)*cumulative)/12;
//    let iron_profit = await this.bonusCalc(imp_left,iron_cap)*iron_production*iron_price - 134*upkeep_modifier - ((((cu_commerce*5)/100)+2.5)*.29*(city["infrastructure"]*0.6)*cumulative)/12;
//    let bauxite_profit = await this.bonusCalc(imp_left,bauxite_cap)*bauxite_production*bauxite_price - 67*upkeep_modifier - ((((cu_commerce*5)/100)+2.5)*.29*(city["infrastructure"]*0.6)*cumulative)/12;
//    let lead_profit = await this.bonusCalc(imp_left,lead_cap)*lead_production*lead_price - 125*upkeep_modifier - ((((cu_commerce*5)/100)+2.5)*.29*(city["infrastructure"]*0.6)*cumulative)/12;
//
//
//    let uranium_profit
//		if(nation["uraniumenrich"] == 0){
//        uranium_profit = await this.bonusCalc(imp_left,uranium_cap)*uranium_production*uranium_price - 417*upkeep_modifier - ((((cu_commerce*5)/100)+2.5)*.29*(city["infrastructure"]*1)*cumulative)/12;
//    }
//		else{
//        uranium_profit = await this.bonusCalc(imp_left,uranium_cap)*uranium_production*uranium_price*2 - 417*upkeep_modifier - ((((cu_commerce*5)/100)+2.5)*.29*(city["infrastructure"]*1)*cumulative)/12;
//    }
//
//
//    let gasoline_bonus = await this.bonusCalc(imp_left,gasoline_cap);
//    let gasoline_profit
//		if(nation["emgasreserve"] == 0){
//        gasoline_profit = gasoline_bonus*gasoline_production*gasoline_price - oil_price*(0.25*gasoline_bonus) -344*upkeep_modifier - ((((cu_commerce*5)/100)+2.5)*.29*(city["infrastructure"]*1.6*m_pollution_modifier)*cumulative)/12;
//    }
//		else{
//        gasoline_profit = (2*gasoline_bonus*gasoline_production*gasoline_price - oil_price*(0.25*gasoline_bonus*2)) - 344*upkeep_modifier - ((((cu_commerce*5)/100)+2.5)*.29*(city["infrastructure"]*1.6*m_pollution_modifier)*cumulative)/12;
//    }
//    let steel_profit
//    let steel_bonus = await this.bonusCalc(imp_left,steel_cap);
//		if(nation["ironworks"] == 0){
//        steel_profit = steel_bonus*steel_production*steel_price - coal_price*(0.25*steel_bonus) - iron_price*(0.25*steel_bonus) - 344*upkeep_modifier - ((((cu_commerce*5)/100)+2.5)*.29*(city["infrastructure"]*2*m_pollution_modifier)*cumulative)/12;
//    }
//		else{
//        steel_profit = (1.36*steel_bonus*steel_production*steel_price - coal_price*(1.36*0.25*steel_bonus) - iron_price*(1.36*0.25*steel_bonus)) - 344*upkeep_modifier - ((((cu_commerce*5)/100)+2.5)*.29*(city["infrastructure"]*2*m_pollution_modifier)*cumulative)/12;
//    }
//
//    let aluminum_bonus = await this.bonusCalc(imp_left,aluminum_cap);
//    let aluminum_profit
//		if(nation["bauxiteworks"] == 0){
//        aluminum_profit = aluminum_bonus*aluminum_production*aluminum_price - bauxite_price*(0.25*aluminum_bonus) - 209*upkeep_modifier - ((((cu_commerce*5)/100)+2.5)*.29*(city["infrastructure"]*2*m_pollution_modifier)*cumulative)/12;
//    }
//		else{
//        aluminum_profit = (1.36*aluminum_bonus*aluminum_production*aluminum_price - bauxite_price*(1.36*0.25*aluminum_bonus)) - 209*upkeep_modifier - ((((cu_commerce*5)/100)+2.5)*.29*(city["infrastructure"]*2*m_pollution_modifier)*cumulative)/12;
//    }
//    let munitions_profit;
//    let munitions_bonus = await this.bonusCalc(imp_left,munitions_cap);
//		if(nation["armsstockpile"] == 0){
//        munitions_profit = munitions_bonus*munitions_production*munitions_price - lead_price*(0.5*munitions_bonus) - 292*upkeep_modifier - ((((cu_commerce*5)/100)+2.5)*.29*(city["infrastructure"]*1.6*m_pollution_modifier)*cumulative)/12;
//    }
//		else{
//        munitions_profit = (1.34*munitions_bonus*munitions_production*munitions_price - lead_price*(1.34*0.5*munitions_bonus)) - 292*upkeep_modifier - ((((cu_commerce*5)/100)+2.5)*.29*(city["infrastructure"]*1.6*m_pollution_modifier)*cumulative)/12;
//
//    }
//}
