package api;

import core.disk.Disk;
import core.disk.DiskSpaceManager;
import core.filestream.FileStream;
import core.inode.INODE;
import core.logic_fs.DeleteException;
import core.logic_fs.FileOrganizationModule;
import core.logic_fs.NoSuchElementException;
import core.single_pointer.SingleIndirectPointer;
import enums.FILE_OPEN_MODE;
import javafx.util.Pair;

import java.util.Arrays;
import java.util.Random;
import java.util.Vector;

public class FileStream_API
{
    private static Vector<Pair<Integer,FileStream>> streams = new Vector<>();

    private static Vector<Integer> takenDescriptors = new Vector<>();


    public static int Open(String filename,int mode)
    {
        Random random = new Random();

        if(takenDescriptors.size() == 255)
        {
            return -1;
        }

        int val = random.nextInt(255) + 1;
        while (takenDescriptors.contains(val))
            val = random.nextInt(255) + 1;

        //todo: open a new stream for process

        if (mode != 0 && mode != 1 &&  mode != 2)
        {
            mode = 0;
        }

        Pair<Integer,FileStream> bang = null;
        switch (mode)
        {
            case 0:
                bang = new Pair<>(val,new FileStream(filename, FILE_OPEN_MODE.READ));
                break;
            case 1:
                bang = new Pair<>(val,new FileStream(filename, FILE_OPEN_MODE.WRITE));
                break;
            case 2:
                bang = new Pair<>(val,new FileStream(filename, FILE_OPEN_MODE.READ_WRITE));
                break;
        }
        //todo: call Open on new created stream
        if(!bang.getValue().Open())
        {
            return -1;
        }
        streams.add(bang);
        takenDescriptors.add(val);
        return val;
    }

    public static void Close(int descr)
    {
        Pair<Integer,FileStream> to_delete = null;
        for(Pair<Integer,FileStream> pair : streams)
        {
            if(pair.getKey() == descr)
            {
                to_delete = pair;
                break;
            }
        }
        int counter = 0;
        for(Integer in : takenDescriptors)
        {
            if(in == descr)
            {
               break;
            }
            counter++;
        }
        if(to_delete == null)
        {
            return;
        }
        if(!to_delete.getValue().Close())
        {
            return;
        }
        takenDescriptors.remove(counter);
        streams.remove(to_delete);
    }

    public static byte[] Read(int f_descr, int n)
    {
        Pair<Integer,FileStream> file = null;
        for(Pair<Integer,FileStream> pair : streams)
        {
            if(pair.getKey() == f_descr)
            {
                file = pair;
                break;
            }
        }
        if(file == null)
        {
            return new byte[0];
        }
        return file.getValue().Read(n);
    }

    public static int Write(int f_descr, byte[] data)
    {
        Pair<Integer,FileStream> file = null;
        for(Pair<Integer,FileStream> pair : streams)
        {
            if(pair.getKey() == f_descr)
            {
                file = pair;
                break;
            }
        }
        if(file == null)
        {
            return 0;
        }
        return file.getValue().Write(data);
    }

    public static void SetAppend(int f_descr)
    {
        Pair<Integer,FileStream> file = null;
        for(Pair<Integer,FileStream> pair : streams)
        {
            if(pair.getKey() == f_descr)
            {
                file = pair;
                break;
            }
        }
        if(file == null)
        {
            return;
        }
        file.getValue().EnableAppend();
    }

    public static void PrintDirContent()
    {
        //todo: print dir content;
        FileOrganizationModule.PrintDirectory();
    }

    public static boolean DeleteFile(String filename)
    {
        try
        {
            FileOrganizationModule.DeleteINODEFILE(filename);
            return true;
        } catch (NoSuchElementException | DeleteException e)
        {
            return false;
        }
    }

    public static void PrintINODE(int number)
    {
        INODE node = INODE.ReadINODE(number);
        System.out.println("MODE:\t" + node.i_mode);
        System.out.println("SIZE:\t" + node.i_size);
        System.out.println("BLOCKS:\t" + node.i_blocks);
        System.out.println("ATIME\t" + node.i_atime);
        System.out.println("MTIME\t" + node.i_mtime);
        System.out.print("BLOCKS:\t");
        for(int x = 0; x < 8; x++)
        {
            System.out.print(" " + (x < 4 ? node.i_block_direct[x] : SingleIndirectPointer.INDIRECT_PTR[node.i_block_single][x - 4]) + " ");
            if(x == 7)
            {
                System.out.print("\n");
            }
        }
        System.out.println("FADDR\t" + node.i_faddress);


    }

    public static void PrintDataBlock(int block)
    {
        try
        {
            System.out.println(Arrays.toString(Disk.ReadDATA(block)));
        } catch (Exception e)
        {
            System.out.println("Taki blok nie istnieje");
        }
    }

    public static void PrintDiskData()
    {
        DiskSpaceManager.PrintDiskStatus();
    }
}
