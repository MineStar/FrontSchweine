package de.minestar.frontschweine.data;

import org.bukkit.Location;

public class Waypoint implements Comparable<Waypoint> {
    private int ID;
    private BlockVector vector;
    private float speed;
    private int placeInLine = 0;

    /**
     * Constructor
     * 
     * @param the
     *            worldName
     * @param the
     *            x
     * @param the
     *            y
     * @param the
     *            z
     */
    public Waypoint(int ID, BlockVector vector, float speed, int placeInLine) {
        this.ID = ID;
        this.vector = vector;
        this.speed = speed;
        this.placeInLine = placeInLine;
    }

    /**
     * Constructor
     * 
     * @param the
     *            location
     */
    public Waypoint(int ID, Location location, float speed, int placeInLine) {
        this(ID, new BlockVector(location), speed, placeInLine);
    }

    /**
     * Get the place in line
     * 
     * @return the place in line
     */
    public int getPlaceInLine() {
        return placeInLine;
    }

    /**
     * Set the place in line
     * 
     */
    public void setPlaceInLine(int placeInLine) {
        this.placeInLine = placeInLine;
    }

    /**
     * Get the Location
     * 
     * @return the location
     */
    public Location getLocation() {
        return this.vector.getLocation();
    }

    /**
     * Get the ID
     * 
     * @return the ID
     */
    public int getID() {
        return ID;
    }

    /**
     * Get the speed
     * 
     * @return the speed
     */
    public float getSpeed() {
        return speed;
    }

    /**
     * Set the speed
     * 
     */
    public void setSpeed(float speed) {
        this.speed = speed;
    }

    /**
     * @return the x
     */
    public int getX() {
        return this.vector.getX();
    }

    /**
     * @return the y
     */
    public int getY() {
        return this.vector.getY();
    }

    /**
     * @return the z
     */
    public int getZ() {
        return this.vector.getZ();
    }

    /**
     * @return the worldName
     */
    public String getWorldName() {
        return this.vector.getWorldName();
    }

    /**
     * Get the BlockVector
     * 
     * @return the BlockVector
     */
    public BlockVector getVector() {
        return vector;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;

        if (obj instanceof Waypoint) {
            return this.equals((Waypoint) obj);
        }
        return false;
    }

    /**
     * Check if another BlockVector equals this BlockVector
     * 
     * @param other
     * @return <b>true</b> if the vectors are equal, otherwise <b>false</b>
     */
    public boolean equals(Waypoint other) {
        return this.vector.equals(other.getVector());
    }

    @Override
    public int hashCode() {
        return this.vector.hashCode();
    }

    @Override
    public String toString() {
        return "Waypoint={ ID : " + this.ID + " ; " + this.vector.toString() + " ; Speed : " + this.speed + " }";
    }

    @Override
    public int compareTo(Waypoint other) {
        return this.hashCode() - other.hashCode();
    }
}
