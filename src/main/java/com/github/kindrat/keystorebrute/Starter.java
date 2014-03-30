package com.github.kindrat.keystorebrute;

import com.github.kindrat.keystorebrute.generator.Dictionary;
import com.github.kindrat.keystorebrute.generator.GeneratorTask;
import com.github.kindrat.keystorebrute.generator.Storage;
import com.github.kindrat.keystorebrute.util.Loggers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.concurrent.ForkJoinPool;

public class Starter {
   private static final Logger LOG = LoggerFactory.getLogger(Starter.class);
   private String keystoreLocation, storageFile;
   private int loggingStep, passwordLength;
   private boolean isForced;

   private Starter(String keystoreLocation, String storageFile, int loggingStep, int passwordLength, boolean isForced){
      this.keystoreLocation = keystoreLocation;
      this.storageFile = storageFile;
      this.loggingStep = loggingStep;
      this.passwordLength = passwordLength;
      this.isForced = isForced;
   }

   public static void main(String... args){
      try {
         Context context = new Context();

         String logLevel = context.config.getString("logging.loglevel");
         String pattern = context.config.getString("logging.pattern");
         Loggers.init(logLevel, pattern);

         LOG.info("Starting Application");

         String keystoreLocation = context.config.getString("keystore.name");
         String storageFile = context.config.getString("generator.storage");
         int loggingStep = context.config.getInt("keystore.loggingStep");
         int passwordLength = context.config.getInt("generator.length");
         boolean isForced = context.config.getBoolean("generator.isForced");

         Starter application = new Starter(keystoreLocation, storageFile, loggingStep, passwordLength, isForced);
         application.brute();

      }catch (Exception e){
         LOG.error("Uncaught exception {}", e.getClass().getCanonicalName(), e);
         throw new RuntimeException(e);
      }
   }

   private void brute() throws IOException, ClassNotFoundException {
      LOG.info("Starting generating passwords");
      Storage storage = !new File(storageFile).exists() || isForced ? getWithForkJoin() : readStorage();
      LOG.info("Finished generating passwords");

      LOG.info("Starting password test");
      //storage.getStream().forEach(password ->  );
      //TODO
      LOG.info("Finishing password test");

      LOG.info("Finished Application");
   }

   private Storage readStorage() throws IOException, ClassNotFoundException {
      FileInputStream fis = new FileInputStream(storageFile);
      ObjectInputStream ois = new ObjectInputStream(fis);
      Storage storage = (Storage) ois.readObject();
      ois.close();
      return storage;
   }

   private Storage getWithForkJoin() throws IOException {
      Storage storage = new Storage();
      int processors = Runtime.getRuntime().availableProcessors();
      LOG.info("Initialising pool for {} cores", processors);
      ForkJoinPool pool = new ForkJoinPool(processors);
      for (int i = 1; i <= passwordLength; i++) {
         LOG.info("Generating passwords with length {}", i);
         char[] charCombination = new char[i];
         GeneratorTask task = new GeneratorTask(storage, charCombination, 0, Dictionary.chars.length-1, 0, i);
         pool.invoke(task);
      }
      LOG.info("Cleaning pool");
      pool.shutdown();

      LOG.info("Flushing passwords to file");
      FileOutputStream fos = new FileOutputStream(storageFile);
      ObjectOutputStream oos = new ObjectOutputStream(fos);
      oos.writeObject(storage);
      oos.flush();
      oos.close();
      LOG.info("Finished flushing");

      return storage;
   }
}
