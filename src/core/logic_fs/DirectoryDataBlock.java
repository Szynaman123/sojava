package core.logic_fs;

import core.utils.Utils;

public class DirectoryDataBlock
{
    private char[] filename = new char[15];
    private byte i_location;

    public DirectoryDataBlock(String filename, byte i_loc)
    {
        for (int x = 0; x < filename.length(); x++)
        {
            this.filename[x] = filename.charAt(x);
        }
        i_location = i_loc;
    }

    public DirectoryDataBlock(byte[] buffer)
    {
        From32ByteArray(buffer);
    }

    public String GetFileName()
    {
        return new String(filename);
    }

    public byte GetFileLocation()
    {
        return i_location;
    }

    public byte[] As32ByteArray()
    {
        byte[] buffer = new byte[32];

        buffer[0] = (i_location);

        int counter = 1;
        for (char c : filename)
        {
            buffer[counter++] = (byte) (c >> 8);
            buffer[counter++] = (byte) (c);
        }

        return buffer;
    }

    private void From32ByteArray(byte[] buffer)
    {
        i_location |= buffer[0];
        int y = 1;
        for (int x = 0; x < 15; x++)
        {
            filename[x] |= (short) (Utils.ConvertToUnsigned(buffer[y++]) << 8);
            filename[x] |= (short) Utils.ConvertToUnsigned(buffer[y++]);
        }
    }
}