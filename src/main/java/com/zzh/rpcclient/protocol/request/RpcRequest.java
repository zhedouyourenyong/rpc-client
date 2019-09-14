package com.zzh.rpcclient.protocol.request;

import com.zzh.rpcclient.protocol.Command;
import com.zzh.rpcclient.protocol.Packet;
import lombok.Data;

@Data
public class RpcRequest extends Packet
{
    private String requestId;
    private String interfaceName;
    private String methodName;
    private Object[] parameters;
    private Class<?>[] parameterTypes;

    @Override
    public Byte getCommand ()
    {
        return Command.RPC_REQUEST;
    }
}
