import time
import sys
import network
import machine
import usocket
import utime
import esp

TELLO_ADDR = ('192.168.10.1', 8889)
BIND_ADDR = ('0.0.0.0', 8889)
RESPONSE_BUFFER_SIZE = 4096
OS_DEBUG = None


def main():
    while True:
        wifi = None
        wifi_ssid = None
        wifi_passphrase = None
        connection = None
        esp.osdebug(OS_DEBUG)
        responseMessage('Tellorche ESP32 Controller.')
        responseCommand('wakeup.')
        try:
            while True:
                line = readLine()
                responseDebugMessage('Received line: `' + line + '`.')
                if isTryCrashCommand(line):
                    raise Exception
                elif isResetCommand(line):
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
                        wifi.active(False)
                        wifi = None
                    else:
                        responseMessage('Wi-Fi already disconnected.')
                    responseCommand('Wi-Fi disconnected.')
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
                        connection = usocket.socket(
                            usocket.AF_INET, usocket.SOCK_DGRAM)
                        connection.bind(BIND_ADDR)
                elif isTelloCommand(line):
                    cmd = sliceTelloCommandBody(line)
                    sendTelloCommand(connection, cmd)
                else:
                    responseMessage('Can\'t understand: `' + line + '`.')
        except Exception as e:
            responseMessage('Exception caught!')
            if connection is not None:
                responseDebugMessage(
                    'Finally block. But connection isn\'t finalized!')
                sendTelloCommand(connection, 'emergency')
            raise e


def readLine():
    responseDebugMessage('wait command...')
    return sys.stdin.readline().splitlines()[0]


def isResetCommand(line):
    return line == '!reset'


def isTryCrashCommand(line):
    return line == '!crash'


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
    responseDebugMessage('Send to tello: `' + cmd + '`.')
    connection.sendto(toBytes(cmd), TELLO_ADDR)
    sentTime = getTickMs()
    responseDebugMessage('Wait response from tello...')

    rawRes = toStr(connection.recv(RESPONSE_BUFFER_SIZE))
    if rawRes is None:
        return None

    reses = rawRes.splitlines()
    if len(reses) > 1:
        responseDebugMessage(
            'Warn: Received multiple responses: [' + ', '.join(reses) + '].')
    ret = reses[-1]
    responseDebugMessage('Response received from tello: `' +
                         ret + '` (' + str(getTickMs() - sentTime) + 'ms).')
    return ret


def toBytes(str):
    return str.encode('utf-8')


def toStr(bytes):
    ret = None
    try:
        ret = bytes.decode('utf-8', 'replace')
    except UnicodeError:
        responseDebugMessage('Failed to decode bytes to string. Ignored.')
        pass
    finally:
        return ret


def getTickMs():
    return utime.ticks_ms()


main()
