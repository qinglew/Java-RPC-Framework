package cn.edu.scut.qinglew.test;

import cn.edu.scut.qinglew.rpc.api.HelloService;
import cn.edu.scut.qinglew.rpc.serializer.HessianSerializer;
import cn.edu.scut.qinglew.rpc.transport.socket.server.SocketServer;

public class TestSocketServer {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();

        SocketServer socketServer = new SocketServer("127.0.0.1", 9998);
        socketServer.setSerializer(new HessianSerializer());
        socketServer.publishService(helloService, HelloService.class);
    }
}
