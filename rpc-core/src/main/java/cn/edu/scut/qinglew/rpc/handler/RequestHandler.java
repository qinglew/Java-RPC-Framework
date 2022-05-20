package cn.edu.scut.qinglew.rpc.handler;

import cn.edu.scut.qinglew.rpc.entity.RpcRequest;
import cn.edu.scut.qinglew.rpc.entity.RpcResponse;
import cn.edu.scut.qinglew.rpc.enumeration.ResponseCode;
import cn.edu.scut.qinglew.rpc.provider.ServiceProvider;
import cn.edu.scut.qinglew.rpc.provider.ServiceProviderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 进行过程调用的处理器
 */
public class RequestHandler {

    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    private final ServiceProvider serviceProvider;

    public RequestHandler(ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    public Object handle(RpcRequest rpcRequest) {
        Object result = null;
        Object service = serviceProvider.getServiceProvider(rpcRequest.getInterfaceName());
        try {
            result = RpcResponse.success(rpcRequest.getRequestId(), invokeTargetMethod(rpcRequest, service));
            logger.info("服务: {} 成功调用方法: {}", rpcRequest.getInterfaceName(), rpcRequest.getMethodName());
        } catch (InvocationTargetException e) {
            logger.error("调用或发送时有错误发生: ", e);
            result = RpcResponse.fail(rpcRequest.getRequestId(), ResponseCode.FAIL);
        } catch (IllegalAccessException e) {
            logger.error("不合法的访问权限: ", e);
            result = RpcResponse.fail(rpcRequest.getRequestId(), ResponseCode.ILLEGAL_ACCESS);
        } catch (NoSuchMethodException e) {
            logger.error("服务调用失败: ", e);
            result = RpcResponse.fail(rpcRequest.getRequestId(), ResponseCode.METHOD_NOT_FOUND);
        }
        return result;
    }

    private Object invokeTargetMethod(RpcRequest rpcRequest, Object service)
            throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        /* 通过方法名和方法参数类型反射获得方法对象 */
        Method method;
        method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());

        // 方法调用
        return method.invoke(service, rpcRequest.getParameters());
    }
}
