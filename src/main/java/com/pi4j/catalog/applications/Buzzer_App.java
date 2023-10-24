package com.pi4j.catalog.applications;

import java.time.Duration;
import java.util.List;

import com.pi4j.context.Context;

import com.pi4j.catalog.Application;
import com.pi4j.catalog.components.base.PIN;
import com.pi4j.catalog.components.Buzzer;

import static com.pi4j.catalog.components.Buzzer.Note.*;

/**
 * The buzzer component is an example of an actuator making use of a PWM-channel to play different tones or even simple melodies.
 * <p>
 * see <a href="https://pi4j.com/examples/components/buzzer/">Description on Pi4J website</a>
 */
public class Buzzer_App implements Application {

    //this is how you compose a simple melody
    //Piano baseline of Seven Nation Army by the white Stripes
    private final List<Buzzer.Sound> melody = List.of(
            new Buzzer.Sound(E7   , 11),
            new Buzzer.Sound(PAUSE, 1),
            new Buzzer.Sound(E7   , 2),
            new Buzzer.Sound(PAUSE, 2),
            new Buzzer.Sound(G6   , 2),
            new Buzzer.Sound(PAUSE, 3),
            new Buzzer.Sound(E7   , 2),
            new Buzzer.Sound(PAUSE, 4),
            new Buzzer.Sound(D6   , 2),
            new Buzzer.Sound(PAUSE, 3),
            new Buzzer.Sound(C7   , 16),
            new Buzzer.Sound(B5   , 8),
            new Buzzer.Sound(PAUSE, 8)
    );

    private final List<Buzzer.Sound> imperialMarch = List.of(
            new Buzzer.Sound(G4,  8),
            new Buzzer.Sound(G4,  8),
            new Buzzer.Sound(G4,  8),
            new Buzzer.Sound(DS4, 6),
            new Buzzer.Sound(AS4, 2),
            new Buzzer.Sound(G4,  8),
            new Buzzer.Sound(DS4, 6),
            new Buzzer.Sound(AS4, 2),
            new Buzzer.Sound(G4, 16));

    @Override
    public void execute(Context pi4j) {
        System.out.println("Buzzer demo started");

        //initialising the buzzer
        Buzzer buzzer = new Buzzer(pi4j, PIN.PWM13);

        //playing a simple tone
        System.out.println("playing note b6 for 1 sec");
        buzzer.playTone(1976, Duration.ofSeconds(1));

        //relax for 1 second
        buzzer.silence(Duration.ofSeconds(1));

        System.out.println("start playing melody");
        buzzer.playMelody(60, melody);

        delay(Duration.ofSeconds(3));

        //first melody is stopped and second is played
        buzzer.playMelody(103, imperialMarch);

        buzzer.awaitEndOfMelody();
        System.out.println("Second melody has finished");

        buzzer.reset();

        System.out.println("buzzer demo finished");
    }
}