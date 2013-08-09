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
