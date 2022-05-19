package cn.edu.scut.qinglew.rpc.transport;

import cn.edu.scut.qinglew.rpc.serializer.CommonSerializer;
import cn.edu.scut.qinglew.rpc.serializer.HessianSerializer;

/**
 * 服务器类通用接口
 */
public interface RpcServer {

    int DEFAULT_SERIALIZER = CommonSerializer.HESSIAN_SERIALIZER;

    /**
     * 启动RPC服务器端
     */
    void start();

    /**
     * 注册服务器及服务
     * @param service 服务对象
     * @param serviceName 服务名
     * @param <T> 服务对象的泛型类型
     */
    <T> void publishService(T service, String serviceName);
}
