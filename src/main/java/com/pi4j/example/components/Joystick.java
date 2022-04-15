package com.pi4j.example.components;

import com.pi4j.context.Context;
import com.pi4j.example.components.events.EventHandler;
import com.pi4j.io.gpio.digital.DigitalInput;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of a joystick using 5 GPIO up, left, down, right and push  with Pi4J
 */
public class Joystick extends Component {

    /**
     * Default debounce time in microseconds
     */
    protected static final long DEFAULT_DEBOUNCE = 10000;

    /**
     * Button component for joystick direction up
     */
    private final SimpleButton up;
    /**
     * Button component for joystick direction left
     */
    private final SimpleButton left;
    /**
     * Button component for joystick direction down
     */
    private final SimpleButton down;
    /**
     * Button component for joystick direction right
     */
    private final SimpleButton right;
    /**
     * Button component for joystick push
     */
    private final SimpleButton push;
    /**
     * Specifies if Joystick with push button
     */
    private final boolean pushIsPresent;


    /**
     * Creates a new joystick component with 5 custom GPIO address, a joystick with push button.
     *
     * @param pi4j     Pi4J context
     * @param addressUp  GPIO address of button up
     * @param addressLeft  GPIO address of button left
     * @param addressDown  GPIO address of button down
     * @param addressRight  GPIO address of button right
     * @param addressPush  GPIO address of button push
     */
    public Joystick (Context pi4j, int addressUp, int addressLeft, int addressDown, int addressRight, int addressPush){
        up = new SimpleButton(pi4j, addressUp, false, DEFAULT_DEBOUNCE);
        left = new SimpleButton(pi4j, addressLeft, false, DEFAULT_DEBOUNCE);
        down = new SimpleButton(pi4j, addressDown, false, DEFAULT_DEBOUNCE);
        right = new SimpleButton(pi4j, addressRight, false, DEFAULT_DEBOUNCE);
        push = new SimpleButton(pi4j, addressPush, false, DEFAULT_DEBOUNCE);
        //joystick with push button
        pushIsPresent = true;
    }

    /**
     * Creates a new joystick component with 4 custom GPIO address, so no push button.
     *
     * @param pi4j     Pi4J context
     * @param addressUp  GPIO address of button up
     * @param addressLeft  GPIO address of button left
     * @param addressDown  GPIO address of button down
     * @param addressRight  GPIO address of button right
     */
    public Joystick (Context pi4j, int addressUp, int addressLeft, int addressDown, int addressRight){
        up = new SimpleButton(pi4j, addressUp, false, DEFAULT_DEBOUNCE);
        left = new SimpleButton(pi4j, addressLeft, false, DEFAULT_DEBOUNCE);
        down = new SimpleButton(pi4j, addressDown, false, DEFAULT_DEBOUNCE);
        right = new SimpleButton(pi4j, addressRight, false, DEFAULT_DEBOUNCE);
        push = null;
        //joystick without push button
        pushIsPresent = false;
    }

    /**
     * Returns a list of current state of the touch sensors
     *
     * @return a list of button states
     */
    public List<SimpleButton.ButtonState> getStates(){

        List<SimpleButton.ButtonState> buttonStates = new ArrayList<>();

        buttonStates.add(up.getState());
        buttonStates.add(left.getState());
        buttonStates.add(down.getState());
        buttonStates.add(right.getState());
        //only if joystick has a push button
        if (pushIsPresent){
            buttonStates.add(push.getState());
        }
        return buttonStates;
    }

    /**
     * Returns the current state of the button up
     *
     * @return Current button state
     */
    public SimpleButton.ButtonState getStateUp (){
        return up.getState();
    }
    /**
     * Returns the current state of the button left
     *
     * @return Current button state
     */
    public SimpleButton.ButtonState getStateLeft (){
        return left.getState();
    }
    /**
     * Returns the current state of the button down
     *
     * @return Current button state
     */
    public SimpleButton.ButtonState getStateDown (){
        return down.getState();
    }
    /**
     * Returns the current state of the button right
     *
     * @return Current button state
     */
    public SimpleButton.ButtonState getStateRight (){
        return right.getState();
    }
    /**
     * Returns the current state of the button push
     *
     * @return Current button state
     */
    public SimpleButton.ButtonState getStatePush (){
        return pushIsPresent ? push.getState() : SimpleButton.ButtonState.UNKNOWN;
    }

    /**
     * Checks if button up is currently pressed
     *
     * @return True if button is pressed
     */
    public boolean buttonUpIsDown() {return up.getState() == SimpleButton.ButtonState.DOWN;}

    /**
     * Checks if button up is currently depressed (= NOT pressed)
     *
     * @return True if button is depressed
     */
    public boolean buttonUpIsUp() {return up.getState() == SimpleButton.ButtonState.UP;}

    /**
     * Checks if button left is currently pressed
     *
     * @return True if button is pressed
     */
    public boolean buttonLeftIsDown() {return left.getState() == SimpleButton.ButtonState.DOWN;}

    /**
     * Checks if button left is currently depressed (= NOT pressed)
     *
     * @return True if button is depressed
     */
    public boolean buttonLeftIsUp() {return left.getState() == SimpleButton.ButtonState.UP;}

    /**
     * Checks if button down is currently pressed
     *
     * @return True if button is pressed
     */
    public boolean buttonDownIsDown() {return down.getState() == SimpleButton.ButtonState.DOWN;}

