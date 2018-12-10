package core.utils;

import java.io.*;

public class Utils
{
    public static byte[] ConvertToBytes(Object object) throws IOException
    {
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream(); ObjectOutput out = new ObjectOutputStream(stream))
        {
            out.writeObject(object);
            return stream.toByteArray();
        }
    }

    public static Object ConvertFromBytes(byte[] bytes) throws IOException, ClassNotFoundException
    {
        try (ByteArrayInputStream stream = new ByteArrayInputStream(bytes); ObjectInput in = new ObjectInputStream(stream))
        {
            return in.readObject();
        }
    }

    public static int ConvertToUnsigned(byte b)
    {
        if (b < 0)
        {
            return b + 256;
        } else
        {
            return (int) b;
        }
    }

    public static String PadFilenameTo15(String filename)
    {
        StringBuilder filenameBuilder = new StringBuilder(filename);
        while (filenameBuilder.length() < 15)
        {
            filenameBuilder.append('\u0000');
        }
        return filenameBuilder.toString();
    }
}
