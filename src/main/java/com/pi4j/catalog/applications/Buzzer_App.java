package com.pi4j.catalog.applications;

import java.time.Duration;

import com.pi4j.context.Context;

import com.pi4j.catalog.Application;
import com.pi4j.catalog.components.Buzzer;
import com.pi4j.catalog.components.base.PIN;

/**
 * This example shows how to use the buzzer component by playing different tunes on it
 * <P>
 * see <a href="https://pi4j.com/examples/components/buzzer/">Description on Pi4J website</a>
 */
public class Buzzer_App implements Application {
    @Override
    public void execute(Context pi4j) {
        //initialising the buzzer
        Buzzer buzzer = new Buzzer(pi4j, PIN.PWM18);

        //playing a simple tone
        System.out.println("playing note b6 for 1 sec");
        buzzer.playTone(1976, 1000);

        //shutting it down for 1 second
        buzzer.pause(1000);

        //Piano baseline of Seven Nation Army by the white Stripes
        Buzzer.Sound[] melody = new Buzzer.Sound[]{
                new Buzzer.Sound(Buzzer.Note.E7, 11),
                new Buzzer.Sound(Buzzer.Note.PAUSE, 1),
                new Buzzer.Sound(Buzzer.Note.E7, 2),
                new Buzzer.Sound(Buzzer.Note.PAUSE, 2),
                new Buzzer.Sound(Buzzer.Note.G6, 2),
                new Buzzer.Sound(Buzzer.Note.PAUSE, 3),
                new Buzzer.Sound(Buzzer.Note.E7, 2),
                new Buzzer.Sound(Buzzer.Note.PAUSE, 4),
                new Buzzer.Sound(Buzzer.Note.D6, 2),
                new Buzzer.Sound(Buzzer.Note.PAUSE, 3),
                new Buzzer.Sound(Buzzer.Note.C7, 16),
                new Buzzer.Sound(Buzzer.Note.B5, 8),
                new Buzzer.Sound(Buzzer.Note.PAUSE, 8),
        };

        //playing the melody once, then shutting down for a second
        System.out.println("playing melody once");
        buzzer.playMelody(60, melody);
        delay(Duration.ofSeconds(1));

        //playing the melody 5 times repeatedly
        System.out.println("playing melody 5 times");
        System.out.println("playing in a different thread, so the app is moving on");
        buzzer.playMelody(60, 5, melody);
        System.out.println("waiting for melody to finish");
        delay(Duration.ofSeconds(3));
    }
}