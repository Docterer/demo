package com.jojo.zzz;

/**
 * 开干
 * 
 * @author jojo
 *
 */
public class Work {

	public static void main(String[] args) {
		new A();
		System.gc();
	}
}

class A {

	@Override
	protected void finalize() throws Throwable {
		System.out.println("被回收了");
	}

}
