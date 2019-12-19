package net.teozfrank.ultimatevotes.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.*;
import net.teozfrank.ultimatevotes.main.UltimateVotes;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * Origional Author: teozfrank
 * Date: 23/09/13
 * Time: 12:12
 * Project: UltimateVotes
 * -----------------------------
 * Removing this header is in breach of the license agreement,
 * please do not remove, move or edit it in any way.
 * -----------------------------
 */
public class ScoreBoardManager {

    private UltimateVotes plugin;
    private ScoreboardManager scoreboardManager;
    private Scoreboard scoreboard;
    private Team team;
    private Objective objective, objective2;
    public HashMap<String, Integer> topVoters;

    public ScoreBoardManager(UltimateVotes plugin) {
        this.plugin = plugin;
        this.setupScoreBoard();
    }

    private void setupScoreBoard() {
        this.topVoters = new HashMap<String, Integer>();
        this.scoreboardManager = Bukkit.getScoreboardManager();
        this.scoreboard = scoreboardManager.getNewScoreboard();
        this.team = scoreboard.registerNewTeam("topvoters");
        team.setDisplayName("topvoters");
        this.objective = scoreboard.registerNewObjective("player1", "dummy");

        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName("Todays Top 5 Voters");
        Score score = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.GREEN + "loading...")); //Get a fake offline player
        score.setScore(0);
    }

    public void updateScoreBoard(HashMap<String, Integer> topVoters) {


    }

    public Scoreboard getScoreboard() {
        return this.scoreboard;
    }

    public Objective getObjective() {
        return this.objective;
    }


}
