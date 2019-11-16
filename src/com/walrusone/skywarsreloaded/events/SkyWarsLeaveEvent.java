package com.walrusone.skywarsreloaded.events;

import com.walrusone.skywarsreloaded.game.GameMap;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SkyWarsLeaveEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    private Player player;
    private GameMap map;

    public SkyWarsLeaveEvent(Player p, GameMap game) {
        this.player = p;
        this.map = game;
    }

    public Player getPlayer() { return player; }
    public GameMap getGame() { return map; }

}
