/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.kafka.clients.jms.security;

import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.common.security.plain.PlainLoginModule;


public class PlainLogin
{
   private static String JAAS_CONFIG_NAME = "KafkaClient";

   public static Configuration createJaasConfig(String username, String password) {

      // Create entry options.
      Map<String, Object> options = new HashMap<>();

      options.put("useFirstPass", "false");  // Do *not* use javax.security.auth.login.{name,password} from shared state.
      options.put("username", username);
      options.put("password", password);

      // Create entries.
      AppConfigurationEntry[] entries = {
         new AppConfigurationEntry(
                                     PlainLoginModule.class.getCanonicalName(),
                                     AppConfigurationEntry.LoginModuleControlFlag.REQUIRED,
                                     options)
      };

      // Create configuration.
      return new Configuration() {
         @Override
         public AppConfigurationEntry[] getAppConfigurationEntry(String name) {
            if(!JAAS_CONFIG_NAME.equals(name)){
               throw new IllegalArgumentException();
            }
            return entries;
         }
      };

   }
}
