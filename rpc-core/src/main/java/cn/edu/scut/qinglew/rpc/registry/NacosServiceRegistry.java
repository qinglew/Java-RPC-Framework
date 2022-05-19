package cn.edu.scut.qinglew.rpc.registry;

import cn.edu.scut.qinglew.rpc.enumeration.RpcError;
import cn.edu.scut.qinglew.rpc.exception.RpcException;
import cn.edu.scut.qinglew.rpc.util.NacosUtils;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;

public class NacosServiceRegistry implements ServiceRegistry {

    private static final Logger logger = LoggerFactory.getLogger(NacosServiceRegistry.class);

    @Override
    public void register(String serviceName, InetSocketAddress inetSocketAddress) {
        try {
            NacosUtils.registerService(serviceName, inetSocketAddress);
        } catch (NacosException e) {
            logger.error("注册服务时有错误发生:", e);
            throw new RpcException(RpcError.REGISTER_SERVICE_FAILED);
        }
    }

    @Override
    public InetSocketAddress lookupService(String serviceName) {
        try {
            List<Instance> instances = NacosUtils.getAllInstances(serviceName);
            // 这里没有做负载均衡，而是直接取第一个注册进来的服务器
            Instance instance = instances.get(0);
            return new InetSocketAddress(instance.getIp(), instance.getPort());
        } catch (NacosException e) {
            logger.error("获取服务时有错误发生: ", e);
        }
        return null;
    }
}
