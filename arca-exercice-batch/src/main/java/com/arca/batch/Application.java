package com.arca.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.StopWatch;

@SpringBootApplication
public class Application {

    // Slf4j logger
    private final static Logger LOGGER = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        StopWatch stopWatch = new StopWatch();
        LOGGER.debug("STARTING STOPWATCH");
        stopWatch.start();

        SpringApplication.run(Application.class, args);

        LOGGER.debug("STOPPING STOPWATCH");
        stopWatch.stop();
        LOGGER.debug("Stopwatch time: " + stopWatch);
    }
}