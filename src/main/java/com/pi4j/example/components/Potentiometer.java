package com.pi4j.example.components;

import com.pi4j.context.Context;

import static java.lang.Thread.sleep;

public class Potentiometer extends Component{

    private final ADS1115 ads1115;

    private final SimpleButton btn;

    /**
     * Config register default configuration
     * <p>
     * OS -> 1 : Start a single conversion
     * MUX -> 000 : AINP = AIN0 and AINN = AIN1
     * PGA -> 000 : FSR = ±6.144 V(
     * MODE -> 0 : Continious-shot mode
     * DR -> 100 : 128 SPS
     * COMP_MODE -> 0 : Traditional comparator
     * COMP_POL -> 0 : Active low
     * COMP_LAT -> 0 : Nonlatching comparator . The ALERT/RDY pin does not latch when asserted
     * COMP_QUE -> 11 : Disable comparator and set ALERT/RDY pin to high-impedance
     */
    private static final int CONFIG_REGISTER_CONTINIOUS_READING =
            ADS1115.OS.WRITE_START.getOs()
                    | ADS1115.MUX.AIN0_AIN1.getMux()
                    | ADS1115.PGA.FSR_6_144.getPga()
                    | ADS1115.MODE.CONTINUOUS.getMode()
                    | ADS1115.DR.SPS_128.getSpS()
                    | ADS1115.COMP_MODE.TRAD_COMP.getCompMode()
                    | ADS1115.COMP_POL.ACTIVE_LOW.getCompPol()
                    | ADS1115.COMP_LAT.NON_LATCH.getLatching()
                    | ADS1115.COMP_QUE.DISABLE_COMP.getCompQue();

    /**
     * Config register default configuration
     * <p>
     * OS -> 1 : Start a single conversion
     * MUX -> 000 : AINP = AIN0 and AINN = AIN1
     * PGA -> 000 : FSR = ±6.144 V(
     * MODE -> 0 : Single-shot mode or power down
     * DR -> 100 : 128 SPS
     * COMP_MODE -> 0 : Traditional comparator
     * COMP_POL -> 0 : Active low
     * COMP_LAT -> 0 : Nonlatching comparator . The ALERT/RDY pin does not latch when asserted
     * COMP_QUE -> 11 : Disable comparator and set ALERT/RDY pin to high-impedance
     */
    private static final int CONFIG_REGISTER_SINGLE_SHOT =
            ADS1115.OS.WRITE_START.getOs()
                    | ADS1115.MUX.AIN0_AIN1.getMux()
                    | ADS1115.PGA.FSR_6_144.getPga()
                    | ADS1115.MODE.SINGLE.getMode()
                    | ADS1115.DR.SPS_128.getSpS()
                    | ADS1115.COMP_MODE.TRAD_COMP.getCompMode()
                    | ADS1115.COMP_POL.ACTIVE_LOW.getCompPol()
                    | ADS1115.COMP_LAT.NON_LATCH.getLatching()
                    | ADS1115.COMP_QUE.DISABLE_COMP.getCompQue();

    public Potentiometer(Context pi4j){
        ads1115 = new ADS1115(pi4j);
        btn = new SimpleButton(pi4j,26,false);
    }



    public void writeNewConfig() throws InterruptedException {
        logger.info("Old configuration is: " + ads1115.readConfigRegister());
        logger.info("new configuration to wirte is: " + CONFIG_REGISTER_CONTINIOUS_READING);
        ads1115.writeConfigRegister(CONFIG_REGISTER_CONTINIOUS_READING);
        sleep(200);
        logger.info("New configuration is: " + ads1115.readConfigRegister());
    }

}
