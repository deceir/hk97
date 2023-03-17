package net.hk.hk97.Commands.SlashCommands.Commands;

import com.fasterxml.jackson.core.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.gdata.client.GoogleService;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.util.AuthenticationException;
import net.hk.hk97.Config;

import java.net.MalformedURLException;
import java.net.URL;

public class SheetCommand {

    private static final String SPREADSHEET_ID = "ID";
    private static final String SPREADSHEET_URL = "https://docs.google.com/spreadsheets/d/1uPjggMhUw5cZa6kXNHOWbQ0ok4hfSWFzKqh47RQhrQE";

    private static final GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();


    public static void MilcomSheetCommand() throws AuthenticationException, MalformedURLException {

        //build API client service
        SpreadsheetService sheetsService = new SpreadsheetService("Print Google Demo");

        //login
        sheetsService.setUserCredentials(Config.gmailName, Config.gmailPass);

        //load sheet
        URL url = new URL(SPREADSHEET_URL);


    }

}
