package cn.edu.scut.qinglew.rpc.transport.socket.server;

import cn.edu.scut.qinglew.rpc.handler.RequestHandler;
import cn.edu.scut.qinglew.rpc.entity.RpcRequest;
import cn.edu.scut.qinglew.rpc.serializer.CommonSerializer;
import cn.edu.scut.qinglew.rpc.transport.socket.util.ObjectReader;
import cn.edu.scut.qinglew.rpc.transport.socket.util.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;

/**
 * RpcRequest请求处理任务
 */
public class RequestHandlerTask implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandlerTask.class);

    private Socket socket;
    private RequestHandler requestHandler;
    private CommonSerializer serializer;

    public RequestHandlerTask(Socket socket, RequestHandler requestHandler, CommonSerializer serializer) {
        this.socket = socket;
        this.requestHandler = requestHandler;
        this.serializer = serializer;
    }

    @Override
    public void run() {
        try (InputStream ins = socket.getInputStream(); OutputStream ous = socket.getOutputStream()) {
            RpcRequest rpcRequest = (RpcRequest) ObjectReader.readObject(ins);
            Object result = requestHandler.handle(rpcRequest);
            ObjectWriter.writeObject(ous, result, serializer);
        } catch (IOException e) {
            logger.error("调用或发送时有错误发生: ", e);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
