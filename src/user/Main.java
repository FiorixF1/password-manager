package user;

public class Main {

	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("Execute with argument \"cli\" or \"gui\".");
			return;
		}
		
		String parameter = args[0];
		
		if (parameter.equals("cli")) {
			CLI.exec();
		} else if (parameter.equals("gui")) {
			GUI.exec();
		} else {
			System.out.println("Execute with argument \"cli\" or \"gui\".");
		}
	}

}
