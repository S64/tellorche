import time
import os
import sys
import network

TELLO_IP_ADDR = '192.168.10.1'
TELLO_UDP_PORT = 8889

global wifi
global wifi_ssid
global wifi_passphrase

def main():
    responseMessage('Tellorche ESP32 Controller.')
    print('cmd: wakeup.')
    while True:
        responseDebugMessage('wait command...')
        line = readLine()
        if isControllerCommand(line):
            cmd = line[16:]
            if cmd == 'reset':
                responseMessage('Disconnecting Wi-Fi...')
                wifi.disconnect()
                while wifi.isconnected():
                    time.sleep(1)
                    responseDebugMessage('.')
                wifi = None
            elif cmd.startswith('wifi_ssid '):
                wifi_ssid = cmd[10:]
                responseMessage('Wi-Fi SSID: `' + wifi_ssid + '`.')
            elif cmd.startswith('wifi_passphrase '):
                wifi_passphrase = cmd[16:]
                responseMessage('Wi-Fi Passphrase: [secret]')
            elif cmd == 'connect':
                responseMessage('Connect to Wi-Fi...')
                wifi = network.WLAN(network.STA_IF)
                wifi.active(True)
                wifi.connect(wifi_ssid, wifi_passphrase)
                while not wifi.isconnected():
                    time.sleep(1)
                    responseDebugMessage('.')
                responseMessage('Wi-Fi connected.')
                print('cmd: Wi-Fi connected.')
            else:
                responseMessage('Can\'t understand controller cmd: `' + cmd + '`.')
        elif line.startswith('cmd-tello: '):
            responseMessage('cmd-tello.')
        else:
            responseMessage('Can\'t understand: `' + line + '`.')

def readLine():
    return sys.stdin.readline().splitlines()[0]

def isControllerCommand(line):
    return line.startswith('cmd-controller: ')

def responseDebugMessage(msg):
    print('dbg: ' + msg)

def responseMessage(msg):
    print('msg: ' + msg)

main()
