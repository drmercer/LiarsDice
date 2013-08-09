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
package com.themercerbros.liarsdice.desktop;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Guess {
	public static final String REGEX = "\\{(\\d+), (\\d+)\\}";
	public final int quantity;
	public final int number;
	
	public Guess(int quantity, int number) {
		if (quantity < 1 || number < 1) {
			throw new IllegalArgumentException("Invalid guess.");
		}
		this.quantity = quantity;
		this.number = number;
	}
	
	@Override
	public String toString() {
		return "{" + quantity + ", " + number + "}";
	}

	public boolean isValidNextGuess(int quantity, int number) {
		return (quantity > this.quantity || (number > this.number && quantity == this.quantity));
	}
	
	public boolean isValidNextGuess(Guess next) {
		return next != null && isValidNextGuess(next.quantity, next.number);
	}

	public static Guess fromString(String string) {
		Pattern p = Pattern.compile(REGEX);
		Matcher m = p.matcher(string);
		if (!m.find()) {
			throw new IllegalArgumentException("string does not contain a Guess.toString() output");
		}
		int quantity = Integer.parseInt(m.group(1));
		int number = Integer.parseInt(m.group(2));
		return new Guess(quantity, number);
	}

	/**
	 * @param input
	 * @return
	 * @throws IllegalArgumentException if the input is not formatted correctly
	 */
	public static Guess fromHumanInput(String input) {
		try {
			String[] tokens = input.split(" ");
			String quantity = tokens[0].toLowerCase();
			String number = tokens[1].toLowerCase();
			
			if (number.endsWith("s")) {
				number = number.substring(0, number.length() - 1);
			}
			
			int q = parseNumber(quantity);
			int n = parseNumber(number);
			
			return new Guess(q, n);
		} catch (RuntimeException e) {
			throw new IllegalArgumentException("Invalid input.", e);
		}
	}

	private static int parseNumber(String str) {
		try {
			return Integer.parseInt(str);
		} catch (NumberFormatException e) {
			if (str.matches("one")) {
				return 1;
			} else if (str.matches("two")) {
				return 2;
			} else if (str.matches("three")) {
				return 3;
			} else if (str.matches("four")) {
				return 4;
			} else if (str.matches("five")) {
				return 5;
			} else if (str.matches("sixe?")) {
				return 6;
			} else {
				throw new IllegalArgumentException("Could not parse number.", e);
			}
		}
	}

	public String toHumanString() {
		StringBuilder sb = new StringBuilder();
		sb.append(quantity);
		sb.append(" ");
		
		numberToHumanString(sb, number, quantity != 1);
		return sb.toString();
	}

	private static void numberToHumanString(StringBuilder sb, int number, boolean plural) {
		switch (number) {
		case 1:
			sb.append("one");
			break;
		case 2:
			sb.append("two");
			break;
		case 3:
			sb.append("three");
			break;
		case 4:
			sb.append("four");
			break;
		case 5:
			sb.append("five");
			break;
		case 6:
			sb.append("six");
			break;
		default:
			sb.append(number);
			break;
		}
		
		if (plural) {
			if (number != 6) {
				sb.append("s");
			} else {
				sb.append("es");
			}
		}
	}
	
	public static String numberToHumanString(int number, boolean plural) {
		StringBuilder sb = new StringBuilder();
		numberToHumanString(sb, number, plural);
		return sb.toString();
	}
}
