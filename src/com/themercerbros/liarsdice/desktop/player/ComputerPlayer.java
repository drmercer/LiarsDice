package com.themercerbros.liarsdice.desktop.player;

import java.security.SecureRandom;
import java.util.Random;

import com.themercerbros.liarsdice.desktop.Call;
import com.themercerbros.liarsdice.desktop.Game;
import com.themercerbros.liarsdice.desktop.Guess;
import com.themercerbros.liarsdice.desktop.StatsMode;

public class ComputerPlayer extends Player {
	public static boolean thinkOutLoud = false;

	private static final Random rand = new SecureRandom();
	private final double alpha = rand.nextDouble() * 0.5 + 0.1;
	private final double gutsiness = rand.nextDouble();

	private final String name;
	
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
		try {
			Thread.sleep(3500);
		} catch (InterruptedException e) {
			// Do nothing.
		}

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
			double r1 = rand.nextDouble() * .5 - .4;
			if (last.number > 1 && (lastGuessProb + r1) < alpha) {
				think("Calling, because (%.3f%+.3f) < %.3f", lastGuessProb, r1, alpha);
				throw new Call();
			}
			int number, quantity;
			if (last.quantity > 1.5 * (rand.nextDouble() + .5) * alpha * numOfDiceInPlay
					&& last.number < Game.NUM_OF_SIDES) {
				number = last.number + 1;
				quantity = last.quantity;
			} else if (last.quantity < numOfDiceInPlay) {
				quantity = last.quantity + 1;
				number = StatsMode.getNumberWithHighestCount(diceRolls);
			} else {
				throw new Call();
			}
			think("Guessing %d %ds", quantity, number);
			return new Guess(quantity, number);
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
