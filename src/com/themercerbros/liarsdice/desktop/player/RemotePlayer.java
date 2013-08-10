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
	public static final String EVENT_REGEX = "event (\\w+)";
	public static final String EVENT_FMT = "event %s";
	public static final String EVENT_WIN = "win";
	public static final String EVENT_LOSS = "loss";
	public static final String EVENT_OFFENSE = "offense";
	public static final String EVENT_DEFENSE = "defense";

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
		out.flush();
	}

	@Override
	public void onWin() {
		out.println(String.format(EVENT_FMT, EVENT_WIN));
		out.flush();
	}

	@Override
	public void onLose() {
		out.println(String.format(EVENT_FMT, EVENT_LOSS));
		out.flush();
	}

	@Override
	public void onSuccessfulOffense() {
		out.println(String.format(EVENT_FMT, EVENT_OFFENSE));
		out.flush();
	}

	@Override
	public void onSuccessfulDefense() {
		out.println(String.format(EVENT_FMT, EVENT_DEFENSE));
		out.flush();
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

	public void disconnect() {
		try {
			in.close();
		} catch (IOException e) {
			// Do nothing
		}
		out.close();
		try {
			conn.close();
		} catch (IOException e) {
			// Do nothing
		}
	}

}
