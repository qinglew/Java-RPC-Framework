package cn.edu.scut.qinglew.rpc.transport;

import cn.edu.scut.qinglew.rpc.entity.RpcRequest;
import cn.edu.scut.qinglew.rpc.serializer.CommonSerializer;

/**
 * 客户端类通用接口
 */
public interface RpcClient {
    Object sendRequest(RpcRequest rpcRequest);
    void setSerializer(CommonSerializer serializer);
}
