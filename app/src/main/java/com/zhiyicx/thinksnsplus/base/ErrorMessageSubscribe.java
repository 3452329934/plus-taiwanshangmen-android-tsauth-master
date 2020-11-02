package com.zhiyicx.thinksnsplus.base;

import com.google.gson.Gson;

public abstract class ErrorMessageSubscribe<E,D> extends BaseSubscribeForV2<D> {

    @Override
    protected boolean handleErrorBodyString(int code, String body) {
        pareseBodyResult(pareseBodyString(body));
        return interceptOnFailure(code);
    }

    protected boolean interceptOnFailure(int code) {
        return true;
    }

    protected abstract Class<E> getErrorMessageType();

    protected E pareseBodyString(String body) {
        Gson gson = new Gson();
        return gson.fromJson(body, getErrorMessageType());
    }

    protected abstract void pareseBodyResult(E error);
}