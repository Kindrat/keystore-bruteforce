package com.github.kindrat.keystorebrute.util;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.github.kindrat.keystorebrute.Storage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class FileUtil {
   private static final Logger LOG = LoggerFactory.getLogger(FileUtil.class);
   private static final Kryo KRYO = new Kryo();

   /**
    * Reading serialized dictionary from file
    * @param storageFile filename
    * @return dictionary storage object
    * @throws FileNotFoundException
    */
   public Storage readStorage(String storageFile) throws FileNotFoundException {
      LOG.info("Starting reading dictionary from file");
      long startTimeStamp = System.currentTimeMillis();
      Storage storage;
      try (
              Input input = new Input(new FileInputStream(storageFile))
      ){
         storage = KRYO.readObject(input, Storage.class);
      }
      LOG.info("Finished reading dictionary from file in {} sec", (System.currentTimeMillis() - startTimeStamp) / 1000);
      return storage;
   }

   /**
    * Writing dictionary to file
    * @param storage dictionary object
    * @param storageFile filename
    * @throws IOException
    */
   public void write(Storage storage, String storageFile) throws IOException {
      LOG.info("Starting writing dictionary to file");
      long startTimestamp = System.currentTimeMillis();
      File file = new File(storageFile);
      if (file.exists()) {
         LOG.debug("Deleting old dictionary file");
         file.delete();
      }

      try (
              RandomAccessFile raf = new RandomAccessFile(storageFile, "rw");
              Output output = new Output(new FileOutputStream(raf.getFD()))
      ){
         KRYO.writeObject(output, storage);
      }
      LOG.info("Finished writing dictionary file in {} sec", (System.currentTimeMillis() - startTimestamp) / 1000);
   }
}
