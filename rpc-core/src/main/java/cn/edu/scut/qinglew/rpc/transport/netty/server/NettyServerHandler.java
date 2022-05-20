package cn.edu.scut.qinglew.rpc.transport.netty.server;

import cn.edu.scut.qinglew.rpc.factory.ThreadPoolFactory;
import cn.edu.scut.qinglew.rpc.handler.RequestHandler;
import cn.edu.scut.qinglew.rpc.entity.RpcRequest;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);

    private final RequestHandler requestHandler;
    private static final String THREAD_NAME_PREFIX = "netty-server-handler";
    private static final ExecutorService threadPool;

    static {
        threadPool = ThreadPoolFactory.createDefaultThreadPool(THREAD_NAME_PREFIX);
    }

    public NettyServerHandler(RequestHandler requestHandler) {
        this.requestHandler = requestHandler;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest rpcRequest) throws Exception {
        threadPool.execute(() -> {
            try {
                logger.info("服务器接受到请求: {}", rpcRequest);

                Object response = requestHandler.handle(rpcRequest);

                ChannelFuture future = channelHandlerContext.writeAndFlush(response);
                future.addListener(ChannelFutureListener.CLOSE);
            } finally {
                ReferenceCountUtil.release(rpcRequest);
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("处理过程调用有错误发生:");
        cause.printStackTrace();
        ctx.close();
    }
}
