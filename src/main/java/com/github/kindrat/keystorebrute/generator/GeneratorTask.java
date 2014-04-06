package com.github.kindrat.keystorebrute.generator;

import com.github.kindrat.keystorebrute.Storage;

import java.util.concurrent.RecursiveAction;

/**
 * Password generator logic. We don't need to return any values, so just recursive action.
 */
public class GeneratorTask extends RecursiveAction {
   private char[] data;
   private byte start, end, index, r;
   private Storage storage;

   /**
    * Injection of all configuration parameters used in generator
    * @param storage storage object, where all passwords will be saved
    * @param data char buffer, that will be combined into password
    * @param start start index of dictionary array (if we want to uwe only subset of characters)
    * @param end end index of dictionary array (if we want to uwe only subset of characters)
    * @param index buffer index of currently replaced character
    * @param length final length of a generated password
    */
   public GeneratorTask(Storage storage, char[] data, byte start, byte end, byte index, byte length){
      this.storage = storage;
      this.data = data;
      this.start = start;
      this.end = end;
      this.index = index;
      this.r = length;
   }

   /**
    * Logic is simple - we try generating new task for every dictionary character at [index] position of a buffer and
    * fork it to release resources. When we get to the last position newly generated tasks will finish in the first
    * branch of "if" block and write result to the storage
    */
   @Override
   protected void compute() {
      if (index == r){
         storage.addPassword(new String(data));
      }else {
         for (byte i = start; i <= end; i++) {
            data[index] = Dictionary.chars[i];
            GeneratorTask task = new GeneratorTask(storage, data.clone(), start, end, (byte) (index + 1), r);
            task.fork();
         }
      }
   }
}
