bonusCalc: async function bonusCalc(imp_avail,imp_cap){
    let bonus = Math.min(1+((0.5*(imp_avail-1))/(imp_cap-1)),1.5);
    return bonus;
}

let commerce_barrier;
if(nation["inttradecenter"] == 0){
    commerce_barrier = 100;
}else{
    commerce_barrier = 114;
}
if(nation["telecommunications_satellite"] == 1){
    commerce_barrier = 125
}

let m_pollution_modifier;
let f_pollution_modifier;
let upkeep_modifier;

if(nation["green_technologies"] == 1){
    m_pollution_modifier = 0.75
    f_pollution_modifier = 0.50
    upkeep_modifier = 0.90
}else{
    m_pollution_modifier = 1
    f_pollution_modifier = 1
    upkeep_modifier = 1
}



let npb
if(nation["cities"] <= 10){
    npb = ((100-(10*(nation["cities"]-1)))/100)+1;
}else{
    npb = 1;
}

let foodmodifier = 1;
if(nation["continent"] == "Antartica"){
    foodmodifier -= 0.5;
}
foodmodifier += nation["radiation_index"]/ (-1000);
let normalfoodmod = foodmodifier

if(nation["season"] == "winter"){
    foodmodifier -= 0.2;
}else if(nation["season"] == "summer"){
    foodmodifier += 0.2;
}


let treasure;
let OM
if(nation["color"] == alliance["color"] && alliance["treasures"] > 0)
{
    treasure = Math.sqrt(alliance["treasures"] * 4)/100+1;
}else
{
    treasure = 1;
}

if(nation["domestic_policy"] == "Open Markets")
{
    OM = 1.01;
}
else
{
    OM = 1;
}

let cumulative = treasure * OM;


let coal_production = 0.25*taxes;
let iron_production = 0.25*taxes;
let oil_production = 0.25*taxes;
let bauxite_production = 0.25*taxes;
let lead_production = 0.25*taxes;
let uranium_production = 0.25*taxes;
let gasoline_production = 0.5*taxes;
let steel_production = 0.75*taxes;
let aluminum_production = 0.75*taxes;
let munitions_production =1.5*taxes;
let food_production = city["land"]/500*taxes;

/*
    CAP OF IMPROVEMENTS PER RESOURCE
 */

let coal_cap = 10;
let oil_cap = 10;
let iron_cap = 10;
let bauxite_cap = 10;
let lead_cap = 10;
let uranium_cap = 5;
let gasoline_cap = 5;
let steel_cap = 5;
let aluminum_cap = 5;
let munitions_cap = 5;
let food_cap = 20;

let cityAud = city;

let population = ((cityAud["basepop"]) - (0 * cityAud["infrastructure"]) - 0) * (1 + (Math.log(cityAud["age"])/15));

let stadium_profit = (((((12*5)/100))*.29*population*cumulative/12)-1013-(((((cu_commerce*5)/100)+2.5)*.29*(city["infrastructure"]*0.25)*cumulative)/12))*npb*taxesm;

let mall_profit = (((((9*5)/100))*.29*population*cumulative/12)-450-(((((cu_commerce*5)/100)+2.5)*.29*(city["infrastructure"]*0.1)*cumulative)/12))*npb*taxesm;

let bank_profit = (((((5*5)/100))*.29*population*cumulative/12)-150)*npb*taxesm;

let supermarket_profit = (((((3*5)/100))*.29*population*cumulative/12)-50)*npb*taxesm;

let food_profit
if(nation["massirrigation"] == 0){
    food_profit = await this.bonusCalc(imp_left,food_cap)*(city["land"]/500)*taxes*food_price*foodmodifier - 25 - ((((cu_commerce*5)/100)+2.5)*.29*(city["infrastructure"]*0.1*f_pollution_modifier)*cumulative)/12;
}else{
    food_profit = await this.bonusCalc(imp_left,food_cap)*(city["land"]/400)*taxes*food_price*foodmodifier - 25 - ((((cu_commerce*5)/100)+2.5)*.29*(city["infrastructure"]*0.1*f_pollution_modifier)*cumulative)/12;
}
let food_profit_normal
if(nation["massirrigation"] == 0){
    food_profit_normal = await this.bonusCalc(imp_left,food_cap)*(city["land"]/500)*taxes*food_price*normalfoodmod - 25 - ((((cu_commerce*5)/100)+2.5)*.29*(city["infrastructure"]*0.1*f_pollution_modifier)*cumulative)/12;
}else{
    food_profit_normal = await this.bonusCalc(imp_left,food_cap)*(city["land"]/400)*taxes*food_price*normalfoodmod - 25 - ((((cu_commerce*5)/100)+2.5)*.29*(city["infrastructure"]*0.1*f_pollution_modifier)*cumulative)/12;
}


