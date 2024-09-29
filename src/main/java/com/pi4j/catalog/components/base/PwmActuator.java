package com.pi4j.catalog.components.base;

import java.util.List;

import com.pi4j.context.Context;
import com.pi4j.io.pwm.Pwm;
import com.pi4j.io.pwm.PwmConfig;
import com.pi4j.plugin.mock.provider.pwm.MockPwm;

public class PwmActuator extends Component {
     protected final Pwm pwm;

    protected PwmActuator(Context pi4J, PwmConfig config) {
        pwm = pi4J.create(config);
    }

    @Override
    public void reset() {
        pwm.off();
    }

    // --------------- for testing --------------------

    public MockPwm mock() {
        return asMock(MockPwm.class, pwm);
    }
}
