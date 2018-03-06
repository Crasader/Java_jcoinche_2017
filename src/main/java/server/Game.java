package server;

import common.Protocol;
import java.util.List;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;

public class Game extends Thread {
    private Utils util;
    private Deck deck;
    private List<Player> players;
    private int index = 0;
    private Score score;
    private int manche = 0;
    private int nbTurn = 1;
    private boolean isContract = false;
    private boolean takeIn = false;

    Game(List<Player> players) {
        System.out.println("New Game");
        deck = new Deck();
        this.players = players;
        Observe observ = new Observe();
        util = new Utils(players);
        score = new Score();
        for (Player it : players) {
            it.observ.addObserver(observ);
        }
    }

    public class Observe implements Observer {
        public void update(Observable observable, Object o) {
            Protocol proto = (Protocol) o;
            if (proto.num == 10) {
                util.checkDeath();
            }
            else if (isContract && proto.num != 10)
            {
                int pos = 0;
                try {
                    pos = Integer.parseInt(proto.msg);
                }
                catch (NumberFormatException ignored) {}
                if (pos > 0 && pos < 9)
                {
                    manche += 1;
                    Protocol game = new Protocol();
                    game.num = 1;
                    game.posCard = -1;
                    game.listCard = players.get(index).list_Card.get(Integer.parseInt(proto.msg) - 1);
                    util.send_all(game);
                    index = index + 1 > 3 ? 0 : index + 1;
                    if (manche == 4)
                        aTurn();
                    else
                        inGame();
                }
                else
                    range();
            }
            else if (!isContract && proto.num != 10) {
                if (Objects.equals(proto.msg, "Pass")) {
                    Protocol pass = new Protocol();
                    pass.msg = "Player " + players.get(index).id + " pass !\n";
                    util.send_all(pass);
                    index = index + 1 > 3 ? 0 : index + 1;
                    contract();
                } else if (Objects.equals(proto.msg, "Take")) {
                    Protocol contract = new Protocol();
                    contract.num = 2;
                    takeIn = true;
                    contract.msg = "Please choose your color [Clubs] [Diamonds] [Hearts] [Spades]";
                    players.get(index).channelWrite(players.get(index).client, contract);
                } else if ((Objects.equals(proto.msg, "Clubs") || Objects.equals(proto.msg, "Diamonds")
                        || Objects.equals(proto.msg, "Hearts") || Objects.equals(proto.msg, "Spades")) && takeIn)
                {
                    Protocol take = new Protocol();
                    String trump = proto.msg;
                    take.msg = "Player " + players.get(index).id + " take ! The color has the trump is " + trump + ".\n";
                    util.send_all(take);
                    isContract = true;
                    inGame();
                } else {
                    Protocol contract = new Protocol();
                    contract.num = 2;
                    takeIn = false;
                    contract.msg = "Please choose : Take or Pass";
                    players.get(index).channelWrite(players.get(index).client, contract);
                }
            }
        }
    }
    private void aTurn()
    {
        score.teamScore1 += 1;
        score.teamScore2 += 1;
        Protocol aTurn = new Protocol();
        if (nbTurn + 1 < 9) {
            aTurn.msg = "\nTurn " + nbTurn + " done. Player " + players.get(index).id + " mark : " + score.teamScore1 + "\nPlayer " + players.get(index).id + " engage turn " + (nbTurn + 1) + ".\n";
            util.send_all(aTurn);
        }
        else {
            aTurn.msg = "\nTurn " + nbTurn + " done. Player " + players.get(index).id + " mark : " + score.teamScore1 + "\nThis is the end of the game.\n";
            util.send_all(aTurn);
            for (Player its : players) {
                try {
                    its.ch.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        nbTurn += 1;
        manche = 0;
        inGame();
    }
    private void range()
    {
        Protocol range = new Protocol();
        range.num = 2;
        range.msg = "Please choose a card in your deck with her number.\n";
        players.get(index).channelWrite(players.get(index).client, range);
    }
    private void inGame() {
        Protocol turn = new Protocol();
        turn.msg = "Player " + players.get(index).id + " choose a card.\n";
        util.send_all(turn);
        Protocol contract = new Protocol();
        contract.num = 2;
        printDeck();
        contract.msg = "Please choose a card in your deck with her number.\n";
        players.get(index).channelWrite(players.get(index).client, contract);
    }
    private void printDeck() {
        for (int i = 0; i < 8; i++)
        {
            Protocol card = new Protocol();
            card.num = 1;
            card.posCard = i + 1;
            card.listCard = players.get(index).list_Card.get(i);
            players.get(index).channelWrite(players.get(index).client, card);
        }
    }
    public void run() {
        util.sleep();
        Protocol start = new Protocol();
        start.msg = "Welcome in a room game, good luck !\n";
        util.send_all(start);
        for (Player it : players) {
            if (it.id == 3 || it.id == 4) {
                Protocol team1 = new Protocol();
                team1.msg = "You are player " + it.id + " you play with player " + (it.id - 2);
                it.channelWrite(it.client, team1);
            } else {
                Protocol team2 = new Protocol();
                team2.msg = "You are player " + it.id + " you play with player " + (it.id + 2);
                it.channelWrite(it.client, team2);
            }
        }
        Protocol distrib = new Protocol();
        distrib.msg = "\nDistribution of cards in progress ...\n";
        util.send_all(distrib);
        util.sleep();
        distribution();
        contract();
    }

    private void contract() {
        Protocol turn = new Protocol();
        turn.msg = "Player " + players.get(index).id + " turn.\n";
        util.send_all(turn);
        Protocol contract = new Protocol();
        contract.num = 2;
        contract.msg = "Please choose : Take or Pass";
        players.get(index).channelWrite(players.get(index).client, contract);
    }

    private void distribution() {
        int y = 0;
        Protocol hand = new Protocol();
        hand.msg = "Your hand is :\n";
        for (Player it : players) {
            it.channelWrite(it.client, hand);
            for (int i = y; i < 8 + y; i++) {
                Protocol card = new Protocol();
                card.num = 1;
                card.posCard = -1;
                it.list_Card.add(deck.pile.get(i));
                card.listCard = deck.pile.get(i);
                it.channelWrite(it.client, card);
            }
            y += 8;
        }
        Protocol n = new Protocol();
        n.msg = "\n";
        util.send_all(n);
    }
}