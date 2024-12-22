package com.zerobee.pillscheduler.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum Status {
    TAKEN,
    NOT_TAKEN
}
