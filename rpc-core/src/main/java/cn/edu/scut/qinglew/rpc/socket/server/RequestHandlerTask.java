package cn.edu.scut.qinglew.rpc.socket.server;

import cn.edu.scut.qinglew.rpc.RequestHandler;
import cn.edu.scut.qinglew.rpc.entity.RpcRequest;
import cn.edu.scut.qinglew.rpc.registry.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * RpcRequest请求处理任务
 */
public class RequestHandlerTask implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandlerTask.class);

    private Socket socket;
    private RequestHandler requestHandler;
    private ServiceRegistry serviceRegistry;

    public RequestHandlerTask(Socket socket, RequestHandler requestHandler, ServiceRegistry serviceRegistry) {
        this.socket = socket;
        this.requestHandler = requestHandler;
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public void run() {
        try (ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())) {
            RpcRequest rpcRequest = (RpcRequest) ois.readObject();
            Object service = serviceRegistry.getService(rpcRequest.getInterfaceName());
            Object handleResult = requestHandler.handle(rpcRequest, service);
            oos.writeObject(handleResult);
            oos.flush();
        } catch (ClassNotFoundException | IOException e) {
            logger.error("调用或发送时有错误发生: ", e);
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
