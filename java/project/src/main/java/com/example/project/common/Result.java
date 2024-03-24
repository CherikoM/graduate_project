package com.example.project.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 返回结果
 * @param <D>
 */
@Data
public class Result<D> implements Serializable {
    //执行成功或失败
    @JsonProperty("isSuccess")
    private boolean success = false;

    //消息短码
    private String code;

    //消息具体信息，出错时可以比较详细地描述错误
    private String message;

    //返回的具体数据
    private D data;

    public static <D> Result<D> success(D data) {
        Result<D> result = new Result<>();
        result.data = data;
        result.success = true;
        return result;
    }

    public static <D> Result<D> error(String message) {
        Result<D> result = new Result<>();
        result.message = message;
        return result;
    }

}
