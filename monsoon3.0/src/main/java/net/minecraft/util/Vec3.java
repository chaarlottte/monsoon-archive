package net.minecraft.util;

public class Vec3
{
    /** X coordinate of Vec3D */
    public final double x;

    /** Y coordinate of Vec3D */
    public final double y;

    /** Z coordinate of Vec3D */
    public final double z;

    public Vec3(double x, double y, double z)
    {
        if (x == -0.0D)
        {
            x = 0.0D;
        }

        if (y == -0.0D)
        {
            y = 0.0D;
        }

        if (z == -0.0D)
        {
            z = 0.0D;
        }

        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3(Vec3i p_i46377_1_)
    {
        this((double)p_i46377_1_.getX(), (double)p_i46377_1_.getY(), (double)p_i46377_1_.getZ());
    }

    /**
     * Returns a new vector with the result of the specified vector minus this.
     */
    public Vec3 subtractReverse(Vec3 vec)
    {
        return new Vec3(vec.x - this.x, vec.y - this.y, vec.z - this.z);
    }

    /**
     * Normalizes the vector to a length of 1 (except if it is the zero vector)
     */
    public Vec3 normalize()
    {
        double d0 = (double)MathHelper.sqrt_double(this.x * this.x + this.y * this.y + this.z * this.z);
        return d0 < 1.0E-4D ? new Vec3(0.0D, 0.0D, 0.0D) : new Vec3(this.x / d0, this.y / d0, this.z / d0);
    }

    public double dotProduct(Vec3 vec)
    {
        return this.x * vec.x + this.y * vec.y + this.z * vec.z;
    }

    /**
     * Returns a new vector with the result of this vector x the specified vector.
     */
    public Vec3 crossProduct(Vec3 vec)
    {
        return new Vec3(this.y * vec.z - this.z * vec.y, this.z * vec.x - this.x * vec.z, this.x * vec.y - this.y * vec.x);
    }

    public Vec3 subtract(Vec3 vec)
    {
        return this.subtract(vec.x, vec.y, vec.z);
    }

    public Vec3 subtract(double x, double y, double z)
    {
        return this.addVector(-x, -y, -z);
    }

    public Vec3 add(Vec3 vec)
    {
        return this.addVector(vec.x, vec.y, vec.z);
    }

    /**
     * Adds the specified x,y,z vector components to this vector and returns the resulting vector. Does not change this
     * vector.
     */
    public Vec3 addVector(double x, double y, double z)
    {
        return new Vec3(this.x + x, this.y + y, this.z + z);
    }

    /**
     * Euclidean distance between this and the specified vector, returned as double.
     */
    public double distanceTo(Vec3 vec)
    {
        double d0 = vec.x - this.x;
        double d1 = vec.y - this.y;
        double d2 = vec.z - this.z;
        return (double)MathHelper.sqrt_double(d0 * d0 + d1 * d1 + d2 * d2);
    }

    /**
     * The square of the Euclidean distance between this and the specified vector.
     */
    public double squareDistanceTo(Vec3 vec)
    {
        double d0 = vec.x - this.x;
        double d1 = vec.y - this.y;
        double d2 = vec.z - this.z;
        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    /**
     * Returns the length of the vector.
     */
    public double lengthVector()
    {
        return (double)MathHelper.sqrt_double(this.x * this.x + this.y * this.y + this.z * this.z);
    }

    /**
     * Returns a new vector with x value equal to the second parameter, along the line between this vector and the
     * passed in vector, or null if not possible.
     */
    public Vec3 getIntermediateWithXValue(Vec3 vec, double x)
    {
        double d0 = vec.x - this.x;
        double d1 = vec.y - this.y;
        double d2 = vec.z - this.z;

        if (d0 * d0 < 1.0000000116860974E-7D)
        {
            return null;
        }
        else
        {
            double d3 = (x - this.x) / d0;
            return d3 >= 0.0D && d3 <= 1.0D ? new Vec3(this.x + d0 * d3, this.y + d1 * d3, this.z + d2 * d3) : null;
        }
    }

    /**
     * Returns a new vector with y value equal to the second parameter, along the line between this vector and the
     * passed in vector, or null if not possible.
     */
    public Vec3 getIntermediateWithYValue(Vec3 vec, double y)
    {
        double d0 = vec.x - this.x;
        double d1 = vec.y - this.y;
        double d2 = vec.z - this.z;

        if (d1 * d1 < 1.0000000116860974E-7D)
        {
            return null;
        }
        else
        {
            double d3 = (y - this.y) / d1;
            return d3 >= 0.0D && d3 <= 1.0D ? new Vec3(this.x + d0 * d3, this.y + d1 * d3, this.z + d2 * d3) : null;
        }
    }

    /**
     * Returns a new vector with z value equal to the second parameter, along the line between this vector and the
     * passed in vector, or null if not possible.
     */
    public Vec3 getIntermediateWithZValue(Vec3 vec, double z)
    {
        double d0 = vec.x - this.x;
        double d1 = vec.y - this.y;
        double d2 = vec.z - this.z;

        if (d2 * d2 < 1.0000000116860974E-7D)
        {
            return null;
        }
        else
        {
            double d3 = (z - this.z) / d2;
            return d3 >= 0.0D && d3 <= 1.0D ? new Vec3(this.x + d0 * d3, this.y + d1 * d3, this.z + d2 * d3) : null;
        }
    }

    public String toString()
    {
        return "(" + this.x + ", " + this.y + ", " + this.z + ")";
    }

    public Vec3 rotatePitch(float pitch)
    {
        float f = MathHelper.cos(pitch);
        float f1 = MathHelper.sin(pitch);
        double d0 = this.x;
        double d1 = this.y * (double)f + this.z * (double)f1;
        double d2 = this.z * (double)f - this.y * (double)f1;
        return new Vec3(d0, d1, d2);
    }

    public Vec3 rotateYaw(float yaw)
    {
        float f = MathHelper.cos(yaw);
        float f1 = MathHelper.sin(yaw);
        double d0 = this.x * (double)f + this.z * (double)f1;
        double d1 = this.y;
        double d2 = this.z * (double)f - this.x * (double)f1;
        return new Vec3(d0, d1, d2);
    }
}
