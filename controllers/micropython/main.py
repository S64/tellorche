import time
import os
import sys
import network
import machine

TELLO_IP_ADDR = '192.168.10.1'
TELLO_UDP_PORT = 8889

global wifi
global wifi_ssid
global wifi_passphrase

def main():
    wifi = None
    wifi_ssid = None
    wifi_passphrase = None
    responseMessage('Tellorche ESP32 Controller.')
    responseCommand('wakeup.')
    while True:
        line = readLine()
        if isResetCommand(line):
            if wifi is not None:
                responseMessage('Disconnecting Wi-Fi...')
                wifi.disconnect()
                while wifi.isconnected():
                    time.sleep(1)
                    responseDebugMessage('.')
                wifi = None
            else:
                responseMessage('Wi-Fi already disconnected.')
            machine.reset()
        if isControllerCommand(line):
            cmd = sliceControllerCommandBody(line)
            if cmd.startswith('wifi_ssid '):
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
                responseCommand('Wi-Fi connected.')
            else:
                responseMessage('Can\'t understand controller cmd: `' + cmd + '`.')
        elif isTelloCommand(line):
            responseMessage('cmd-tello.')
        else:
            responseMessage('Can\'t understand: `' + line + '`.')

def readLine():
    responseDebugMessage('wait command...')
    return sys.stdin.readline().splitlines()[0]

def isResetCommand(line):
    return line == '!reset'

def isControllerCommand(line):
    return line.startswith('cmd-controller: ')

def isTelloCommand(line):
    return line.startswith('cmd-tello: ')

def sliceControllerCommandBody(line):
    return line[16:]

def responseDebugMessage(msg):
    print('dbg: ' + msg)

def responseMessage(msg):
    print('msg: ' + msg)

def responseCommand(cmd):
    print('cmd: ' + cmd)

main()
