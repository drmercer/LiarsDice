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
package com.themercerbros.liarsdice.desktop;

import java.util.ArrayList;
import java.util.HashMap;

import com.themercerbros.liarsdice.desktop.client.Client;
import com.themercerbros.liarsdice.desktop.io.IO;


public class Main {
	private static final String USAGE = 
			"USAGE: [[ --port <port-number> ] [ --dice-per-player <num-of-dice> ] | --join <ip-address> <port-number> ]\n" +
			"For more help, see the README at https://github.com/drmercer/LiarsDice";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		IO io = new IO();
		
		io.say("  _      _____drmercer's_____  _  _____    _____  _          ");
		io.say(" | |    |_   _|   /\\   |  __ \\( )/ ____|  |  __ \\(_)         ");
		io.say(" | |      | |    /  \\  | |__) |/| (___    | |  | |_  ___ ___ ");
		io.say(" | |      | |   / /\\ \\ |  _  /   \\___ \\   | |  | | |/ __/ _ \\");
		io.say(" | |____ _| |_ / ____ \\| | \\ \\   ____) |  | |__| | | (_|  __/");
		io.say(" |______|_____/_/    \\_\\_|  \\_\\ |_____/   |_____/|_|\\___\\___|");
		io.say("\n              Welcome to Liar's Dice!\n");
		
		boolean join = false;
		String address = null;
		int port = 4444;
		int numOfDice = 0;
		
		HistoryHelper.INSTANCE.printHistory();

		ArrayList<String> argsList = new ArrayList<String>();
		HashMap<String, String> argVals = new HashMap<String, String>();
		
		try {
			for (int i = 0; i < args.length; i++) {
				String arg = args[i];
				if (arg.equalsIgnoreCase("--clear-history")) {
					if (args.length != 1) {
						throw new IllegalArgumentException();
					}
					if (io.askBoolean("Are you sure you want to clear your history?")) {
						HistoryHelper.clearHistory();
					}
					return;
				} else if (arg.equalsIgnoreCase("--help")) {
					printUsage();
					return;
				} else if (arg.equalsIgnoreCase("--port") && !join) {
					if (args.length <= i + 1) {
						throw new IllegalArgumentException();
					}
					port = Integer.parseInt(args[i + 1]);
					i += 1;

				} else if (arg.equals("--join")) {
					join = true;
					if (args.length > i + 2) {
						if (args[i + 1].matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")
								&& args[i + 2].matches("\\d+")) {
							address = args[i + 1];
							port = Integer.parseInt(args[i + 2]);
							i += 2;
						}
					}
				} else if (arg.equals("--dice-per-player")) {
					if (args.length <= i + 1) {
						throw new IllegalArgumentException();
					}
					try {
						int num = Integer.parseInt(args[i + 1]);
						if (num > 0)
							numOfDice = num;
					} catch (NumberFormatException e) {
						throw new IllegalArgumentException(e);
					}
					i += 1;
				} else {
					if (args.length <= i + 1) {
						argsList.add(arg);
						continue;
					}
					String next = args[i+1];
					if (next.matches("--[\\w-]+")) {
						argsList.add(arg);
					} else {
						argVals.put(arg, next);
						i += 1;
					}
				}
				
			}
			
		} catch (IllegalArgumentException e) {
			printUsage();
			return;
		}
		
		if (!join) {
			do {
				Game g = new Game(port, numOfDice, argVals, argsList);
				g.play();
			} while (io.askBoolean("Play again? :)"));
		} else {
			do {
				Client c = new Client(address, port, argVals);
				c.run();
				if (!io.askBoolean("Join another game? :)")) {
					break;
				}
				if (!io.askBoolean("Join the same address and port?")) {
					address = io.ask("What IP address?");
					port = io.askForInt("  and what port?", 1, Integer.MAX_VALUE);
				}
			} while (true);
		}
	}

	private static void printUsage() {
		System.out.print(USAGE);
	}

}
