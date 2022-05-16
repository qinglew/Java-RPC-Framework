package cn.edu.scut.qinglew.rpc.server;

import cn.edu.scut.qinglew.rpc.entity.RpcRequest;
import cn.edu.scut.qinglew.rpc.entity.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

/**
 * 实际进行过程调用的工作线程，接收客户端请求，调用方法，返回结果.
 *
 * @author Qing Lew
 */
public class WorkerThread  implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(WorkerThread.class);

    private Socket socket;
    private Object service;

    public WorkerThread(Socket socket, Object service) {
        this.socket = socket;
        this.service = service;
    }

    @Override
    public void run() {
        try (ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {
            // 读取客户端请求对象
            RpcRequest rpcRequest = (RpcRequest) ois.readObject();

            // 反射获取方法对象
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());

            // 方法调用
            Object invokeResult = method.invoke(service, rpcRequest.getParameters());

            // 发送响应对象
            oos.writeObject(RpcResponse.success(invokeResult));
            oos.flush();
        } catch (IOException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            logger.error("调用或发送时有错误发生: ", e);
        }
    }
}
