package com.example;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class LeggiFileCSVJob implements Job {
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String csvFile = "/Users/fabrizioferla/Downloads/prova.csv";
        CSVReader reader = null;
        try {
            File file = new File(csvFile);
            reader = new CSVReader(new FileReader(file));
            String[] line;
            while ((line = reader.readNext()) != null) {
                System.out.println(line[0] + " " + line[1] + " " + line[2] + " " + line[3] + " " + line[4] + " " +line[5] + " " +line[6] + " " +line[7] + " " + line[8] + " " +line[9] + " " );
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Job eseguito alle " + new Date());
        int c = countPosition();
        System.out.println("prova"+c);
    }


    public int countPosition(){

        String text= "Year,Industry_aggregation_NZSIOC,Industry_code_NZSIOC,Industry_name_NZSIOC,Units,Variable_code,Variable_name,Variable_category,Value,Industry_code_ANZSIC06";
        int count=0;

        count = text.split(",").length - 1;

        return count;
    }
}

