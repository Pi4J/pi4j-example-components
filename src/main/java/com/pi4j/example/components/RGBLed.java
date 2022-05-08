package com.pi4j.example.components;

import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalOutputConfig;

public class RGBLed extends Component{
    /**
     * Pi4J digital output instance used by this component
     */
    protected final DigitalOutput digitalOutput;

    /**
     * Creates a new simpleLed component with a custom BCM pin.
     *
     * @param pi4j    Pi4J context
     * @param address Custom BCM pin address
     */
    public RGBLed(Context pi4j, PIN address) {
        this.digitalOutput = pi4j.create(buildDigitalOutputConfig(pi4j, address));
    }

    /**
     * Configure Digital Output
     *
     * @param pi4j    PI4J Context
     * @param address GPIO Address of the relay
     * @return Return Digital Output configuration
     */
    protected DigitalOutputConfig buildDigitalOutputConfig(Context pi4j, PIN address) {
        return DigitalOutput.newConfigBuilder(pi4j)
                .id("BCM" + address)
                .name("RGBLed")
                .address(address.getPin())
                .build();
    }
}
