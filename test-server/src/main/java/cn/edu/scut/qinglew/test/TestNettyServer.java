package cn.edu.scut.qinglew.test;

import cn.edu.scut.qinglew.rpc.annotation.ServiceScan;
import cn.edu.scut.qinglew.rpc.transport.netty.server.NettyServer;

@ServiceScan
public class TestNettyServer {
    public static void main(String[] args) {
        NettyServer server = new NettyServer("127.0.0.1", 9999);
        server.start();
    }
}
