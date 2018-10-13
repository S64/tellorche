## How to debug

```sh
socat -d -d pty,raw,echo=0 pty,raw,echo=0
# 2018/10/13 20:02:06 socat[8511] N PTY is /dev/ttys002
# 2018/10/13 20:02:06 socat[8511] N PTY is /dev/ttys003
# 2018/10/13 20:02:06 socat[8511] N starting data transfer loop with FDs [5,5] and [7,7]

nano -w $PATH_TO_YOUR_CONFIG_FILE
# Edit $.controllers.your-controller.type-esp32-config.com_descriptor = ${socat tty (e.g. /dev/ttys002)}

cat < /dev/ttys003
# connect to serial port

# run Tellorche master app

echo 'Wi-Fi connected.' > /dev/ttys003

# in master-app
exec

# in shell
echo 'Wi-Fi disconnected.' > /dev/ttys003
```
