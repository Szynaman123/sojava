package pipes;
import java.util.*;
public class PipeQueue
{
    int desIn;                                                  //deskryptor zapisu
    int desOut;                                                 //deskryptor odczytu
                                                     //ilosć bajtów znajdujących się w kolejce
    Queue<Byte> eQueue = new ArrayDeque<>(64);      //kolejka bajtów
    public PipeQueue(int a, int b)
    {
        desOut=a;
        desIn=b;
    }

}
