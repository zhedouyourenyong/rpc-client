package com.zzh.rpcclient;

import com.zzh.rpcclient.proxy.RpcProxy;
import com.zzh.rpcclient.service.HelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class RpcClientApplication
{

    @Autowired
    RpcProxy rpcProxy;

    public static void main (String[] args)
    {
        SpringApplication.run(RpcClientApplication.class, args);
    }

    @PostConstruct
    public void test()
    {
        HelloService helloService=rpcProxy.create(HelloService.class);
        System.out.println(helloService.say("test"));
    }

}
