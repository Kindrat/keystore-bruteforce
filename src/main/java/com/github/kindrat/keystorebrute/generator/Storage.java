package com.github.kindrat.keystorebrute.generator;

import java.io.Serializable;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Stream;

public final class Storage implements Serializable{
   private static final long serialVersionUID = -6578793091933529663L;

   private Set<String> passwords = new CopyOnWriteArraySet<>();

   public void addPassword(String password){
      passwords.add(password);
   }

   public Stream<String> getStream(){
      return passwords.parallelStream();
   }
}
