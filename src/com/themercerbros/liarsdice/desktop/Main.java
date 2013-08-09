package com.themercerbros.liarsdice.desktop;

import java.util.ArrayList;
import java.util.HashMap;

import com.themercerbros.liarsdice.desktop.client.Client;
import com.themercerbros.liarsdice.desktop.io.IO;


public class Main {
	private static final String USAGE = 
			"USAGE: [[ --port <port-number> ] [ --dice-per-player <num-of-dice> ] | --join <ip-address> <port-number> ]";

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
		

		ArrayList<String> argsList = new ArrayList<String>();
		HashMap<String, String> argVals = new HashMap<String, String>();
		
		try {
			for (int i = 0; i < args.length; i++) {
				// --publish switch
				String arg = args[i];
				if (arg.equalsIgnoreCase("--help")) {
					printUsage();
					return;
				}
				if (arg.equalsIgnoreCase("--port")) {
					if (args.length <= i + 1) {
						throw new IllegalArgumentException();
					}
					try {
						port = Integer.parseInt(args[i + 1]);
					} catch (NumberFormatException e) {
						throw new IllegalArgumentException(e);
					}
					i += 1;

				} else if (arg.equals("--join")) {
					join = true;
					if (args.length > i + 2) {
						address = args[i + 1];
						try {
							port = Integer.parseInt(args[i + 2]);
						} catch (NumberFormatException e) {
							throw new IllegalArgumentException(e);
						}
						i += 2;
					} else {
						address = io.ask("Join what IP address?");
						port = io.askForInt("   and what port?", 1, Integer.MAX_VALUE);
						break;
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
