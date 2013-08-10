/*
 * Copyright 2013 Dan Mercer
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.themercerbros.liarsdice.desktop.io;

import java.io.PrintStream;
import java.util.Scanner;

public class IO {
	private final PrintStream o = System.out;
	private static final Scanner i = new Scanner(System.in);
	private int indent = 0;
	
	private String indent(String msg) {
		if (indent == 0) {
			return msg;
		} else {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < indent; i++) {
				sb.append(" ");
			}
			sb.append(msg);
			return sb.toString();
		}
	}
	
	public void say(String message, Object... args) {
		if (args != null && args.length != 0) {
			message = String.format(message, args);
		}
		o.println(indent(message));
	}
	
	public String listen() {
		o.print(indent(" >> "));
		return i.nextLine();
	}
	
	public String ask(String prompt) {
		say(prompt);
		return listen();
	}

	public int askForInt(String prompt) {
		say(prompt);
		
		while (true) {
			String input = listen();
			try {
				int i = Integer.parseInt(input);
				return i;
			} catch (NumberFormatException e) {
			}
			say("That is not an integer. Please try again (Ctrl+C to quit).");
		}
	}

	/**
	 * @param prompt
	 * @param min The inclusive min
	 * @param max The exclusive max
	 * @return
	 */
	public int askForInt(String prompt, int min, int max) {
		say(prompt);
		
		while (true) {
			String input = listen();
			try {
				int i = Integer.parseInt(input);
				if (i < max && i >= min) {
					return i;
				}
			} catch (NumberFormatException e) {
			}
			say("That is not a valid response. Please try again (Ctrl+C to quit).");
		}
	}

	public boolean askBoolean(String string) {
		String input = ask(string + " (Y/N)");
		return input.toLowerCase().matches("y|yes|true|affirmative");
	}

	public void setIndent(int newIndent) {
		indent = newIndent;
	}

}
