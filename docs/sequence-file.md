# シーケンスファイル

Tellorcheでは飛行パターンを記述した設定ファイルを「シーケンスファイル」と呼んでいます。  
ここでは、シーケンスファイルの仕様および作成について記述します。

## ファイル形式

Tellorcheにおけるシーケンスファイルは[JSON形式](https://dev.classmethod.jp/etc/concrete-example-of-json/)で記述されています。ざっくり言えば、JavaScriptにおけるデータ記述書式です。

Tellorhceのシーケンスファイルでは、おおまかに以下のような情報が必要です:

- コマンドを送信する時間精度
- 移動する際の（記述内容に対する）距離, 速度の倍率
- 同時に利用するTELLOと、それに対応するコントローラの情報一覧
  - 利用するマイコンボードとそのCOMポート
  - Tello本体のWi-Fi APへ接続するSSID, パスフレーズ
- シーケンス情報
  - 開始から何ミリ秒のタイミングで実行するか
  - Telloへ送信するコマンド名
  - コマンドに付与するパラメータ
  - そのコマンドを同時に送信するドローン一覧

具体的には後述します。

## 構造

以下のような状況を想定します:

- 使用マイコンボード: MicroPython (on ESP32-DevKitC)
- 使用ドローン: 2機
  1. Tello1機目
    - SSID: TELLO-XXXX01
    - PW: mypassword01
    - COMポート: COM0
  2. Tello2機目
    - SSID: TELLO-XXXX02
    - PW: mypassword02
    - COMポート: COM1

シーケンスファイル全体像は以下のようになります:

```json
{
  "accuracy_in_millis": 100,
  "scale": {
    "x_in_cm": 1,
    "y_in_cm": 1,
    "z_in_cm": 1,
    "speed_in_cm_per_sec": 1
  },
  "controllers": {
    "my-controller-1": {
      "type": "MicroPython",
      "type-micropython-config": {
        "ssid": "TELLO-XXXX01",
        "passphrase": "mypassword01",
        "com_descriptor": "COM0"
      }
    },
    "my-controller-2": {
      "type": "MicroPython",
      "type-micropython-config": {
        "ssid": "TELLO-XXXX02",
        "passphrase": "mypassword02",
        "com_descriptor": "COM1"
      }
    }
  },
  "sequence": {
    "0": [
      {
        "command": "command",
        "params": [],
        "controllers": [
          "my-controller-1",
          "my-controller-2",
        ]
      },
      {
        "command": "set_speed",
        "params": ["100"],
        "controllers": [
          "my-controller-1",
          "my-controller-2",
        ]
      }
    ],
    "5000": [
      {
        "command": "left",
        "params": ["30"],
        "controllers": [
          "my-controller-1"
        ]
      },
      {
        "command": "right",
        "params": ["30"],
        "controllers": [
          "my-controller-2"
        ]
      }
    ],
    "10000": [
      {
        "command": "land",
        "params": [],
        "controllers": [
          "my-controller-1",
          "my-controller-2",
        ]
      }
    ]
  }
}
```

###  ルート

ルートは中括弧（`{`, `}`）に囲まれているため、オブジェクト値となり配下にはkey-value形式のデータが列挙されます。  
ルート直下の必須項目は以下です:

| key | 説明 | 内包されるデータ型 |
|-----|------|-----------------|
| `"accuracy_in_millis"` | 時間精度。何ミリ秒ごとにまとめてコマンドを送信するか | 数値 (ミリ秒) |
| `"scale"` | 各コマンドの倍率。3d方向に何倍の距離移動させるか、何倍の速度で移動させるか | オブジェクト |
| `"controllers"` | 利用するコントローラ（マイコンボード）の設定。これらにネストされ、Telloへの接続情報も記述される | オブジェクト |
| `"sequence"` | 実行するコマンドおよびその順序（タイミング） | オブジェクト |

簡略化すると、以下のようになります:

```json
{
  "accuracy_in_millis": ...,
  "scale": {...},
  "controllers": {...},
  "sequence": {...}
}
```

#### scale

scaleオブジェクト内には以下の内容が含まれます:

| key | 説明 | 内包されるデータ型 |
|-----|------|----------------|
| `"x_in_cm"` | 水平方向に何倍移動させるか | 数値（倍率） |
| `"y_in_cm"` | 垂直方向に何倍移動させるか | 数値（倍率） |
| `"z_in_cm"` | 奥行き方向に何倍移動させるか | 数値（倍率） |
| `"speed_in_cm_per_sec"` | 移動時に何倍早くするか | 数値（倍率） |

以下のように記述できます:

```json
...
  "scale": {
    "x_in_cm": 1,
    "y_in_cm": 1,
    "z_in_cm": 1,
    "speed_in_cm_per_sec": 1
  },
...
```

#### controllers

controllersオブジェクトには、利用するコントローラを列挙します。keyにはシーケンスファイル内で用いる、開発者が自由に決定する識別子を設定してください:

```json
...
  "controllers": {
    "識別子1": {...},
    "識別子2": {...},
    ...
  },
...
```

valueにはコントローラに関する情報、およびそれらが利用するドローンへの接続情報を記述します:

```json
...
    "識別子x": {
      "type": "MicroPython",
      "type-micropython-config": {
        "ssid": "TELLO-XXXX01",
        "passphrase": "mypassword01",
        "com_descriptor": "COM0"
      }
    },
...
```

| key | 説明 | 内包されるデータ型 |
|-----|------|----------------|
| `"type"` | コントローラ（マイコンボード）の種別。MicroPythonを設定 | 文字列 |
| `"type-micropython-config"."ssid"` | Tello本体のSSID | 文字列 |
| `"type-micropython-config"."passphrase"` | Tello本体のパスワード | 文字列 |
| `"type-micropython-config"."passphrase"` | マイコンボードのCOMディスクリプタ | 数値（倍率） |

COMディスクリプタには、接続したマイコンボードがPC上で認識される値を設定します。Tello GUIなどを利用すると確認できます。

#### sequence

sequenceオブジェクトには、コマンドを実行するタイミング、およびそのコマンドと送信先を列挙します。なお、ここで指定されたタイミングに対し `accuracy_in_millis` の精度が影響します。

keyにはコマンドを実行するタイミングをミリ秒単位で記述します:


```json
...
  "sequence": {
    "0": [...],     // 開始0秒で実行
    "500": [...],   // 開始0.5秒で実行
    "1000": [...],  // 開始1秒で実行
    "5250": [...],  // 開始5.25秒で実行
    "12000": [...], // 開始12秒で実行
    ...
  },
...
```

valueには角括弧（`[`, `]`）が指定されているとおり、内容として配列、リスト構造が期待されます。

##### リスト内アイテム

リスト内には複数オブジェクトが設定でき、この内容は指定されたタイミングで同時に実行されます。

```json
...
    "0": [
      {...},
      {...},
      ...
    ]
...
```

オブジェクトは以下のような構造を持ちます:

```json
...
      {
        "command": "takeoff",
        "params": [...],
        "controllers": [
          "識別子1",
          "識別子2",
          ...
        ]
      },
      ...
...
```

| key | 説明 | 内包されるデータ型 |
|-----|------|----------------|
| `"command"` | 実行するコマンド | 文字列 |
| `"params"` | コマンドに渡すパラメータ | 文字列 |
| `"controllers"` | コマンドを送出するコントローラ識別子 | 配列 => 文字列 |

コマンド名とそのパラメータを記述し、同様のコマンドを送出したいコントローラ識別子を列挙します。

コマンドには一部の例外を除き、Telloのサポートするものを直接利用できます。  
例えば `go 20 20 20 100` を送出する場合は以下のように記述できます:

```json
...
      {
        "command": "go",
        "params": ["20", "20", "20", "100"],
        "controllers": [...]
      },
      ...
...
```

## 特殊コマンド

Tellorcheでは、シーケンスファイル記述に際する形式の統一を目的とし、一部コマンドの挙動変更、及び特殊なコマンドの追加を行っています:

| コマンド名 | パラメータ | 説明 |
|----------|-----------|------|
| `tellorche-keepalive` | (none) | タイムアウトによる自動着陸を阻止する目的で、サポートされないパケットを送出します |
| `up` | <x: distance x cm> | `y_in_cm`が適用されます |
| `down` | <x: distance x cm> | `y_in_cm`が適用されます |
| `left` | <x: distance x cm> | `x_in_cm`が適用されます |
| `right` | <x: distance x cm> | `x_in_cm`が適用されます |
| `forward` | <x: distance x cm> | `z_in_cm`が適用されます |
| `back` | <x: distance x cm> | `z_in_cm`が適用されます |
| `set_speed` | <x: x cm per second> | `speed x`に対応し、`speed_in_cm_per_sec`が適用されます |
| `go` | <x, y, z, speed> | scaleの各値が適用されます |
| `curve` | <x1, y1, z1, x2, y2, z2, speed> | scaleの各値が適用されます |
