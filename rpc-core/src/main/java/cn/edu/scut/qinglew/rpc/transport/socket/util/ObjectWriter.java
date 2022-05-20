package cn.edu.scut.qinglew.rpc.transport.socket.util;

import cn.edu.scut.qinglew.rpc.entity.RpcRequest;
import cn.edu.scut.qinglew.rpc.enumeration.PackageType;
import cn.edu.scut.qinglew.rpc.serializer.CommonSerializer;

import java.io.IOException;
import java.io.OutputStream;

/**
 * 对象序列化后写入输出流
 * 按照自定义的通信协议格式写入
 */
public class ObjectWriter {
    // 4 bytes magic number
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
     * @param outputStream
     * @param object
     * @param serializer
     * @throws IOException
     */
    public static void writeObject(OutputStream outputStream, Object object, CommonSerializer serializer) throws IOException {
        // 4 bytes magic number: 0xCAFEBABE
        outputStream.write(intToBytes(MAGIC_NUMBER));

        // 4 bytes package type: request or response
        if (object instanceof RpcRequest) {
            outputStream.write(intToBytes(PackageType.REQUEST_PACK.getCode()));
        } else {
            outputStream.write(intToBytes(PackageType.RESPONSE_PACK.getCode()));
        }

        // 4 bytes serializer code: which kind of serializer is used
        outputStream.write(intToBytes(serializer.getCode()));

        // serialize into bytes
        byte[] bytes = serializer.serialize(object);

        // 4 bytes data length
        outputStream.write(intToBytes(bytes.length));

        // data
        outputStream.write(bytes);

        outputStream.flush();
    }

    private static byte[] intToBytes(int value) {
        byte[] src = new byte[4];
        src[0] = (byte) ((value>>24) & 0xFF);
        src[1] = (byte) ((value>>16)& 0xFF);
        src[2] = (byte) ((value>>8)&0xFF);
        src[3] = (byte) (value & 0xFF);
        return src;
    }
}
