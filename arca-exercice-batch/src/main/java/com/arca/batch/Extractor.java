package com.arca.batch;


import com.arca.core.entity.DataEntity;
import com.arca.core.manager.DataManager;
import com.kolich.common.util.io.JumpToLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Scanner;

/**
 * Extract batch
 */
public class Extractor {
    // Slf4j logger
    private final static Logger LOGGER = LoggerFactory.getLogger(Extractor.class);

    // Source text file to extract
    private final static String DATA_FILE_NAME = "C:\\Users\\Wifsimster\\mercurial\\arca-exercice\\data.txt";
    private static long nbLines;

    // Custom constructor
    public Extractor() {
        try {

            String path = new File(".").getCanonicalPath();
            LOGGER.debug("RealPath : {}", path);

            nbLines = getNbLines();
            LOGGER.info("{} lines to extract", nbLines);
        } catch (IOException e) {
            LOGGER.error("Error : {}", e);
        }
    }

    public static void main(String... args) throws IOException {
        Extractor ext = new Extractor();
        ext.extractFile();
    }

    public void extractFile() {
        try {
            long startTime = System.nanoTime();

            DataEntity dataEntity = DataManager.getLastData();

            if (dataEntity != null) {
                LOGGER.info("Percentage extracted {}%", getPercentage(dataEntity.getLine()));
                LOGGER.info("Last line extracted was {}", dataEntity.getLine());
                readFile(dataEntity.getLine(), DATA_FILE_NAME);
            } else {
                LOGGER.info("Data file is read for the first time !");
                readFile(0, DATA_FILE_NAME);
            }

            long endTime = System.nanoTime();
            long duration = (endTime - startTime) / 1000000;

            LOGGER.info("Extract duration {} ms", duration);
        } catch (IOException e) {
            LOGGER.error("Error : {}", e);
        }
    }

    public float getPercentage(long line) {
        float percentage = ((float) line / (float) nbLines) * 100.0f;
        LOGGER.debug("Percentage {}%", percentage);
        return percentage;
    }

    public static long getNbLines() throws IOException {

        LOGGER.debug("Getting nb lines ...");

        long startTime = System.nanoTime();

        BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE_NAME));
        long lines = 0;
        while (reader.readLine() != null) {
            lines++;
        }
        reader.close();

        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1000000;

        LOGGER.info("Getting nb lines in : {} ms", duration);

        return lines;
    }

    /**
     * Ready each line of a text file starting to line X
     *
     * @param fileName
     * @throws IOException
     */
    private static void readFile(long lastLine, String fileName) throws IOException {
        final JumpToLine jtl = new JumpToLine(new File(fileName));

        try {
            // Seek to the last line read since we last tried to read any lines from this file
            jtl.seek(lastLine);

            // While there are any more lines to read from the last line read position, then read them
            while (jtl.hasNext()) {
                processLine(jtl.readLine(), jtl.getLastLineRead());
            }
        } catch (Exception e) {
            LOGGER.error("Error : {}", e);
        } finally {
            // Close the underlying reader and LineIterator
            jtl.close();
        }
    }

    /**
     * Process and save a line in db for the structure : timestamp, value, country
     * ie : 1229737658592,72,Russie
     *
     * @param line
     */
    private static void processLine(String line, Long lineNumber) {
        // Use a second Scanner to parse the content of each line
        Scanner scanner = new Scanner(line);
        scanner.useDelimiter(",");
        if (scanner.hasNext()) {

            // Assumes the line has a certain structure
            String timestamp = scanner.next();
            String value = scanner.next();
            String country = scanner.next();

            try {
                // Build Data com.arca.core.entity
                DataEntity dataEntity = new DataEntity();
                dataEntity.setDate(new Date(Long.valueOf(timestamp)));  // Assuming timestamp is in milliseconds
                dataEntity.setValue(Integer.parseInt(value));
                dataEntity.setCountry(country);
                dataEntity.setLine(lineNumber);

                LOGGER.debug("Data : {}", dataEntity.toString());
                DataManager.createData(dataEntity);

            } catch (UnknownHostException | NumberFormatException e) {
                LOGGER.error("Error : {}", e);
            }
        } else {
            LOGGER.warn("Empty or invalid line. Unable to process.");
        }
    }
}
