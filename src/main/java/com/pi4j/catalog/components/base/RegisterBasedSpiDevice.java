package com.pi4j.catalog.components.base;

import com.pi4j.context.Context;
import com.pi4j.io.spi.SpiConfig;

public abstract class RegisterBasedSpiDevice extends SpiDevice {
    protected static final byte READ_BIT_MASK = (byte) 0x80;

    protected RegisterBasedSpiDevice(Context pi4j, SpiConfig config) {
        super(pi4j, config);
    }

    /**
     * Reads a single byte from a given register.
     *
     * @param registerAddress The address of the register to read from.
     * @return The byte value read from the register.
     */
    protected byte readRegister(byte registerAddress) {
        byte[] result = spiTransfer((byte) (registerAddress | READ_BIT_MASK), (byte) 0x00);
        return result[1];
    }

    /**
     * Reads a sequence of bytes starting from a given register.
     *
     * @param startRegister The starting register address.
     * @param numBytesToRead The number of consecutive bytes/registers to read.
     * @return An array of bytes containing the data read.
     */
    protected byte[] readRegisters(byte startRegister, int numBytesToRead) {
        if (numBytesToRead <= 0) {
            return new byte[0];
        }
        byte[] txBuffer = new byte[numBytesToRead + 1];
        txBuffer[0] = (byte) (startRegister | READ_BIT_MASK);

        byte[] rxBuffer = spiTransfer(txBuffer);

        byte[] result = new byte[numBytesToRead];
        System.arraycopy(rxBuffer, 1, result, 0, numBytesToRead);
        return result;
    }

    /**
     * Writes a single byte to a given register.
     *
     * @param registerAddress The address of the register to write to.
     * @param value The byte value to write.
     */
    protected void writeRegister(byte registerAddress, byte value) {
        // Note: For write operations, the READ_BIT_MASK is NOT set.
        spiWrite(registerAddress, value);
    }

    /**
     * Helper method to set specific bits in a register without changing others.
     *
     * @param registerAddress The register to modify.
     * @param bitMask The bits to set.
     */
    protected void setBitsInRegister(byte registerAddress, byte bitMask) {
        byte currentValue = readRegister(registerAddress);
        writeRegister(registerAddress, (byte) (currentValue | bitMask));
    }

    /**
     * Helper method to clear specific bits in a register without changing others.
     *
     * @param registerAddress The register to modify.
     * @param bitMask The bits to clear.
     */
    protected void clearBitsInRegister(byte registerAddress, byte bitMask) {
        byte currentValue = readRegister(registerAddress);
        writeRegister(registerAddress, (byte) (currentValue & ~bitMask));
    }
}
