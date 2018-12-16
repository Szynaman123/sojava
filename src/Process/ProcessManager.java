package Process;

import java.util.Vector;

public class ProcessManager {
	private static PCB init = new PCB((short)0, "init", null);
	private static Vector<PCB> pcbv = vectorInit();
	private static short index = 0;
	
	private static Vector<PCB> vectorInit()
	{
		Vector<PCB> vec = new Vector<PCB>();
		vec.add(init);
		return vec;
	}
	
	public static void fork(PCB parent, String name)
	{		
		PCB child = new PCB(parent, (short)(index + 1), name);
		parent.childs.addElement(child);
		pcbv.add(child);
		index++;
	}
	
	public static void exec()
	{
		
	}
	
	public static void exit(String name)
	{
		if(name.equals("init"))
		{
			System.out.println("Process init(0) can't be terminated!");
		}
		else
		{
			removePCB(init, name);
			for(int i = 0; i < pcbv.size(); i++)
			{
				if(pcbv.get(i).getName().equals(name))
				{
					pcbv.remove(i);
				}
			}
		}		
	}
	
	public static void exit(Short pid)
	{
		if(pid == 0)
		{
			System.out.println("Process init(0) can't be terminated!");
		}
		else
		{
			removePCB(init, pid);
			for(int i = 0; i < pcbv.size(); i++)
			{
				if(pcbv.get(i).getPid() == pid)
				{
					pcbv.remove(i);
				}
			}
		}		
	}
	
	public static void printProcessTree()
	{
		System.out.println("Process tree:");
		recursiveTree(init, 0);
	}
	
	public static PCB getPcb(Short pid)
	{
		for(int i = 0; i< pcbv.size(); i++)
		{
			if(pcbv.get(i).getPid() == pid) return pcbv.get(i);
		}
		return null;
	}
	
	public static PCB getPcb(String name)
	{
		for(int i = 0; i< pcbv.size(); i++)
		{
			if(pcbv.get(i).getName().equals(name)) return pcbv.get(i);
		}
		return null;
	}
	
	private static void recursiveTree(PCB parent, int level)
	{
		for(int i=0; i < level; i++)
		{
			System.out.print("|  ");
		}
		System.out.print(parent.getAsTreeElement()+"\n");
		for(int i=0; i < parent.childs.size(); i++)
		{
			recursiveTree(parent.childs.get(i) , level + 1);
		}
	}
	
	private static void removePCB(PCB process, String name)
	{
		for(int i = 0; i < process.childs.size(); i++)
		{
			if(process.childs.get(i).getName().equals(name))
			{
				for(int j = 0; j < process.childs.get(i).childs.size(); j++)
				{
					process.childs.get(i).childs.get(j).parent = init;
					init.childs.addElement(process.childs.get(i).childs.get(j));
				}
				System.out.println("Process " + process.childs.get(i).getAsTreeElement() + " has been terminated!");
				process.childs.remove(i);
			}
			else
			{
				removePCB(process.childs.get(i), name);
			}
		}
	}
	
	private static void removePCB(PCB process, Short pid)
	{
		for(int i = 0; i < process.childs.size(); i++)
		{
			if(process.childs.get(i).getPid() == pid)
			{
				for(int j = 0; j < process.childs.get(i).childs.size(); j++)
				{
					process.childs.get(i).childs.get(j).parent = init;
					init.childs.addElement(process.childs.get(i).childs.get(j));
				}
				System.out.println("Process " + process.childs.get(i).getAsTreeElement() + " has been terminated!");
				process.childs.remove(i);
			}
			else
			{
				removePCB(process.childs.get(i), pid);
			}
		}
	}
	
	
	/*
	//testy drzewa i usuwania
	public static void main(String [] args)
	{
		fork(init, "proc1es");
		fork(init, "proc2es");
		fork(pcbv.get(1), "proc3es");
		fork(init, "proc4es");		
		fork(pcbv.get(1), "proc5es");		
		fork(pcbv.get(4), "proc6es");		
		fork(pcbv.get(3), "proc7es");	
		
		printProcessTree();		
		System.out.println("-----");
		
		exit((short)3);
		
		System.out.println("-----");
		printProcessTree();
		
	}*/
}