    /**
     * Checks if button down is currently depressed (= NOT pressed)
     *
     * @return True if button is depressed
     */
    public boolean buttonDownIsUp() {return down.getState() == SimpleButton.ButtonState.UP;}

    /**
     * Checks if button right is currently pressed
     *
     * @return True if button is pressed
     */
    public boolean buttonRightIsDown() {return right.getState() == SimpleButton.ButtonState.DOWN;}

    /**
     * Checks if button right is currently depressed (= NOT pressed)
     *
     * @return True if button is depressed
     */
    public boolean buttonRightIsUp() {return right.getState() == SimpleButton.ButtonState.UP;}

    /**
     * Checks if button push is currently pressed
     *
     * @return True if button is pressed, False if button is not pressed or button does not exist
     */
    public boolean buttonPushIsDown() {
        return pushIsPresent && push.getState() == SimpleButton.ButtonState.DOWN;}

    /**
     * Checks if button push is currently depressed (= NOT pressed)
     *
     * @return True if button is depressed, False if button is pressed od button does not exits
     */
    public boolean buttonPushIsUp() {
        return pushIsPresent && push.getState() == SimpleButton.ButtonState.UP;}




    /**
     * Sets or disables the handler for the onDown event.
     * This event gets triggered whenever the button is pressed.
     * Only a single event handler can be registered at once.
     *
     * @param handler Event handler to call or null to disable
     */
    public void buttonUpOnDown(EventHandler handler) {
        up.onDown(handler);
    }

    /**
     * Sets or disables the handler for the onUp event.
     * This event gets triggered whenever the button is no longer pressed.
     * Only a single event handler can be registered at once.
     *
     * @param handler Event handler to call or null to disable
     */
    public void buttonUpOnUp(EventHandler handler) {
        up.onUp(handler);
    }

    /**
     * Sets or disables the handler for the onDown event.
     * This event gets triggered whenever the button is pressed.
     * Only a single event handler can be registered at once.
     *
     * @param handler Event handler to call or null to disable
     */
    public void buttonLeftOnDown(EventHandler handler) {
        left.onDown(handler);
    }

    /**
     * Sets or disables the handler for the onUp event.
     * This event gets triggered whenever the button is no longer pressed.
     * Only a single event handler can be registered at once.
     *
     * @param handler Event handler to call or null to disable
     */
    public void buttonLeftOnUp(EventHandler handler) {
        left.onUp(handler);
    }
    /**
     * Sets or disables the handler for the onDown event.
     * This event gets triggered whenever the button is pressed.
     * Only a single event handler can be registered at once.
     *
     * @param handler Event handler to call or null to disable
     */
    public void buttonDownOnDown(EventHandler handler) {
        down.onDown(handler);
    }

    /**
     * Sets or disables the handler for the onUp event.
     * This event gets triggered whenever the button is no longer pressed.
     * Only a single event handler can be registered at once.
     *
     * @param handler Event handler to call or null to disable
     */
    public void buttonDownOnUp(EventHandler handler) {
        down.onUp(handler);
    }
    /**
     * Sets or disables the handler for the onDown event.
     * This event gets triggered whenever the button is pressed.
     * Only a single event handler can be registered at once.
     *
     * @param handler Event handler to call or null to disable
     */
    public void buttonRightOnDown(EventHandler handler) {
        right.onDown(handler);
    }

    /**
     * Sets or disables the handler for the onUp event.
     * This event gets triggered whenever the button is no longer pressed.
     * Only a single event handler can be registered at once.
     *
     * @param handler Event handler to call or null to disable
     */
    public void buttonRightOnUp(EventHandler handler) {
        right.onUp(handler);
    }

    /**
     * Sets or disables the handler for the onDown event.
     * This event gets triggered whenever the button is pressed.
     * Only a single event handler can be registered at once.
     *
     * @param handler Event handler to call or null to disable
     */
    public void buttonPushOnDown(EventHandler handler) {
        if (pushIsPresent){push.onDown(handler);}
    }

    /**
     * Sets or disables the handler for the onUp event.
     * This event gets triggered whenever the button is no longer pressed.
     * Only a single event handler can be registered at once.
     *
     * @param handler Event handler to call or null to disable
     */
    public void buttonPushOnUp(EventHandler handler) {
        if (pushIsPresent){push.onUp(handler);}
    }

    /**
     * Returns the Pi4J DigitalInput associated with this component.
     *
     * @return Returns the Pi4J DigitalInput associated with this component.
     */
    public DigitalInput getDigitalInputButtonUp(){return up.getDigitalInput();}

    /**
     * Returns the Pi4J DigitalInput associated with this component.
     *
     * @return Returns the Pi4J DigitalInput associated with this component.
     */
    public DigitalInput getDigitalInputButtonLeft(){return left.getDigitalInput();}

    /**
     * Returns the Pi4J DigitalInput associated with this component.
     *
     * @return Returns the Pi4J DigitalInput associated with this component.
     */
    public DigitalInput getDigitalInputButtonDown(){return down.getDigitalInput();}

    /**
     * Returns the Pi4J DigitalInput associated with this component.
     *
     * @return Returns the Pi4J DigitalInput associated with this component.
     */
    public DigitalInput getDigitalInputButtonRight(){return right.getDigitalInput();}

    /**
     * Returns the Pi4J DigitalInput associated with this component.
     *
     * @return Returns the Pi4J DigitalInput associated with this component.
     */
    public DigitalInput getDigitalInputButtonPush(){
        return pushIsPresent? up.getDigitalInput() : null;
    }
}
