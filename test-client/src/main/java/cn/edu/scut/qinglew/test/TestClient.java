package cn.edu.scut.qinglew.test;

import cn.edu.scut.qinglew.rpc.RpcClient;
import cn.edu.scut.qinglew.rpc.RpcClientProxy;
import cn.edu.scut.qinglew.rpc.api.HelloObject;
import cn.edu.scut.qinglew.rpc.api.HelloService;
import cn.edu.scut.qinglew.rpc.netty.client.NettyClient;

/**
 * 客户端/服务调用者
 * 1. 注册服务（模拟，暂未实现）
 * 2. 发送请求对象
 * 3. 接收响应结果
 */
public class TestClient {
    public static void main(String[] args) {
        RpcClient client = new NettyClient("127.0.0.1", 9000);
        RpcClientProxy rpcClientProxy = new RpcClientProxy(client);

        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        String result = helloService.hello(new HelloObject(2, "this is client!"));
        System.out.println(result);
    }
}
