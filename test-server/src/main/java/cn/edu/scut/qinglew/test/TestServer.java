package cn.edu.scut.qinglew.test;

import cn.edu.scut.qinglew.rpc.api.HelloService;
import cn.edu.scut.qinglew.rpc.server.RpcServer;

/**
 * 测试：服务器/服务提供者
 * 1. 提供服务实现类
 * 2. 启动服务器，监听请求，获取请求对象，服务调用，返回调用结果
 */
public class TestServer {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        RpcServer rpcServer = new RpcServer();
        rpcServer.register(helloService, 9000);
    }
}
