package com.github.kindrat.keystorebrute.generator;

import java.util.concurrent.RecursiveTask;

public class GeneratorTask extends RecursiveTask<String> {
   private final char[] data;
   private final int start, end, index, r;
   private final Storage storage;

   public GeneratorTask(Storage storage, char[] data, int start, int end, int index, int r){
      this.storage = storage;
      this.data = data;
      this.start = start;
      this.end = end;
      this.index = index;
      this.r = r;
   }
   @Override
   protected String compute() {
      // replace index with all possible elements. The condition
      // "end-i+1 >= r-index" makes sure that including one element
      // at index will make a combination with remaining elements
      // at remaining positions
      if (index != r) {
         for (int i = start; i <= end && end - i + 1 >= r - index; i++) {
            data[index] = Dictionary.chars[i];
            GeneratorTask task = new GeneratorTask(storage, data, i + 1, end, index + 1, r);
            task.fork();
            storage.addPassword(task.compute());
            task.tryUnfork();
         }
      }
      return new String(data);
   }
}
