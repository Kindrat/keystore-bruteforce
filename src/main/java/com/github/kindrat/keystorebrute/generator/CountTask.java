package com.github.kindrat.keystorebrute.generator;

import java.util.concurrent.RecursiveTask;

/**
 * Recursive task to calculate the amount of all character combination of a given dictionary length.
 */
public class CountTask extends RecursiveTask<Double> {

   private byte pow;
   private byte passwordLength;

   public CountTask(byte pow, byte passwordLength){
      this.pow = pow;
      this.passwordLength = passwordLength;
   }

   @Override
   protected Double compute() {
      if (pow == passwordLength)
         return directResult();
      else{
         CountTask task = new CountTask((byte) (pow+1), passwordLength);
         task.fork();
         return directResult() + task.join();
      }
   }

   private Double directResult(){
      return Math.pow(Dictionary.chars.length, pow);
   }
}
