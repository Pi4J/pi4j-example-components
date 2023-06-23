package com.pi4j.catalog.components;

import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalOutput;

import com.pi4j.catalog.components.base.DigitalActuatorComponent;
import com.pi4j.catalog.components.base.PIN;

public class SimpleLed extends DigitalActuatorComponent {

    /**
     * Creates a new SimpleLed component with a custom BCM pin.
     *
     * @param pi4j    Pi4J context
     * @param address Custom BCM pin address
     */
    public SimpleLed(Context pi4j, PIN address) {
        super(pi4j,
              DigitalOutput.newConfigBuilder(pi4j)
                      .id("BCM" + address)
                      .name("LED #" + address)
                      .address(address.getPin())
                      .build());
        logDebug("Created new SimpleLed component");
    }

    /**
     * Sets the LED to on.
     */
    public void on() {
        logDebug("LED turned ON");
        getDigitalOutput().on();
    }

    /**
     * Sets the LED to off
     */
    public void off() {
        logDebug("LED turned OFF");
        getDigitalOutput().off();
    }

    /**
     * Toggle the LED state depending on its current state.
     *
     * @return Return true or false according to the new state of the relay.
     */
    public boolean toggle() {
        getDigitalOutput().toggle();
        logDebug("LED toggled, now it is %s", getDigitalOutput().isOff() ? "OFF" : "ON");

        return getDigitalOutput().isOff();
    }

    @Override
    public void reset() {
        off();
    }
}
