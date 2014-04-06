package com.github.kindrat.keystorebrute.util;

import com.github.kindrat.keystorebrute.generator.CountTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ForkJoinPool;

public class ConcurrentUtil {
   private static final Logger LOG = LoggerFactory.getLogger(ConcurrentUtil.class);

   public static ForkJoinPool getNewForkJoinPool(){
      int processors = Runtime.getRuntime().availableProcessors();
      LOG.info("Initialising pool for {} cores", processors);
      return new ForkJoinPool(processors);
   }

   public static ForkJoinPool getNewForkJoinPool(int a){
      LOG.info("Initialising pool for {} cores", a);
      return new ForkJoinPool(a);
   }

   public static long calculateCombinationsAmount(ForkJoinPool pool, byte passwordLength){
      return pool.invoke(new CountTask((byte) 1, passwordLength)).longValue();
   }

}
