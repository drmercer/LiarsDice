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
package com.themercerbros.liarsdice.desktop.client;

import java.util.Arrays;

import com.themercerbros.liarsdice.desktop.player.HumanPlayer;

public class ClientPlayer extends HumanPlayer {
	private int[] rolls;

	public ClientPlayer(int numOfDice) {
		super(numOfDice);
	}

	public ClientPlayer(int numOfDice, String name) {
		super(numOfDice, name);
	}

	@Override
	public int[] getDiceRolls() {
		return rolls;
	}
	
	public void setDiceRolls(int[] dice) {
		this.rolls = dice;
		io.say("You rolled " + Arrays.toString(dice));
	}

	@Override
	public void rollDice() {
		throw new UnsupportedOperationException("Should not roll dice on client side.");
	}

}
