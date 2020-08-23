package pw.cdmi.starlink.core.orbit;

/**
 * Encapsulates a simple 4-component vector
 */
public class Vector {

    /// <summary>
    /// Standard constructor.
    /// </summary>
    public Vector()
    {
    }

    /// <summary>
    /// Creates a new vector from an existing vector.
    /// </summary>
    /// <param name="v">The existing vector to copy.</param>
    public Vector(Vector v)
    {
        this(v.X, v.Y, v.Z, v.W);
    }

    /// <summary>
    /// Creates a new vector with the given XYZ components.
    /// </summary>
    /// <param name="x">The X component.</param>
    /// <param name="y">The Y component.</param>
    /// <param name="z">The Z component.</param>
    public Vector(double x, double y, double z)
    {
        X = x;
        Y = y;
        Z = z;
    }

    /// <summary>
    /// Creates a new vector with the given XYZ-W components.
    /// </summary>
    /// <param name="x">The X component.</param>
    /// <param name="y">The Y component.</param>
    /// <param name="z">The Z component.</param>
    /// <param name="w">The W component.</param>
    public Vector(double x, double y, double z, double w)
    {
        X = x;
        Y = y;
        Z = z;
        W = w;
    }

    public double X;
    public double Y;
    public double Z;
    public double W;


    /// <summary>
    /// Multiply each component in the vector by a given factor.
    /// </summary>
    /// <param name="factor">The factor.</param>
    public void Mul(double factor)
    {
        X *= factor;
        Y *= factor;
        Z *= factor;
        W *= Math.abs(factor);
    }

    /// <summary>
    /// Subtracts a vector from this vector.
    /// </summary>
    /// <param name="vec">The vector to subtract.</param>
    public void Sub(Vector vec)
    {
        X -= vec.X;
        Y -= vec.Y;
        Z -= vec.Z;
        W -= vec.W;
    }

    /// <summary>
    /// Calculates the angle, in radians, between this vector and another.
    /// </summary>
    /// <param name="vec">The second vector.</param>
    /// <returns>
    /// The angle between the two vectors, in radians.
    /// </returns>
    public double Angle(Vector vec)
    {
        return Math.acos(Dot(vec) / (Magnitude() * vec.Magnitude()));
    }

    /// <summary>
    /// Calculates the magnitude of the vector.
    /// </summary>
    /// <returns>The vector magnitude.</returns>
    public double Magnitude()
    {
        return Math.sqrt((X * X) + (Y * Y) + (Z * Z));
    }

    /// <summary>
    /// Calculates the dot product of this vector and another.
    /// </summary>
    /// <param name="vec">The second vector.</param>
    /// <returns>The dot product.</returns>
    public double Dot(Vector vec)
    {
        return (X * vec.X) + (Y * vec.Y) + (Z * vec.Z);
    }

    /// <summary>
    /// Calculates the distance between two vectors as points in XYZ space.
    /// </summary>
    /// <param name="vec">The second vector.</param>
    /// <returns>The calculated distance.</returns>
    public double Distance(Vector vec)
    {
        return Math.sqrt(Math.pow(X - vec.X, 2.0) +
                Math.pow(Y - vec.Y, 2.0) +
                Math.pow(Z - vec.Z, 2.0));
    }

    /// <summary>
    /// Rotates the XYZ coordinates around the X-axis.
    /// </summary>
    public void RotateX(double radians)
    {
        double y = Y;

        Y = (Math.cos(radians) * y) - (Math.sin(radians) * Z);
        Z = (Math.sin(radians) * y) + (Math.cos(radians) * Z);
    }

    /// <summary>
    /// Rotates the XYZ coordinates around the Y-axis.
    /// </summary>
    public void RotateY(double radians)
    {
        double x = X;

        X = ( Math.cos(radians) * x) + (Math.sin(radians) * Z);
        Z = (-Math.sin(radians) * x) + (Math.cos(radians) * Z);
    }

    /// <summary>
    /// Rotates the XYZ coordinates around the Z-axis.
    /// </summary>
    public void RotateZ(double radians)
    {
        double x = X;

        X = (Math.cos(radians) * x) - (Math.sin(radians) * Y);
        Y = (Math.sin(radians) * x) + (Math.cos(radians) * Y);
    }

    /// <summary>
    /// Offsets the XYZ coordinates.
    /// </summary>
    public void Translate(double x, double y, double z)
    {
        X += x;
        Y += y;
        Z += z;
    }
}
