package common;

import server.Card;
import java.io.Serializable;

public class Protocol implements Serializable
{
    public int num;
    public String msg;
    public Card listCard;
    public int posCard;
}
