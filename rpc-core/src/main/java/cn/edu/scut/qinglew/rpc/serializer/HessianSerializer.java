package cn.edu.scut.qinglew.rpc.serializer;

import cn.edu.scut.qinglew.rpc.enumeration.SerializerCode;
import cn.edu.scut.qinglew.rpc.exception.SerializeException;
import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class HessianSerializer implements CommonSerializer {
    private static final Logger logger = LoggerFactory.getLogger(HessianSerializer.class);

    @Override
    public byte[] serialize(Object object) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Hessian2Output output = new Hessian2Output(baos);
            output.writeObject(object);
            output.close();
            return baos.toByteArray();
        } catch (IOException e) {
            logger.error("序列化时有错误发生:", e);
            throw new SerializeException("序列化时有错误发生");
        }
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
            Hessian2Input input = new Hessian2Input(bais);
            Object o = input.readObject();
            input.close();
            return o;
        } catch (IOException e) {
            logger.error("反序列化时有错误发生:", e);
            throw new SerializeException("反序列化时有错误发生");
        }
    }

    @Override
    public int getCode() {
        return SerializerCode.HESSIAN.getCode();
    }
}
