
import java.util.Scanner;
public class loop  {
	Scanner reader = new Scanner(System.in); 
	checking checking;
	public void start() {
		checking=new checking();
		while(true) {
			if(checking.flag==true) {
				break;
			}
			String data=new String();
			data=reader.nextLine();
			checking.check(data);
		}
	}}
