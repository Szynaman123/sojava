package Srt;
import Process.*;
import java.util.Vector;

public class srt {
    private static int MAXSIZE = 25;
    private static double ALFA = 0.5;
    public volatile static Vector<PCB> readyProc = new Vector<>(MAXSIZE); //lista procesow w stanie ready, gotowych do przydzialu procesora
    private static PCB DUMMY = new PCB((short)0, "Dummy", null); //Proces, ktory pracuje, kiedy wektor jest pusty /konstruktor dla inita
    private static PCB RUNNING = DUMMY; //wykonywany proces
    private static final int TAU_OF_FIRST_PROC = 6;
    private static int last_tau_of_proc_in_vector; //przechowuje tau ostatniego wykonanywanego procesu z wektora (potrzebne do obliczenia czasu, jesli wektor by zostal pusty i przyszedl kolejny proces)
    private static short last_real_time_of_proc_in_vector = 0; //przechowuje rzeczywisty czas ostatniego wykonanywanego procesu z wektora (potrzebne do obliczenia czasu, jesli wektor by zostal pusty i przyszedl kolejny proces)
    private static boolean FIRST_PROC_START = false;

    public void add_proc(PCB proc) {
            if(proc != null) { //sprawdzenie czy dodawane PCB nie jest puste
                if (readyProc.size() < MAXSIZE) { //sprawdzenie czy jest miejsce w wektorze
                    if(readyProc.size() == 0) { //dodawanie dla pierwszego procesu
                        System.out.println("Add first element from ready process list: " + proc.getName() + " (" + proc.getPid() + ", commands: " + proc.programCounter + ")" );
                        proc.setStateRunning();
                        readyProc.add(proc);
                        calculate_srt();
                        last_tau_of_proc_in_vector = proc.getTau();
                        RUNNING = proc;

                    }
                    else {
                        System.out.println("Add next element from ready process list: " + proc.getName() + " (" + proc.getPid() + ", commands: " + proc.programCounter + ")" );
                        proc.setStateReady(); // TODO ustalic czy jest ustawiane tu czy w konstruktorze PCB
                        readyProc.add(proc);
                        update_srt_RUNNING();
                        calculate_srt();
                        setRunning();
                    }
                    System.out.println("The number of processes in the state Ready: " + (readyProc.size()-1));
                }
                else {
                    System.out.println("The vector of process is full");
                }
            }
            else {

                System.out.println("Passed PCB is null");
            }
    }
    private static void calculate_srt() { //obliczanie przewidywanego czasu wykonywania zgodnie ze wzorem dla SRT
        if( ALFA > 0 && ALFA < 1) {
            for(PCB p : readyProc) {
                int oldTau = p.getTau();
                if(p.getTau() == -1) { //obliczanie Tau dla pozostalych procesow
                    int index = readyProc.indexOf(p) - 1;
                    if (index >= 0) {
                        int t = readyProc.get(index).programCounter;
                        p.setTau((int) (ALFA * t + (1 - ALFA) * readyProc.get(index).getTau())); //ze wzoru tau+1 = licznikrozkazow*alfa + (1-alfa)*tau-1
//                        System.out.println("Process " + p.getName() + " tau: " + p.getTau());
                        //System.out.println("[TAU] Calculate TAU for " + p.getName() + " value " + p.getTau() + "(" + oldTau + ")");
                    } else if (!FIRST_PROC_START && index < 0) { //przypisanie stalej 5 dla pierwszego procesu w wektorze
                        p.setTau(TAU_OF_FIRST_PROC);
                        //System.out.println("[TAU-F] Calculate TAU for " + p.getName() + " value " + p.getTau() + "(" + oldTau + ")");
                        FIRST_PROC_START = true;
                    }
                    else if(FIRST_PROC_START && index < 0) { //warunek dla pierwszego elementu w wektorze, ktory byl ponownie wyzerowany
                        p.setTau((int) (ALFA * last_real_time_of_proc_in_vector + (1 - ALFA) * last_tau_of_proc_in_vector));
                        //System.out.println("[TAU-C] Calculate TAU from const values for " + p.getName() + " value " + p.getTau() + "(" + oldTau + ")");
                    }
                }
            }
        }
    }

    private static void update_srt_RUNNING() { // func to update tau after execute XX commands and preemption
        if (RUNNING.getName() != "Dummy") {
            int oldTau = RUNNING.getTau();
            RUNNING.setTau((int) (ALFA * RUNNING.programCounter + (1 - ALFA) * RUNNING.getTau())); //ze wzoru tau+1 = licznikrozkazow*alfa + (1-alfa)*tau-1
            //System.out.println("[TAU-U] RUNNING bprogramCounter " + RUNNING.getBprogrammCounter() + " programmCounter " + RUNNING.programCounter);
            //System.out.println("[TAU-U] Updated TAU for RUNNING " + RUNNING.getName() + " value " + RUNNING.getTau() + "(" + oldTau + ")");
        }
    }


    public static void remove_process(PCB toremove) { //usuwanie obiektow ze stanem terminated
        if (toremove != null) {
            if (toremove.getState() == State.Terminated) {
                if (readyProc.remove(toremove)) {
                    System.out.println("Successfully removed: " + toremove.getPid());
                    setRunning();
                } else {
                    System.out.println("Failed removing process: " + toremove.getPid());
                }

            } else {
                System.out.println("Passed PCB " + toremove.getName() +" isn't in 'Terminated' state! Cannot remove!");
            }
        } else {
            System.out.println("Passed PCB to remove is null");
        }

    }
    public static void printreadyProc() {
        for(PCB p: readyProc) {
            System.out.println("ID: " + p.getPid() + " Name: " + p.getName() + " BurstTime: " + p.getTau());
        }
    }
    public static void setRunning() {
        if(readyProc.isEmpty()) {
            RUNNING = DUMMY;
        }
        else {
            int index = 0;
            double t_1 = readyProc.get(index).getTau(); // zmienna do porownywania czasu wykonania // TODO nie wiem czy nie powinno byc porownywane do RUNNING
            for (int i = 1; i < readyProc.size(); i++) { // jesli 1 element w wektorze to for sie nie wykona
                int t = readyProc.get(i).getTau();
                if (t < t_1 ) { //&& t > 0
                    t_1 = readyProc.get(i).getTau();
                    index = i;
                }
            }
            RUNNING.setStateReady(); //zmiana stanu wywlaszczanego elementu;
            last_tau_of_proc_in_vector = RUNNING.getTau();  // TODO nadal nie jestem pewien czy jest to poprawne w 100% (tn)
            last_real_time_of_proc_in_vector = RUNNING.programCounter;
            readyProc.get(index).setStateRunning();
            System.out.println("PCB preemption index: " + index + " " + getPidTau(RUNNING) + "->" + getPidTau(readyProc.get(index)));
            RUNNING = readyProc.get(index); // TODO t(n+1) ?
        }
    }
    public static void stepWork(String[] command) { // do zmiany w zaleznosci od interpretera
        if(command[0].equals("SRT")) {
            if(command[1].equals("READY")) {
                System.out.println("Ready processes in OS: ");
                printreadyProc();
            }
        }
    }

    private static String getPidTau(PCB p) {
        StringBuilder strB = new StringBuilder();
        strB.append(" name: ");
        strB.append(" ");
        strB.append(p.getName());
        strB.append(" ");
        strB.append(" PID: ");
        strB.append(p.getPid());
        strB.append(" ");
        strB.append("(Tau: ");
        strB.append(p.getTau());
        strB.append(") ");

        return strB.toString();
    }

    public static PCB getRUNNING() {
        return RUNNING;
    }
}



