package com.pi4j.example.applications;

import com.pi4j.context.Context;
import com.pi4j.example.Application;
import com.pi4j.example.components.Buzzer;
import com.pi4j.example.components.helpers.PIN;

/**
 * This example shows how to use the buzzer component by playing different tunes on it
 */
public class Buzzer_App implements Application {
    @Override
    public void execute(Context pi4j) {
        //initialising the buzzer
        Buzzer buzzer = new Buzzer(pi4j, PIN.PWM18);

        //playing a simple note
        System.out.println("playing note b6 for 1 sec");
        buzzer.playTone(Buzzer.Note.B6.getFrequency(), 1000);

        //shutting it down for 1 second
        buzzer.playSilence(1000);

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
        delay(1000);

        //playing the melody 5 times repeatedly
        System.out.println("playing melody 5 times");
        System.out.println("playing in a different thread, so the app is moving on");
        buzzer.playMelody(60, 5, melody);
        System.out.println("waiting for melody to finish");
        delay(3000);
    }
}