package core.logic_fs;

import core.disk.Disk;
import core.disk.NoFreeDiskSpaceException;
import core.inode.INODE;
import core.open_ft.OpenFileTable;
import core.single_pointer.SingleIndirectPointer;
import core.utils.Utils;
import enums.DIR_SEARCH_MODE;
import enums.INODE_TYPE;
import javafx.util.Pair;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;


public class FileOrganizationModule
{
    private static INODE root;

    // Create root directory
    public static void Init() throws NoFreeDiskSpaceException
    {
        root = INODE.PrototypeINODE(INODE_TYPE.DIRECTORY);
    }

    // Create file
    public static INODE CreateINODEFILE(String filename) throws NoFreeDiskSpaceException, NameTakenException
    {
        try
        {
            SearchDirectoryForFile(filename, DIR_SEARCH_MODE.REGULAR);
        } catch (NoSuchElementException e)
        {
            Pair<Boolean, Byte> allocate_result = root.AllocateDataBlockDirectory();
            if(allocate_result.getKey())
            {
                INODE inode = INODE.PrototypeINODE(INODE_TYPE.FILE);
                DirectoryDataBlock block = new DirectoryDataBlock(filename, inode.i_faddress);
                Disk.WriteDATA(allocate_result.getValue(), block.As32ByteArray());

                root.RiseModify();
                root.UpdateINODE();
                return inode;
            }else
            {
                throw new NoFreeDiskSpaceException();
            }
        } catch (DeleteException ignored)
        {

        }
        throw new NameTakenException();
    }

    // Delete file
    public static void DeleteINODEFILE(String filename) throws NoSuchElementException, DeleteException
    {
        INODE.FreeINODE(SearchDirectoryForFile(filename, DIR_SEARCH_MODE.DELETE));
    }

    // Access file
    public static INODE AccessINODEFILE(String filename) throws NoSuchElementException
    {
        try
        {
            return SearchDirectoryForFile(filename, DIR_SEARCH_MODE.REGULAR);
        } catch (DeleteException ignored)
        {
            throw new NoSuchElementException();
        }
    }

    public static void PrintDirectory()
    {
        try
        {
            SearchDirectoryForFile("", DIR_SEARCH_MODE.PRINT);
        } catch (NoSuchElementException | DeleteException ignore)
        {
        }
    }



    // internal search of directory
    private static INODE SearchDirectoryForFile(String filename, DIR_SEARCH_MODE mode) throws NoSuchElementException, DeleteException
    {
        DirectoryDataBlock block = null;
        for(int x = 0; x < 8; x++)
        {
            block = null;
            if(x < 4)
            {
                if (root.i_block_direct[x] != - 1)
                {
                    block = new DirectoryDataBlock(Disk.ReadDATA(root.i_block_direct[x]));
                }

            }else
            {
                if(SingleIndirectPointer.INDIRECT_PTR[root.i_block_single][x - 4] != - 1)
                {
                    block = new DirectoryDataBlock(Disk.ReadDATA(SingleIndirectPointer.INDIRECT_PTR[root.i_block_single][x - 4]));
                }
            }
            if(block != null)
            {
                if (block.GetFileLocation() != -1)
                {

                    if(Utils.PadFilenameTo15(filename).equals(block.GetFileName()) | mode == DIR_SEARCH_MODE.PRINT)
                    {
                        switch (mode)
                        {
                            case REGULAR:
                                return INODE.ReadINODE(block.GetFileLocation());

                            case DELETE:
                                if (OpenFileTable.Contains((int) INODE.ReadINODE(block.GetFileLocation()).i_faddress))
                                {
                                    throw new DeleteException();
                                }
                                if(x < 4)
                                {
                                    root.i_block_direct[x] = - 1;
                                }else
                                {
                                    SingleIndirectPointer.INDIRECT_PTR[root.i_block_single][x - 4] = - 1;
                                }
                                return INODE.ReadINODE(block.GetFileLocation());
                            case PRINT:
                                INODE inode = INODE.ReadINODE(block.GetFileLocation());
                                Timestamp d = new Timestamp(inode.i_atime);
                                String s = new SimpleDateFormat("MMMMM dd HH:mm").format(d);
                                Timestamp f = new Timestamp(inode.i_mtime);
                                String s2 = new SimpleDateFormat("MMMMM dd HH:mm").format(d);
                                System.out.println(inode.i_size + (inode.i_size >= 100 ? " ": "\t") + inode.i_faddress + " A:" + s + " M:" + s2 + " " + block.GetFileName());

                        }
                    }
                }
            }
        }
        throw new NoSuchElementException();
    }


}

