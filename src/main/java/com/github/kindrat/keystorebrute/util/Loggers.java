package com.github.kindrat.keystorebrute.util;


import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

/**
 * Configuration class for application loggers
 */
public final class Loggers {

   private Loggers(){}

   /**
    * Util method for loggers initialisation
    * @param logLevel log level to use in loggers
    * @param pattern log output format
    * @throws IllegalArgumentException
    */
   public static void init(String logLevel, String pattern) throws IllegalArgumentException{
      Logger rootLogger = org.apache.log4j.Logger.getRootLogger();
      if (!rootLogger.getAllAppenders().hasMoreElements()) {
         rootLogger.setLevel(Level.toLevel(logLevel));
         rootLogger.addAppender(new ConsoleAppender(new PatternLayout(pattern)));
      }
   }
}
