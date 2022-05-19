package cn.edu.scut.qinglew.test;

import cn.edu.scut.qinglew.rpc.api.HelloObject;
import cn.edu.scut.qinglew.rpc.api.HelloService;
import cn.edu.scut.qinglew.rpc.transport.RpcClientProxy;
import cn.edu.scut.qinglew.rpc.transport.socket.client.SocketClient;

public class TestSocketClient {
    public static void main(String[] args) {
        SocketClient client = new SocketClient();

        RpcClientProxy proxy = new RpcClientProxy(client);
        HelloService helloService = proxy.getProxy(HelloService.class);

        String res = helloService.hello(new HelloObject(13, "This is a message"));
        System.out.println(res);
    }
}
