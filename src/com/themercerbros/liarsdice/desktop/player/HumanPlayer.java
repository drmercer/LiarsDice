package com.themercerbros.liarsdice.desktop.player;

import java.util.Arrays;

import com.themercerbros.liarsdice.desktop.Call;
import com.themercerbros.liarsdice.desktop.Guess;
import com.themercerbros.liarsdice.desktop.StatsMode;

public class HumanPlayer extends Player {
	public final String name;
	
	public HumanPlayer(int numOfDice, String name) {
		super(numOfDice);
		this.name = name;
	}

	public HumanPlayer(int numOfDice) {
		super(numOfDice);
		this.name = io.ask("What is your name?");
	}

	@Override
	public void rollDice() {
		super.rollDice();
		tell("You rolled " + Arrays.toString(getDiceRolls()));
	}

	@Override
	public Guess takeTurn(Guess last, int numOfDiceInPlay) throws Call {
		String prompt;
		if (last == null) {
			io.say("You're first, " + name + "!");
			prompt = "What is your guess?";
		} else {
			io.say("You're up, " + name + "!");
			prompt = "What is your guess? (Type \"call\" to call out the previous guess.)";
		}
		do {
			String input = io.ask(prompt);
			if (input.equalsIgnoreCase("stats")) {
				new StatsMode(last, getDiceRolls(), numOfDiceInPlay).run();
				continue;
			} else if (input.equalsIgnoreCase("hide thoughts")) {
				ComputerPlayer.thinkOutLoud = false;
				io.say("Will not show CPU thoughts.");
				continue;
			} else if (input.equalsIgnoreCase("show thoughts")) {
				ComputerPlayer.thinkOutLoud = true;
				io.say("Will show CPU thoughts.");
				continue;
			} else if (last != null && input.equalsIgnoreCase("call")) {
				throw new Call();
			}
			try {
				return Guess.fromHumanInput(input);
			} catch (IllegalArgumentException e) {
				io.say("I can't understand that. Try again.");
			}
		} while (true);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void tell(String message) {
		io.say(message);
	}

}
