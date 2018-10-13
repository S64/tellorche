// NOTICE: This program is Work-In-Progress.

#include <WiFi.h>
#include <WiFiUdp.h>

const String TELLO_IP_ADDR = "192.168.10.1";
const int TELLO_UDP_PORT = 8889;

void setup() {
    Serial.begin(115200);
    Serial.setTimeout(15000UL);
}

String wifiSsid = "";
String wifiPassphrase = "";

WiFiUDP connection;

void loop() {
    if (Serial.available() > 0) {
        String line = Serial.readStringUntil('\n');

        if (WiFi.status() != WL_CONNECTED) {
            if (line.equals("connect")) {
                /*
                char ssid[wifiSsid.length()];
                wifiSsid.toCharArray(ssid, wifiSsid.length() + 1);

                char passphrase[wifiPassphrase.length()];
                wifiPassphrase.toCharArray(passphrase,  + 1);

                WiFi.begin(ssid, passphrase);
                */
                WiFi.begin(wifiSsid.c_str(), wifiPassphrase.c_str());

                while (WiFi.status() != WL_CONNECTED) {
                    Serial.print(".");
                    delay(100);
                }

                Serial.println("Wi-Fi connected.");

                // fallthrough
            } else if (line.substring(0, 10).equals("wifi_ssid ")) {
                wifiSsid = line.substring(10);
                Serial.println("Wi-Fi SSID: `" + wifiSsid + "`.");
                return;
            } else if (line.substring(0, 16).equals("wifi_passphrase ")) {
                wifiPassphrase = line.substring(16);
                Serial.println("Wi-Fi Passphrase: [secret].");
                return;
            } else {
                Serial.println("warn: Wi-Fi not connected.");
                return;
            }
        } else if (line.equals("disconnect")) {
            WiFi.disconnect();
            while (WiFi.status() != WL_DISCONNECTED) {
                Serial.print(".");
                delay(100);
            }
            Serial.println("Wi-Fi disconnected.");
            return;
        }
        
        Serial.println("Send `" + line +"`.");

        connection.beginPacket(TELLO_IP_ADDR.c_str(), TELLO_UDP_PORT);
        connection.println(line.c_str());
        connection.endPacket();
    }
}
