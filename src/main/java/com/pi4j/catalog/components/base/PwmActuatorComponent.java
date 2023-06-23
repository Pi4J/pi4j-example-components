package com.pi4j.catalog.components.base;

import java.util.List;

import com.pi4j.context.Context;
import com.pi4j.io.pwm.Pwm;
import com.pi4j.io.pwm.PwmConfig;

public class PwmActuatorComponent extends Component implements PwmActuator {
    protected static final List<PIN> AVAILABLE_PWM_PINS = List.of(PIN.PWM12, PIN.PWM13, PIN.PWM18, PIN.PWM19);
    /**
     * PI4J PWM used by this buzzer
     */
    private final Pwm pwm;

    protected PwmActuatorComponent(Context pi4J, PwmConfig config) {
        if(AVAILABLE_PWM_PINS.stream().noneMatch(p -> p.getPin() == config.address().intValue())){
            throw new IllegalArgumentException("Pin " + config.address().intValue() + "is not a PWM Pin");
        }
        pwm = pi4J.create(config);
    }

    @Override
    public Pwm getPwm() {
        return pwm;
    }
}
