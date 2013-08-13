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

import java.util.Arrays;

import com.themercerbros.liarsdice.desktop.Call;
import com.themercerbros.liarsdice.desktop.Guess;
import com.themercerbros.liarsdice.desktop.HistoryHelper;
import com.themercerbros.liarsdice.desktop.Main;
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
		Main.beep(2); // beep twice

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
				// Stats mode
				new StatsMode(last, getDiceRolls(), numOfDiceInPlay).run();
				continue;

			} else if (input.equalsIgnoreCase("thoughts on")) {
				// Enable thoughts
				ComputerPlayer.thinkOutLoud = true;
				io.say("Will show CPU thoughts.");
				continue;

			} else if (input.equalsIgnoreCase("thoughts off")) {
				// Disable thoughts
				ComputerPlayer.thinkOutLoud = false;
				io.say("Will not show CPU thoughts.");
				continue;

			} else if (input.equalsIgnoreCase("cpuwait on")) {
				// Enable thoughts
				ComputerPlayer.thinkingDelay = true;
				io.say("CPU will take extra time guessing.");
				continue;

			} else if (input.equalsIgnoreCase("cpuwait off")) {
				// Disable thoughts
				ComputerPlayer.thinkingDelay = false;
				io.say("CPU will not take extra time guessing.");
				continue;

			} else if (input.equalsIgnoreCase("sound on")) {
				// Enable sound
				Main.allowNoise = true;
				io.say("Will play sounds.");
				continue;

			} else if (input.equalsIgnoreCase("sound off")) {
				// Disable sound
				Main.allowNoise = false;
				io.say("Will not play sounds.");
				continue;

			} else if (input.equalsIgnoreCase("history")) {
				// Disable sound
				HistoryHelper.INSTANCE.printHistory();
				continue;

			} else if (last != null && input.equalsIgnoreCase("call")) {
				// Call other player
				throw new Call();
			}

			try {
				return Guess.fromHumanInput(input);
			} catch (IllegalArgumentException e) {
				Main.buzz();
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

	@Override
	public void onWin() {
		HistoryHelper.INSTANCE.recordWin();
	}

	@Override
	public void onLose() {
		HistoryHelper.INSTANCE.recordLoss();
	}

	@Override
	public void onSuccessfulOffense() {
		HistoryHelper.INSTANCE.onSuccessfulOffense();
	}

	@Override
	public void onSuccessfulDefense() {
		HistoryHelper.INSTANCE.onSuccessfulDefense();
	}

}
