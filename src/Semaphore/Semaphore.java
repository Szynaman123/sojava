package Semaphore;

import Process.PCB;
import Srt.srt;

import java.util.LinkedList;
import java.util.Queue;

public class Semaphore {
    public int value;
    public Queue<PCB> qu = new LinkedList<PCB>();

    public Semaphore(int value){
        this.value = value;
        System.out.println("Stworzono semafor o wartosci: " + value);
    }

    public void listPCB(){
        System.out.println("Zawartosc listy PCB oczekujacych na zwolnienie semafora: ");
        for(PCB p : qu){
            System.out.println(p.toString());
        }
    }

    public void wait(Semaphore s){
        s.value--;
        System.out.println("Wywolano metode wait, wartosc semafora po dekrementacji: " + value);
        if(s.value < 0){

            PCB current = srt.getRUNNING();
            qu.add(current);
            System.out.println("Dodano proces" + current.getName() + " do kolejki oczekujacych");
            current.setStateWaiting();
            listPCB();

        }
    }

    public void signal(Semaphore s){
        s.value++;
        System.out.println("Wywolano metode signal, wartosc semafora po inkrementacji: " + value);
        if(s.value>=0){
            if(!qu.isEmpty()){
                PCB p = qu.peek();
                qu.remove();
                System.out.println("Usunieto proces " + p.getName() + " z kolejki oczekujacych");
                listPCB();
                p.setStateReady();
            }
        }
    }
}
