package com.themercerbros.liarsdice.desktop;

import com.themercerbros.liarsdice.desktop.io.IO;

public class StatsMode {
	private final IO io = new IO();
	
	private final int diceCount;
	private final int[] dice;
	private final Guess last;

	public StatsMode(Guess last, int[] diceRolls, int numOfDiceInPlay) {
		this.last = last;
		this.dice = diceRolls;
		this.diceCount = numOfDiceInPlay;
	}

	public void run() {
		io.say("==========STATS=MODE=========");
		io.setIndent(3);
		io.say("There are %d dice in play, and you have %d of them.", diceCount, dice.length);
		if (last != null) {
			io.say("The last guess was %s, which has a %.4f probability of being true.", 
					last.toHumanString(), computeProbability(last, diceCount, dice));
		}
		io.say("What guess do you want to compute the probability of?");
		io.say("(Type \"exit\" to exit stats mode.)");
		
		while (true) {
			String input = io.listen().toLowerCase();
			if (input.equals("exit")) {
				break;
			}
			try {
				Guess guess = Guess.fromHumanInput(input);
				double prob = computeProbability(guess, diceCount, dice);
				if (prob == 1.0f) {
					io.say("That guess is definitely true.");
				} else if (prob == 0.0f) {
					io.say("That guess is definitely not true.");
				} else {
					io.say("There is a %.4f probability of that guess being true.", 
							prob);
				}
			} catch (IllegalArgumentException e){
				io.say("I can't understand that. Try again.");
			}
		}
		io.setIndent(0);
		io.say("=============================");
	}

	public static double computeProbability(Guess guess, int diceInPlay, int[] diceInHand) {
		final int number = guess.number;
		final int populationSize = diceInPlay - diceInHand.length; // Number of unknown dice
		final int quantity = guess.quantity - getCountOfNumber(diceInHand, number);
		if (quantity <= 0) {
			return 1.0; // Player has more than the number guessed.
		} else if (quantity > populationSize) {
			return 0.0;
		}

		double prob = 0.0;
		double probSingleSuccess = 1 / (double) Game.NUM_OF_SIDES;
		for (int i = quantity; i <= populationSize; i++) {
			prob += p(i, populationSize, probSingleSuccess);
		}
		return prob;
	}
	
	private static double p(int numSuccesses, int numTrials, double probSuccess) {
		int numFailures = numTrials - numSuccesses;
		double probFailure = 1 - probSuccess;
		
		double x = 1;
		for (double i = numTrials, j = numSuccesses, k = numFailures; i > 1; i--, j--, k--) {
			x *= i;
			if (j > 1) x /= j;
			if (k > 1) x /= k;
		}
		
		double prob = x * Math.pow(probSuccess, numSuccesses)
				* Math.pow(probFailure, numFailures);
		return prob;
	}

	public static int getCountOfNumber(int[] dice, int number) {
		int count = 0;
		for (int val : dice) {
			if (val == number) count++;
		}
		return count;
	}

	public static int getNumberWithHighestCount(int[] dice) {
		int number = 0, count = 0;
		for (int n : dice) {
			int newCount = getCountOfNumber(dice, n);
			if (newCount > count) {
				number = n;
				count = newCount;
			} else if (newCount == count && n < number) {
				number = n;
			}
		}
		return number;
	}

}
