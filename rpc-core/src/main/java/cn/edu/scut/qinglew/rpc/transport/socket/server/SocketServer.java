package cn.edu.scut.qinglew.rpc.transport.socket.server;

import cn.edu.scut.qinglew.rpc.enumeration.RpcError;
import cn.edu.scut.qinglew.rpc.exception.RpcException;
import cn.edu.scut.qinglew.rpc.factory.ThreadPoolFactory;
import cn.edu.scut.qinglew.rpc.handler.RequestHandler;
import cn.edu.scut.qinglew.rpc.hook.ShutdownHook;
import cn.edu.scut.qinglew.rpc.provider.ServiceProvider;
import cn.edu.scut.qinglew.rpc.provider.ServiceProviderImpl;
import cn.edu.scut.qinglew.rpc.registry.NacosServiceRegistry;
import cn.edu.scut.qinglew.rpc.transport.RpcServer;
import cn.edu.scut.qinglew.rpc.registry.ServiceRegistry;
import cn.edu.scut.qinglew.rpc.serializer.CommonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * Socket方式远程方法调用的提供者（服务端）
 */
public class SocketServer implements RpcServer {
    private static final Logger logger = LoggerFactory.getLogger(SocketServer.class);

    private final ExecutorService threadPool;;

    private final String host;
    private final int port;

    private CommonSerializer serializer;

    private RequestHandler requestHandler = new RequestHandler();

    private final ServiceRegistry serviceRegistry;
    private final ServiceProvider serviceProvider;

    public SocketServer(String host, int port) {
        this.host = host;
        this.port = port;
        threadPool = ThreadPoolFactory.createDefaultThreadPool("socket-rpc-server");
        this.serviceRegistry = new NacosServiceRegistry();
        this.serviceProvider = new ServiceProviderImpl();
    }

    @Override
    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info("服务器启动...");
            ShutdownHook.getShutdownHook().addClearAllHook();
            Socket socket;
            while ((socket = serverSocket.accept()) != null) {
                logger.info("消费者连接: {}:{}", socket.getInetAddress(), socket.getPort());
                threadPool.execute(new RequestHandlerTask(socket, requestHandler, serviceRegistry, serializer));
            }
            threadPool.shutdown();
        } catch (IOException e) {
            logger.error("服务器启动时有错误发生: ", e);
        }
    }

    @Override
    public void setSerializer(CommonSerializer serializer) {
        this.serializer = serializer;
    }

    @Override
    public <T> void publishService(Object service, Class<T> serviceClass) {
        if(serializer == null) {
            logger.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        serviceProvider.addServiceProvider(service);
        serviceRegistry.register(serviceClass.getCanonicalName(), new InetSocketAddress(host, port));
        start();
    }
}
