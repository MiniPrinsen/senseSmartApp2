package com.example.gustaf.touchpoint.Enumerations;

/**
 * Created by Gustaf on 16-07-15.
 */
public enum LocationState {
    ONLINE(0), OFFLINE(1), LOCATIONCHANGED(2);

    public int val;


    LocationState(int val) { this.val = val; }

}
