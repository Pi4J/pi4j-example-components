package com.pi4j.example.components;

import com.pi4j.context.Context;
import com.pi4j.example.components.helpers.PIN;

public class JoystickAnalog extends Component{

    private final ADS1115 ads1115;

    private final Potentiometer x;

    private final Potentiometer y;

    private final SimpleButton push;


    public JoystickAnalog(Context pi4j, ADS1115 ads1115){
        this.ads1115 = ads1115;
        this.x = new Potentiometer(ads1115, ADS1115.MUX.AIN0_GND, 3.3);
        this.y = new Potentiometer(ads1115, ADS1115.MUX.AIN1_GND, 3.3);
        this.push = new SimpleButton(pi4j, PIN.D26, false);
    }

    public void setXRunnable(Runnable methode){
        x.setRunnable(methode);
    }

    public void setYRunnable(Runnable methode){
        y.setRunnable(methode);
    }

    public void setPushOnUp(Runnable methode){
        push.onDown(methode);
    }

    public void setPushOnDown(Runnable methode){
        push.onUp(methode);
    }

    public void setPushWhilePressed(Runnable methode, long whilePressedDelay){
        push.whilePressed(methode, whilePressedDelay);
    }

    public double getXValue(){
        return x.continiousReadingGetNormalizedValue();
    }

    public double getYValue(){
        return y.continiousReadingGetNormalizedValue();
    }

    public void start(double threshold, int readFrequency){
        x.startSlowContiniousReading(threshold, readFrequency);
        y.startSlowContiniousReading(threshold, readFrequency);
    }

    public void stop(){
        x.stopSlowContiniousReading();
        y.stopSlowContiniousReading();
    }

    public void deregisterAll(){
        x.deregisterAll();
        y.deregisterAll();
        push.deRegisterAll();
    }


}
