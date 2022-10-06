# shelly1pm-mqtt-demo

This is a simple demonstration of how to use the MQTT protocol for piloting a Shelly 1PM device.
The power consumption by any devices connected to the switch are loaded on a SQLite db.

# Running with Maven dependencies

The EXECUTABLE .jar file with dependencies can be automatically generated with an IDE. 
Below is a video tutorial to do it with IntelliJ:
https://www.youtube.com/watch?v=870XIYMrlSo

Command from terminal:
mvn exec:java -Dexec.mainClass"simpleShellyConsumer"
