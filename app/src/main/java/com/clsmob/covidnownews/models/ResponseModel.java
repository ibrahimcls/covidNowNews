package com.clsmob.covidnownews.models;

public class ResponseModel<T> {
    public Boolean success;
    public T result;

    public ResponseModel() {
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }


    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}
