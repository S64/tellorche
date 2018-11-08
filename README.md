# Tellorche

[![CircleCI](https://circleci.com/gh/S64/tellorche.svg?style=svg&circle-token=4ad500a05a550e4c8f15a1b015f109fc6a027f88)](https://circleci.com/gh/S64/tellorche)

**Orche**stration toolchain for **TELLO** drone.

## What's this?

Ryze Tech社のトイドローン[Tello](https://amzn.to/2yz09m5)を編隊飛行させるためのツールチェインです。  
PC向けのマスターアプリとコントローラにあたるデバイスを用意すれば、設定した飛行シーケンスに従ってTelloを動かすことができます。

## Getting Started

Tellorcheでは標準のビルドツールとしてGradleを採用しています。  
起動コマンドの `Gradle Wrapper` は、Windowsの場合は `gradlew.bat` に、\*nix系OSの場合は `gradlew` として読み替えてください。

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

※ 特定のモジュールのみビルドしたい場合、全ての依存関係を解決する必要はありません

```sh
./gradlew.bat checkRequirements
# 
# > Task :tellorche:checkMasterAppRequirements
# OK: Java 8がインストールされています
# OK: JDK 8がインストールされています
# 
# > Task :tellorche:checkMicroPythonWriterRequirements
# OK: esptoolがインストールされています
# 
# > Task :tellorche:checkControllerWriterRequirements
# OK: ampyがインストールされています
# 
# > Task :tellorche:checkControllerLinterRequirements
# OK: pycodestyleがインストールされています
# 
# > Task :tellorche:checkControllerFormatterRequirements
# OK: autopep8がインストールされています
# 
# BUILD SUCCESSFUL in 2s
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
./gradlew.bat printCommandLineWriteMicroPythonForEsp32
./gradlew.bat printCommandLineWriteController
```

## Supported Platforms

以下のプラットフォームをサポートします

### master-app

Windows / Mac / Linux distributions

利用には**Java SE 8以降**のインストールが必要です。

### controllers

Telloを操作するためには、いずれかのcontrollerが必要です。

#### controllers/micropython

MicroPythonに対応した開発ボード。  
[ESP32-DevKitC](https://amzn.to/2OZk3B0) などが該当します。

## Documents

- [開発の手引き](docs/development-tutorial.md)
- [関連資料](docs/useful-docs.md)

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
