package cn.edu.scut.qinglew.rpc.registry;

import java.net.InetSocketAddress;

/**
 * 将原来的ServiceRegistry拆分为ServiceDiscovery和ServiceRegistry,
 * ServiceDiscovery针对客户端.
 */
public interface ServiceDiscovery {
    /**
     * 根据服务名查找服务实体
     * @param serviceName 服务名
     * @return 服务实体（服务器）
     */
    InetSocketAddress lookupService(String serviceName);
}
