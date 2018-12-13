# Tellorche

[![CircleCI](https://circleci.com/gh/S64/tellorche.svg?style=svg&circle-token=4ad500a05a550e4c8f15a1b015f109fc6a027f88)](https://circleci.com/gh/S64/tellorche)

**Orche**stration toolchain for **TELLO** drone.

## What's this?

Ryze Tech社のトイドローン[Tello](https://amzn.to/2yz09m5)を編隊飛行させるためのツールチェインです。  
PC向けのマスターアプリとコントローラにあたるデバイスを用意すれば、設定した飛行シーケンスに従ってTelloを動かすことができます。

## Getting Started

Tellorcheでは標準のビルドツールとしてGradleを採用しています。  
起動コマンドの `Gradle Wrapper` は、Windowsの場合は `gradlew.bat` に、\*nix系OSの場合は `gradlew` として読み替えてください。

`master-app`を利用する際は、事前に`controller`を用意してください。

<details>
<summary> `controllers/micropython` のプロビジョニング</summary>

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

※ 特定のモジュールのみ利用したい場合、全ての依存関係を解決する必要はありません

```sh
./gradlew.bat checkMicroPythonWriterRequirements
./gradlew.bat checkControllerWriterRequirements
```

### 3. 使用方法チェック

```sh
./gradlew.bat printCommandLineWriteMicroPythonForEsp32
./gradlew.bat printCommandLineWriteController
```

</details>

### 1. Java Runtime Environment 8以降がインストールされていることを確認

`master-app`はJavaで開発されています。利用するPCに **JRE8** または **それ以降** のバージョンがインストールされていることを確認してください。  
JREは[Oracleのウェブサイト](https://java.com/download/)からダウンロードできます。

なおTellorche GUIを利用したい場合は、上記のOracle版JREのような「JavaFXに対応したJRE」であること併せて確認してください。

JDKで利用したい場合、[Amazon Corretto](https://aws.amazon.com/jp/corretto/)などが該当します。

### 2. USB to UART Driverをインストール

ESP32系ボードのMicroPython Controllerを用いる場合などに必要です。  
インストーラは[Silicon Labsのウェブサイト](https://jp.silabs.com/products/development-tools/software/usb-to-uart-bridge-vcp-drivers)からダウンロードすることができます。  
なお、UbuntuなどのLinux系OSを利用している場合はドライバは不要です。

Windowsの場合は`COM*`、  
macOSの場合は`/dev/tty.SLAB_USBtoUART*`
Ubuntu等Linux系OSの場合は`/dev/ttyUSB*`

の形式でシリアルポートを検出することができます。

<details>
<summary>Ubuntuなどを使っていて、ポートの読書がPermission Deniedになる場合</summary>

以下のコマンドを実行し、再起動をしてみてください。

```sh
sudo usermod -a -G dialout $USER
```

</details>

### 3. 最新リリースをダウンロード

GitHubリポジトリの[Releases](https://github.com/S64/tellorche/releases)から、`tellorche-master-app.jar`をダウンロードしてください。

### 4. 飛行シーケンス設定ファイルを作成

サンプルが[examples/](examples/)に掲載されています。  
必要に応じて、`controllers`プロパティ内の`ssid / passphrase / com_descriptor`などを編集してください。

### 5. Tellorche GUIを起動

以下のコマンドで実行できます。

```sh
java -jar ${ダウンロードしたjarファイルのパス} gui
```

たとえば`C:\Users\myuser\Downloads\tellorche-master-app.jar`へダウンロードした場合、以下のようになります:

```dos
java -jar C:\Users\myuser\Downloads\tellorche-master-app.jar gui
```

## Supported Platforms

以下のプラットフォームをサポートします

### master-app

Windows / Mac / Linux distributions

利用には**Java SE 8以降**のインストールが必要です。

### controllers

Telloを操作するためには、いずれかのcontrollerが必要です。

#### controllers/micropython

MicroPythonに対応した開発ボード。[ESP32-DevKitC](https://amzn.to/2OZk3B0)での利用を想定しています。  
ESP32互換製品として[一部他社製のより安価なボード](https://amzn.to/2R8HYv0)でも動作確認をしています。

## Documents

- [開発の手引き](docs/development-tutorial.md)
- [関連資料](docs/useful-docs.md)
- [How to debug](docs/how-to-debug.md)

## How to build

前述の `./gradlew.bat build` による全ビルド以外に、特定モジュールのみビルドする方法があります。

### master-app

```sh
./gradlew.bat buildMasterApp
```

### controllers/micropython

Pythonはビルドする必要がありません。  
構文をチェックするには、以下を実行してください。

```sh
./gradlew.bat lintController
```

## License / 利用許諾

[LICENSE](./LICENSE) ファイルを参照してください。
