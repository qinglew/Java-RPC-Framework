package cn.edu.scut.qinglew.rpc.entity;

import cn.edu.scut.qinglew.rpc.enumeration.ResponseCode;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 服务提供者完成或出错后向服务消费者返回的结果对象
 *
 * @author Qing Lew
 */
@Data
@NoArgsConstructor
public class RpcResponse implements Serializable {

    /**
     * 响应对应的请求号
     */
    private String requestId;

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
    private Object data;

    public static RpcResponse success(String requestId, Object data) {
        RpcResponse response = new RpcResponse();
        response.setRequestId(requestId);
        response.setStatusCode(ResponseCode.SUCCESS.getCode());
        response.setMessage(ResponseCode.SUCCESS.getMessage());
        response.setData(data);
        return response;
    }

    public static RpcResponse fail(String requestId, ResponseCode code) {
        RpcResponse response = new RpcResponse();
        response.setRequestId(requestId);
        response.setStatusCode(code.getCode());
        response.setMessage(code.getMessage());
        return response;
    }
}
