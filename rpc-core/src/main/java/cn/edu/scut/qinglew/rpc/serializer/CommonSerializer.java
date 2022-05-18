package cn.edu.scut.qinglew.rpc.serializer;

/**
 * 通用序列化/反序列化接口
 */
public interface CommonSerializer {

    /**
     * 序列化
     * @param object
     * @return
     */
    byte[] serialize(Object object);

    /**
     * 反序列化
     * @param bytes
     * @param clazz
     * @return
     */
    Object deserialize(byte[] bytes, Class<?> clazz);


    int getCode();

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
}
