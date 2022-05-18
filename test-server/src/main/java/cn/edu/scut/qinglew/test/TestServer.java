package cn.edu.scut.qinglew.test;

import cn.edu.scut.qinglew.rpc.RpcServer;
import cn.edu.scut.qinglew.rpc.api.HelloService;
import cn.edu.scut.qinglew.rpc.netty.server.NettyServer;
import cn.edu.scut.qinglew.rpc.registry.DefaultServiceRegistry;
import cn.edu.scut.qinglew.rpc.registry.ServiceRegistry;
import cn.edu.scut.qinglew.rpc.socket.server.SocketServer;

/**
 * 测试：服务器/服务提供者
 * 1. 提供服务实现类
 * 2. 启动服务器，监听请求，获取请求对象，服务调用，返回调用结果
 */
public class TestServer {
    public static void main(String[] args) {
        /* 服务 */
        HelloService helloService = new HelloServiceImpl();

        /* 创建注册表并注册服务 */
        ServiceRegistry serviceRegistry = new DefaultServiceRegistry();
        serviceRegistry.registry(helloService);

        /* 创建并启动服务器 */
//        SocketServer rpcServer = new SocketServer(serviceRegistry);
        RpcServer server = new NettyServer();
        server.start(9000);
    }
}
