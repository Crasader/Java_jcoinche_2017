package server;

public class Server
{
    public static void main(String[] args) throws Exception
    {
        try
        {
            int port;
            if (args.length > 0)
                port = Integer.parseInt(args[0]);
            else
                port = 4242;
            new Connection(port).run();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
