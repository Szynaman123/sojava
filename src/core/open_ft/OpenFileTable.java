package core.open_ft;

import java.util.Vector;

public class OpenFileTable
{
    static class TableEntry
    {
        int ID;
        int refers = 1;
        //todo: sem here

        TableEntry(int id)
        {
            this.ID = id;
            //todo: call wait on sem
        }
    }

    private static Vector<TableEntry> OFT = new Vector<>();

    public static boolean Open(Integer id)
    {
        for (TableEntry pair0 : OFT)
        {
            if(pair0.ID == id)
            {
                pair0.refers++;
                //todo: call wait on sem
                return false;
            }
        }
        OFT.add(new TableEntry(id));
        return true;
    }

    public static void Close(Integer id)
    {
        TableEntry to_delete = null;
        for (TableEntry pair0 : OFT)
        {
            if(pair0.ID == id)
            {
                pair0.refers--;
                //todo: call signal on sem
                if(pair0.refers == 0)
                {
                    to_delete = pair0;
                    break;
                }
            }
        }
        if(to_delete != null)
        {
            OFT.remove(to_delete);
        }
    }

    public static boolean Contains(Integer id)
    {
        for (TableEntry pair0 : OFT)
        {
            if(pair0.ID == id)
            {
                return true;
            }
        }
        return false;
    }


}
