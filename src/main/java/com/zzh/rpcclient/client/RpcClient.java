package com.zzh.rpcclient.client;

import com.zzh.rpcclient.codec.PacketCodecHandler;
import com.zzh.rpcclient.codec.Spliter;
import com.zzh.rpcclient.protocol.request.RpcRequest;
import com.zzh.rpcclient.protocol.response.RpcResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpcClient extends SimpleChannelInboundHandler<RpcResponse>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcClient.class);
    private final String host;
    private final int port;
    private RpcResponse response;

    public RpcClient (String host, int port)
    {
        this.host = host;
        this.port = port;
    }

    public RpcResponse send (RpcRequest request) throws Exception
    {
        EventLoopGroup group = new NioEventLoopGroup();
        try
        {
            // 创建并初始化 Netty 客户端 Bootstrap 对象
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.handler(new ChannelInitializer<SocketChannel>()
            {
                @Override
                public void initChannel (SocketChannel channel)
                {
                    ChannelPipeline pipeline = channel.pipeline();
                    pipeline.addLast("spliter", new Spliter());
                    pipeline.addLast("handler", PacketCodecHandler.INSTANCE);
                    pipeline.addLast(RpcClient.this); // 处理 RPC 响应
                }
            });

            bootstrap.option(ChannelOption.TCP_NODELAY, true);
            ChannelFuture future = bootstrap.connect(host, port).sync();
            Channel channel = future.channel();
            channel.writeAndFlush(request).sync();

            channel.closeFuture().sync();
            return response;
        } finally
        {
            group.shutdownGracefully();
        }
    }

    @Override
    protected void channelRead0 (ChannelHandlerContext ctx, RpcResponse msg) throws Exception
    {
        this.response = msg;
    }

    @Override
    public void exceptionCaught (ChannelHandlerContext ctx, Throwable cause) throws Exception
    {
        LOGGER.error("api caught exception", cause);
        ctx.close();
    }
}
