package com.pi4j.catalog.applications;

import com.pi4j.Pi4J;
import com.pi4j.catalog.components.RfidReader;
import com.pi4j.context.Context;

public class RfidReaderApp {

    public static void main(String[] args) {
        final Context pi4j = Pi4J.newAutoContext();

        System.out.println("Starting Pi4J RFID Reader demo");

        final RfidReader reader = new RfidReader(pi4j);

        /**
         * Differences between onCardDetected, waitForNewCard, and waitForAnyCard:
         *
         * - onCardDetected(EventHandler<RfidCard> handler):
         *   Registers a handler that is called every time a new card is detected.
         *   The handler remains active until you unregister it (by passing null).
         *   This is asynchronous and persistent.
         *
         * - waitForNewCard(EventHandler<RfidCard> handler):
         *   Blocks the current thread until a new card is detected, then calls the handler once and returns.
         *   Only new cards (not previously detected) are considered.
         *   This is synchronous and one-shot.
         *
         * - waitForAnyCard(EventHandler<RfidCard> handler):
         *   Blocks the current thread until any card (new or previously detected) is detected,
         *   then calls the handler once and returns.
         *   This is synchronous and one-shot.
         */
        reader.onCardDetected(card -> {
            System.out.println("Detected card with serial number: " + card.getSerial() + " and capacity: " + card.getCapacity() + " bytes.");
        });
    }
}