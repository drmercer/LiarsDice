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

import java.security.SecureRandom;
import java.util.Random;

import com.themercerbros.liarsdice.desktop.Call;
import com.themercerbros.liarsdice.desktop.Game;
import com.themercerbros.liarsdice.desktop.Guess;
import com.themercerbros.liarsdice.desktop.io.IO;
import com.themercerbros.liarsdice.desktop.player.RemotePlayer.Quit;

public abstract class Player {
	protected final IO io = new IO();
	private int[] rolls;
	
	public Player(int numOfDice) {
		rolls = new int[numOfDice];
	}
	
	public void rollDice() {
		final Random r = new SecureRandom();
		for (int i = 0; i < rolls.length; i++) {
			rolls[i] = r.nextInt(Game.NUM_OF_SIDES) + 1;
		}
	}
	
	public int[] getDiceRolls() {
		return rolls;
	}

	public int getDiceCount() {
		return getDiceRolls().length;
	}
	
	/**
	 * If this is the first turn this round, last is null;
	 * @param last
	 * @param numOfDiceInPlay
	 * @return
	 * @throws Call If the player calls out the previous guess
	 * @throws Quit If the player quits
	 */
	public abstract Guess takeTurn(Guess last, int numOfDiceInPlay) throws Call, Quit;
	
	/**
	 * Returns a name for this Player
	 * @return
	 */
	public abstract String getName();

	public void loseOne() {
		int newCount = rolls.length - 1;
		if (newCount < 0) {
			newCount = 0;
		}
		rolls = new int[newCount];
	}

	public boolean isOut() {
		return rolls.length == 0;
	}
	
	/**
	 * Does nothing by default
	 */
	public void tell(String message) {
		// Does nothing by default
	}
	
	/**
	 * Does nothing by default
	 */
	public void onWin() {
		// Does nothing by default
	}

	/**
	 * Does nothing by default
	 */
	public void onLose() {
		// Does nothing by default
	}
	
	/**
	 * Does nothing by default
	 */
	public void onSuccessfulOffense() {
		// Does nothing by default
	}
	
	/**
	 * Does nothing by default
	 */
	public void onSuccessfulDefense() {
		// Does nothing by default
	}

}
