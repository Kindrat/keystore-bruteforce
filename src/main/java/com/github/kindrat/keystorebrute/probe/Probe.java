package com.github.kindrat.keystorebrute.probe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.concurrent.atomic.AtomicLong;

public class Probe {

   private static final Logger LOG = LoggerFactory.getLogger(Probe.class);
   private static final AtomicLong COUNTER = new AtomicLong(0);

   public static boolean test(String keystoreLocation, String password, int loggingStep) {
      try {
         File file = new File(keystoreLocation);
         FileInputStream is = new FileInputStream(file);
         KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
         keystore.load(is, password.toCharArray());
      } catch (FileNotFoundException e) {
         LOG.error("Keystore was not found in given location : {}", keystoreLocation, e);
         System.exit(1);
      } catch (CertificateException | NoSuchAlgorithmException | KeyStoreException e) {
         LOG.error("Internal keystore system error", e);
         System.exit(1);
      } catch (IOException e) {
         if (COUNTER.getAndIncrement()%loggingStep == 0){
            LOG.info("Currently processing {} iteration", COUNTER.get());
         }
         return false;
      }

      return true;
   }

}
