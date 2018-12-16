import api.FileStream_API;
import core.disk.DiskSpaceManager;
import core.disk.NoFreeDiskSpaceException;
import core.filestream.FileStream;
import core.logic_fs.FileOrganizationModule;
import core.single_pointer.SingleIndirectPointer;

import java.util.Scanner;

public class Main
{
    public static void main(String[] args) throws NoFreeDiskSpaceException
    {
        SingleIndirectPointer.Init();
        DiskSpaceManager.Init();
        FileOrganizationModule.Init();

        int dscr = FileStream_API.Open("maciek_xd", 2);
        int dscr0 = FileStream_API.Open("LOfa", 2);
        int dscr1 = FileStream_API.Open("plik", 2);
        int dscr2 = FileStream_API.Open("F", 2);
        int dscr3 = FileStream_API.Open("xd", 2);
        int dscr4 = FileStream_API.Open("lul", 2);
        FileStream_API.PrintDiskData();

        System.out.println("Deskryptor pliku to:" + dscr);
        for(int x = 0; x < 2000000000; x++)
        {

        }
        FileStream_API.Write(dscr, "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaxd".getBytes());
        FileStream_API.PrintDirContent();
        FileStream_API.Write(dscr, " no kurwa maciek prosze cie".getBytes());


        FileStream_API.PrintDirContent();

        FileStream_API.PrintDirContent();
        FileStream_API.PrintINODE(0);
        FileStream_API.PrintDataBlock(9);
        Scanner in = new Scanner(System.in);
        int n = in.nextInt();
        FileStream_API.Write(dscr, "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaxd".getBytes());
        FileStream_API.PrintDiskData();
        FileStream_API.PrintDirContent();
        FileStream_API.Close(dscr);
        System.out.println(FileStream_API.DeleteFile("maciek_xd"));
        FileStream_API.PrintDirContent();
        FileStream_API.PrintDiskData();
    }
}
