# Serial GPS with NEO-7M

### WIRING

The GPS module used in this example: NEO-7M.

| NEO-7M    | Raspberry Pi            |
| :---      | :---                    |
| VCC       | Power 5V (e.g. pin 2)   |
| GND       | Ground (e.g. pin 6)     |
| RX        | UART TX, GPIO 15        |
| TX        | UART RX, GPIO 16        |

![Wiring between Raspberry Pi and GPS module](../../../../../../../../assets/raspberrypi_gps.png)

### TEST IN TERMINAL

```
$ sudo apt-get install gpsd gpsd-clients
$ sudo service gpsd start
$ sudo systemctl status gpsd.socket
● gpsd.socket - GPS (Global Positioning System) Daemon Sockets
   Loaded: loaded (/lib/systemd/system/gpsd.socket; enabled; vendor preset: enabled)
   Active: active (running) since Tue 2021-10-05 16:03:17 CEST; 1min 37s ago
   Listen: /var/run/gpsd.sock (Stream)
           [::1]:2947 (Stream)
           127.0.0.1:2947 (Stream)
    Tasks: 0 (limit: 4915)
   CGroup: /system.slice/gpsd.socket

Oct 05 16:03:17 raspberrypi systemd[1]: Listening on GPS (Global Positioning System) Daemon Sockets.
$ gpsmon /dev/ttyS0
```

It can take a while before you receive signal from enough satellites!

![Screenshot oif gpsmon](../../../../../../../../assets/screenshot-gpsmon.png)

### Running the application on Raspberry Pi

```
$ git clone https://github.com/Pi4J/pi4j-example-serial.git
$ cd https://github.com/Pi4J/pi4j-example-serial.git
$ mvn package
$ cd target/distribution
$ sudo ./run.sh

[main] INFO com.pi4j.util.Console - ************************************************************
[main] INFO com.pi4j.util.Console - ************************************************************
[main] INFO com.pi4j.util.Console - 
[main] INFO com.pi4j.util.Console -                   <-- The Pi4J Project -->                  
[main] INFO com.pi4j.util.Console -                    Serial Example project                   
[main] INFO com.pi4j.util.Console - 
[main] INFO com.pi4j.util.Console - ************************************************************
[main] INFO com.pi4j.util.Console - ************************************************************
[main] INFO com.pi4j.util.Console - 
[main] INFO com.pi4j.Pi4J - New auto context
[main] INFO com.pi4j.Pi4J - New context builder
[main] INFO com.pi4j.platform.impl.DefaultRuntimePlatforms - adding platform to managed platform map [id=raspberrypi; name=RaspberryPi Platform; priority=5; class=com.pi4j.plugin.raspberrypi.platform.RaspberryPiPlatform]
...
[main] INFO com.pi4j.util.Console - Waiting till serial port is open
[main] INFO com.pi4j.util.Console - 
[main] INFO com.pi4j.util.Console - Serial port is open
...
[SerialReader] INFO com.pi4j.util.Console - Data: '$GPVTG,,T,,M,2.349,N,4.351,K,A*2C'
[SerialReader] INFO com.pi4j.util.Console - Data: '$GPGGA,143723.00,5054.02265,N,00301.10531,E,1,04,10.46,86.2,M,45.9,M,,*5C'
[SerialReader] INFO com.pi4j.util.Console - Data: '$GPGSA,A,3,09,16,05,07,,,,,,,,,17.19,10.46,13.64*33'
[SerialReader] INFO com.pi4j.util.Console - Data: '$GPGSV,2,1,06,05,33,304,20,07,67,101,21,09,39,079,29,11,38,228,20*7E'
[SerialReader] INFO com.pi4j.util.Console - Data: '$GPGSV,2,2,06,16,11,030,24,20,63,283,*73'
[SerialReader] INFO com.pi4j.util.Console - Data: '$GPGLL,5054.02265,N,00301.10531,E,143723.00,A,A*6A'
```