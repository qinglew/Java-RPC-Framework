package cn.edu.scut.qinglew.rpc.server;

import cn.edu.scut.qinglew.rpc.entity.RpcRequest;
import cn.edu.scut.qinglew.rpc.entity.RpcResponse;
import cn.edu.scut.qinglew.rpc.enumeration.ResponseCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 进行过程调用的处理器
 */
public class RequestHandler {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    public Object handle(RpcRequest rpcRequest, Object service) {
        Object result = null;
        try {
            result = invokeTargetMethod(rpcRequest, service);
            logger.info("服务: {} 成功调用方法: {}", rpcRequest.getInterfaceName(), rpcRequest.getMethodName());
        } catch (InvocationTargetException | IllegalAccessException e) {
            logger.error("调用或发送时有错误发生", e);
        }
        return result;
    }

    private Object invokeTargetMethod(RpcRequest rpcRequest, Object service) throws InvocationTargetException, IllegalAccessException {
        /* 通过方法名和方法参数类型反射获得方法对象 */
        Method method;
        try {
            method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
        } catch (NoSuchMethodException e) {
            return RpcResponse.fail(ResponseCode.METHOD_NOT_FOUND);
        }

        // 方法调用
        return RpcResponse.success(method.invoke(service, rpcRequest.getParameters()));
    }
}
