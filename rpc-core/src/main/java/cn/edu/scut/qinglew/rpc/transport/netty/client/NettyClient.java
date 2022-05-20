package cn.edu.scut.qinglew.rpc.transport.netty.client;

import cn.edu.scut.qinglew.rpc.enumeration.ResponseCode;
import cn.edu.scut.qinglew.rpc.loadbalancer.LoadBalancer;
import cn.edu.scut.qinglew.rpc.registry.NacosServiceDiscovery;
import cn.edu.scut.qinglew.rpc.registry.ServiceDiscovery;
import cn.edu.scut.qinglew.rpc.transport.RpcClient;
import cn.edu.scut.qinglew.rpc.entity.RpcRequest;
import cn.edu.scut.qinglew.rpc.entity.RpcResponse;
import cn.edu.scut.qinglew.rpc.enumeration.RpcError;
import cn.edu.scut.qinglew.rpc.exception.RpcException;
import cn.edu.scut.qinglew.rpc.serializer.CommonSerializer;
import io.netty.channel.*;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicReference;

/**
 * NIO消费者客户端
 */
public class NettyClient implements RpcClient {
    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);

    private final ServiceDiscovery serviceDiscovery;
    private final CommonSerializer serializer;

    public NettyClient() {
        this(DEFAULT_SERIALIZER, null);
    }

    public NettyClient(LoadBalancer loadBalancer) {
        this(DEFAULT_SERIALIZER, loadBalancer);
    }

    public NettyClient(Integer serializer) {
        this(serializer, null);
    }

    public NettyClient(Integer serializer, LoadBalancer loadBalancer) {
        this.serviceDiscovery = new NacosServiceDiscovery(loadBalancer);
        this.serializer = CommonSerializer.getByCode(serializer);
    }

    @Override
    public Object sendRequest(RpcRequest rpcRequest) {
        if (serializer == null) {
            logger.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }

        // ??? why need CAS ???
        AtomicReference<Object> result = new AtomicReference<>();
        try {
            InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest.getInterfaceName());

            // 客户端连接服务器
            Channel channel = ChannelProvider.get(inetSocketAddress, serializer);

            // 连接成功
            if (channel.isActive()) {
                // 写入RpcRequest对象
                channel.writeAndFlush(rpcRequest).addListener(future1 -> {
                    if (future1.isSuccess()) {
                        logger.info(String.format("客户端发送消息: %s", rpcRequest));
                    } else {
                        logger.error("发送消息时有错误发生: ", future1.cause());
                    }
                });
                channel.closeFuture().sync();

                // BIO获取响应对象
                AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
                RpcResponse rpcResponse = channel.attr(key).get();

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
                result.set(rpcResponse.getData());
            } else {
                System.exit(0);
            }
        } catch (InterruptedException e) {
            logger.error("发送消息时有错误发生: ", e);
        }
        return result.get();
    }
}
