package com.usyd.backend.model.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BaseResponse {
    private Result result;


    @Getter
    @Setter
    @NoArgsConstructor
    public static class Result {
        private String message;
        private String responseCode;

        public Result(String message, String responseCode) {
            this.message = message;
            this.responseCode = responseCode;
        }
    }

    public BaseResponse(String message, String responseCode) {
        this.result = new Result(message, responseCode);
    }
}
