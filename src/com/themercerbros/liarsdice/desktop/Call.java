package com.themercerbros.liarsdice.desktop;

public class Call extends Exception {

	public Call() {
		super();
	}

	public Call(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	public Call(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public Call(String arg0) {
		super(arg0);
	}

	public Call(Throwable arg0) {
		super(arg0);
	}

}
