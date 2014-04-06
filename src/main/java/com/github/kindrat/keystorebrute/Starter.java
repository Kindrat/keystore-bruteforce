package com.github.kindrat.keystorebrute;

import com.github.kindrat.keystorebrute.util.ConcurrentUtil;
import com.github.kindrat.keystorebrute.util.Loggers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Starter {
   private static final Logger LOG = LoggerFactory.getLogger(Starter.class);

   public static void main(String... args){
      try {
         Context context = new Context();

         String logLevel = context.config.getString("logging.loglevel");
         String pattern = context.config.getString("logging.pattern");
         Loggers.init(logLevel, pattern);

         LOG.info("Starting Application");

         String keystoreLocation = context.config.getString("keystore.name");
         String storageFile = context.config.getString("generator.storage");
         byte passwordLength = context.config.getNumber("generator.length").byteValue();
         boolean isForced = context.config.getBoolean("generator.isForced");

         Bruter application = new Bruter(keystoreLocation, storageFile, passwordLength);
         application.brute(ConcurrentUtil.getNewForkJoinPool(), isForced);

      }catch (Exception e){
         LOG.error("Uncaught exception {}", e.getClass().getCanonicalName(), e);
         throw new RuntimeException(e);
      }
   }

}
