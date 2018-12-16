package MMU;

public class FrameTableEntry {

    private boolean taken;
    private byte PID;

    public boolean isTaken() {return taken;}


    public void setTaken(boolean taken) {this.taken = taken;}


    public byte getPID() {return PID;}


    public void setPID(byte PID) {this.PID = PID;}


    public FrameTableEntry() {

        this.taken = false;
        this.PID = (-1);
    }


    public FrameTableEntry(boolean t, byte p) {

        this.taken = t;
        this.PID = p;
    }
}
