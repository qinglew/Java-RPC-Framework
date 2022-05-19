package cn.edu.scut.qinglew.test;

import cn.edu.scut.qinglew.rpc.api.HelloService;
import cn.edu.scut.qinglew.rpc.serializer.HessianSerializer;
import cn.edu.scut.qinglew.rpc.transport.netty.server.NettyServer;

public class TestNettyServer {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();

        NettyServer server = new NettyServer("127.0.0.1", 9999);
        server.setSerializer(new HessianSerializer());

        server.publishService(helloService, HelloService.class);
    }
}
