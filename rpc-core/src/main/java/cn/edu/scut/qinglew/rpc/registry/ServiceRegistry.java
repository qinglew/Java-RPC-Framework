package cn.edu.scut.qinglew.rpc.registry;

/**
 * 服务注册表
 */
public interface ServiceRegistry {
    /**
     * 注册服务
     * @param service
     * @param <T>
     */
    <T> void registry(T service);

    /**
     * 获取服务
     * @param serviceName
     * @return
     */
    Object getService(String serviceName);
}
