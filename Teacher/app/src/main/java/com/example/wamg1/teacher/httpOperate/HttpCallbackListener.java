package com.example.wamg1.teacher.httpOperate;

public interface HttpCallbackListener {
    void onFinish(String response);

    void onError(Exception e);
}
