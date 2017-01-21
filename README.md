libxbee
=======


Android USB X-Bee driver. For details, on how to use, see: [here](https://github.com/uvwxy/daisy_main/blob/master/src/de/uvwxy/daisy/nodemap/guicontent/CMXBee.java
)


```java
XBeeDevice  xBeeDevice = (XBeeDevice) new XBeeDevice_UART(ctx);
((XBeeDevice_UART) xBeeDevice).setFrameCallback(frameCallback);
((UARTDevice) xBeeDevice).populateDevices();

Thread.sleep(3000); // ;)

if (((UARTDevice) xBeeDevice).bindToFirst()) {
  Log.i("XBEE", "CONNECTION SUCCESS");
  ((XBeeDevice_UART) xBeeDevice).setBaud(baud);
  xBeeDevice.init();
  Log.i("XBEE", "Connection initialized");

}
```
