package MMU;

public class MemoryManager {

    private static FrameTableEntry[] frameTable = new FrameTableEntry[32];
    private static byte[] RAM = new byte[256];
    private static final byte PAGE_SIZE = 8;


    private static short byteToUShort (byte c) {

        if (c < 0)
            return (short)(c + 128);
        else
            return (short)((c + 128) & 0xff);
    }


    public static void init() {

        for (byte r = 0; r < frameTable.length; ++r) {

            frameTable[r] = FrameTableEntry();
        }

        for (short g = 0; g < 256; ++g) {

            RAM[g] = 0;
        }
    }


    public static byte freeFrames() {

        byte counter = 0;

        for (byte x = 0; x < frameTable.length; ++x) {

            if (frameTable[x].isTaken() == false) {
                ++counter;
            }
        }

        return counter;
    }


    public static byte neededFrames(PCB p) {

        if (p.code.size() % 8 == 0)
            return (byte)(p.code.size()/PAGE_SIZE);
        else
            return (byte)( (p.code.size()/PAGE_SIZE) + 1);
    }


    public static short read (PCB p, short adr) {

        if (p.pageTable.isEmpty()) {

            System.out.println("Podanemu procesowi nie przydzielono żadnej pamięci! Najpierw zaalokuj ją za pomocą odpowiedniego polecenia.\n");
        }

        else {

            short physAdr = (short)(p.pageTable.get(adr / PAGE_SIZE) + (adr - ((adr / PAGE_SIZE) * 8)));

            if (frameTable[physAdr / PAGE_SIZE].getPID() == p.getPID()) {

                return byteToUShort(RAM[physAdr]);

            } else {

                System.out.println("Podany proces nie ma dostępu do tej ramki pamięci!\n");
            }
        }
    }


    public static void write (PCB p, short adr, short data) {

        if (p.pageTable.isEmpty()) {

            System.out.println("Podanemu procesowi nie przydzielono żadnej pamięci! Najpierw zaalokuj ją za pomocą odpowiedniego polecenia.\n");
        }

        else {

            short physAdr = (short)(p.pageTable.get(adr / PAGE_SIZE) + (adr - ((adr / PAGE_SIZE) * 8)));

            if (frameTable[physAdr / PAGE_SIZE].getPID() == p.getPID()) {

                RAM[physAdr] = (byte)(data - 128);

            } else {

                System.out.println("Podany proces nie ma dostępu do tej ramki pamięci!\n");
            }
        }
    }


    public static void allocate (PCB p) {


        if (neededFrames(p) > 32) {

            System.out.println("Za duży rozmiar programu! (ponad 32 bajty)\n");
        }


        else if (neededFrames(p) > freeFrames() ) {

            System.out.println("Za mało pamięci, by załadować program!\n");
        }


        else {

            byte page = 0;

            for (byte w = 0; w < frameTable.length; ++w) {

                if (frameTable[w].isTaken() == false) {

                    frameTable[w].setTaken(true);
                    frameTable[w].setPID( p.getPID() );
                    p.pageTable.addElement( (short)(w*PAGE_SIZE) );
                    ++page;
                }

                if ( page == neededFrames(p) ) break;
            }

            for (short t = 0; t < p.code.size(); ++t) {

                write( p, t, p.code.get(t) );
            }
        }
    }


    public static void deallocate (PCB p) {

        if (p.pageTable.isEmpty()) {

            System.out.println("Nie ma czego dealokować. Proces nie ma żadnej przypisanej pamięci.\n");
        }

        else {

            byte e = (byte)(p.pageTable.size() - 1);

            while (!p.pageTable.isEmpty()) {

                for (byte f = 0; f < PAGE_SIZE; ++f) {

                    RAM[p.pageTable.get(e) + f] = 0;
                }

                frameTable[p.pageTable.get(e)].setTaken(false);
                frameTable[p.pageTable.get(e)].setPID(-1);
                p.pageTable.removeElementAt(e);
                --e;
            }
        }
    }
}


/*public static void Main (String[] args) {

    PCB pcb = PCB();
}*/