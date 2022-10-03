package it.centro.iot.shelly.mqtt;

import it.centro.iot.shelly.mqtt.jdbc.DBManager;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Date;
import java.sql.SQLException;
import java.util.UUID;

/**
 * @author Riccardo Prevedi
 * @created 28/09/2022 - 17:31
 * @project shelly-mqtt-smart-metering
 */

public class simpleShellyConsumer {

    private static final Logger logger = LoggerFactory.getLogger(simpleShellyConsumer.class);

    private static final String BROKER_ADDRESS = "192.168.10.19";

    private static final int BROKER_PORT = 1883;

    private static final String SHELLY_ID = "E09806A9D61F";

    private static final String SHELLY_MOD = "shelly1pm";

    //E.g.: shellies/<model>-<deviceid>/relay/0/power reports instantaneous power in Watts
    private static final String TOPIC = String.format("shellies/%s-%s/relay/0/power", SHELLY_MOD, SHELLY_ID);

    public static void main(String[] args) {

        logger.info("MQTT Consumer Started ...");

        try {

            //Generate a random MQTT client ID
            String clientId = UUID.randomUUID().toString();

            //Represents a persistent data store, used to store outbound and inbound messages while
            //they are in flight, enabling delivery to the QoS specified. In that case use a memory persistence.
            //When the application stops all the temporary data will be deleted.
            MqttClientPersistence persistence = new MemoryPersistence();

            //If the persistence is not passed to the constructor the default file persistence is used.
            //In case of a file-based storage the same MQTT client UUID should be used
            IMqttClient client = new MqttClient(String.format("tcp://%s:%d", BROKER_ADDRESS, BROKER_PORT), clientId, persistence);

            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setConnectionTimeout(10);

            //Connect to the target broker
            client.connect(options);

            logger.info("Connected ! Client ID: {}", clientId);

            //DBManager db = new DBManager(DBManager.JDBC_Driver_SQLite, DBManager.JDBCURLSQLite);
            DBManager db = new DBManager(DBManager.JDBCDriverMySQL, DBManager.JDBCURLMySQL);

            initSQLite(db);

            client.subscribe(TOPIC, (t, msg) -> {
                byte[] payload = msg.getPayload();
                logger.info("Message Received ({}) Message Received: {}", t, new String(payload));
                db.executeUpdate(String.format("INSERT INTO switch(measurement) VALUES(%s)", new String(payload)));
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void initSQLite(DBManager db) throws SQLException {

        try {

            db.executeQuery("SELECT * FROM switch LIMIT 1");

        } catch (SQLException e) {
            db.executeUpdate("DROP TABLE IF EXISTS switch");
            db.executeUpdate(
                    "CREATE TABLE switch (" + "measurement_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                            + "Timestamp DATETIME DEFAULT CURRENT_TIMESTAMP, " + "measurement VARCHAR(30))");
        }
    }
}



