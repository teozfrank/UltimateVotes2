package net.teozfrank.ultimatevotes.util;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.util.Vector;
import net.teozfrank.ultimatevotes.main.UltimateVotes;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Origional Author: teozfrank
 * Date: 26/09/13
 * Time: 20:59
 * Project: UltimateVotes
 * -----------------------------
 * Removing this header is in breach of the license agreement,
 * please do not remove, move or edit it in any way.
 * -----------------------------
 */
public class SignManager {

    private UltimateVotes plugin;

    public SignManager(UltimateVotes plugin){
        this.plugin = plugin;
    }

    public void loadSigns() {

    }

    public boolean isWallSign(Location location) {
        Block block = location.getBlock();


        /*if(block.getBlockData().getAsString().contains("WallSign") || block.getBlockData().getAsString().contains("wall_sign")) {
            return true;
        }*/
        //TODO find universal way to check is wall sign
        return false;
    }

    public boolean isWallSign(Block block) {

        /*if(block.getBlockData().getAsString().contains("WallSign") || block.getBlockData().getAsString().contains("wall_sign")) {
            return true;
        }*/
        //TODO find universal way to check is wall sign
        return false;
    }

    public boolean isRegionAllWallSigns(Location pos1, Location pos2) {
        String world = pos1.getWorld().getName();
        int totalBlockCount = 0;
        int totalSignCount = 0;

            Vector max = Vector.getMaximum(pos1.toVector(), pos2.toVector());
            Vector min = Vector.getMinimum(pos1.toVector(), pos2.toVector());
            for (int i = min.getBlockX(); i <= max.getBlockX();i++) {
                for (int j = min.getBlockY(); j <= max.getBlockY(); j++) {
                    for (int k = min.getBlockZ(); k <= max.getBlockZ();k++) {
                        Block block = Bukkit.getServer().getWorld(world).getBlockAt(i,j,k);
                        if(isWallSign(block)) {
                            totalSignCount++;
                        }
                        totalBlockCount++;
                    }
                }
            }

        if(totalBlockCount == totalSignCount && totalBlockCount == 9) {
            return true;
        }

        return false;
    }

    public Location getWallSignLocation(WallOfSignsPositions wallOfSignsLocationsIn) {
        FileManager fm = plugin.getFileManager();
        Location pos1 = fm.getWallSignsPos1();
        Location pos2 = fm.getWallSignsPos2();

        World world = pos1.getWorld();
        double pos1x = pos1.getBlockX();
        double pos1y = pos1.getBlockY();
        double pos1z = pos1.getBlockZ();

        if (wallOfSignsLocationsIn.equals(WallOfSignsPositions.CENTER)) {
           return new Location(world, pos1x, pos1y - 1, pos1z - 1);
        } else if (wallOfSignsLocationsIn.equals(WallOfSignsPositions.TOP_MIDDLE)) {
           return new Location(world, pos1x, pos1y, pos1z - 1);
        } else if (wallOfSignsLocationsIn.equals(WallOfSignsPositions.BOTTOM_MIDDLE)) {
           return new Location(world, pos1x , pos1y - 2, pos1z - 1);
        } else if (wallOfSignsLocationsIn.equals(WallOfSignsPositions.BOTTOM_RIGHT)) {
           return new Location(world, pos1x, pos1y - 2, pos1z);
        } else if (wallOfSignsLocationsIn.equals(WallOfSignsPositions.BOTTOM_LEFT)) {
           return new Location(world, pos1x, pos1y - 2, pos1z - 2);
        } else {
           return null;
        }
    }

    /**
     * Method to update a sign in a given location
     * @param signLoc Signs Location in the world
     * @param line1   sets Signs first line
     * @param line2   sets Signs second line
     * @param line3   sets Signs third line
     * @param line4   sets Signs fourth line
     */
    public void updateSign(Location signLoc, String line1, String line2, String line3, String line4) {

        try {
            Block block = Bukkit.getWorld(signLoc.getWorld().getName()).getBlockAt(signLoc);


            if (isWallSign(block)) {
                Sign sign = (Sign) block.getState();
                sign.setLine(0, line1);
                sign.setLine(1, line2);
                sign.setLine(2, line3);
                sign.setLine(3, line4);
                sign.update();
            }

        } catch (Exception e) { e.printStackTrace(); }

    }

