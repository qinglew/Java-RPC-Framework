package cn.edu.scut.qinglew.rpc.loadbalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;
import java.util.Random;

/**
 * 随机算法实现负载均衡
 */
public class RandomLoadBalancer implements LoadBalancer {
    private final Random random = new Random();

    @Override
    public Instance select(List<Instance> instances) {
        return instances.get(random.nextInt(instances.size()));
    }
}
