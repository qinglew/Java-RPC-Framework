package cn.edu.scut.qinglew.rpc.entity;

import cn.edu.scut.qinglew.rpc.enumeration.ResponseCode;
import lombok.Data;

import java.io.Serializable;

/**
 * 服务提供者完成或出错后向服务消费者返回的结果对象
 *
 * @author Qing Lew
 */
@Data
public class RpcResponse<T> implements Serializable {

    public RpcResponse() {

    }

    /**
     * 响应状态码
     */
    private Integer statusCode;
    /**
     * 响应状态补充信息
     */
    private String message;
    /**
     * 响应数据
     */
    private T data;

    public static <T> RpcResponse<T> success(T data) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setStatusCode(ResponseCode.SUCCESS.getCode());
        response.setMessage(ResponseCode.SUCCESS.getMessage());
        response.setData(data);
        return response;
    }

    public static <T> RpcResponse<T> fail(ResponseCode code) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setStatusCode(code.getCode());
        response.setMessage(code.getMessage());
        return response;
    }
}
