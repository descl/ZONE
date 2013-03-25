package org.zoneproject.extractor.utils;

import org.apache.log4j.Logger;

/*
 * #%L
 * ZONE-utils
 * %%
 * Copyright (C) 2012 ZONE-project
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

//import org.apache.log4j.Logger;

/**
 *
 * @author Desclaux Christophe <christophe@zouig.org>
 */

public class TestLogger {
    private static final Logger  logger = Logger.getLogger(TestLogger.class);
    //org.zoneproject.extractor.utils.Logger.log.warn("titi");
    //Logger.log.debug("eee");
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
