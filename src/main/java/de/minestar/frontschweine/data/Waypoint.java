package de.minestar.frontschweine.data;

import org.bukkit.Location;

public class Waypoint implements Comparable<Waypoint> {
    private int ID;
    private BlockVector vector;
    private float speed;

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
    public Waypoint(int ID, BlockVector vector, float speed) {
        this.ID = ID;
        this.vector = vector;
        this.speed = speed;
    }

    /**
     * Constructor
     * 
     * @param the
     *            location
     */
    public Waypoint(int ID, Location location, float speed) {
        this(ID, new BlockVector(location), speed);
    }

    /**
     * Update the BlockVector
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
    public void update(int ID, BlockVector vector, float speed) {
        this.ID = ID;
        this.vector = vector;
        this.speed = speed;
    }

    /**
     * Update the BlockVector
     * 
     * @param location
     */
    public void update(int ID, Location location, float speed) {
        this.update(ID, new BlockVector(location), speed);
    }

    /**
     * Get the Location
     * 
     * @return the location
     */
    public Location getLocation() {
        return this.vector.getLocation();
    }

    public int getID() {
        return ID;
    }

    public float getSpeed() {
        return speed;
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
    public Waypoint clone() {
        return new Waypoint(this.ID, this.vector, this.speed);
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
