package server;

import java.util.ArrayList;
import java.util.List;

class Room {
    List<Player> list_player;
    int nbPlayers = 0;

    Room()
    {
        list_player = new ArrayList<Player>();
    }
    void check_connected() {
        nbPlayers = 0;
        List<Integer> list_id = new ArrayList<Integer>();
        int i = 0;
        for (Player it : list_player) {
            if (it.connected)
                nbPlayers += 1;
            else
                list_id.add(i);
            i += 1;
        }
        for (int in : list_id)
            list_player.remove(in);
    }
    void removeAll() {
        for (int i = 3; i >= 0; i--)
            list_player.remove(i);
    }
}
