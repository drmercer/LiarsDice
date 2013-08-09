package com.themercerbros.liarsdice.desktop.player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;

import com.themercerbros.liarsdice.desktop.Call;
import com.themercerbros.liarsdice.desktop.Guess;

public class RemotePlayer extends Player {
	public static class Quit extends Exception {}

	public static final String TAKE_TURN_PREFIX = "take_turn ";
	public static final String MESSAGE_REGEX = "msg\\: \".*\"";
	public static final String MESSAGE_FMT = "msg: \"%s\"";
	public static final String CALL = "call";
	public static final String GUESS = "guess ";
	public static final String ROLLED = "rolled ";
	public static final String TAKE_TURN_FIRST_REGEX = TAKE_TURN_PREFIX + "(\\d+)";
	public static final String TAKE_TURN_REGEX = TAKE_TURN_PREFIX + Guess.REGEX + " (\\d+)";

	private final Socket conn;
	private final BufferedReader in;
	private final PrintWriter out;
	
	private final String name;
	
	public RemotePlayer(int numOfDice, ServerSocket server, String hostPlayerName) throws IOException {
		super(numOfDice);
		System.out.print("Tell your friend to join the game.");
		conn = server.accept();
		in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		out = new PrintWriter(conn.getOutputStream());
		System.out.print(".");
		
		out.println(hostPlayerName);
		out.println(numOfDice);
		out.flush();
		this.name = in.readLine();
		System.out.println(".");
	}

	@Override
	public void rollDice() {
		super.rollDice(); // TODO: left off here
		String string = Arrays.toString(getDiceRolls());
		out.println(ROLLED + string);
//		tell("You rolled " + string);
	}

	@Override
	public Guess takeTurn(Guess last, int numOfDiceInPlay) throws Call, Quit {
		if (last == null) { // First turn of this round
			out.println(TAKE_TURN_PREFIX + numOfDiceInPlay);
		} else {
			out.println(TAKE_TURN_PREFIX + last.toString() + " " + numOfDiceInPlay);
		}
		out.flush();
		String result;
		try {
			result = in.readLine();
		} catch (SocketException e) {
			throw new Quit();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
			return null;
		}
		if (last != null && result.matches(CALL)) {
			throw new Call();
		}
		
		return Guess.fromString(result);
	}

	@Override
	public String getName() {
		return name;
	}

	public void tell(String message) {
		String msg = String.format(RemotePlayer.MESSAGE_FMT, message);
		out.println(msg);
		out.flush();
	}

}
