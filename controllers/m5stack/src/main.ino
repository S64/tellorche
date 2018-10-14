// NOTICE: This program is Work-In-Progress.

#include <M5Stack.h>
#include <WiFi.h>
#include <WiFiUdp.h>

const String TELLO_IP_ADDR = "192.168.10.1";
const int TELLO_UDP_PORT = 8889;
const char LINE_SEPARATOR = '\n';

const int LOCAL_PORT = 9000;

void setup() {
    M5.begin();
    Serial.begin(921600);
    Serial.setTimeout(1000UL);

    M5.Lcd.println("Tellorche M5Stack Controller.");
}

String wifiSsid = "";
String wifiPassphrase = "";

char *buff;

WiFiUDP connection;

void loop() {
    // M5.Lcd.println("Wait command...");
    if (Serial.available() > 0) {
        String line = Serial.readStringUntil(LINE_SEPARATOR);

        M5.Lcd.println("Received command: `" + line + "`.");

        if (WiFi.status() != WL_CONNECTED) {
            if (line.equals("connect")) {
                M5.Lcd.println("Connect to Wi-Fi...");
                WiFi.begin(wifiSsid.c_str(), wifiPassphrase.c_str());

                while (WiFi.status() != WL_CONNECTED) {
                    M5.Lcd.print(WiFi.status());
                    delay(100);
                }

                Serial.println("Wi-Fi connected.");
                M5.Lcd.println("Wi-Fi connected.");
                connection.begin(LOCAL_PORT);

                //printReceived();

                return;
            } else if (line.substring(0, 10).equals("wifi_ssid ")) {
                wifiSsid = line.substring(10);
                M5.Lcd.println("Wi-Fi SSID: `" + wifiSsid + "`.");
                return;
            } else if (line.substring(0, 16).equals("wifi_passphrase ")) {
                wifiPassphrase = line.substring(16);
                M5.Lcd.println("Wi-Fi Passphrase: [secret].");
                return;
            } else {
                M5.Lcd.println("warn: Wi-Fi not connected.");
                return;
            }
        } else if (line.equals("disconnect") && WiFi.status() == WL_CONNECTED) {
            M5.Lcd.println("Disconnecting Wi-Fi...");
            WiFi.disconnect(true);
            while (WiFi.status() == WL_CONNECTED) {
                M5.Lcd.print(WiFi.status());
                delay(100);
            }
            M5.Lcd.println("Wi-Fi disconnected.");
            return;
        }
        
        M5.Lcd.println("Send `" + line +"`.");

        connection.beginPacket(TELLO_IP_ADDR.c_str(), TELLO_UDP_PORT);
        connection.print(line);
        connection.endPacket();

        //printReceived();
    }
}

/*
void printReceived() {
    while (true) {
        int receiveLength = connection.parsePacket();
        if (receiveLength != 0) {
            connection.read(buff, receiveLength);
            String packet = String(buff).substring(0, receiveLength);
            M5.Lcd.println(packet);
        } else {
            break;
        }
    }
}
*/
