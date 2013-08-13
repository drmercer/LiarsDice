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
	public static boolean thinkingDelay = true;

	private static final Random rand = new SecureRandom();
	private double alpha = rand.nextDouble() * 0.5 + 0.1; // range [.1, .6]
	private final double gutsiness = rand.nextDouble();

	private final String name;
	private boolean justCalled = false;

	
	public static void main(String[] args) {
		IO io = new IO();
		
		ComputerPlayer cp = new ComputerPlayer(5);
		cp.rollDice();
		io.say(Arrays.toString(cp.getDiceRolls()));
		
		for (int i = 0; i < 100; i++) {
			try {
				io.say(cp.takeTurn(null, 10).toHumanString());
			} catch (Call e) {
				io.say("Call!");
			}
		}
	}
	
	public ComputerPlayer(int numOfDice) {
		super(numOfDice);
		name = getNewName(rand);
	}
	
	@Override
	public void loseOne() {
		super.loseOne();
		onLoseADie();
	}
	
	private void onLoseADie() {
		// Learning algorithm!
		if (justCalled) {
			// If losing a dice because I called out a correct guess
			alpha += Math.max(.1, (1 - alpha) * .3); // Increase alpha
			if (alpha > 1) {
				alpha = 1;
			}
		} else {
			// If losing a dice because I made a bad guess
			alpha -= Math.max(.1, (alpha) * .3); // Decrease alpha
			if (alpha < 0) {
				alpha = 0;
			}
		}
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
		if (thinkingDelay){
			try {
				Thread.sleep(3500);
			} catch (InterruptedException e) {
				// Do nothing.
			}
		}
		
		justCalled = false;

		int[] diceRolls = getDiceRolls();
		if (last == null) { // First guess in round

			double weight = (10 / diceRolls.length) * (1 - gutsiness);
			double x = (Game.NUM_OF_SIDES + (diceRolls.length * weight));
			double[] thresholds = new double[Game.NUM_OF_SIDES];
			double lastThreshold = 0.0;
			for (int i = 0; i < thresholds.length; i++) {
				lastThreshold += (1 + StatsMode.getCountOfNumber(diceRolls, i + 1) * weight) / x;
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
			if (gutsiness > rand.nextDouble() + .4) {
				quantity++;
				if (gutsiness > rand.nextDouble() + .7) {
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
				justCalled = true;
				throw new Call();
			}

			if (last.quantity >= numOfDiceInPlay) {
				think("Calling, because they guessed all the dice");
				justCalled = true;
				throw new Call();
			}

			int number, quantity;
			int count = StatsMode.getCountOfNumber(diceRolls, last.number);
			int claimedCount = getClaimedCount(count);
			if (claimedCount > 0) {
				number = last.number;
				quantity = last.quantity + claimedCount;

			} else if (last.number < Game.NUM_OF_SIDES) {
				int min = last.number + 1;
				number = StatsMode.getNumberWithHighestCount(diceRolls, min);
				if (number == 0) {
					number = min + rand.nextInt(Game.NUM_OF_SIDES + 1 - min);
				}
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
