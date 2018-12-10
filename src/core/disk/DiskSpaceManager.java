package core.disk;

import java.util.BitSet;

public class DiskSpaceManager
{
    private static BitSet table = new BitSet(41);
    private static int FreeUnitsINODE;
    private static int FreeUnitsData;

    public static int getFreeUnitsINODE()
    {
        return FreeUnitsINODE;
    }

    public static int getFreeUnitsData()
    {
        return FreeUnitsData;
    }

    public static void Init()
    {
        table.set(0,41, true);
        CalculateFreeBlocks();
    }

    public static byte AllocateINODE() throws NoFreeDiskSpaceException
    {
        for(int x = 0; x < 9; x++)
        {
            if(table.get(x))
            {
                table.set(x, false);
                System.out.println("core.disk.DiskSpaceManager.AllocateINODE() - 1 inode allocated at " + x);
                return (byte) x;
            }
        }
        throw new NoFreeDiskSpaceException();
    }

    public static byte AllocateDateBlock() throws NoFreeDiskSpaceException
    {
        for(int x = 9; x < 41; x++)
        {
            if(table.get(x))
            {
                table.set(x, false);
                System.out.println("core.disk.DiskSpaceManager.AllocateDateBlock() - 1 date block allocated at " + (x - 9));
                return (byte) (x - 9);
            }
        }
        throw new NoFreeDiskSpaceException();
    }

    public static void DeallocateINODE(byte loc)
    {
        System.out.println("core.disk.DiskSpaceManager.DeallocateINODE() 1 inode deallocate at " + loc);
        table.set(loc, true);
    }

    public static void DeallocateBLOCKDATA(short loc)
    {
        System.out.println("core.disk.DiskSpaceManager.DeallocateDateBlock() - 1 date block deallocate at " + (loc));
        table.set(9 + loc, true);
    }

    private static void CalculateFreeBlocks()
    {
        FreeUnitsINODE = FreeUnitsData = 0;
        for(int x = 0; x < 9; x++)
        {
            if(table.get(x))
            {
                FreeUnitsINODE++;
            }
        }
        for(int x = 9; x < 41; x++)
        {
            if(table.get(x))
            {
                FreeUnitsData++;
            }
        }
    }
}
