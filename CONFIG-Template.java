package net.hk.hk97;

/**
 * Copy to src/main/java/net/hk/hk97/Config.java and fill in the values before running.
 * Once copied under that filename, all existing Config references have matching
 * field names and compatible types. Replace an existing Config.java only after
 * preserving any values you still need from it.
 * Keep this template outside src: it must be named Config.java to compile.
 * Config.java is ignored by Git; keep credentials out of this tracked template.
 * Empty strings, zero, and empty arrays are placeholders, not working defaults.
 * Names and capitalization match the existing callers.
 * Database/Spring properties, Google credentials, and hardcoded IDs elsewhere
 * still require separate setup; this template covers existing Config references.
 * All 26 referenced fields are included, including commented-out legacy fields.
 * The template was compiled on its own for Java 11; the full project's build
 * and runtime behavior have not been verified. Copying it alone does not make
 * the bot ready to run: fill in the required values and finish the separate setup.
 */
public class Config {

    // Discord bot token (Hk97Application and AllianceAuditService).
    public static String discordToken = "";

    // Politics & War API keys. Original account names retained for compatibility.
    // Main key: nation queries, bank history, recruitment, and withdrawals.
    public static String itachiPnwKey = "";
    // Recruitment queries and alternate offshore bank balance.
    public static String adamApiKey = "";
    // Activity, military, warchest audits, and alliance rebuild queries.
    public static String kastorKey = "";
    // Growth program queries and X-Api-Key header on withdrawals.
    public static String sniperKey = "";
    // Politics & War bot key sent as X-Bot-Key on withdrawals.
    public static String xboxKeySniper = "";

    // Politics & War IDs: decimal strings without URL prefixes.
    // Main alliance; several callers use Long.parseLong(aaId).
    public static String aaId = "";
    // Offshore alliance queried by NationUtil.
    public static String offshoreAaId = "";
    // Alternate offshore alliance; callers use Long.parseLong(adamOffshoreId).
    public static String adamOffshoreId = "";
    // Tax bracket ID for the main alliance's growth program queries.
    public static String growthProgramTaxId = "";
    // Alliance IDs accepted as deposit receivers. Use string entries, not numbers.
    public static String[] alliancesForDeposit = new String[] {};

    // Discord server and role IDs: decimal strings.
    public static String mainServerId = "";
    public static String mainServerGovId = "";
    public static String mainServerLowGovId = "";
    public static String mainServerStaffId = "";
    public static String mainServerHkRole = "";
    public static String mainserverIaRoleId = "";
    public static String mainServerBankAdminId = "";

    // Discord channels and categories.
    // Channel used for war room creation commands.
    public static String mainServerCommandRoom = "";
    public static String mainServerWarroomCategoryId = "";
    // Category IDs monitored by MilcomListenerImpl. Include all war room categories.
    public static String[] warCategories = new String[] {};
    // Category where application interview channels are created.
    public static String applicationsCategoryId = "";
    // Application message channel: numeric Discord ID with an L suffix.
    public static long applicationsChannelId = 0L;
    // Channel for grant and resource disbursement logs.
    public static String loggingChannelId = "";

    // Legacy Sheets: referenced only in commented-out SheetCommand code.
    // Leave blank unless that integration is restored.
    public static String gmailName = "";
    public static String gmailPass = "";
}
