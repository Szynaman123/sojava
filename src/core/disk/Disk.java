package core.disk;

public class Disk
{
    public static final int BN_SIZE = 32;
    private static byte[][] Disk0 = new byte[9][32];
    private static byte[][] Disk1 = new byte[32][32];

    public static byte[] ReadINODE(int blockIndex)
    {
        //System.out.println("core.disk.Disk.ReadDATA() - reading inode from " + blockIndex + " disk block");
        return Disk0[blockIndex];
    }

    public static void WriteINODE(int blockIndex, byte[] block)
    {
        //System.out.println("core.disk.Disk.WriteINODE() - writing inode at " + blockIndex + " disk block");
        Disk0[blockIndex] = block;
    }

    public static byte[] ReadDATA(int blockIndex)
    {
        //System.out.println("core.disk.Disk.ReadDATA() - reading data from " + blockIndex + " disk block");
        return Disk1[blockIndex];
    }

    public static void WriteDATA(int blockIndex, byte[] block)
    {
        //System.out.println("Write.ReadDATA() - writing data at " + blockIndex + " disk block");
        Disk1[blockIndex] = block;
    }
}
