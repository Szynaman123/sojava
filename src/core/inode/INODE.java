package core.inode;

import core.disk.Disk;
import core.disk.DiskSpaceManager;
import core.disk.NoFreeDiskSpaceException;
import core.open_ft.OpenFileTable;
import core.single_pointer.SingleIndirectPointer;
import core.utils.Utils;
import enums.INODE_TYPE;
import javafx.util.Pair;

import java.util.Date;

public class INODE
{
    public static final int DIRECT_POINTERS = 4;
    public byte i_mode;         // 1b type
    public short i_size;         // 1b size
    public byte i_blocks;       // 1b used blocks
    public long i_atime;        // 8b access time
    public long i_mtime;        // 8b modify time

    // 4b 4 direct pointer
    public byte[]i_block_direct = new byte[DIRECT_POINTERS];
    public byte i_block_single; // 1b single indirect pointer
    public byte i_faddress;     // 1b inode address;
                                // 25 b total

    // core.inode.INODE creation prototype
    public static INODE PrototypeINODE(INODE_TYPE type) throws NoFreeDiskSpaceException
    {
        Date date = new Date();
        INODE temp = new INODE();

        switch (type)
        {
            case DIRECTORY:
                temp.i_mode = 0;
                break;

            case FILE:
                temp.i_mode = 1;
                break;

        }
        temp.i_size = 0;
        temp.i_blocks = 0;

        temp.i_atime = date.getTime();
        temp.i_mtime = date.getTime();

        temp.i_block_single = SingleIndirectPointer.ReserveSinglePointer();
        temp.i_faddress = DiskSpaceManager.AllocateINODE();
        for(int x = 0; x < 4; x++)
        {
            temp.i_block_direct[x] = -1;
        }
        Disk.WriteINODE(temp.i_faddress, temp.As32ByteArray());
        return temp;
    }

    // Read core.inode.INODE from disk
    public static INODE ReadINODE(int disk_location)
    {
        INODE temp = new INODE();
        temp.From32ByteArray(Disk.ReadINODE(disk_location));
        return temp;
    }

    // Release core.inode.INODE
    public static void FreeINODE(INODE inode)
    {
        //todo: allocation table data release
        for (int x = 0; x < inode.i_blocks; x++)
        {
            if (x < 4)
            {
                DiskSpaceManager.DeallocateBLOCKDATA(inode.i_block_direct[x]);
            }else
            {
                DiskSpaceManager.DeallocateBLOCKDATA(SingleIndirectPointer.INDIRECT_PTR[inode.i_block_single][x - 4]);
            }
        }

        //todo: allocation table inode release
        DiskSpaceManager.DeallocateINODE(inode.i_faddress);

        //todo: single pointer release
        SingleIndirectPointer.FreeSinglePointer(inode.i_block_single);

    }

    // Try to allocate another data block on disk
    public boolean AllocateDataBlockFile()
    {
        if(i_blocks == 8)
        {
            return false;
        }else
        {
            try
            {
                if(i_blocks < 4)
                {
                    i_block_direct[i_blocks] = DiskSpaceManager.AllocateDateBlock();
                }else
                {
                    SingleIndirectPointer.INDIRECT_PTR[i_block_single][i_blocks - 4] = DiskSpaceManager.AllocateDateBlock();
                }
                i_blocks++;
                return true;
            } catch (NoFreeDiskSpaceException e)
            {
                return false;
            }
        }
    }

    // Try to allocate another data block on disk
    public Pair<Boolean,Byte> AllocateDataBlockDirectory()
    {
        try
        {
            for (int x = 0; x < 8; x++)
            {
                if (x < 4)
                {
                    if (i_block_direct[x] == -1)
                    {
                        i_block_direct[x] = DiskSpaceManager.AllocateDateBlock();
                        i_blocks++;
                        return new Pair<>(true, i_block_direct[x]);
                    }
                } else
                {
                    if (SingleIndirectPointer.INDIRECT_PTR[i_block_single][x - 4] == -1)
                    {
                        SingleIndirectPointer.INDIRECT_PTR[i_block_single][x - 4] = DiskSpaceManager.AllocateDateBlock();
                        i_blocks++;
                        return new Pair<>(true, SingleIndirectPointer.INDIRECT_PTR[i_block_single][x - 4]);
                    }
                }
            }
            return new Pair<>(false, (byte)-1);
        } catch (NoFreeDiskSpaceException e)
        {
            return new Pair<>(false, (byte)-1);
        }
    }

    // Sets block as -1
    public void FreePointerDirectory(byte disk_location)
    {
        if(disk_location < 4)
        {
            i_block_direct[disk_location] = -1;
        }else
        {
            SingleIndirectPointer.INDIRECT_PTR[i_block_single][disk_location - 4] = -1;
        }
    }

