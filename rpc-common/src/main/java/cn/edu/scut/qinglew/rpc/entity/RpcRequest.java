package cn.edu.scut.qinglew.rpc.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 服务消费者向服务提供者发送的请求对象，包括以下信息：
 * 1. 接口名
 * 2. 方法名
 * 3. 方法参数类型数组
 * 4. 方法参数数组
 *
 * @author Qing Lew
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RpcRequest implements Serializable {

    /**
     * 请求号
     */
    private String requestId;

    /**
     * 接口名
     */
    private String interfaceName;

    /**
     * 方法名
     */
    private String methodName;

    /**
     * 方法参数
     */
    private Object[] parameters;

    /**
     * 方法参数类型
     */
    private Class<?>[] paramTypes;
}
