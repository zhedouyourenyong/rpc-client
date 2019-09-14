package com.zzh.rpcclient.discovery;

import com.zzh.rpcclient.config.Constant;
import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class ZkServiceDiscovery implements ServiceDiscovery
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ZkServiceDiscovery.class);
    private ZkClient zkClient;
    @Value("${rpc.zkAddress}")
    private String zkAddress;


    @Override
    public String discover (String serviceName)
    {
        try
        {
            zkClient = new ZkClient(zkAddress, Constant.ZK_SESSION_TIMEOUT, Constant.ZK_CONNECTION_TIMEOUT);
            LOGGER.debug("connect zookeeper");
            String servicePath = Constant.ZK_REGISTRY_PATH + "/" + serviceName;
            if(!zkClient.exists(servicePath))
            {
                throw new RuntimeException(String.format("can not find any service node on path: %s", servicePath));
            }
            List<String> addressList = zkClient.getChildren(servicePath);
            if(addressList == null || addressList.size() == 0)
            {
                throw new RuntimeException(String.format("can not find any address node on path: %s", servicePath));
            }

            String address;
            int size = addressList.size();
            if(size == 1)
            {
                address = addressList.get(0);
                LOGGER.debug("get only address node: {}", address);
            } else
            {
                address = addressList.get(ThreadLocalRandom.current().nextInt(size));
                LOGGER.debug("get random address node: {}", address);
            }

            String addressPath = servicePath + "/" + address;
            return zkClient.readData(addressPath);
        } finally
        {
            zkClient.close();
        }
    }
}
