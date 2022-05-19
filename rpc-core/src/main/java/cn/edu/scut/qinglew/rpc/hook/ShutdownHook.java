package cn.edu.scut.qinglew.rpc.hook;

import cn.edu.scut.qinglew.rpc.factory.ThreadPoolFactory;
import cn.edu.scut.qinglew.rpc.util.NacosUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 服务器关闭时调用的钩子，单例模式
 */
public class ShutdownHook {

    private static final Logger logger = LoggerFactory.getLogger(ShutdownHook.class);

    private static final ShutdownHook shutdownHook = new ShutdownHook();

    public static ShutdownHook getShutdownHook() {
        return shutdownHook;
    }

    public void addClearAllHook() {
        logger.info("关闭后自动注销所有服务");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // 该调用将在JVM关闭前被调用
            NacosUtils.clearRegistry();
            ThreadPoolFactory.shutDownAll();
        }));
    }
}
