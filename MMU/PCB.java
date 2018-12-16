package MMU;

import java.util.Vector;

public class PCB {

    private byte PID;
    public Vector <Short> pageTable;
    public Vector <Short> code;


    public byte getPID() {return PID;}


    public void setPID (byte PID) {this.PID = PID;}



    /*public PCB() {

        this.PID = 1;
        this.pageTable = new Vector <Short>;
        this.code = new Vector <Short>;

        this.code.addElement( (short)78 );
        this.code.addElement( (short)255 );
        this.code.addElement( (short)0 );
        this.code.addElement( (short)47 );
        this.code.addElement( (short)128 );
    }*/
}
