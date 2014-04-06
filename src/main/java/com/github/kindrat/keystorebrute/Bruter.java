package com.github.kindrat.keystorebrute;

import com.github.kindrat.keystorebrute.generator.Dictionary;
import com.github.kindrat.keystorebrute.generator.GeneratorTask;
import com.github.kindrat.keystorebrute.util.ConcurrentUtil;
import com.github.kindrat.keystorebrute.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicLong;

public class Bruter {
   private static final Logger LOG = LoggerFactory.getLogger(Bruter.class);

   private String keystoreLocation, storageFile;
   private byte passwordLength;
   private Storage storage;

   public Bruter(String keystoreLocation, String storageFile, byte passwordLength){
      this.keystoreLocation = keystoreLocation;
      this.storageFile = storageFile;
      this.passwordLength = passwordLength;
   }

   /**
    * Lifecycle of a program. Here we generate dictionary if needed and try it against the given keystore
    * @throws java.io.IOException
    * @throws ClassNotFoundException in case of reading dictionary from file
    */
   public void brute(ForkJoinPool pool, boolean isForced) throws IOException, ClassNotFoundException {
      long combinations = ConcurrentUtil.calculateCombinationsAmount(pool, passwordLength);
      LOG.info("With given configuration will try {} passwords", combinations);
      storage = !new File(storageFile).exists() || isForced ? getWithForkJoin(pool, combinations) : new FileUtil().readStorage(storageFile);
      LOG.info("Starting password test");
      //storage.getStream().forEach(password ->  );
      //TODO
      LOG.info("Finishing password test");
      LOG.info("Finished Application");
   }

   /**
    * One of methods to generate dictionary is to use fork-join pool. It will use system resources in a most appropriate
    * way and complete task as fast as possible. But it's java 7 style, so later will be added something with streams
    * or lambdas...
    * @return dictionary storage object
    * @throws IOException in case of exception during saving dictionary to file
    */
   private Storage getWithForkJoin(ForkJoinPool pool, long combinations) throws IOException {
      long startTimestamp = System.currentTimeMillis();
      LOG.info("Generating {} passwords", combinations);
      AtomicLong counter = new AtomicLong(combinations);
      long loggingStep = combinations/100 > 0 ? combinations/100 : 1;
      Storage storage = new Storage(counter, loggingStep);

      for (byte i = 1; i <= passwordLength; i++) {
         calculateCombinationsOfALength(pool, storage, i);
      }

      while (true){
         if (pool.isQuiescent()) break;
         // Here we'll be waiting till all tasks finish adding passwords to storage.
      }
      LOG.info("Finished generating. Total time {} sec", (System.currentTimeMillis()-startTimestamp)/1000);
      pool.shutdown();
      new FileUtil().write(storage, storageFile);

      return storage;
   }

   /**
    * Single iteration logic, which adds new task for specific pass length to the pool
    * @param length password length to generate with pool
    */
   private void calculateCombinationsOfALength(final ForkJoinPool pool, final Storage storage, byte length) {
      char[] charCombination = new char[length];
      GeneratorTask task = new GeneratorTask(storage, charCombination, (byte) 0, (byte) (Dictionary.chars.length-1), (byte) 0, length);
      pool.invoke(task);
   }
}
