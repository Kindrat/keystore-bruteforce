package com.github.kindrat.keystorebrute;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public final class Context {
   public Config config;

   public Context(){
      config = withConfigFile();
   }

   private Config withConfigFile(){
      return ConfigFactory.load();
   }
}
