/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: EXAMPLE  :: Sample Code
 * FILENAME      :  module-info.java
 *
 * This file is part of the Pi4J project. More information about
 * this project can be found here:  https://pi4j.com/
 * **********************************************************************
 * %%
 * Copyright (C) 2012 - 2020 Pi4J
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
module com.pi4j.example {
    // Module Exports
    exports com.pi4j.catalog;
    exports com.pi4j.catalog.components;
    exports com.pi4j.catalog.components.base;
    exports com.pi4j.catalog.components.helpers;

    // Pi4J MODULES
    requires com.pi4j;
    requires com.pi4j.library.pigpio;
    requires com.pi4j.plugin.pigpio;
    requires com.pi4j.plugin.raspberrypi;
    requires com.pi4j.plugin.mock;
    requires com.pi4j.plugin.linuxfs;

    uses com.pi4j.extension.Extension;
    uses com.pi4j.provider.Provider;

    requires java.logging;
    requires info.picocli;

    // allow access to classes in the following namespaces for Pi4J annotation processing
    opens com.pi4j.catalog to info.picocli, com.pi4j;
}
