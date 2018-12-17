
public class checking {
	public static boolean flag=false;

	public void check(String data) {
		String[] arg=data.split("\\s+");
		
		
		if(arg[0].toString().equals("rm")){
			System.out.println("deletingObject arg:"+arg[1]);
			
		}
		if(arg[0].toString().equals("ls")){
			System.out.println("GetCurrentDirInfo");
			
		}
		if(arg[0].toString().equals("kill")){
			System.out.println("exit arg:"+arg[1]);
			
		}
		if(arg[0].toString().equals("ps")){
			System.out.println("process name");
			
		}
		if(arg[0].toString().equals("exit")){
			flag=true;
			
		}
		
		if(arg[0].toString().equals("help")){
			System.out.println("rm nazwa_pliku -usuwa plik/katalog");
			System.out.println("ls -listowanie katalogu");
			System.out.println("kill nazwa_procesu -zabija podany proces");
			System.out.println("ps -zwraca nazwê aktualnie wykonywanego procesu");
			System.out.println("exit -wy³¹czanie");
			System.out.println("help -wyswietla pomoc");
			
			
			
		}
		
		
	
	}
}
