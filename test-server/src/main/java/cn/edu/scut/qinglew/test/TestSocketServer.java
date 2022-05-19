package cn.edu.scut.qinglew.test;

import cn.edu.scut.qinglew.rpc.annotation.ServiceScan;
import cn.edu.scut.qinglew.rpc.transport.socket.server.SocketServer;

@ServiceScan
public class TestSocketServer {
    public static void main(String[] args) {
        SocketServer socketServer = new SocketServer("127.0.0.1", 9998);
        socketServer.start();
    }
}
