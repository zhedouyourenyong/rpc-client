package com.zzh.rpcclient.protocol.response;

import com.zzh.rpcclient.protocol.Command;
import com.zzh.rpcclient.protocol.Packet;
import lombok.Data;

@Data
public class RpcResponse extends Packet
{
    private String requestId;
    private Exception exception;
    private Object result;

    public boolean hasException()
    {
        return exception!=null;
    }

    @Override
    public Byte getCommand ()
    {
        return Command.RPC_RESPONSE;
    }
}
