package cn.edu.scut.qinglew.rpc.loadbalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

/**
 * 轮转算法实现负载均衡
 */
public class RoundRobinLoadBalancer implements LoadBalancer {
    private int index = 0;

    @Override
    public Instance select(List<Instance> instances) {
        if (index >= instances.size())
            index = 0;
        return instances.get(index++);
    }
}
