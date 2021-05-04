package com.naqelexpress.naqelpointer.Models.Enum;

public enum Enum {


    NORMAL_TYPE(0), //0
    ERROR_TYPE(1), //1
    SUCCESS_TYPE(2), //2
    WARNING_TYPE(3), //3
    CUSTOM_IMAGE_TYPE(4), //4
    PROGRESS_TYPE(5);

    private int value = 0;

    Enum(int i) {
        value = i;
    }

    public int getValue() {
        return value;
    }
}