package cn.edu.scut.qinglew.rpc.transport.socket.client;

import cn.edu.scut.qinglew.rpc.entity.RpcResponse;
import cn.edu.scut.qinglew.rpc.enumeration.ResponseCode;
import cn.edu.scut.qinglew.rpc.enumeration.RpcError;
import cn.edu.scut.qinglew.rpc.exception.RpcException;
import cn.edu.scut.qinglew.rpc.loadbalancer.LoadBalancer;
import cn.edu.scut.qinglew.rpc.registry.NacosServiceDiscovery;
import cn.edu.scut.qinglew.rpc.registry.ServiceDiscovery;
import cn.edu.scut.qinglew.rpc.serializer.CommonSerializer;
import cn.edu.scut.qinglew.rpc.transport.RpcClient;
import cn.edu.scut.qinglew.rpc.entity.RpcRequest;
import cn.edu.scut.qinglew.rpc.transport.socket.util.ObjectReader;
import cn.edu.scut.qinglew.rpc.transport.socket.util.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Socket方式远程方法调用的消费者（客户端）
 */
public class SocketClient implements RpcClient {

    private static final Logger logger = LoggerFactory.getLogger(SocketClient.class);

    private final CommonSerializer serializer;
    private final ServiceDiscovery serviceDiscovery;

    public SocketClient() {
        this(DEFAULT_SERIALIZER, null);
    }

    public SocketClient(Integer serializer) {
        this(serializer, null);
    }

    public SocketClient(LoadBalancer loadBalancer) {
        this(DEFAULT_SERIALIZER, loadBalancer);
    }

    public SocketClient(Integer serializer, LoadBalancer loadBalancer) {
        this.serializer = CommonSerializer.getByCode(serializer);
        this.serviceDiscovery = new NacosServiceDiscovery(loadBalancer);
    }

    @Override
    public Object sendRequest(RpcRequest rpcRequest) {
        if(serializer == null) {
            logger.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }

        // 从Nacos中获得有该服务的服务器
        InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest.getInterfaceName());

        try (Socket socket = new Socket()) {
            // 客户端连接服务器
            socket.connect(inetSocketAddress);

            // 获取输入输出流
            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();

            // 将请求对象写入输出流，由于是对象，因此需要将序列化
            ObjectWriter.writeObject(outputStream, rpcRequest, serializer);

            // Socket为BIO，阻塞等待服务器写回响应对象
            Object response =  ObjectReader.readObject(inputStream);
            RpcResponse rpcResponse = (RpcResponse) response;

            // 服务器写会的响应对象为空，服务调用失败
            if (rpcResponse == null) {
                logger.error("服务调用失败, service: {}", rpcRequest.getInterfaceName());
                throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE, "service: " + rpcRequest.getInterfaceName());
            }

            // 服务器写会响应对象，但响应对象状态码不为200，服务调用失败，读取失败信息
            if (rpcResponse.getStatusCode() != ResponseCode.SUCCESS.getCode()) {
                logger.error(rpcResponse.getMessage());
                throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE, "service: " + rpcRequest.getInterfaceName());
            }

            // TODO: check response data
            //

            return rpcResponse.getData();
        } catch (IOException e) {
            logger.error("调用时发生错误: ", e);
            return null;
        }
    }
}
