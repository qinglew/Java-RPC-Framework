package cn.edu.scut.qinglew.rpc.transport;

import cn.edu.scut.qinglew.rpc.serializer.CommonSerializer;

/**
 * 服务器类通用接口
 */
public interface RpcServer {
    /**
     * 启动RPC服务器端
     */
    void start();

    void setSerializer(CommonSerializer serializer);

    /**
     * 注册服务
     * @param service 服务对象
     * @param serviceClass 服务类型
     * @param <T>
     */
    <T> void publishService(Object service, Class<T> serviceClass);
}
