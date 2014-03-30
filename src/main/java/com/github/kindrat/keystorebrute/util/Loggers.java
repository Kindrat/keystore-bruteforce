package com.github.kindrat.keystorebrute.util;


import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

public final class Loggers {

   private Loggers(){}

   public static void init(String logLevel, String pattern) throws IllegalArgumentException{
      Logger rootLogger = org.apache.log4j.Logger.getRootLogger();
      if (!rootLogger.getAllAppenders().hasMoreElements()) {
         rootLogger.setLevel(Level.toLevel(logLevel));
         rootLogger.addAppender(new ConsoleAppender(new PatternLayout(pattern)));
      }
   }
}
