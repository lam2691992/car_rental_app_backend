package com.noob.example.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseResponse {
    String errorCode;
    String errorMess;
    Object data;
}
