package util;

import org.bukkit.Location;

public class WorldEditSelection {

    private Location pos1;
    private Location pos2;
    private boolean success;

    public WorldEditSelection() {

    }

    public WorldEditSelection(Location pos1, Location pos2, boolean success) {
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.success = success;
    }

    public Location getPos1() {
        return pos1;
    }

    public void setPos1(Location pos1) {
        this.pos1 = pos1;
    }

    public Location getPos2() {
        return pos2;
    }

    public void setPos2(Location pos2) {
        this.pos2 = pos2;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
