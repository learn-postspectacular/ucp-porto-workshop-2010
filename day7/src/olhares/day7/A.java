package olhares.day7;

import java.util.ArrayList;

public class A {

	public static int count=0;
	
	public int foo;
	
	public A(int f) {
		foo=f;
		count++;
	}
	
	
	public static void main(String[] args) {
		A a1=new A(5);
		A a2=new A(88);
		A a3=new A(27);
		ArrayList<A> list = new ArrayList<A>();
		list.add(a3);
		list.add(a2);
		
		System.out.println("a1: "+a1.foo);
		System.out.println("a2: "+a2.foo);
		System.out.println(a1.count);
		System.out.println(a2.count);
	}
}
