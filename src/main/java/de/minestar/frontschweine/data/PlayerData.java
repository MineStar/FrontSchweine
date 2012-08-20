package de.minestar.frontschweine.data;

public class PlayerData {
    private final String name;
    private PlayerState state;
    private Line line = null;
    private Waypoint waypoint = null;
    private boolean isBackwards = false;

    public PlayerData(String playerName) {
        this.name = playerName;
        this.state = PlayerState.NORMAL;
    }

    public void update(Line line, Waypoint waypoint, boolean isBackwards) {
        this.line = line;
        this.waypoint = waypoint;
        this.isBackwards = isBackwards;
    }

    public Waypoint getWaypoint() {
        return waypoint;
    }

    public Line getLine() {
        return line;
    }

    public boolean isBackwards() {
        return isBackwards;
    }

    /**
     * @return the state
     */
    public PlayerState getState() {
        return state;
    }

    /**
     * @param state
     *            the state to set
     */
    public void setState(PlayerState state) {
        this.state = state;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
}
