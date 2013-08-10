package com.themercerbros.liarsdice.desktop;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.themercerbros.liarsdice.desktop.io.IO;

public enum HistoryHelper {
	INSTANCE;
	
	private File logFile;
	private IO io = new IO();
	private int wins = 0,
			losses = 0,
			offenses = 0,
			defenses = 0,
			longestStreak = 0,
			currentStreak = 0;
	
	private HistoryHelper() {
		logFile = new File("history.log");
		if (!logFile.exists()) {
			try {
				logFile.createNewFile();
			} catch (IOException e) {
				io.say("Error creating history file at " + logFile.getAbsolutePath());
			}
		} else {
			if (logFile.exists()) {
				DataInputStream dis;
				try {
					dis = new DataInputStream(new FileInputStream(logFile));
				} catch (FileNotFoundException e) {
					return;
				}
				
				try {
					wins = dis.readInt();
					losses = dis.readInt();
					offenses = dis.readInt();
					defenses = dis.readInt();
					longestStreak = dis.readInt();
					currentStreak = dis.readInt();
				} catch (IOException e) {
					io.say("Error reading history file.");
					return;
				} finally {
					try {
						dis.close();
					} catch (IOException e) {
						// Nothing to do
					}
				}
			}
		}
		
	}

	public void printHistory() {
		io.say("Wins:                " + wins);
		io.say("Losses:              " + losses);
		io.say("Offenses:            " + offenses);
		io.say("Defenses:            " + defenses);
		io.say("Best winning streak: " + longestStreak);
		io.say("Current streak:      " + currentStreak);
	}
	
	public void onSuccessfulOffense() {
		offenses += 1;
	}
	
	public void onSuccessfulDefense() {
		defenses += 1;
	}

	public void recordWin() {
		wins += 1;
		currentStreak += 1;
		if (currentStreak > longestStreak) {
			longestStreak = currentStreak;
		}
		writeToFile();
	}

	public void recordLoss() {
		losses += 1;
		currentStreak = 0;
		writeToFile();
	}

	public void writeToFile() {
		if (logFile.exists()) {
			DataOutputStream dos;
			try {
				dos = new DataOutputStream(new FileOutputStream(logFile));
			} catch (FileNotFoundException e) {
				return;
			}
			
			try {
				dos.writeInt(wins);
				dos.writeInt(losses);
				dos.writeInt(offenses);
				dos.writeInt(defenses);
				dos.writeInt(longestStreak);
				dos.writeInt(currentStreak);
			} catch (IOException e) {
				io.say("Error writing to history file.");
				return;
			} finally {
				try {
					dos.close();
				} catch (IOException e) {
					// Nothing to do
				}
			}
		}
	}

	public static void clearHistory() {
		new File("history.log").delete();
	}

}
