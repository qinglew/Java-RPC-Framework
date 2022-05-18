package cn.edu.scut.qinglew.rpc.codec;

import cn.edu.scut.qinglew.rpc.entity.RpcRequest;
import cn.edu.scut.qinglew.rpc.entity.RpcResponse;
import cn.edu.scut.qinglew.rpc.enumeration.PackageType;
import cn.edu.scut.qinglew.rpc.enumeration.RpcError;
import cn.edu.scut.qinglew.rpc.exception.RpcException;
import cn.edu.scut.qinglew.rpc.serializer.CommonSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 通用解码拦截器
 */
public class CommonDecoder extends ReplayingDecoder {

    private static final Logger logger = LoggerFactory.getLogger(CommonDecoder.class);

    // 4 bytes magic number
    private static final int MAGIC_NUMBER = 0xCAFEBABE;

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        // 4 bytes magic number
        int magicNumber = byteBuf.readInt();
        if (magicNumber != MAGIC_NUMBER) {
            logger.error("不识别的协议包: {}", magicNumber);
            throw new RpcException(RpcError.UNKNOWN_PROTOCOL);
        }

        // 4 bytes package type
        int packageCode = byteBuf.readInt();
        Class<?> clazz;
        if (packageCode == PackageType.REQUEST_PACK.getCode()) {
            clazz = RpcRequest.class;
        } else if (packageCode == PackageType.RESPONSE_PACK.getCode()) {
            clazz = RpcResponse.class;
        } else {
            logger.error("不识别的数据包类型: {}", packageCode);
            throw new RpcException(RpcError.UNKNOWN_PACKAGE_TYPE);
        }

        // 4 bytes serializer code
        int serializerCode = byteBuf.readInt();
        CommonSerializer serializer = CommonSerializer.getByCode(serializerCode);
        if (serializer == null) {
            logger.error("不识别的反序列化器: {}", serializerCode);
            throw new RpcException(RpcError.UNKNOWN_SERIALIZER);
        }

        // 4 bytes data length
        int length = byteBuf.readInt();

        // data
        byte[] data = new byte[length];
        byteBuf.readBytes(data);
        Object obj = serializer.deserialize(data, clazz);

        list.add(obj);
    }
}
