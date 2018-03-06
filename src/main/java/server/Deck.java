package server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    List<Card> pile = new ArrayList<Card>();

    Deck() {
        for (int y = 0; y < 4; y++) {
            for (int i = 0; i < 8; i++) {
                pile.add(new Card(y, i));
            }
        }
        Collections.shuffle(pile);
        Collections.shuffle(pile);
        Collections.shuffle(pile);
        printDeck();
        System.out.println("Deck has been succesfully created");
    }
    private void printDeck()
    {
        int i = 1;
        for (Card it : pile)
            it.printCard(i++);
    }
}
