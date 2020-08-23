package pw.cdmi.starlink.core.orbit;

public class Ecef {
    public Vector position;
    public Vector velocity;
    
    public Ecef(Vector pos)
    {
        this(pos, new Vector());
    }
    
    public Ecef(Vector pos, Vector vel)
    {
        position = pos;
        velocity = vel;
    }
    
    public Ecef(Ecef ecef)
    {
        position = new Vector(ecef.position);
        velocity = new Vector(ecef.velocity);
    }
    
    public Vector getPosition() {
    	return this.position;
    }
    
}
