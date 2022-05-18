package cn.edu.scut.qinglew.rpc;

import cn.edu.scut.qinglew.rpc.entity.RpcRequest;

/**
 * 客户端类通用接口
 */
public interface RpcClient {
    Object sendRequest(RpcRequest rpcRequest);
}