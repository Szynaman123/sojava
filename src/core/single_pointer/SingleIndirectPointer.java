package core.single_pointer;

import java.util.BitSet;

public class SingleIndirectPointer
{
    private static final int INDIRECT_PTRS_AMOUNT = 9;
    private static BitSet singlePointerMap = new BitSet(INDIRECT_PTRS_AMOUNT);

    public static byte[][] INDIRECT_PTR = new byte[INDIRECT_PTRS_AMOUNT][4];

    public static void Init()
    {
        singlePointerMap.set(0,9,true);
        for(int x = 0; x < INDIRECT_PTRS_AMOUNT; x++)
        {
            for(int y = 0; y < 4; y++)
            {
                INDIRECT_PTR[x][y] = -1;
            }
        }
    }

    public static byte ReserveSinglePointer()
    {
        for(int x = 0; x < INDIRECT_PTRS_AMOUNT; x++)
        {
            if(singlePointerMap.get(x))
            {
                singlePointerMap.set(x, false);
                return (byte) x;
            }
        }
        return -1;
    }

    public static void FreeSinglePointer(byte loc)
    {
        singlePointerMap.set(loc, true);
        for(int y = 0; y < 4; y++)
        {
            INDIRECT_PTR[loc][y] = -1;
        }
    }
}
