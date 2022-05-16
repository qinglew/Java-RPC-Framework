package cn.edu.scut.qinglew.test;

import cn.edu.scut.qinglew.rpc.api.HelloObject;
import cn.edu.scut.qinglew.rpc.api.HelloService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Qing Lew
 */
public class HelloServiceImpl implements HelloService {

    private static final Logger logger = LoggerFactory.getLogger(HelloServiceImpl.class);

    @Override
    public String hello(HelloObject object) {
        logger.info("接收到: {}", object.getMessage());
        return "这是调用的返回值, id=" + object.getId();
    }
}
