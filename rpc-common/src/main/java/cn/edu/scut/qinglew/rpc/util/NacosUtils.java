package cn.edu.scut.qinglew.rpc.util;

import cn.edu.scut.qinglew.rpc.enumeration.RpcError;
import cn.edu.scut.qinglew.rpc.exception.RpcException;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 管理Nacos连接等工具类
 */
public class NacosUtils {

    private static final Logger logger = LoggerFactory.getLogger(NacosUtils.class);

    private static final String NACOS_SERVER = "127.0.0.1:8848";
    private static final NamingService namingService;

    private static final Set<String> serviceNames = new HashSet<>();
    private static InetSocketAddress address;

    static {
        namingService = getNacosNamingService();
    }

    public static NamingService getNacosNamingService() {
        try {
            return NamingFactory.createNamingService(NACOS_SERVER);
        } catch (NacosException e) {
            logger.error("连接到Nacos时发生错误: ", e);
            throw new RpcException(RpcError.FAILED_TO_CONNECT_TO_SERVICE_REGISTRY);
        }
    }

    /**
     * 向Nacos注册本机上的某个服务
     * @param serviceName
     * @param address
     * @throws NacosException
     */
    public static void registerService(String serviceName, InetSocketAddress address) throws NacosException {
        namingService.registerInstance(serviceName, address.getHostName(), address.getPort());
        NacosUtils.address = address;
        serviceNames.add(serviceName);
    }

    public static List<Instance> getAllInstances(String serviceName) throws NacosException {
        return namingService.getAllInstances(serviceName);
    }

    /**
     * 向Nacos注销该服务器上的所有服务
     */
    public static void clearRegistry() {
        if (!serviceNames.isEmpty() && address != null) {
            for (String serviceName : serviceNames) {
                try {
                    namingService.deregisterInstance(serviceName, address.getHostName(), address.getPort());
                    logger.info("注销 {}:{} 上的服务 {} 成功", address.getHostName(), address.getPort(), serviceName);
                } catch (NacosException e) {
                    logger.error("注销服务 {} 失败", serviceName, e);
                }
            }
        }
    }
}
