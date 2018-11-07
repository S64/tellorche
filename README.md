# Tellorche

[![CircleCI](https://circleci.com/gh/S64/tellorche.svg?style=svg&circle-token=4ad500a05a550e4c8f15a1b015f109fc6a027f88)](https://circleci.com/gh/S64/tellorche)

**Orche**stration toolchain for **TELLO** drone.

## What's this?

Ryze Tech社のトイドローン[Tello](https://amzn.to/2yz09m5)を編隊飛行させるためのツールチェインです。  
PC向けのマスターアプリとコントローラにあたるデバイスを用意すれば、設定した飛行シーケンスに従ってTelloを動かすことができます。

## Getting Started

Tellorcheでは標準のビルドツールとしてGradleを採用しています。  
起動コマンドの `Gradle Wrapper` は、Windowsの場合は `gradlew.bat` に、*nix系OSの場合は `gradlew` として読み替えてください。

### 1. プロジェクトをclone

[GitHub Desktop](https://desktop.github.com/) アプリなどを使うと、簡単にGitリポジトリをcloneすることができます。  
このリポジトリをcloneし、clone先ディレクトリをTerminalで開いてください。

<details>

<summary>または、コマンドライン上でcloneしてください</summary>

```sh
cd ~/Documents # プロジェクトを設置したいディレクトリ
git clone git@github.com:S64/tellorche.git
cd tellorche
```

</details>

### 2. 依存するツールを確認

```sh
./gradlew.bat checkRequirements
# 
#  Task :tellorche:checkMasterAppRequirements
# OK: Java 8がインストールされています
# OK: JDK 8がインストールされています
# 
# > Task :tellorche:checkEsp32ControllerRequirements
# OK: PlatformIO Core (CLI) がインストールされています
# 
# BUILD SUCCESSFUL in 1s
# 1 actionable task: 1 executed
```

### 3. 一括ビルド

```sh
./gradlew.bat build
# ...
# BUILD SUCCESSFUL in 4s
# 1 actionable task: 1 executed
```

### 4. 使用方法チェック

```sh
./gradlew.bat printCommandLineMasterApp
./gradlew.bat printCommandLineUploadEsp32Controller
./gradlew.bat printCommandLineWriteMicroPythonForEsp32
```

## Supported Platforms

以下のプラットフォームをサポートします

### master-app

Windows / Mac / Linux distributions

利用には**Java SE 8以降**のインストールが必要です。

### controllers

Telloを操作するためには、いずれかのcontrollerが必要です。

#### controllers/esp32

[ESP32-DevKitC](https://amzn.to/2OZk3B0)

## Documents

- [開発の手引き](docs/development-tutorial.md)

## How to debug **without ESP32-DevKitC**

```sh
socat -d -d pty,raw,echo=0 pty,raw,echo=0
# 2018/10/13 20:02:06 socat[8511] N PTY is /dev/ttys002
# 2018/10/13 20:02:06 socat[8511] N PTY is /dev/ttys003
# 2018/10/13 20:02:06 socat[8511] N starting data transfer loop with FDs [5,5] and [7,7]

nano -w $PATH_TO_YOUR_CONFIG_FILE
# Edit $.controllers.your-controller.type-esp32-config.com_descriptor = ${socat tty (e.g. /dev/ttys002)}

cat < /dev/ttys002
# connect to serial port

# run Tellorche master app

echo 'cmd: Wi-Fi connected.' > /dev/ttys003

# in master-app
exec

# in shell
echo 'cmd: Wi-Fi disconnected.' > /dev/ttys003
```
