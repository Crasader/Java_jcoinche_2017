package server;

import common.Protocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class Player extends SimpleChannelInboundHandler<Protocol>
{
    public class Obs extends Observable {
        Obs() {
        }
        void notifyServer(Protocol proto){
            setChanged();
            notifyObservers(proto);
        }
    }
    Obs observ = new Obs();
    int id;
    private String ip;
    private Room room;
    SocketChannel ch;
    boolean connected = true;
    ChannelHandlerContext client;
    List<Card> list_Card;
    private Protocol prot;
    Player(String ip, SocketChannel ch, Room room)
    {
        prot = new Protocol();
        this.ip = ip;
        this.ch = ch;
        this.room = room;
        list_Card = new ArrayList<Card>();
    }
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception
    {
        client = ctx;
        Protocol welcome = new Protocol();
        welcome.msg = "Welcome to the Jcoinche game !\n";
        channelWrite(client, welcome);
        id = room.nbPlayers;
        Protocol wait = new Protocol();
        wait.msg = "Wait for " + (4 - room.nbPlayers) + " more player(s) please.\n";
        for (Player it : room.list_player)
            channelWrite(it.client, wait);
    }
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Protocol proto) throws Exception
    {

        System.out.println(ip.substring(1).split(":")[0] + " send: " + proto.msg);
        observ.notifyServer(proto);
    }
    void channelWrite(ChannelHandlerContext ctx, Protocol send)
    {
        ctx.writeAndFlush(send);
    }
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception
    {
        connected = false;
        System.out.println("Client ip : " + ip.substring(1).split(":")[0] + " disconnected");
        prot.num = 10;
        observ.notifyServer(prot);
    }
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
    {
        cause.printStackTrace();
        ctx.close();
    }
}
