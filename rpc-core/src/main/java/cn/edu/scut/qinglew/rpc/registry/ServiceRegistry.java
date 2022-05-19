package cn.edu.scut.qinglew.rpc.registry;

import java.net.InetSocketAddress;

/**
 * 将原来的ServiceRegistry拆分为ServiceDiscovery和ServiceRegistry,
 * ServiceRegistry针对服务器.
 */
public interface ServiceRegistry {
    /**
     * 将一个服务注册进注册表
     * @param serviceName 服务名称
     * @param inetSocketAddress 提供服务的地址
     */
    void register(String serviceName, InetSocketAddress inetSocketAddress);
}
