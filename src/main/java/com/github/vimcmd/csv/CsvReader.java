package com.github.vimcmd.csv;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class CsvReader {

    public static void parse() throws IOException {
        char columnSeparator = ';';
        CsvMapper mapper = new CsvMapper();
        CsvSchema schema = CsvSchema.builder().setColumnSeparator(columnSeparator).build();
        mapper.enable(CsvParser.Feature.WRAP_AS_ARRAY);

        // TODO: 04.07.2016 upload file and parse
        File csvFile = new File("src\\main\\resources\\users.csv");
        MappingIterator<String[]> it = mapper.readerFor(String[].class).with(schema).readValues(csvFile);

        while (it.hasNext()) {
            System.out.println(Arrays.toString(it.next()));
        }

    }

    public static void main(String[] args) {
        try {
            CsvReader.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