    public void updateTopVotersOnWall() {
        VoteManager vm = plugin.getVoteManager();
        FileManager fm = plugin.getFileManager();

        LinkedHashMap<String, Integer> topMonthlyVotes = vm.getMonthlyVotes();

        if(plugin.isDebugEnabled()) {
            SendConsoleMessage.debug("Top monthly votes size: "+topMonthlyVotes.size());
        }

        try {
            this.clearSignWall();

            if(topMonthlyVotes.size() == 0) {
                updateSign(getWallSignLocation(WallOfSignsPositions.TOP_MIDDLE),
                        ChatColor.DARK_BLUE +"No", ChatColor.DARK_BLUE + "Votes",
                        ChatColor.DARK_BLUE + "Exist",ChatColor.DARK_BLUE + ":(");
                showCreditsSign(WallOfSignsPositions.BOTTOM_RIGHT);
                return;
            }

            displaySignWall(topMonthlyVotes);
        } catch (NullPointerException e) {
            if(plugin.isDebugEnabled()) {
                SendConsoleMessage.debug("Wall of Signs not set, not loading.");
            }

        }

    }

    private void displaySignWall(LinkedHashMap<String, Integer> topMonthlyVotes) {
        FileManager fm = plugin.getFileManager();
        MessageManager mm = plugin.getMessageManager();
        Long lastUpdate = plugin.getLastVotesUpdate();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        String lastUpdateTime = df.format(lastUpdate);
        String[] names = new String[10];//array to hold the top 10 voter names

        int index = 0;

        for (Map.Entry<String, Integer> values : topMonthlyVotes.entrySet()) {
            String signwallFormat = fm.getSignWallLayout();
            signwallFormat = signwallFormat.replaceAll("%playername%", String.valueOf(values.getKey()));
            signwallFormat = signwallFormat.replaceAll("%player%", String.valueOf(values.getKey()));
            signwallFormat = signwallFormat.replaceAll("%position%", String.valueOf(index+1));
            names[index] = signwallFormat;//populate the array with our name value
            index++; //increment the index by one
        }

        Location topMiddleSignLocation = getWallSignLocation(WallOfSignsPositions.TOP_MIDDLE);
        Location centerSignLocation = getWallSignLocation(WallOfSignsPositions.CENTER);
        Location bottomMiddleSignLocation = getWallSignLocation(WallOfSignsPositions.BOTTOM_MIDDLE);
        Location bottomLeftSignLocation = getWallSignLocation(WallOfSignsPositions.BOTTOM_LEFT);
        Location bottomRightSignLocation = getWallSignLocation(WallOfSignsPositions.BOTTOM_RIGHT);

        if(plugin.isDebugEnabled()) {
            SendConsoleMessage.debug("Top Middle Sign Location: " + topMiddleSignLocation);
            SendConsoleMessage.debug("Center Sign Location: " + centerSignLocation);
            SendConsoleMessage.debug("Bottom Middle Sign Location: " + bottomMiddleSignLocation);
            SendConsoleMessage.debug("Bottom Left Sign Location: " + bottomLeftSignLocation);
            SendConsoleMessage.debug("Bottom Right Sign Location: " + bottomRightSignLocation);
        }


        updateSign(topMiddleSignLocation, mm.getSignWallTitle1(), mm.getSignWallTitle2(), names[0], names[1]);

        updateSign(centerSignLocation, names[2], names[3], names[4], names[5]);

        updateSign(bottomMiddleSignLocation, names[6], names[7], names[8], names[9]);

        updateSign(bottomLeftSignLocation, "", mm.getSignWallLastUpdated1(),
                mm.getSignWallLastUpdated2(), ChatColor.DARK_BLUE +lastUpdateTime);

        showCreditsSign(WallOfSignsPositions.BOTTOM_RIGHT);
    }

    public void showCreditsSign(WallOfSignsPositions wallOfSignsPositionsIn) {
        if(plugin.isDebugEnabled()) {
            SendConsoleMessage.debug("Credits sign: "+getWallSignLocation(wallOfSignsPositionsIn));
        }
        updateSign(getWallSignLocation(wallOfSignsPositionsIn), "",
                "",
                UltimateVotes.getPluginPrefix(), ChatColor.DARK_BLUE + "V"+ UltimateVotes.getPluginVersion());
    }

    public void clearSignWall() {
        updateSign(getWallSignLocation(WallOfSignsPositions.CENTER),"","","","");
        updateSign(getWallSignLocation(WallOfSignsPositions.TOP_MIDDLE),"","","","");
        updateSign(getWallSignLocation(WallOfSignsPositions.BOTTOM_RIGHT),"","","","");
        updateSign(getWallSignLocation(WallOfSignsPositions.BOTTOM_RIGHT),"","","","");
        updateSign(getWallSignLocation(WallOfSignsPositions.BOTTOM_LEFT),"","","","");
    }


}
