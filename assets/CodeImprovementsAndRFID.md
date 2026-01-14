# Code improvements and RFID Implementation

This document describes the improvements made to the Pi4J component catalogue and the implementation of the RFID reader. Basically what I did besides helping students.

## Recent Code Improvements

During my work on the project, I identified and fixed several issues to improve code quality and maintainability. Here's what I have changed:

### Standardized Component Initialization Logging

One of the first things I noticed was that some components logged their initialization while others didn't. This made debugging harder because you couldn't always tell if a component was properly set up. I added consistent logging to all components using a standard format:

```java
logDebug("Created new SimpleButton component on pin %s", address);
```

Now every component logs when it's created, which makes it much easier to trace what's happening during startup. Components that already had logging were updated to use the same format, so everything is consistent.

### Improved Exception Handling

I found a few places where exceptions were caught but not logged or documented. The main issue was in the `Buzzer` class where `InterruptedException` and `ExecutionException` were silently ignored. I added proper logging and documentation explaining why these exceptions can be safely ignored (they're expected during shutdown when melodies are interrupted).

Similarly, in the RFID components, I added comments explaining why certain exceptions like `RfidCollisionException` are expected and can be ignored: it happens when multiple cards are present, which is a normal scenario.

### Removed Code Duplication

All the application classes had their own `delay()` method that did exactly the same thing. I moved this to the base `Component` class as a static method, so now all applications just call `Component.delay(Duration.ofSeconds(5))` instead of duplicating the same code 13 times. This makes maintenance easier. Also if you need to change how delays works, you only change it once instead of 13 times.

### Fixed SPI Transfer Method

There was a bug in `SpiDevice.spiTransfer()` where it was returning the input array instead of the actual transfer result. SPI transfers are full-duplex, meaning you send data and receive data at the same time. The method now correctly returns the received data from `spi.transfer()`, which is what callers actually need.

## SPIDevice

A big part of my work was to implement and improve spi devices as the RFID reader is SPI based.

### SpiDevice Base Class

The `SpiDevice` class provides the foundation for all SPI-based components. It handles the low-level SPI communication and provides three main methods:

1. **`spiWrite(byte... data)`**: Sends data to the device without reading anything back. Useful when you just need to send commands.

2. **`spiTransfer(byte... data)`**: Performs a full-duplex transfer. You send data and get data back in the same operation. This is what most register-based devices use.

3. **`spiRead(int bytesToRead)`**: Reads a specific number of bytes by sending dummy bytes (0x00) to generate clock pulses. The device responds with actual data.

The `SpiDevice` class also handles SPI initialization, configuration, and cleanup. When you create a component that extends `SpiDevice`, you pass in the SPI configuration (channel, baud rate, etc.) and it sets everything up for you.

## Register-Based SPI Devices

Many SPI devices work with a register-based model. Instead of just sending raw data, you read and write to specific registers on the device. Each register has an address and contains configuration or data.

### The RegisterBasedSpiDevice Class

This class extends `SpiDevice` and adds methods specifically for working with register-based devices:

- **`readRegister(byte address)`**: Reads a single byte from a register
- **`readRegisters(byte startAddress, int count)`**: Reads multiple consecutive registers
- **`writeRegister(byte address, byte value)`**: Writes a byte to a register
- **`setBitsInRegister()`** and **`clearBitsInRegister()`**: Helper methods to modify specific bits without changing others

### How Register Reading Works

When you want to read a register, you need to tell the device which register you want. Most register-based devices use a protocol where:
- The MSB (most significant bit) of the address byte indicates read (1) or write (0)
- You send the register address with the read bit set
- The device responds with the register's value

In the implementation, `READ_BIT_MASK = 0x80` is used to set the read bit:

```java
byte[] result = spiTransfer((byte) (registerAddress | READ_BIT_MASK), (byte) 0x00);
return result[1];
```

The code sends two bytes: the register address with the read bit set, and a dummy byte. The device responds with two bytes: typically a status byte and the actual register value. The method returns the second byte which contains the data we want.

### Writing to Registers

Writing is simpler. You just send the register address (without the read bit) followed by the value you want to write:

```java
spiWrite(registerAddress, value);
```

The device receives this and updates the register accordingly.

## RFID Implementation

The RFID reader classes were originally from CrowPi but rewritten with the new SpiDevice and RegisterBasedSpiDevice.

### Architecture

The RFID implementation follows a layered architecture:

1. **`SpiDevice`**: Base class handling SPI communication
2. **`RegisterBasedSpiDevice`**: Adds register read/write functionality
3. **`MFRC522`**: Implements the MFRC522 chip protocol using register operations
4. **`RfidReader`**: High-level API that adds polling and event handling

### MFRC522 Class

The `MFRC522` class extends `RegisterBasedSpiDevice` and implements all the low-level operations needed to communicate with the RFID chip. It uses the register methods to:

- Configure the chip (set baud rates, antenna gain, etc.)
- Detect cards
- Read card data (UID, card type)
- Write to cards (for writable cards like Mifare)

For example, to detect if a card is present, it might read the status register:

```java
byte status = readRegister(PcdRegister.STATUS_REG);
// Check status bits to see if card is detected
```

### RfidReader Class

The `RfidReader` class provides a higher-level interface. Instead of manually polling for cards, you can register event handlers:

```java
RfidReader reader = new RfidReader(pi4j);
reader.onCardDetected(card -> {
    System.out.println("Card detected: " + card.getSerial());
});
```

Internally, `RfidReader` runs a polling thread that continuously checks for cards using the `MFRC522` methods. When a card is detected, it triggers the registered event handler.

### Different Methods for Card Detection

The `RfidReader` class provides three different methods for detecting cards, each with different use cases:

- **`onCardDetected(EventHandler<RfidCard> handler)`**:
  Registers a handler that is called every time a new card is detected.
  The handler remains active until you unregister it (by passing null).
  This is asynchronous and persistent.

- **`waitForNewCard(EventHandler<RfidCard> handler)`**:
  Blocks the current thread until a new card is detected, then calls the handler once and returns.
  Only new cards (not previously detected) are considered.
  This is synchronous and one-shot.

- **`waitForAnyCard(EventHandler<RfidCard> handler)`**:
  Blocks the current thread until any card (new or previously detected) is detected,
  then calls the handler once and returns.
  This is synchronous and one-shot.

### How to Use Register-Based Devices

If you want to create a new component for a register-based SPI device, here's the general approach:

1. **Extend RegisterBasedSpiDevice**: Your class should extend this base class
2. **Define register addresses**: Create constants or an enum for your device's registers
3. **Implement device-specific methods**: Use `readRegister()` and `writeRegister()` to interact with your device
4. **Handle device initialization**: Override the constructor to configure your device

Here's a simplified example:

```java
public class MySpiDevice extends RegisterBasedSpiDevice {
    private static final byte CONFIG_REG = 0x01;
    private static final byte DATA_REG = 0x02;
    
    public MySpiDevice(Context pi4j, SpiConfig config) {
        super(pi4j, config);
        // Initialize device
        writeRegister(CONFIG_REG, (byte) 0x80);
    }
    
    public byte readData() {
        return readRegister(DATA_REG);
    }
    
    public void writeData(byte value) {
        writeRegister(DATA_REG, value);
    }
}
```

The `RegisterBasedSpiDevice` class handles all the SPI communication details, so you can focus on your device's specific functionality.

Thank your for reading this far. If you have any questions don't hesitate to contact andreas.felder@students.fhnw.ch.