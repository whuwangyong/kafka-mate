package cn.whu.wy.kafkamate.restapi;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDto {

    int code;
    String msg;
    Object data;

    public static ResponseDto success(Object data) {
        return new ResponseDto(0, "ok", data);
    }

    public static ResponseDto fail(String msg, Object data) {
        return new ResponseDto(1, msg, data);
    }
}
