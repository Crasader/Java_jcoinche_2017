package server;

import common.Protocol;
import java.util.List;
import java.util.concurrent.TimeUnit;

class Utils {
    private List<Player> players;

    Utils(List<Player> players) {
        this.players = players;
    }

    void sleep() {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    void send_all(Protocol proto) {
        for (Player its : players) {
            its.channelWrite(its.client, proto);
        }
    }

    void checkDeath() {
        for (Player it : players) {
            if (!it.connected)
                death(it);
        }
    }
    private void death(Player it) {
        Protocol disconnect = new Protocol();
        disconnect.msg = "A player is disconnected, you have also been disconnected !\n";
        send_all(disconnect);
        for (Player its : players) {
            if (it != its) {
                try {
                    its.ch.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}