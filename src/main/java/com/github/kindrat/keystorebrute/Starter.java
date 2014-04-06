package com.github.kindrat.keystorebrute;

import com.github.kindrat.keystorebrute.generator.CountTask;
import com.github.kindrat.keystorebrute.generator.Dictionary;
import com.github.kindrat.keystorebrute.generator.GeneratorTask;
import com.github.kindrat.keystorebrute.util.Loggers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Entry point for a program, the whole lifecycle of will be placed here
 */
public class Starter {
   private static final Logger LOG = LoggerFactory.getLogger(Starter.class);
   private String keystoreLocation, storageFile;
   private byte passwordLength;
   private boolean isForced;
   private ForkJoinPool pool;
   private Storage storage;

   private Starter(String keystoreLocation, String storageFile, byte passwordLength, boolean isForced){
      this.keystoreLocation = keystoreLocation;
      this.storageFile = storageFile;
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
         byte passwordLength = context.config.getNumber("generator.length").byteValue();
         boolean isForced = context.config.getBoolean("generator.isForced");

         Starter application = new Starter(keystoreLocation, storageFile, passwordLength, isForced);
         application.brute();

         Thread.currentThread().join();

      }catch (Exception e){
         LOG.error("Uncaught exception {}", e.getClass().getCanonicalName(), e);
         throw new RuntimeException(e);
      }
   }

   /**
    * Lifecycle of a program. Here we generate dictionary if needed and try it against the given keystore
    * @throws IOException
    * @throws ClassNotFoundException in case of reading dictionary from file
    */
   private void brute() throws IOException, ClassNotFoundException {
      LOG.info("Starting generating passwords");
      int processors = Runtime.getRuntime().availableProcessors();
      LOG.info("Initialising pool for {} cores", processors);
      pool = new ForkJoinPool(processors);
      long combinations = pool.invoke(new CountTask((byte) 1, passwordLength)).longValue();
      LOG.info("With given configuration will try {} passwords", combinations);
      if (!new File(storageFile).exists() || isForced)
         getWithForkJoin();
      else
         readStorage();
      LOG.info("Finished generating passwords");

      LOG.info("Starting password test");
      //storage.getStream().forEach(password ->  );
      //TODO
      LOG.info("Finishing password test");

      LOG.info("Finished Application");
   }

   /**
    * Reading serialized dictionary from file
    * @throws IOException
    * @throws ClassNotFoundException
    */
   private void readStorage() throws IOException, ClassNotFoundException {
      FileInputStream fis = new FileInputStream(storageFile);
      ObjectInputStream ois = new ObjectInputStream(fis);
      storage = (Storage) ois.readObject();
      ois.close();
   }

   /**
    * One of methods to generate dictionary is to use fork-join pool. It will use system resources in a most appropriate
    * way and complete task as fast as possible. But it's java 7 style, so later will be added something with streams
    * or lambdas...
    * @throws IOException in case of exception during saving dictionary to file
    */
   private void getWithForkJoin() throws IOException {
      long combinations = pool.invoke(new CountTask((byte) 1, passwordLength)).longValue();
      long startTimestamp = System.currentTimeMillis();
      LOG.info("Generating {} passwords", combinations);
      AtomicLong counter = new AtomicLong(combinations);
      long loggingStep = combinations/100 > 0 ? combinations/100 : 1;
      storage = new Storage(counter, loggingStep);

      for (byte i = 1; i <= passwordLength; i++) {
         calculateCombinationsOfALength(i);
      }

      while (true){
         if (pool.isQuiescent()) break;
         // Here we'll be waiting till all tasks finish adding passwords to storage.
      }

      LOG.info("Cleaning pool");
      pool.shutdown();

      LOG.info("Flushing passwords to file");
      FileOutputStream fos = new FileOutputStream(storageFile);
      ObjectOutputStream oos = new ObjectOutputStream(fos);
      oos.writeObject(storage);
      oos.flush();
      oos.close();
      long finishTimestamp = System.currentTimeMillis();
      LOG.info("Finished flushing. Total time {} sec", (finishTimestamp-startTimestamp)/1000);
   }

   /**
    * Single iteration logic, which adds new task for specific pass length to the pool
    * @param length password length to generate with pool
    */
   private void calculateCombinationsOfALength(byte length) {
      char[] charCombination = new char[length];
      GeneratorTask task = new GeneratorTask(storage, charCombination, (byte) 0, (byte) (Dictionary.chars.length-1), (byte) 0, length);
      pool.invoke(task);
   }
}
