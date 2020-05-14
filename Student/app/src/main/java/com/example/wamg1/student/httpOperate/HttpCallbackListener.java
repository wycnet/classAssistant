package com.example.wamg1.student.httpOperate;

public interface HttpCallbackListener {
    void onFinish(String response);

    void onError(Exception e);
}
