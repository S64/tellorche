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
    print('msg: Tellorche ESP32 Controller.')
    print('cmd: wakeup.')
    while True:
        print('dbg: wait command...')
        line = sys.stdin.readline().splitlines()[0]
        if line.startswith('cmd-controller: '):
            cmd = line[16:]
            if cmd == 'reset':
                print('msg: Disconnecting Wi-Fi...')
                wifi.disconnect()
                while wifi.isconnected():
                    time.sleep(1)
                    print('dbg: .')
                wifi = None
            elif cmd.startswith('wifi_ssid '):
                wifi_ssid = cmd[10:]
                print('msg: Wi-Fi SSID: `' + wifi_ssid + '`.')
            elif cmd.startswith('wifi_passphrase '):
                wifi_passphrase = cmd[16:]
                print('msg: Wi-Fi Passphrase: [secret]')
            elif cmd == 'connect':
                print('msg: Connect to Wi-Fi...')
                wifi = network.WLAN(network.STA_IF)
                wifi.active(True)
                wifi.connect(wifi_ssid, wifi_passphrase)
                while not wifi.isconnected():
                    time.sleep(1)
                    print('dbg: .')
                print('msg: Wi-Fi connected.')
                print('cmd: Wi-Fi connected.')
            else:
                print('msg: Can\'t understand controller cmd: `' + cmd + '`.')
        elif line.startswith('cmd-tello: '):
            print('msg: cmd-tello.')
        else:
            print('msg: Can\'t understand: `' + line + '`.')

main()