    // Deallocate all blocks from core.inode.INODE
    public void DeallocateDataBlocksFile()
    {
        for (int x = 0; x < i_blocks; x++)
        {
            if (x < 4)
            {
                DiskSpaceManager.DeallocateBLOCKDATA(i_block_direct[x]);
            }else
            {
                DiskSpaceManager.DeallocateBLOCKDATA(SingleIndirectPointer.INDIRECT_PTR[i_block_single][x - 4]);
            }
        }
        i_blocks = 0;
        i_size = 0;
    }

    // Update core.inode.INODE on disk
    public void UpdateINODE()
    {
        Disk.WriteINODE(i_faddress, As32ByteArray());
    }

    // core.inode.INODE open
    public void Open()
    {
        //todo: add file to open file table/increment open number reference
        OpenFileTable.Open((int) this.i_faddress);
        //todo: call wait on semaphore in open file table
    }

    // core.inode.INODE close
    public void Close()
    {
        //todo: remove file from open file table/decrement open number reference
        OpenFileTable.Close((int) this.i_faddress);
        //todo: call signal on semaphore in open file table
    }

    // Check if core.inode.INODE is directory
    public boolean IsDirectory()
    {
        return i_mode == 0;
    }

    // Update access date to now
    public void RiseAccess()
    {
        i_atime = (new Date().getTime());
    }

    // Update modify date to now
    public void RiseModify()
    {
        i_mtime = (new Date().getTime());
    }

    // Manual serialization of core.inode.INODE struct
    private byte[] As32ByteArray()
    {
        byte[] buffer = new byte[32];

        buffer[0] = i_mode;
        buffer[1] = (byte) (i_size >> 8);
        buffer[2] = i_blocks;

        buffer[3] = (byte) (i_atime >> 56);
        buffer[4] = (byte) (i_atime >> 48);
        buffer[5] = (byte) (i_atime >> 40);
        buffer[6] = (byte) (i_atime >> 32);
        buffer[7] = (byte) (i_atime >> 24);
        buffer[8] = (byte) (i_atime >> 16);
        buffer[9] = (byte) (i_atime >> 8);
        buffer[10] = (byte) (i_atime);

        buffer[11] = (byte) (i_mtime >> 56);
        buffer[12] = (byte) (i_mtime >> 48);
        buffer[13] = (byte) (i_mtime >> 40);
        buffer[14] = (byte) (i_mtime >> 32);
        buffer[15] = (byte) (i_mtime >> 24);
        buffer[16] = (byte) (i_mtime >> 16);
        buffer[17] = (byte) (i_mtime >> 8);
        buffer[18] = (byte) (i_mtime);

        buffer[19] = i_block_direct[0];
        buffer[20] = i_block_direct[1];
        buffer[21] = i_block_direct[2];
        buffer[22] = i_block_direct[3];

        buffer[23] = i_block_single;
        buffer[24] = i_faddress;
        buffer[25] = (byte) (i_size);

        return buffer;
    }

    // Manual deserialization of core.inode.INODE struct
    private void From32ByteArray(byte[] buffer)
    {
        i_mode = buffer[0];
        i_blocks = buffer[2];

        i_size |= (short) (Utils.ConvertToUnsigned(buffer[1])  << 8);
        i_size |= (short) (Utils.ConvertToUnsigned(buffer[25]));

        i_atime |= ((long) Utils.ConvertToUnsigned(buffer[3])  << 56);
        i_atime |= ((long) Utils.ConvertToUnsigned(buffer[4])  << 48);
        i_atime |= ((long) Utils.ConvertToUnsigned(buffer[5])  << 40);
        i_atime |= ((long) Utils.ConvertToUnsigned(buffer[6])  << 32);
        i_atime |= ((long) Utils.ConvertToUnsigned(buffer[7])  << 24);
        i_atime |= ((long) Utils.ConvertToUnsigned(buffer[8])  << 16);
        i_atime |= ((long) Utils.ConvertToUnsigned(buffer[9])  << 8);
        i_atime |= (long) Utils.ConvertToUnsigned(buffer[10]);

        i_mtime |= ((long) Utils.ConvertToUnsigned(buffer[11])  << 56);
        i_mtime |= ((long) Utils.ConvertToUnsigned(buffer[12])  << 48);
        i_mtime |= ((long) Utils.ConvertToUnsigned(buffer[13])  << 40);
        i_mtime |= ((long) Utils.ConvertToUnsigned(buffer[14])  << 32);
        i_mtime |= ((long) Utils.ConvertToUnsigned(buffer[15])  << 24);
        i_mtime |= ((long) Utils.ConvertToUnsigned(buffer[16])  << 16);
        i_mtime |= ((long) Utils.ConvertToUnsigned(buffer[17])  << 8);
        i_mtime |= (long) Utils.ConvertToUnsigned(buffer[18]);

        i_block_direct[0] = buffer[19];
        i_block_direct[1] = buffer[20];
        i_block_direct[2] = buffer[21];
        i_block_direct[3] = buffer[22];

        i_block_single = buffer[23];
        i_faddress = buffer[24];
    }
}
