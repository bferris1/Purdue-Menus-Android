package com.moufee.purduemenus.menus;

import org.joda.time.LocalTime;

import java.io.Serializable;

import androidx.annotation.Keep;

@Keep
public class Hours implements Serializable {

    private static final long serialVersionUID = 1L;
    private LocalTime StartTime;
    private LocalTime EndTime;

    public LocalTime getStartTime() {
        return StartTime;
    }

    public LocalTime getEndTime() {
        return EndTime;
    }
}
