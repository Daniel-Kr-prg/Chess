package chess.model;

import java.util.ArrayList;

public class Vector implements Comparable<Vector> {
    public int X;
    public int Y;
    public Vector(int X, int Y)
    {
        this.X = X;
        this.Y = Y;
    }
    /**
     * Add vector to vector
     */
    public Vector add(int X, int Y)
    {
        return new Vector(this.X + X, this.Y + Y);
    }
    /**
     * Add vector to vector
     */
    public Vector add(Vector v)
    {
        return new Vector(this.X + v.X, this.Y + v.Y);
    }
    /**
     * compare vectors
     */
    @Override
    public int compareTo(Vector v)
    {
        if (this.X == v.X && this.Y == v.Y)
            return 0;
        if (X*X + Y*Y > v.X * v.X + v.Y * v.Y) {
            return 1;
        }
        else {
            return -1;
        }

    }
    /**
     * check if the v vector is in the list
     */
    public static boolean contains(ArrayList<Vector> list, Vector v)
    {
        if (list == null || list.size() == 0)
            return false;
        for (Vector fromList : list)
        {
            if (fromList.compareTo(v) == 0)
                return true;
        }
        return false;
    }

    /**
     * check if the vector is in the range by 2 coordinates
     */
    public static boolean inRange(Vector v, int range_min, int range_max)
    {
        if (v.X > range_min && v.X < range_max && v.Y >  range_min && v.Y < range_max)
            return true;
        return false;
    }
}
