package com.pi4j.catalog.components.base;

import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalOutputConfig;

public abstract class DigitalActuatorComponent extends Component implements DigitalActuator {
    /**
     * Pi4J digital output instance used by this component
     */
    private final DigitalOutput digitalOutput;

    protected DigitalActuatorComponent(Context pi4j, DigitalOutputConfig config) {
        digitalOutput = pi4j.create(config);
    }

    public int pinNumber(){
        return digitalOutput.address().intValue();
    }

    @Override
    public DigitalOutput getDigitalOutput() {
        return digitalOutput;
    }
}
