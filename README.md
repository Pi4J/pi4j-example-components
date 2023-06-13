Pi4J V2 :: Java I/O Library for Raspberry Pi :: Example applications for various components
===========================================================================================

GitHub Actions:
![Maven build](https://github.com/pi4j/pi4j-example-serial/workflows/Maven/badge.svg)

## PROJECT OVERVIEW

This project shows how to use Pi4J with Maven for various electronic components connected to the Raspberry Pi.

## DETAILED INFORMATION

See [the Pi4J website](https://pi4j.com/examples/components/).


## Start application on Pi

Make sure that Pi and your development machine are in the same WLAN. 

There are ready-made run-configurations for 

- Set `launcher.class` in `pom.xml`:
    - `<launcher.class>com.pi4j.mvc/com.pi4j.mvc.templatepuiapp.AppStarter</launcher.class>`
- `Run local` makes no sense for PUI only applications
- With `Run on Pi` starts remotely on the Raspberry Pi

## Start application in debugger

To start the application on the Raspberry Pi in debug mode the two run configurations `Debug on Pi` and `Attach to Pi Debugger` are required.

The sequence of starting the run configurations is critical:

1. Start `Debug on Pi` using the **Run** button
2. Wait till the console contains the following message: `Listening for transport dt_socket at address: 5005 (Attach debugger)`
3. Start `Attach to Pi Debugger` using the **Debug** button
4. Only now will the GUI be shown on the Raspberry Pi screen

Now one can use the debugger from IntelliJ IDEA, setting breakpoints and stepping through the application.

## LICENSE

Pi4J Version 2.0 and later is licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
except in compliance with the License. You may obtain a copy of the License at:
http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "
AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
language governing permissions and limitations under the License.

