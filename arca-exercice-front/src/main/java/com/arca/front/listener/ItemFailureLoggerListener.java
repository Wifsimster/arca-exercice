package com.arca.front.listener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.listener.ItemListenerSupport;

/**
 * Logging Item Processing and Failures
 * FYI : http://docs.spring.io/spring-batch/trunk/reference/html/patterns.html
 */
public class ItemFailureLoggerListener extends ItemListenerSupport {

    private static Log LOGGER = LogFactory.getLog("item.error");

    public void onReadError(Exception e) {
        LOGGER.error("Encountered error on read", e);
    }

    public void onWriteError(Exception e, Object item) {
        LOGGER.error("Encountered error on write", e);
    }
}