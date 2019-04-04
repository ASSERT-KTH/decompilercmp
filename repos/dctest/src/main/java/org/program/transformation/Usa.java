package org.program.transformation;

public class Usa {
	public String name = "Detroit";
	public England england = new England();

	public class England
	{
		public String name = "London";
		public Ireland ireland = new Ireland();

		public class Ireland
		{
			public String name = "Dublin";

			public void print_names() {
				System.out.println(name);
			}
		}
	}

	public static void main(String[] args) {
		Usa usa = new Usa();
		System.out.println(usa.name);
		System.out.println(usa.england.name);
		System.out.println(usa.england.ireland.name);
		usa.england.ireland.print_names();
	}
}
