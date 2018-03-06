package client;

import common.Protocol;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public final class Client
{
    public static void main(String[] args) throws Exception
    {
        EventLoopGroup group = new NioEventLoopGroup();
        try
        {
            String host;
            if (args.length > 0)
                host = args[0];
            else
                host = "127.0.0.1";
            int port;
            if (args.length > 1)
                port = Integer.parseInt(args[1]);
            else
                port = 4242;
            Bootstrap b = new Bootstrap();
            b.group(group);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ObjectDecoder(ClassResolvers.weakCachingResolver(Protocol.class.getClassLoader())));
                    ch.pipeline().addLast(new ObjectEncoder());
                    ch.pipeline().addLast(new ClientHandler());
                }
            });
          b.connect(host, port).sync().channel().closeFuture().sync();
        }
        finally
        {
            group.shutdownGracefully();
        }
    }
}
