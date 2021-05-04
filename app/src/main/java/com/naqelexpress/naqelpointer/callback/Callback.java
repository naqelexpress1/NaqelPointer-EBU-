package com.naqelexpress.naqelpointer.callback;

public interface Callback<T> {
    public abstract void returnResult(T t);

    public abstract void returnError(String error);
}
