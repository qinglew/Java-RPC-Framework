package cn.edu.scut.qinglew.rpc.transport.socket.util;

import cn.edu.scut.qinglew.rpc.entity.RpcRequest;
import cn.edu.scut.qinglew.rpc.entity.RpcResponse;
import cn.edu.scut.qinglew.rpc.enumeration.PackageType;
import cn.edu.scut.qinglew.rpc.enumeration.RpcError;
import cn.edu.scut.qinglew.rpc.exception.RpcException;
import cn.edu.scut.qinglew.rpc.serializer.CommonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

public class ObjectReader {
    private static final Logger logger = LoggerFactory.getLogger(ObjectReader.class);

    private static final int MAGIC_NUMBER = 0xCAFEBABE;

    /**
     * 协议格式:
     * +----------------+----------------+-------------------+---------------+
     * |  Magic Number  |  Package Type  |  Serializer Type  |  Data Length  |
     * |     4 bytes    |     4 bytes    |      4 bytes      |    4 bytes    |
     * +----------------+----------------+-------------------+---------------+
     * |                             Data Bytes                              |
     * |                         Length: ${Data Length}                      |
     * +----------------+----------------+-------------------+---------------+
     * @param in
     * @return
     * @throws IOException
     */
    public static Object readObject(InputStream in) throws IOException {
        byte[] numberBytes = new byte[4];

        // 4 bytes magic number
        in.read(numberBytes);
        int magic = bytesToInt(numberBytes);
        if (magic != MAGIC_NUMBER) {
            logger.error("不识别的协议包: {}", magic);
            throw new RpcException(RpcError.UNKNOWN_PROTOCOL);
        }

        // 4 bytes package type: request or response
        in.read(numberBytes);
        int packageCode = bytesToInt(numberBytes);
        Class<?> packageClass;
        if (packageCode == PackageType.REQUEST_PACK.getCode()) {
            packageClass = RpcRequest.class;
        } else if (packageCode == PackageType.RESPONSE_PACK.getCode()) {
            packageClass = RpcResponse.class;
        } else {
            logger.error("不识别的数据包: {}", packageCode);
            throw new RpcException(RpcError.UNKNOWN_PACKAGE_TYPE);
        }

        // 4 bytes serializer code
        in.read(numberBytes);
        int serializerCode = bytesToInt(numberBytes);
        CommonSerializer serializer = CommonSerializer.getByCode(serializerCode);
        if (serializer == null) {
            logger.error("不识别的反序列化器: {}", serializerCode);
            throw new RpcException(RpcError.UNKNOWN_SERIALIZER);
        }

        // 4 bytes data length
        in.read(numberBytes);
        int length = bytesToInt(numberBytes);

        // data
        byte[] bytes = new byte[length];
        in.read(bytes);

        return serializer.deserialize(bytes, packageClass);
    }

    public static int bytesToInt(byte[] src) {
        int value;
        value = ((src[0] & 0xFF)<<24)
                |((src[1] & 0xFF)<<16)
                |((src[2] & 0xFF)<<8)
                |(src[3] & 0xFF);
        return value;
    }
}
