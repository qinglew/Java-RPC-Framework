package cn.edu.scut.qinglew.rpc.registry;

import cn.edu.scut.qinglew.rpc.enumeration.RpcError;
import cn.edu.scut.qinglew.rpc.exception.RpcException;
import cn.edu.scut.qinglew.rpc.loadbalancer.LoadBalancer;
import cn.edu.scut.qinglew.rpc.loadbalancer.RandomLoadBalancer;
import cn.edu.scut.qinglew.rpc.util.NacosUtils;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;

public class NacosServiceDiscovery implements ServiceDiscovery {
    private static final Logger logger = LoggerFactory.getLogger(NacosServiceDiscovery.class);

    private final LoadBalancer loadBalancer;

    public NacosServiceDiscovery() {
        this(null);
    }

    public NacosServiceDiscovery(LoadBalancer loadBalancer) {
        if (loadBalancer == null)
            loadBalancer = new RandomLoadBalancer();
        this.loadBalancer = loadBalancer;
    }

    @Override
    public InetSocketAddress lookupService(String serviceName) {
        try {
            List<Instance> instances = NacosUtils.getAllInstances(serviceName);
            if (instances.size() == 0) {
                logger.error("找不到对应的服务: " + serviceName);
                throw new RpcException(RpcError.SERVICE_NOT_FOUND);
            }
            Instance instance = loadBalancer.select(instances);
            return new InetSocketAddress(instance.getIp(), instance.getPort());
        } catch (NacosException e) {
            logger.error("从Nacos获取服务实体时有错误发生: ", e);
        }
        return null;
    }
}
