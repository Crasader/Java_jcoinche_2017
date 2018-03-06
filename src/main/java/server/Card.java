package server;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Card implements Serializable
{
    public int color;
    public int value;
    private static Map<Integer, String> colors = new HashMap<Integer, String>();
    private static Map<Integer, String> values = new HashMap<Integer, String>();

    public Card(int color, int value) {
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
        this.color = color;
        this.value = value;
    }
    public void printCard(int i)
    {
        System.out.println(i + " : " + values.get(value) + " of " + colors.get(color));
    }
}
