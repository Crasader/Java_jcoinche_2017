package client;

import common.Protocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ClientHandler extends SimpleChannelInboundHandler<Protocol>
{
    ClientHandler()
    {
    }
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception
    {
    }
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Protocol proto) throws Exception
    {
        if (proto.num == 1)
        {
            int color = proto.listCard.color;
            int value = proto.listCard.value;
            Map<Integer, String> colors = new HashMap<Integer, String>();
            Map<Integer, String> values = new HashMap<Integer, String>();
            colors.put(0, "Clubs");
            colors.put(1, "Diamonds");
            colors.put(2, "Hearts");
            colors.put(3, "Spades");
            values.put(0, "7");
            values.put(1, "8");
            values.put(2, "9");
            values.put(3, "10");
            values.put(4, "Jack");
            values.put(5, "Queen");
            values.put(6, "King");
            values.put(7, "Ace");
            if (proto.posCard == -1)
                System.out.println("Card : " + colors.get(color) + " of " + values.get(value));
            else
                System.out.println("Card " + proto.posCard + " : " + colors.get(color) + " of " + values.get(value));
        }
        else if (proto.num == 2)
        {
            System.out.println(proto.msg);
            Scanner scan = new Scanner(System.in);
            Protocol rep = new Protocol();
            rep.msg = scan.next();
            channelWrite(ctx, rep);
        }
        else
            System.out.println(proto.msg);
    }
    private void channelWrite(ChannelHandlerContext ctx, Protocol send)
    {
        ctx.writeAndFlush(send);
    }
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception
    {
    }
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
    {
        cause.printStackTrace();
        ctx.close();
    }
}
