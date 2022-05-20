package cn.edu.scut.qinglew.rpc.provider;

import cn.edu.scut.qinglew.rpc.enumeration.RpcError;
import cn.edu.scut.qinglew.rpc.exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 服务注册表默认实现
 */
public class ServiceProviderImpl implements ServiceProvider {

    private static final Logger logger = LoggerFactory.getLogger(ServiceProviderImpl.class);

    /**
     * 存储接口名-服务映射
     */
    private static final Map<String, Object> serviceMap = new HashMap<>();

    /**
     * 存储服务名
     */
    private static final Set<String> registeredService = new HashSet<>();

    @Override
    public <T> void addServiceProvider(T service, String serviceName) {
        if (registeredService.contains(serviceName))
            return;
        registeredService.add(serviceName);
        serviceMap.put(serviceName, service);
        logger.info("向接口 {} 注册服务 {}", service.getClass().getInterfaces(), serviceName);
    }

    @Override
    public Object getServiceProvider(String serviceName) {
        Object service = serviceMap.get(serviceName);
        if (service == null) {
            throw new RpcException(RpcError.SERVICE_NOT_FOUND);
        }
        return service;
    }
}
