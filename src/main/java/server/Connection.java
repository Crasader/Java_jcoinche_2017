package server;

import common.Protocol;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import java.util.ArrayList;
import java.util.List;

class Connection {
    private Room room;
    private int port;

    Connection(int port) {
        this.port = port;
        room = new Room();
    }
    void run() throws Exception
    {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try
        {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception
                {
                    System.out.println("Client connected with ip : " + ch.remoteAddress().toString().substring(1).split(":")[0]);
                    Player player = new Player(ch.remoteAddress().toString(), ch, room);
                    ch.pipeline().addLast(new ObjectDecoder(ClassResolvers.weakCachingResolver(Protocol.class.getClassLoader())));
                    ch.pipeline().addLast(new ObjectEncoder());
                    ch.pipeline().addLast(player);
                    room.list_player.add(player);
                    room.check_connected();
                    System.out.println("Player in room = " + room.nbPlayers);
                    if (room.nbPlayers == 4)
                    {
                        List<Player> cp = new ArrayList<Player>();
                        cp.addAll(room.list_player);
                        Game game = new Game(cp);
                        game.start();
                        room.removeAll();
                    }
            }
        }).option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true);
            System.out.println("Server started with port : " + port);
            b.bind(port).sync().channel().closeFuture().sync();
        }
        finally
        {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
