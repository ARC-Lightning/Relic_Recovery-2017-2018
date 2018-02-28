echo Connect
read
~/Library/Android/sdk/platform-tools/adb tcpip 5555
echo Disconnect, connect to DIRECT WiFi
read
~/Library/Android/sdk/platform-tools/adb connect 192.168.49.1:5555
