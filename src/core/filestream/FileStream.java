package core.filestream;

import core.disk.Disk;
import core.disk.NoFreeDiskSpaceException;
import core.inode.INODE;
import core.logic_fs.FileOrganizationModule;
import core.logic_fs.NameTakenException;
import core.logic_fs.NoSuchElementException;
import core.single_pointer.SingleIndirectPointer;
import enums.FILE_OPEN_MODE;

public class FileStream
{
    private FILE_OPEN_MODE mode;
    private INODE inode;
    public String val;
    private int rpointer = 0;
    private int wpointer = 0;

    private boolean ANY_ERRORS = false;
    private boolean FLAG_NO_DISK_SPACE = false;
    private boolean OPENED = false;


    public FileStream(String filename, FILE_OPEN_MODE mode)
    {
        val = filename;
        this.mode = mode;
        try
        {
            inode = FileOrganizationModule.AccessINODEFILE(filename);
        } catch (NoSuchElementException e) // jezeli nie ma takiego elementu
        {
            if (mode != FILE_OPEN_MODE.READ)
            {
                try
                {
                    inode = FileOrganizationModule.CreateINODEFILE(filename);
                } catch (NoFreeDiskSpaceException e1)
                {
                    ANY_ERRORS = true;
                    FLAG_NO_DISK_SPACE = true;
                } catch (NameTakenException ignore) // to nigdy nie wystapi ale java jest glupia bo jesli nei ma takiego elementu nie moze nazwa byc zajeta :)
                {
                    ANY_ERRORS = true;
                }
            }
        }
    }

    public void SetPointerRead(int pointer)
    {
        this.rpointer = pointer;
    }

    public void SetPointerWrite(int pointer)
    {
        this.wpointer = pointer;
    }

    public boolean Open()
    {
        if(ANY_ERRORS || OPENED)
        {
            return false;
        }else
        {
            if(mode == FILE_OPEN_MODE.WRITE)
            {
                inode.DeallocateDataBlocksFile();
                inode.UpdateINODE();
            }
            inode.Open();
            OPENED = true;
            return true;
        }
    }

    public void EnableAppend()
    {
        wpointer = inode.i_size;
    }

    public boolean Close()
    {
        if(OPENED)
        {
            OPENED = false;
            inode.Close();
            return true;
        }else
        {
            return false;
        }
    }

    public byte[] Read(int n)
    {
        int to_read;
        if(rpointer > inode.i_size | mode == FILE_OPEN_MODE.WRITE | !OPENED)
        {
            return new byte[0];
        }
        int cpied;
        byte[] buffer = new byte[to_read = (inode.i_size - rpointer < n ? inode.i_size - rpointer : n)];
        int begin_block = rpointer / 32;
        int begin_byte = rpointer % 32;
        int end_block = (rpointer + to_read) / 32;
        int end_byte = (rpointer + to_read) % 32;
        int full_blocks_to_read = end_block - 1 - begin_block;

        if (full_blocks_to_read == 0 && begin_block == end_block)
        {
            byte[]b = Disk.ReadDATA(begin_block < 4 ? inode.i_block_direct[begin_block] : SingleIndirectPointer.INDIRECT_PTR[inode.i_block_single][begin_block - 4]);
            System.arraycopy(b, begin_byte,buffer,0,to_read);
            rpointer += n;
            return buffer;
        }

        byte[]b = Disk.ReadDATA(begin_block < 4 ? inode.i_block_direct[begin_block] : SingleIndirectPointer.INDIRECT_PTR[inode.i_block_single][begin_block - 4]);
        begin_block++;
        System.arraycopy(b, begin_byte,buffer,0,32 - begin_byte);
        rpointer += 32 - begin_byte;
        cpied = 32 - begin_byte;


        for(int x = 0; x < full_blocks_to_read; x++)
        {
            b = Disk.ReadDATA(begin_block < 4 ? inode.i_block_direct[begin_block] : SingleIndirectPointer.INDIRECT_PTR[inode.i_block_single][begin_block - 4]);
            begin_block++;
            System.arraycopy(b, 0,buffer,cpied,32);
            cpied += 32;
            rpointer += 32;
        }
        if(cpied == buffer.length)
        {
            return buffer;
        }

        b = Disk.ReadDATA(begin_block < 4 ? inode.i_block_direct[begin_block] : SingleIndirectPointer.INDIRECT_PTR[inode.i_block_single][begin_block - 4]);
        System.arraycopy(b, 0, buffer, cpied, end_byte);
        cpied += end_byte;
        rpointer += end_byte;
        inode.RiseAccess();
        inode.UpdateINODE();
        return buffer;
    }

