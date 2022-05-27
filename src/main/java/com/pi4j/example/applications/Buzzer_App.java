package com.pi4j.example.applications;

import com.pi4j.context.Context;
import com.pi4j.example.Application;
import com.pi4j.example.components.Buzzer;
import com.pi4j.example.components.helpers.PIN;

public class Buzzer_App implements Application {
    @Override
    public void execute(Context pi4j) {
        //initialising the buzzer
        Buzzer buzzer = new Buzzer(pi4j, PIN.PWM18);

        //playing a simple note
        buzzer.playTone(Buzzer.Note.B6.getFrequency(), 1000);

        //shutting it down for 1 second
        buzzer.playSilence(1000);

        //Piano baseline of Seven Nation Army by the white Stripes
        Buzzer.Sound[] melody = new Buzzer.Sound[]{
                new Buzzer.Sound(Buzzer.Note.E1, 11),
                new Buzzer.Sound(Buzzer.Note.PAUSE, 1),
                new Buzzer.Sound(Buzzer.Note.E1, 2),
                new Buzzer.Sound(Buzzer.Note.PAUSE, 2),
                new Buzzer.Sound(Buzzer.Note.G1, 2),
                new Buzzer.Sound(Buzzer.Note.PAUSE, 3),
                new Buzzer.Sound(Buzzer.Note.E1, 2),
                new Buzzer.Sound(Buzzer.Note.PAUSE, 4),
                new Buzzer.Sound(Buzzer.Note.D1, 2),
                new Buzzer.Sound(Buzzer.Note.PAUSE, 3),
                new Buzzer.Sound(Buzzer.Note.C1, 16),
                new Buzzer.Sound(Buzzer.Note.B0, 8),
                new Buzzer.Sound(Buzzer.Note.PAUSE, 8),
        };

        //playing the melody once, then shutting down for a second
        buzzer.playMelody(1, melody);
        buzzer.playSilence(1000);

        //playing the melody 5 times repeatedly
        buzzer.playMelody(1, 5, melody);
    }
}