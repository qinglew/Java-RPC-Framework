package cn.edu.scut.qinglew.rpc.transport;

import cn.edu.scut.qinglew.rpc.annotation.Service;
import cn.edu.scut.qinglew.rpc.annotation.ServiceScan;
import cn.edu.scut.qinglew.rpc.enumeration.RpcError;
import cn.edu.scut.qinglew.rpc.exception.RpcException;
import cn.edu.scut.qinglew.rpc.provider.ServiceProvider;
import cn.edu.scut.qinglew.rpc.registry.ServiceRegistry;
import cn.edu.scut.qinglew.rpc.serializer.CommonSerializer;
import cn.edu.scut.qinglew.rpc.util.ReflectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Set;

public abstract class AbstractRpcServer implements RpcServer {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    protected String host;
    protected int port;
    protected CommonSerializer serializer;
    protected ServiceRegistry serviceRegistry;
    protected ServiceProvider serviceProvider;

    /**
     * 自动扫描服务（扫描所有@Service的类）并创建服务对象，注册
     */
    public void scanServices() {
        String mainClassName = ReflectUtil.getStackTrace();
        // 寻找配置@ServiceScan注解的类
        Class<?> startClass;
        try {
            startClass = Class.forName(mainClassName);
            if (!startClass.isAnnotationPresent(ServiceScan.class)) {
                logger.error("启动类缺少 @ServiceScan 注解");
                throw new RpcException(RpcError.SERVICE_SCAN_PACKAGE_NOT_FOUND);
            }
        } catch (ClassNotFoundException e) {
            logger.error("出现未知错误");
            throw new RpcException(RpcError.UNKNOWN_ERROR);
        }

        // 获取 @Service注解 配置的值，即要扫描的包
        String basePackage = startClass.getAnnotation(ServiceScan.class).value();
        if ("".equals(basePackage)) {
            basePackage = mainClassName.substring(0, mainClassName.lastIndexOf("."));
        }

        // 扫描包下的所有配置了@Service注解的类
        Set<Class<?>> classSet = ReflectUtil.getClasses(basePackage);
        for (Class<?> clazz : classSet) {
            if (clazz.isAnnotationPresent(Service.class)) {
                // 从注解上获取服务名
                String serviceName = clazz.getAnnotation(Service.class).name();
                // 创建服务对象
                Object obj;
                try {
                    obj = clazz.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    logger.error("创建 " + clazz + " 时有错误发生");
                    continue;
                }

                // 如果没有在@Service上配置name，使用类上接口的全限定名作为服务名
                if ("".equals(serviceName)) {
                    Class<?>[] interfaces = clazz.getInterfaces();
                    for (Class<?> i : interfaces) {
                        publishService(obj, i.getCanonicalName());
                    }
                } else {
                    publishService(obj, serviceName);
                }
            }
        }
    }

    @Override
    public <T> void publishService(T service, String serviceName) {
        serviceProvider.addServiceProvider(service, serviceName);
        serviceRegistry.register(serviceName, new InetSocketAddress(host, port));
    }
}
