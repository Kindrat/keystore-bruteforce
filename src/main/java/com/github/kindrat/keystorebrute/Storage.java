package com.github.kindrat.keystorebrute;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

public final class Storage implements Serializable{
   private static final Logger LOG = LoggerFactory.getLogger(Storage.class);
   private static final long serialVersionUID = -6578793091933529663L;
   private AtomicLong counter;
   private long loggingStep;
   private Set<String> passwords = new ConcurrentSkipListSet<>();

   public Storage() {
   }

   /**
    * For generator
    * @param counter passwords to write
    * @param loggingStep when to log
    */
   public Storage(AtomicLong counter, long loggingStep) {
      this.counter = counter;
      this.loggingStep = loggingStep;
   }

   public void addPassword(String password){
      passwords.add(password);
      if (counter.decrementAndGet()%loggingStep==0)
         LOG.debug("{} remains", counter.get());
   }

   public Set<String> getPasswords() {
      return passwords;
   }

   public void addPasswords(Collection<String> passwords){
      passwords.addAll(passwords);
   }

   public Stream<String> getStream(){
      return passwords.parallelStream();
   }

}
