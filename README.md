EditPi4J V2 :: Java I/O Library for Raspberry Pi :: Example applications for various components
===============================================================================================

GitHub Actions:
![Maven build](https://github.com/pi4j/pi4j-example-serial/workflows/Maven/badge.svg)

## PROJECT OVERVIEW

This project shows how to use Pi4J with Maven for various electronic components connected to the Raspberry Pi.

## DETAILED INFORMATION

See [Pi4J website](https://pi4j.com/examples/components/).

## Start application on Pi


It's strongly recommended to use the Linux image [Pi4J-Basic-OS](https://pi4j-download.com/latest.php?flavor=basic) and [IntelliJ IDEA](https://www.jetbrains.com/idea/) as IDE on your developer machine. But, of course, you can use any other IDE and use maven commands to experiment with our component catalogue.

In IntelliJ IDEA ready-made run-configurations are available for running the demo application on the RaspPi.

## Start application on Raspberry Pi
- Make sure that Pi and your development machine are in the same WLAN (see recommendations in [Pi4J OS](https://github.com/Pi4J/pi4j-os))
- Check whether the correct IP-address is set in `pom.xml`
  - set properties `<pi.hostname>` and `<pi.ipnumber>`
- `Run on Pi` compiles and packages the demo application on your developer machine, deploys it to Raspberry Pi and starts the demo application remotely on the Raspberry Pi

## Restart application on Raspberry Pi
Once you have started the demo application on Raspberry Pi using `Run on Pi`, you can restart it without recompiling
- `Rerun version on Pi` starts the demo application remotely on the Raspberry Pi without applying any changes

## Start application in debugger
To start the application on the Raspberry Pi in debug mode, two run configurations are required: `Debug on Pi` and `Attach to Pi Debugger`.

The sequence of starting the run configurations is critical:
1. Check whether the correct IP-address (or hostname) is set in `pom.xml`
1. Start `Debug on Pi` using the **Run** button 
1. Wait till the following message is displayed in console:  
`Listening for transport dt_socket at address: 5005 (Attach debugger)`
1. Start `Attach to Pi Debugger` using the **Debug** button
1. Only now the demo application is started

Now you can use the debugger from IntelliJ IDEA, setting breakpoints and stepping through the application. 

The output to console is in `Debug on Pi` tab  the debugger output in `Attach to Pi Debugger` tab. You have to switch between these tabs.

## LICENSE

Pi4J Version 2.0 and later is licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
except in compliance with the License. You may obtain a copy of the License at:
http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "
AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
language governing permissions and limitations under the License.
