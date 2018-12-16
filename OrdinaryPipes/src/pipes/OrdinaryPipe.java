package pipes;

import java.io.IOException;

import java.util.*;

public class OrdinaryPipe
{
    public final Vector<PipeQueue> pipes = new Vector<>();
    public final Vector<Integer> busyDescr= new Vector<>();

    public int pipe(int pdesk[])
    { //tworzenie potoku
        Random rand= new Random();
        int out= rand.nextInt(32);
        while(busyDescr.contains(out))
        {
            out= rand.nextInt(32);
        }
        pdesk[0]=out;               //przypisanie deskryptora odczytu
        busyDescr.add(out);
        int in = rand.nextInt(32);
        while(busyDescr.contains(in))
        {
            in= rand.nextInt(32);
        }
        pdesk[1]=in;                //przypisanie deskryptora zapisu
        busyDescr.add(in);
        PipeQueue queueToAdd = new PipeQueue(out,in);
        pipes.add(queueToAdd);      //dodanie potoku(kolejki) do wektora kolejek
        return 0;
    }                                   //buf to segment danych w procesie do którego mają zostać przekazane dane
    public int read(int fd, Vector<Byte> buf, int count){       //czytanie z potoku o deskryptorze odczytu "fd" danych i przekazanie do "buf"
        int readed=0;               //liczba odczytanych bajtów
        for (PipeQueue e:pipes)     //przejście po tablicy kolejek w celu znalezienia potoku o odpowiednim deskryptorze
            {
            if(e.desOut==fd)
            {

                for(int i=0; i<count; i++)
                {
                     if(!e.eQueue.isEmpty())
                     {              //przekazanie danych z kolejki do bufora, jeżeli kolejka nie jest pusta
                         Byte a = e.eQueue.remove();

                         buf.add(a);
                         readed++;                          //zwiększenie o 1 liczby odczytanych bajtów
                     }
                     else
                         break;

                }
            }

        }
        return readed;                                      //zwrócenie rzeczywistej liczby odczytanych bajtów
    }
    public int write(int fd, Vector<Byte> buf, int count)
    {  //zapis danych z "buf"-segment danych w procesie do kolejki
        int writed=0;                                       //liczba odczytanych bajtów
        for(PipeQueue e:pipes)
        {                             //przejście po tablicy kolejek w celu znalezienia potoku o odpowiednim deskryptorze
            if(e.desIn==fd)
            {
                if(count<64-e.eQueue.size())    //sprawdzenie czy w potoku jest dość miejsca do zapisu "count" bajtów
                    for(int i=0; i<count; i++)
                    {
                        Byte a=buf.get(i);
                        e.eQueue.add(a);            //dodanie kolejnego bajtu do kolejki
                                          //zwiększenie "zajętych" bajtów w potoku
                        writed++;

                    }
            }
        }
        return writed;                              //zwrócenie rzeczywistej liczby zapisanych bajtów
    }
    public static void main(String args[])
    {
        int[] pdesk= new int[2];
        OrdinaryPipe a= new OrdinaryPipe();
        a.pipe(pdesk);

        Vector<Byte> pocz= new Vector<Byte>(4);
        Vector<Byte> konc = new Vector<Byte>(4);
        Byte b= '5';
        Byte c='t';
        pocz.add(b);
        pocz.add(c);
        a.write(pdesk[1],pocz,2);
        a.read(pdesk[0],konc,1);
        a.read(pdesk[0],konc,1);
        System.out.println(konc);

    }

}