let coal_profit = await this.bonusCalc(imp_left,coal_cap)*coal_production*coal_price - 34*upkeep_modifier - ((((cu_commerce*5)/100)+2.5)*.29*(city["infrastructure"]*0.6)*cumulative)/12;
let oil_profit = await this.bonusCalc(imp_left,oil_cap)*oil_production*oil_price - 50*upkeep_modifier - ((((cu_commerce*5)/100)+2.5)*.29*(city["infrastructure"]*0.6)*cumulative)/12;
let iron_profit = await this.bonusCalc(imp_left,iron_cap)*iron_production*iron_price - 134*upkeep_modifier - ((((cu_commerce*5)/100)+2.5)*.29*(city["infrastructure"]*0.6)*cumulative)/12;
let bauxite_profit = await this.bonusCalc(imp_left,bauxite_cap)*bauxite_production*bauxite_price - 67*upkeep_modifier - ((((cu_commerce*5)/100)+2.5)*.29*(city["infrastructure"]*0.6)*cumulative)/12;
let lead_profit = await this.bonusCalc(imp_left,lead_cap)*lead_production*lead_price - 125*upkeep_modifier - ((((cu_commerce*5)/100)+2.5)*.29*(city["infrastructure"]*0.6)*cumulative)/12;


let uranium_profit
if(nation["uraniumenrich"] == 0){
    uranium_profit = await this.bonusCalc(imp_left,uranium_cap)*uranium_production*uranium_price - 417*upkeep_modifier - ((((cu_commerce*5)/100)+2.5)*.29*(city["infrastructure"]*1)*cumulative)/12;
}
else{
    uranium_profit = await this.bonusCalc(imp_left,uranium_cap)*uranium_production*uranium_price*2 - 417*upkeep_modifier - ((((cu_commerce*5)/100)+2.5)*.29*(city["infrastructure"]*1)*cumulative)/12;
}


let gasoline_bonus = await this.bonusCalc(imp_left,gasoline_cap);
let gasoline_profit
if(nation["emgasreserve"] == 0){
    gasoline_profit = gasoline_bonus*gasoline_production*gasoline_price - oil_price*(0.25*gasoline_bonus) -344*upkeep_modifier - ((((cu_commerce*5)/100)+2.5)*.29*(city["infrastructure"]*1.6*m_pollution_modifier)*cumulative)/12;
}
else{
    gasoline_profit = (2*gasoline_bonus*gasoline_production*gasoline_price - oil_price*(0.25*gasoline_bonus*2)) - 344*upkeep_modifier - ((((cu_commerce*5)/100)+2.5)*.29*(city["infrastructure"]*1.6*m_pollution_modifier)*cumulative)/12;
}
let steel_profit
let steel_bonus = await this.bonusCalc(imp_left,steel_cap);
if(nation["ironworks"] == 0){
    steel_profit = steel_bonus*steel_production*steel_price - coal_price*(0.25*steel_bonus) - iron_price*(0.25*steel_bonus) - 344*upkeep_modifier - ((((cu_commerce*5)/100)+2.5)*.29*(city["infrastructure"]*2*m_pollution_modifier)*cumulative)/12;
}
else{
    steel_profit = (1.36*steel_bonus*steel_production*steel_price - coal_price*(1.36*0.25*steel_bonus) - iron_price*(1.36*0.25*steel_bonus)) - 344*upkeep_modifier - ((((cu_commerce*5)/100)+2.5)*.29*(city["infrastructure"]*2*m_pollution_modifier)*cumulative)/12;
}

let aluminum_bonus = await this.bonusCalc(imp_left,aluminum_cap);
let aluminum_profit
if(nation["bauxiteworks"] == 0){
    aluminum_profit = aluminum_bonus*aluminum_production*aluminum_price - bauxite_price*(0.25*aluminum_bonus) - 209*upkeep_modifier - ((((cu_commerce*5)/100)+2.5)*.29*(city["infrastructure"]*2*m_pollution_modifier)*cumulative)/12;
}
else{
    aluminum_profit = (1.36*aluminum_bonus*aluminum_production*aluminum_price - bauxite_price*(1.36*0.25*aluminum_bonus)) - 209*upkeep_modifier - ((((cu_commerce*5)/100)+2.5)*.29*(city["infrastructure"]*2*m_pollution_modifier)*cumulative)/12;
}
let munitions_profit;
let munitions_bonus = await this.bonusCalc(imp_left,munitions_cap);
if(nation["armsstockpile"] == 0){
    munitions_profit = munitions_bonus*munitions_production*munitions_price - lead_price*(0.5*munitions_bonus) - 292*upkeep_modifier - ((((cu_commerce*5)/100)+2.5)*.29*(city["infrastructure"]*1.6*m_pollution_modifier)*cumulative)/12;
}
else{
    munitions_profit = (1.34*munitions_bonus*munitions_production*munitions_price - lead_price*(1.34*0.5*munitions_bonus)) - 292*upkeep_modifier - ((((cu_commerce*5)/100)+2.5)*.29*(city["infrastructure"]*1.6*m_pollution_modifier)*cumulative)/12;

}