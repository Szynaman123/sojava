package Process;
import java.util.Vector;

public class PCB {
	private Short pid;
	private String name;
	private State state;
	public PCB parent;
	public Vector<PCB> childs;
	public Integer ax, bx, cx, dx;
	public Short programCounter;
	public boolean carryFlag;
	public boolean zeroFlag;
	//priorytety
	//zasoby

	public PCB(short pid, String name, PCB parent)//konstruktor dla inita
	{
		this.pid = pid;
		this.name = name;
		this.parent = parent;
		childs = new Vector<PCB>();
		ax = bx = cx = dx = 0;
		programCounter = 0;
		carryFlag = false;
		zeroFlag = true;
	}
	
	public PCB(PCB p, short newPid, String newName)//konstruktor dla wszystkich innych procesów
	{
		pid = newPid;
		name = newName;
		state = p.state;
		parent = p;
		childs = new Vector<PCB>();
		ax = p.ax;
		bx = p.bx;
		cx = p.cx;
		dx = p.dx;
		programCounter = p.programCounter;
		carryFlag = p.carryFlag;
		zeroFlag = p.zeroFlag;
	}
	
	public State getState()
	{
		return state;
	}
	
	public boolean setStateReady()
	{
		if(state != State.Ready)
		{
			state = State.Ready;
			return true;
		}
		else return false;
	}
	
	public boolean setStateWaiting()
	{
		if(state != State.Waiting)
		{
			state = State.Waiting;
			return true;
		}
		else return false;
	}
	
	public boolean setStateRunning()
	{
		if(state != State.Running)
		{
			state = State.Running;
			return true;
		}
		else return false;
	}
	
	public boolean setStateTerminated()
	{
		if(state != State.Terminated)
		{
			state = State.Terminated;
			return true;
		}
		else return false;
	}
	
	public boolean setStateZombie()
	{
		if(state != State.Zombie)
		{
			state = State.Zombie;
			return true;
		}
		else return false;
	}
	
	public short getPid()
	{
		return pid;
	}
	
	public String getName()
	{
		return name;
	}		
	
	public String getAsTreeElement()
	{
		return name + "(" + pid.toString() + ")";
	}
	
	public String ToString()
	{
		String pcb;
		
		pcb = "PID: " + pid.toString() + " | Name: " + name + " | State: " + state.toString() + " | Parent: ";
		
		if(parent != null)
		{
			pcb += parent.pid.toString() + " | "; 
		}
		else pcb += "none "; 
		
		if(!childs.isEmpty())
		{
			pcb += "| Childs: ";
			for(int i=0; i<childs.size(); i++)
			{
				pcb += childs.get(i).pid.toString() + " ";
			}			
		}
		
		pcb += "| AX: " + ax + " | BX: " + bx + " | CX: " + cx + " | DX: " + dx + " | Program Counter: " + programCounter;
		
		return pcb;
	}
	
}
