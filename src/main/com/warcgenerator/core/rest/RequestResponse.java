package com.warcgenerator.core.rest;

public interface RequestResponse<T> {
    void onRequestSuccess(T response);
    void onRequestFail(String error);
}
