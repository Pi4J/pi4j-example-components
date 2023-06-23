package com.pi4j.catalog.components.base;

import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalInputConfig;

public abstract class DigitalSensorComponent extends Component implements DigitalSensor {
    /**
     * Pi4J digital input instance used by this component (that's the low-level Pi4J Class)
     */
    private final DigitalInput digitalInput;

    protected DigitalSensorComponent(Context pi4j, DigitalInputConfig config){
        digitalInput = pi4j.create(config);
    }

    public int pinNumber(){
        return digitalInput.address().intValue();
    }


    @Override
    public DigitalInput getDigitalInput() {
        return digitalInput;
    }
}
