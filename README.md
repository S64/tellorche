# Tellorche

**Orche**stration toolchain for **TELLO** drone.

## What's this?

Ryze Tech社のトイドローン[Tello](https://amzn.to/2yz09m5)を編隊飛行させるためのツールチェインです。  
PC向けのマスターアプリとコントローラにあたるデバイスを用意すれば、設定した飛行シーケンスに従ってTelloを動かすことができます。

## Supported Platforms

以下のプラットフォームをサポートします

### master-app

Windows / Mac / Linux distributions

利用には**Java SE 8以降**のインストールが必要です。

### controllers

Telloを操作するためには、いずれかのcontrollerが必要です。

#### controllers/m5stack

[M5Stack Basic](https://amzn.to/2SidgjO)

## How to debug **without M5Stack**

```sh
socat -d -d pty,raw,echo=0 pty,raw,echo=0
# 2018/10/13 20:02:06 socat[8511] N PTY is /dev/ttys002
# 2018/10/13 20:02:06 socat[8511] N PTY is /dev/ttys003
# 2018/10/13 20:02:06 socat[8511] N starting data transfer loop with FDs [5,5] and [7,7]

nano -w $PATH_TO_YOUR_CONFIG_FILE
# Edit $.controllers.your-controller.type-m5stack-config.com_descriptor = ${socat tty (e.g. /dev/ttys002)}

cat < /dev/ttys003
# connect to serial port

# run Tellorche master app

echo 'Wi-Fi connected.' > /dev/ttys003

# in master-app
exec

# in shell
echo 'Wi-Fi disconnected.' > /dev/ttys003
```
