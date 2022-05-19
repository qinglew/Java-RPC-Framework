package cn.edu.scut.qinglew.rpc.serializer;

/**
 * 通用序列化/反序列化接口
 */
public interface CommonSerializer {

    Integer KRYO_SERIALIZER = 0;
    Integer JSON_SERIALIZER = 1;
    Integer JDK_SERIALIZER = 2;
    Integer HESSIAN_SERIALIZER = 3;

    Integer DEFAULT_SERIALIZER = HESSIAN_SERIALIZER;

    /**
     * 根据code获取(反)序列化器
     * @param code
     * @return
     */
    static CommonSerializer getByCode(int code) {
        switch (code) {
            case 0:
                return new KryoSerializer();
            case 1:
                return new JsonSerializer();
            case 2:
                return new JdkSerializer();
            case 3:
                return new HessianSerializer();
            default:
                return null;
        }
    }

    byte[] serialize(Object object);

    Object deserialize(byte[] bytes, Class<?> clazz);

    int getCode();
}
