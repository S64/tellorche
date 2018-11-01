// NOTICE: This program is Work-In-Progress.

#include <ESP.h>
#include <WiFi.h>
#include <WiFiUdp.h>

const String TELLO_IP_ADDR = "192.168.10.1";
const int TELLO_UDP_PORT = 8889;
const char LINE_SEPARATOR = '\n';

const int LOCAL_PORT = 9000;

void setup() {
    Serial.begin(921600);
    Serial.setTimeout(1000UL);

    Serial.println("msg: Tellorche M5Stack Controller.");
    Serial.println("cmd: wakeup.");
}

String wifiSsid = "";
String wifiPassphrase = "";

WiFiUDP connection;

void loop() {
    if (Serial.available() > 0) {
        String line = Serial.readStringUntil(LINE_SEPARATOR);

        Serial.println("msg: Received command: `" + line + "`.");

        if (line.equals("reset")) {
            Serial.println("msg: Disconnecting Wi-Fi...");
            WiFi.disconnect(true);
            while (WiFi.status() == WL_CONNECTED) {
                // Serial.println("msg: " + WiFi.status());
                delay(100);
            }
            Serial.println("msg: Wi-Fi disconnected.");
            Serial.println("cmd: Wi-Fi disconnected.");

            ESP.restart();

            return;
        } else if (line.equals("connect")) {
            Serial.println("msg: Connect to Wi-Fi...");
            WiFi.begin(wifiSsid.c_str(), wifiPassphrase.c_str());

            while (WiFi.status() != WL_CONNECTED) {
                // Serial.println("msg: " + WiFi.status());
                delay(100);
            }

            Serial.println("cmd: Wi-Fi connected.");
            Serial.println("msg: Wi-Fi connected.");
            connection.begin(LOCAL_PORT);

            return;
        } else if (line.substring(0, 10).equals("wifi_ssid ")) {
            wifiSsid = line.substring(10);
            Serial.println("msg: Wi-Fi SSID: `" + wifiSsid + "`.");
            return;
        } else if (line.substring(0, 16).equals("wifi_passphrase ")) {
            wifiPassphrase = line.substring(16);
            Serial.println("msg: Wi-Fi Passphrase: [secret].");
            return;
        } else if (WiFi.status() != WL_CONNECTED) {
            Serial.println("msg: warn: Wi-Fi not connected.");
            return;
        }

        Serial.println("msg: Send `" + line +"`.");

        connection.beginPacket(TELLO_IP_ADDR.c_str(), TELLO_UDP_PORT);
        connection.print(line);
        connection.endPacket();
    }
}
