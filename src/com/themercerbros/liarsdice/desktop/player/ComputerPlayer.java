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
import java.util.Arrays;
import java.util.Random;

import com.themercerbros.liarsdice.desktop.Call;
import com.themercerbros.liarsdice.desktop.Game;
import com.themercerbros.liarsdice.desktop.Guess;
import com.themercerbros.liarsdice.desktop.StatsMode;
import com.themercerbros.liarsdice.desktop.io.IO;

public class ComputerPlayer extends Player {
	public static boolean thinkOutLoud = false;

	private static final Random rand = new SecureRandom();
	private final double alpha = rand.nextDouble() * 0.5 + 0.1; // range [.1, .6]
	private final double gutsiness = rand.nextDouble();

	private final String name;
	
	public static void main(String[] args) {
		IO io = new IO();
		
		ComputerPlayer cp = new ComputerPlayer(5);
		cp.rollDice();
		io.say(Arrays.toString(cp.getDiceRolls()));
		
		do {
			Guess g = Guess.fromHumanInput(io.ask("please guess >> "));
			try {
				io.say(cp.takeTurn(g, 10).toHumanString());
			} catch (Call e) {
				io.say("Call");
			}
		} while (true);
	}
	
	public ComputerPlayer(int numOfDice) {
		super(numOfDice);
		name = getNewName(rand);
	}
	
	private static String getNewName(Random r) {
		char a = (char) ('A' + r.nextInt(26));
		char b = (char) ('A' + r.nextInt(26));
		int c = r.nextInt(10);
		int d = r.nextInt(10);
		return String.format("%c%c-%d%d", a, b, c, d);
	}

	@Override
	public Guess takeTurn(Guess last, int numOfDiceInPlay) throws Call {
//		try {
//			Thread.sleep(3500);
//		} catch (InterruptedException e) {
//			// Do nothing.
//		}

		int[] diceRolls = getDiceRolls();
		if (last == null) { // First guess in round
			double x = 1 / (double) (Game.NUM_OF_SIDES + diceRolls.length);
			double[] thresholds = new double[Game.NUM_OF_SIDES];
			double lastThreshold = 0.0;
			for (int i = 0; i < thresholds.length; i++) {
				lastThreshold += (1 + StatsMode.getCountOfNumber(diceRolls, i + 1)) * x;
				thresholds[i] = lastThreshold;
			}
			double r = rand.nextDouble();
			int number = 1;
			for (int i = 0; i < thresholds.length; i++) {
				if (thresholds[i] > r) {
					number = i + 1;
					break;
				}
			}
			int quantity = Math.max(1, StatsMode.getCountOfNumber(diceRolls, number));
			if (gutsiness > rand.nextDouble()) {
				quantity++;
				if (gutsiness > rand.nextDouble() + .6) {
					quantity++;
				}
			}
			think("Guessing %d %ds (first guess)", quantity, number);
			return new Guess(quantity, number);
			
		} else { // Continuing round
			double lastGuessProb = StatsMode.computeProbability(last, numOfDiceInPlay, diceRolls);
			double r1 = rand.nextDouble() * .2 - .1; // range [-.1, .1]
			if ((rand.nextDouble() > .75 || last.quantity > 1) && (lastGuessProb + r1) < alpha) {
				think("Calling, because (%.3f%+.3f) < %.3f", lastGuessProb, r1, alpha);
				throw new Call();
			}

			if (last.quantity >= numOfDiceInPlay) {
				think("Calling, because they guessed all the dice");
				throw new Call();
			}

			int number, quantity;
			int count = StatsMode.getCountOfNumber(diceRolls, last.number);
			int claimedCount = getClaimedCount(count);
			if (claimedCount > 0) {
				number = last.number;
				quantity = last.quantity + claimedCount;

			} else if (last.number < Game.NUM_OF_SIDES) {
				number = StatsMode.getNumberWithHighestCount(diceRolls, last.number + 1);
				quantity = Math.max(StatsMode.getCountOfNumber(diceRolls, number), last.quantity);

			} else { // Nowhere to go but up
				quantity = last.quantity + 1;
				number = StatsMode.getNumberWithHighestCount(diceRolls);
				if (count >= StatsMode.getCountOfNumber(diceRolls, number)) {
					number = last.number;
				}
			}
			think("Guessing %d %ds", quantity, number);
			return new Guess(quantity, number);
		}
	}

	private int getClaimedCount(int count) {
		double r = (rand.nextDouble() * 2 - 1) * gutsiness; // range [-1, 1]
		if (r < -.1) {
			return (int) (count * (-r));
		} else if (r > .4) {
			return count + 1;
		} else {
			return count;
		}
	}
	
	private void think(String thought, Object... args) {
		if (thinkOutLoud)
			io.say(" ** " + thought, args);
	}

	@Override
	public String getName() {
		return name;
	}

}
