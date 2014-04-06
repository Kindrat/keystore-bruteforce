package com.github.kindrat.keystorebrute;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * Configuration for application. Useful for mocks.
 */
public final class Context {
   public final Config config;

   public Context(){
      config = withConfigFile();
   }

   private Config withConfigFile(){
      return ConfigFactory.load();
   }
}
