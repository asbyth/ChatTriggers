package com.chattriggers.ctjs.minecraft.wrappers;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class TabList {
    /**
     * Gets names set in scoreboard objectives (useful for newer games)
     *
     * @return The formatted names
     */
    public static List<String> getNamesByObjectives() {
        List<String> tabNames = new ArrayList<>();
        try {
            Scoreboard scoreboard = World.getWorld().getScoreboard();
            ScoreObjective sidebarObjective = scoreboard.getObjectiveInDisplaySlot(0);
            Collection<Score> scores = scoreboard.getSortedScores(sidebarObjective);
            for (Score score : scores) {
                ScorePlayerTeam team = scoreboard.getPlayersTeam(score.getPlayerName());
                String name = ScorePlayerTeam.formatPlayerName(team, score.getPlayerName());
                tabNames.add(name);
            }
        } catch (Exception e) {
            return new ArrayList<>();
        }
        return tabNames;
    }

    /**
     * Gets all names in tabs without formatting
     *
     * @return the unformatted names
     */
    public static List<String> getNames() {
        List<String> names = new ArrayList<>();

        Ordering<NetworkPlayerInfo> tab = Ordering.from(new PlayerComparator());

        if (Player.getPlayer() == null) return names;

        NetHandlerPlayClient nethandlerplayclient = Player.getPlayer().connection;
        List<NetworkPlayerInfo> list = tab.sortedCopy(nethandlerplayclient.getPlayerInfoMap());

        for (NetworkPlayerInfo player : list) {
            names.add(player.getGameProfile().getName());
        }

        return names;
    }

    @SideOnly(Side.CLIENT)
    static class PlayerComparator implements Comparator<NetworkPlayerInfo> {
        private PlayerComparator() { }

        public int compare(NetworkPlayerInfo playerOne, NetworkPlayerInfo playerTwo) {
            ScorePlayerTeam teamOne = playerOne.getPlayerTeam();
            ScorePlayerTeam teamTwo = playerTwo.getPlayerTeam();

            return ComparisonChain
                    .start()
                    .compareTrueFirst(
                            playerOne.getGameType() != GameType.SPECTATOR,
                            playerTwo.getGameType() != GameType.SPECTATOR
                    ).compare(
                            teamOne != null ? teamOne.getName() : "",
                            teamTwo != null ? teamTwo.getName() : ""
                    ).compare(
                            playerOne.getGameProfile().getName(),
                            playerTwo.getGameProfile().getName()
                    ).result();
        }
    }
}
