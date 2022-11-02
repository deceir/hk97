package net.hk.hk97.Controllers;

import net.hk.hk97.Models.Bank.Bank;
import net.hk.hk97.Repositories.BankRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Controller
public class BankController {


    @Autowired
    private BankRepository bankDao;

    @GetMapping("/banks")
    public @ResponseBody
    ResponseEntity<?> viewAllBanksInJSONFormat() {
        ResponseEntity<List<Bank>> responseEntity;
        List<Bank> list = bankDao.findAll();
        responseEntity = new ResponseEntity<>(bankDao.findAll(), HttpStatus.OK);
        return responseEntity;
    }

    @GetMapping(value = "/bankCSV", produces = "text/csv")
    public ResponseEntity<?> exportCSV() {
        // replace this with your header (if required)
        String[] csvHeader = {
                "name", "nation_id","discord_id", "cash", "food", "uranium", "lead", "coal", "iron", "oil", "bauxite", "steel", "munitions", "gasoline", "aluminum"
        };

        // replace this with your data retrieving logic
        List<Bank> banks = bankDao.findAll();


        ByteArrayInputStream byteArrayOutputStream;

        // closing resources by using a try with resources
        // https://www.baeldung.com/java-try-with-resources
        try (
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                // defining the CSV printer
                CSVPrinter csvPrinter = new CSVPrinter(
                        new PrintWriter(out),
                        // withHeader is optional
                        CSVFormat.DEFAULT.withHeader(csvHeader)
                );
        ) {
            // populating the CSV content
            for (Bank bank : banks)
                csvPrinter.printRecord(bank.bankString());

            // writing the underlying stream
            csvPrinter.flush();

            byteArrayOutputStream = new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

        InputStreamResource fileInputStream = new InputStreamResource(byteArrayOutputStream);

        String csvFileName = "banks.csv";

        // setting HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + csvFileName);
        // defining the custom Content-Type
        headers.set(HttpHeaders.CONTENT_TYPE, "text/csv");

        return new ResponseEntity<>(
                fileInputStream,
                headers,
                HttpStatus.OK
        );
    }

}
