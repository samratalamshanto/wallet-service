package com.samratalam.bankservice.dto;

import com.samratalam.ewallet_system.enums.CommonStatus;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class BaseResponse {
    public Object data;
    @Builder.Default
    public Boolean success = true;
    @Builder.Default
    public String message = CommonStatus.SUCCESS.name();

    public static BaseResponse successResponse(Object data) {
        return BaseResponse.builder().data(data).build();
    }

    public static BaseResponse failedResponse() {
        return BaseResponse.builder().success(false).message(CommonStatus.FAILED.name()).build();
    }

    public static BaseResponse failedResponse(String message) {
        return BaseResponse.builder().success(false).message(message).build();
    }


}
