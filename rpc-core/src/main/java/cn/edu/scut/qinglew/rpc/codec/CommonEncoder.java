package cn.edu.scut.qinglew.rpc.codec;

import cn.edu.scut.qinglew.rpc.entity.RpcRequest;
import cn.edu.scut.qinglew.rpc.entity.RpcResponse;
import cn.edu.scut.qinglew.rpc.enumeration.PackageType;
import cn.edu.scut.qinglew.rpc.serializer.CommonSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 通用编码拦截器
 */
public class CommonEncoder extends MessageToByteEncoder {

    // 4 bytes magic number
    private static final int MAGIC_NUMBER = 0xCAFEBABE;

    private final CommonSerializer serializer;

    public CommonEncoder(CommonSerializer serializer) {
        this.serializer = serializer;
    }

    /**
     * 协议格式:
     * +----------------+----------------+-------------------+---------------+
     * |  Magic Number  |  Package Type  |  Serializer Type  |  Data Length  |
     * |     4 bytes    |     4 bytes    |      4 bytes      |    4 bytes    |
     * +----------------+----------------+-------------------+---------------+
     * |                             Data Bytes                              |
     * |                         Length: ${Data Length}                      |
     * +----------------+----------------+-------------------+---------------+
     * @param channelHandlerContext
     * @param o
     * @param byteBuf
     * @throws Exception
     */
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        // magic number, 4 bytes
        byteBuf.writeInt(MAGIC_NUMBER);

        // package type, 4 bytes
        if (o instanceof RpcRequest) {
            byteBuf.writeInt(PackageType.REQUEST_PACK.getCode());
        } else if (o instanceof RpcResponse) {
            byteBuf.writeInt(PackageType.RESPONSE_PACK.getCode());
        }

        // serializer type, 4 bytes
        byteBuf.writeInt(serializer.getCode());

        byte[] bytes = serializer.serialize(o);

        // data length, 4 bytes
        byteBuf.writeInt(bytes.length);

        // data bytes
        byteBuf.writeBytes(bytes);
    }
}
