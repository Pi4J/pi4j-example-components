package com.pi4j.catalog.components.base;

import com.pi4j.context.Context;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CConfig;
import com.pi4j.plugin.mock.provider.i2c.MockI2C;

public abstract class I2CDevice extends Component {
    /**
     * The PI4J I2C component
     */
    protected final I2C i2c;

    protected I2CDevice(Context pi4j, I2CConfig config){
        i2c = pi4j.create(config);
        init(i2c);
    }


    /**
     * Write a single command
     */
    protected void executeCommand(byte cmd) {
        i2c.write(cmd);
        delay(0, 100_000);
    }

    /**
     * Execute Display commands
     *
     * @param command Select the LCD Command
     * @param data    Setup command data
     */
    protected void executeCommand(byte command, byte data) {
        executeCommand((byte) (command | data));
    }

    protected abstract void init(I2C i2c);

    // --------------- for testing --------------------

    public MockI2C mock() {
        return asMock(MockI2C.class, i2c);
    }

}
