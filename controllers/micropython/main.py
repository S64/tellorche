import time
import os
import sys
import network
import machine
import usocket

TELLO_ADDR = ('192.168.10.1', 8889)

def main():
    wifi = None
    wifi_ssid = None
    wifi_passphrase = None
    connection = None
    responseMessage('Tellorche ESP32 Controller.')
    responseCommand('wakeup.')
    try:
        while True:
            line = readLine()
            responseDebugMessage('Received line: `' + line +'`.')
            if isResetCommand(line):
                if connection is not None:
                    connection.close()
                    connection = None
                if wifi is not None:
                    responseMessage('Disconnecting Wi-Fi...')
                    wifi.disconnect()
                    while wifi.isconnected():
                        time.sleep(1)
                        responseDebugMessage('.')
                    responseMessage('Wi-Fi disconnected.')
                    responseCommand('Wi-Fi disconnected.')
                    wifi.active(False)
                    wifi = None
                else:
                    responseMessage('Wi-Fi already disconnected.')
                break
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
                    connection = usocket.socket(usocket.AF_INET, usocket.SOCK_DGRAM)
                else:
                    responseMessage('Can\'t understand controller cmd: `' + cmd + '`.')
            elif isTelloCommand(line):
                cmd = sliceTelloCommandBody(line)
                responseDebugMessage('Send to tello: `' + cmd + '`.')
                sendTelloCommand(connection, cmd)
            else:
                responseMessage('Can\'t understand: `' + line + '`.')
    finally:
        if wifi is None or wifi.isconnected():
            responseDebugMessage('Finally block. But Wi-Fi isn\'t disconnected!')


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

def sliceTelloCommandBody(line):
    return line[11:]

def responseDebugMessage(msg):
    print('dbg: ' + msg)

def responseMessage(msg):
    print('msg: ' + msg)

def responseCommand(cmd):
    print('cmd: ' + cmd)

def sendTelloCommand(connection, cmd):
    connection.sendto(cmd.encode('utf-8'), TELLO_ADDR)

main()
