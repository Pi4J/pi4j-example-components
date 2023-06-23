package com.pi4j.catalog.components.base;

import com.pi4j.io.gpio.digital.DigitalInput;

public interface DigitalSensor {

    /**
     * It's a public method for testing, mainly.
     *
     * In your application it shouldn't be necessary to deal with the low-level Pi4J DigitalInput.BTW: That's the main reason why you implement a Component.
     *
     * Inside of your component, a protected access to the underlying digital input would be appropriate.
     *
     * But for test cases it's necessary to be able to mock the digital input.
     *
     * Therefore, this method should be used inside a DigitalInputComponent or in TestCases.
     *
     * @return the DigitalInput the component is working on
     */
    DigitalInput getDigitalInput();
}
