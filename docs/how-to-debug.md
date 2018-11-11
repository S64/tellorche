# How to debug

各種デバッグ手法の案内です。

## Debug w/o master-app

`master-app`を使わず`controllers/micropython`を通しTelloのコントロールする方法の解説です。

### 1. `controllers/micropython`の書き込まれたボードへシリアル接続する

たとえば\*nix系OSを利用している場合、以下のようにするとよいでしょう:

```sh
screen /dev/cu.SLAB_USBtoUART 115200
```

screenは`Ctrl + A`, `Ctrl + K`の順で入力後に表示される`Really kill this window [y/n]`から終了できます。

### 2. Telloへ接続する

Telloの電源を投入して待機してください。

```
cmd-controller: wifi_ssid TELLO-******
cmd-controller: wifi_passphrase ******
cmd-controller: connect
# msg: Wi-Fi connected.
# cmd: Wi-Fi connected.
# dbg: wait command...
```

### 3. Telloをコマンドモードに移行する

```
cmd-tello: command
# dbg: Received line: `cmd-tello: command`.
# dbg: Send to tello: `command`.
# dbg: Wait response from tello...
# dbg: Response received from tello: `ok` (56ms).
# dbg: wait command...
```

### 4. Telloを離陸させる

```
cmd-tello: takeoff
# dbg: Received line: `cmd-tello: takeoff`.
# dbg: Send to tello: `takeoff`.
# dbg: Wait response from tello...
# dbg: Response received from tello: `ok` (4309ms).
# dbg: wait command...
```

### 5. Telloを着陸させる

```
cmd-tello: land
# dbg: Received line: `cmd-tello: land`.
# dbg: Send to tello: `land`.
# dbg: Wait response from tello...
# dbg: Response received from tello: `ok` (4463ms).
# dbg: wait command...
```

### Appendix: Telloを緊急着陸させる

`emergency`コマンドによって緊急着陸させることができます。

```
cmd-tello: emergency
# dbg: Received line: `cmd-tello: emergency`.
# dbg: Send to tello: `emergency`.
# dbg: Wait response from tello...
```
