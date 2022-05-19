package cn.edu.scut.qinglew.test;

import cn.edu.scut.qinglew.rpc.api.HelloObject;
import cn.edu.scut.qinglew.rpc.api.HelloService;
import cn.edu.scut.qinglew.rpc.serializer.HessianSerializer;
import cn.edu.scut.qinglew.rpc.transport.RpcClient;
import cn.edu.scut.qinglew.rpc.transport.RpcClientProxy;
import cn.edu.scut.qinglew.rpc.transport.netty.client.NettyClient;

public class TestNettyClient {
    public static void main(String[] args) {
        RpcClient client = new NettyClient();
        client.setSerializer(new HessianSerializer());

        RpcClientProxy proxy = new RpcClientProxy(client);
        HelloService helloService = proxy.getProxy(HelloService.class);

        String res = helloService.hello(new HelloObject(12, "This is a message"));
        System.out.println(res);
    }
}
