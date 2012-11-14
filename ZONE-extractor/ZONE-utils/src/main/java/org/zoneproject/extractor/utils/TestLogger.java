package org.zoneproject.extractor.utils;

import org.apache.log4j.Logger;

/**
 *
 * @author Desclaux Christophe <christophe@zouig.org>
 */

public class TestLogger {
    private static final Logger  logger = Logger.getLogger(TestLogger.class);
    public static void main(String[] args) {
        for(int i=1 ; i<10; i++) {
            System.out.println("Counter = " + i);
            logger.debug("App message with DEBUG level");
            logger.info("App message with INFO level ");
            logger.warn("App message with WARN level ");
            logger.error("App message with ERROR level ");
            logger.fatal("App message with FATAL level ");
        }
    }
}
