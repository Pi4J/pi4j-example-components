package com.pi4j.example.components;

import com.pi4j.context.Context;
import com.pi4j.example.components.helpers.PIN;
import com.pi4j.io.pwm.Pwm;
import com.pi4j.io.pwm.PwmConfig;
import com.pi4j.io.pwm.PwmType;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Buzzer extends Component{

    /**
     * PI4J PWM used by this buzzer
     */
    protected final Pwm pwm;
    /**
     * the Thread, under which the melodies are played.
     */
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private Runnable playWorker = () -> {};

    /**
     * Creates a new buzzer component with a custom BCM pin.
     *
     * @param pi4j    Pi4J context
     * @param address Custom BCM pin address
     */
    public Buzzer(Context pi4j, PIN address) {
        this.pwm = pi4j.create(buildPwmConfig(pi4j, address));
        logDebug("Created new Buzzer Component");
    }

    /**
     * Builds a new PWM configuration for the buzzer
     *
     * @param pi4j    Pi4J context
     * @param address BCM pin address
     * @return PWM    configuration
     */
    protected static PwmConfig buildPwmConfig(Context pi4j, PIN address) {
        return Pwm.newConfigBuilder(pi4j)
                .id("BCM" + address)
                .name("Buzzer")
                .address(address.getPin())
                .pwmType(PwmType.HARDWARE)
                .initial(0)
                .shutdown(0)
                .build();
    }

    /**
     * Plays a tone with the given frequency in Hz indefinitely.
     * This method is non-blocking and returns immediately.
     * A frequency of zero causes the buzzer to play silence.
     *
     * @param frequency Frequency in Hz
     */
    public void playTone(int frequency) {
        playTone(frequency, 0);
    }

    /**
     * Plays a tone with the given frequency in Hz for a specific duration.
     * This method is blocking and will sleep until the specified duration has passed.
     * A frequency of zero causes the buzzer to play silence.
     * A duration of zero to play the tone indefinitely and return immediately.
     *
     * @param frequency Frequency in Hz
     * @param duration  Duration in milliseconds
     */
    public void playTone(int frequency, int duration) {
        if (frequency > 0) {
            // Activate the PWM with a duty cycle of 50% and the given frequency in Hz.
            // This causes the buzzer to be on for half of the time during each cycle, resulting in the desired frequency.
            pwm.on(50, frequency);

            // If the duration is larger than zero, the tone should be automatically stopped after the given duration.
            if (duration > 0) {
                delay(duration);
                playSilence();
            }
        } else {
            playSilence();
        }
    }

    /**
     * plays a defined Melody out of Sounds, which are compromised from
     * a frequency and an amount of beats
     *
     * @param tempo  how fast should it be played
     * @param sounds the defined sounds, can be an Array
     */
    public void playMelody(int tempo, Sound... sounds){
        //named thread to set it as Daemon and start it
        playWorker = () -> {
            //to begin the melody, we first wait for 8 beats to pass
            playSilence(tempo * 8);
            for (Sound s : sounds) {
                playNote(s.note, tempo, s.beats);
            }
            //when the melody is finished, we turn it off
            playSilence();
        };
        executor.submit(playWorker);
    }

    /**
     * plays a defined Melody out of Sounds, which are compromised from
     * a frequency and an amount of beats for several times
     *
     * @param tempo       how fast should it be played
     * @param repetitions how much it should be played
     * @param sounds      the defined sounds, can be an Array
     */
    public void playMelody(int tempo, int repetitions, Sound... sounds){
        //named thread to set it as Daemon and start it
        playWorker = () -> {
            for (int i = 0; i < repetitions; i++) {
                //to begin the melody, we first wait for 8 beats to pass
                //we can't just call the other playMelody, as it would always
                //wait for the 8 beats to finish between each repetition
                playSilence(tempo * 8);
                for (Sound s : sounds) {
                    playNote(s.note, tempo, s.beats);
                }
                //when the melody is finished, we turn it off
                playSilence();
            }
        };
        executor.submit(playWorker);
    }

    /**
     * Plays a simple note
     *
     * @param note  Which frequency on the buzzer
     * @param tempo how fast shall it be played?
     * @param beats how many beats long
     */
    public void playNote(Note note, int tempo, int beats) {
        playTone(note.frequency, tempo * beats);
        //playSilence is used, because if the Note is not long enough,
        //the component wouldn't have enough time to change frequency
        playSilence(20);
    }

    /**
     * Silences the buzzer and returns immediately.
     */
    public void playSilence() {
        pwm.off();
    }

    /**
     * Silences the buzzer and waits for the given duration.
     * This method is blocking and will sleep until the specified duration has passed.
     *
     * @param duration Duration in milliseconds
     */
    public void playSilence(int duration) {
        playSilence();
        delay(duration);
    }

    /**
     * Returns the created PWM instance for the buzzer
     *
     * @return PWM instance
     */
    protected Pwm getPwm() {
        return this.pwm;
    }

    /**
     * A note you can play. https://en.wikipedia.org/wiki/Musical_note
     */
    public enum Note {
        B0(31),
        C1(33),
        CS1(35),
        D1(37),
        DS1(39),
        E1(41),
        F1(44),
        FS1(46),
        G1(49),
        GS1(52),
        A1(55),
        AS1(58),
        B1(62),
        C2(65),
        CS2(69),
        D2(73),
        DS2(78),
        E2(82),
        F2(87),
        FS2(93),
        G2(98),
        GS2(104),
        A2(110),
        AS2(117),
        B2(123),
        C3(131),
        CS3(139),
        D3(147),
        DS3(156),
        E3(165),
        F3(175),
        FS3(185),
        G3(196),
        GS3(208),
        A3(220),
        AS3(233),
        B3(247),
        C4(262),
        CS4(277),
        D4(294),
        DS4(311),
        E4(330),
        F4(349),
        FS4(370),
        G4(392),
        GS4(415),
        A4(440),
        AS4(466),
        B4(494),
        C5(523),
        CS5(554),
        D5(587),
        DS5(622),
        E5(659),
        F5(698),
        FS5(740),
        G5(784),
        GS5(831),
        A5(880),
        AS5(932),
        B5(988),
        C6(1047),
        CS6(1109),
        D6(1175),
        DS6(1245),
        E6(1319),
        F6(1397),
        FS6(1480),
        G6(1568),
        GS6(1661),
        A6(1760),
        AS6(1865),
        B6(1976),
        C7(2093),
        CS7(2217),
        D7(2349),
        DS7(2489),
        E7(2637),
        F7(2794),
        FS7(2960),
        G7(3136),
        GS7(3322),
        A7(3520),
        AS7(3729),
        B7(3951),
        C8(4186),
        CS8(4435),
        D8(4699),
        DS8(4978),
        PAUSE(0);

        private int frequency = 0;

        Note(int frequency) {
            this.frequency = frequency;
        }

        public int getFrequency() {
            return frequency;
        }
    }

    /**
     * A Sound is defined through a frequency, that is played for
     * a defined range of beats.
     */
    public record Sound(Note note, int beats) {}
}
