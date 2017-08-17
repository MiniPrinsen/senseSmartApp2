package com.example.gustaf.touchpoint.Fragment;

/**
 * Class for specifying the gravity of the chat bubbles in ChatWindowFragment. Sets the gravity to
 * LEFT if it's the bot that writes, or right if it is the user.
 */
public class MessageContainer {
    public final boolean left;
    public final String message;

    /*
     * @param left if the bot wrote the message
     * @param message chat message
     */
    MessageContainer(boolean left, String message) {
        this.left = left;
        this.message = message;
    }
}
