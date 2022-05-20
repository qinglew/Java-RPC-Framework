package cn.edu.scut.qinglew.rpc.transport.socket.server;

import cn.edu.scut.qinglew.rpc.factory.ThreadPoolFactory;
import cn.edu.scut.qinglew.rpc.handler.RequestHandler;
import cn.edu.scut.qinglew.rpc.hook.ShutdownHook;
import cn.edu.scut.qinglew.rpc.transport.AbstractRpcServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * Socket方式远程方法调用的提供者（服务端）
 */
public class SocketServer extends AbstractRpcServer {

    private final ExecutorService threadPool;


    public SocketServer(String host, int port) {
        this(host, port, DEFAULT_SERIALIZER);
    }

    public SocketServer(String host, int port, Integer serializer) {
        super(host, port, serializer);
        threadPool = ThreadPoolFactory.createDefaultThreadPool("socket-rpc-server");
    }

    @Override
    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info("服务器启动...");
            ShutdownHook.getShutdownHook().addClearAllHook();
            Socket socket;
            while ((socket = serverSocket.accept()) != null) {
                logger.info("消费者连接: {}:{}", socket.getInetAddress(), socket.getPort());
                threadPool.execute(new RequestHandlerTask(socket, requestHandler, serializer));
            }
        } catch (IOException e) {
            logger.error("服务器启动时有错误发生: ", e);
        }
    }
}
