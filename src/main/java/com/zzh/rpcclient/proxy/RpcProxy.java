package com.zzh.rpcclient.proxy;

import com.zzh.rpcclient.client.RpcClient;
import com.zzh.rpcclient.discovery.ServiceDiscovery;
import com.zzh.rpcclient.protocol.request.RpcRequest;
import com.zzh.rpcclient.protocol.response.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

@Component
public class RpcProxy
{
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcProxy.class);

    @Autowired
    private ServiceDiscovery serviceDiscovery;


    @SuppressWarnings("unchecked")
    public <T> T create (final Class<?> interfaceClass)
    {
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new InvocationHandler()
                {
                    @Override
                    public Object invoke (Object proxy, Method method, Object[] args) throws Throwable
                    {
                        RpcRequest request = new RpcRequest();
                        request.setRequestId(UUID.randomUUID().toString());
                        request.setInterfaceName(method.getDeclaringClass().getSimpleName());
                        request.setMethodName(method.getName());
                        request.setParameterTypes(method.getParameterTypes());
                        request.setParameters(args);

                        String serviceAddress = null;
                        if(serviceDiscovery != null)
                        {
                            String serviceName = interfaceClass.getSimpleName();
                            serviceAddress = serviceDiscovery.discover(serviceName);
                            LOGGER.debug("discover service: {} => {}", serviceName, serviceAddress);
                        }
                        if(serviceAddress == null)
                        {
                            throw new RuntimeException("server address is empty");
                        }
                        String[] array = serviceAddress.split(":");
                        String host = array[0];
                        int port = Integer.parseInt(array[1]);
                        RpcClient client = new RpcClient(host, port);
                        long time = System.currentTimeMillis();
                        RpcResponse response = client.send(request);
                        LOGGER.debug("time: {}ms", System.currentTimeMillis() - time);
                        if(response == null)
                        {
                            throw new RuntimeException("response is null");
                        }

                        if(response.hasException())
                        {
                            throw response.getException();
                        } else
                        {
                            return response.getResult();
                        }
                    }
                }
        );
    }

}