    public int Write(byte[] to_write)
    {
        if(!OPENED)
        {
            return 0;
        }

        if(mode == FILE_OPEN_MODE.READ)
        {
            return 0;
        }

        int can_write = 256 - wpointer < to_write.length ? 256 - wpointer : to_write.length; // ile max mozna zapisac do pliku jesli 256 - wskaznik > dlugosci tablicy to jest to dlugosc tablicy, jesli tablica jest dluza to jest to 256 - wskaznik
        int begin_block = wpointer / 32;    // blok od ktorego zaczniemy zapis w notacji pozycyjnej
        int begin_byte = wpointer % 32;     // bajt od ktorego zaczniemy zapis w notacji pozycyjnej w pierwszym bloku liczone wedle skaznika

        int needed_blocks = (can_write - (32 - begin_byte)) / 32 + 1; // ilosc potrzebych nam blokow aby dokonac zapisu to to ((ile mozemy zapisac) - wolne miejsce w 1 bloku) / 32 + 1 (calkowita ilosc potrzebnych blokow;
        int wroten = 0; // ile zapisalismy do tej pory





        int end_block = (wpointer + can_write) / 32;     //
        int full_blocks_to_write = end_block - 1 - begin_block; // ilosc blokow ktore nadpiszemy w pelni

        int additional_needed_blocks = (needed_blocks + begin_block + 1) - inode.i_blocks;
        int blocks_that_can_overwrite = inode.i_blocks - (begin_block + 1); // ilosc blokow ktore mozemy nadpisac
        int current = 0; // obecna wartosc wykonanych zapisow

        if(inode.i_blocks == 0)
        {
            if(!inode.AllocateDataBlockFile())// alokuj
            {
                return wroten; // alokacji sie nie powiodla zwroc ile udalo sie zapisac
            }
        }


        if(begin_block + 1 > inode.i_blocks) // jesli wskaznik wskazuje poza zaalokowany obaszar nie zapisuj
        {
            return 0;
        }

        int iter_read = 32 - begin_byte < can_write ? 32 - begin_byte : can_write; // ile zapiszemy w pierwszej iteracji
        byte[] data = Disk.ReadDATA(begin_block < 4 ? inode.i_block_direct[begin_block] : SingleIndirectPointer.INDIRECT_PTR[inode.i_block_single][begin_block - 4]);
        System.arraycopy(to_write, 0, data, begin_byte, iter_read); // kopiowanie danych
        Disk.WriteDATA(begin_block < 4 ? inode.i_block_direct[begin_block] : SingleIndirectPointer.INDIRECT_PTR[inode.i_block_single][begin_block - 4], data); // zapis bloku po przetworzeniu
        begin_block++; //wskazuj na nastepny blok
        can_write -= iter_read; // aktualizuj ile pozostalo do zapisu
        wroten += iter_read;    // ile zapisano
        wpointer += iter_read;
        if(wpointer > inode.i_size)
        {
            inode.i_size = (short) wpointer;
        }
        inode.RiseModify();
        inode.UpdateINODE();
        current++;              // 1 zapis na 1 bloku

        if (can_write == 0)     // jesli zapisano wszysko skoncz
        {
            return wroten;
        }
        for (int x = 0; x < full_blocks_to_write; x++) // zapisuj pelne bloki, najprostsza czesc
        {
            if(blocks_that_can_overwrite - current <= 0) // jesli potrzeba kolejny blok alokuj
            {
                if(!inode.AllocateDataBlockFile())// alokuj
                {
                    return wroten; // alokacji sie nie powiodla zwroc ile udalo sie zapisac
                }
            }
            byte[] temp = Disk.ReadDATA(begin_block < 4 ? inode.i_block_direct[begin_block] : SingleIndirectPointer.INDIRECT_PTR[inode.i_block_single][begin_block - 4]);
            System.arraycopy(to_write,wroten,temp,0,32);
            Disk.WriteDATA(begin_block < 4 ? inode.i_block_direct[begin_block] : SingleIndirectPointer.INDIRECT_PTR[inode.i_block_single][begin_block - 4], temp);
            begin_block++;
            can_write -= 32;
            wroten += 32;
            wpointer += 32;
            if(wpointer > inode.i_size)
            {
                inode.i_size = (short) wpointer;
            }
            inode.RiseModify();
            inode.UpdateINODE();
            current++;
        }
        if (can_write == 0) // jesli zapisano wszysko wyjdz
        {
            return wroten;
        }
        if(blocks_that_can_overwrite - current <= 0) // nie zapisano wszystkiego i potrzebna alokacja
        {
            if(!inode.AllocateDataBlockFile())// alokuj
            {
                return wroten; // alokacji sie nie powiodla zwroc ile udalo sie zapisac
            }
        }
        byte[] temp = Disk.ReadDATA(begin_block < 4 ? inode.i_block_direct[begin_block] : SingleIndirectPointer.INDIRECT_PTR[inode.i_block_single][begin_block - 4]);
        System.arraycopy(to_write, wroten,temp,0,can_write);
        Disk.WriteDATA(begin_block < 4 ? inode.i_block_direct[begin_block] : SingleIndirectPointer.INDIRECT_PTR[inode.i_block_single][begin_block - 4], temp);
        wpointer += can_write;
        can_write -= can_write;
        wroten += can_write;

        if(wpointer > inode.i_size)
        {
            inode.i_size = (short) wpointer;
        }
        inode.RiseModify();
        inode.UpdateINODE();
        return wroten;

    }
        
}
