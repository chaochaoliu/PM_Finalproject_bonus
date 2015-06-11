/*
 * File: Yahtzee.java
 * ------------------
 * This program will eventually play the Yahtzee game.
 */

import acm.io.*;
import acm.program.*;
import acm.util.*;

public class Yahtzee extends GraphicsProgram implements YahtzeeConstants {

	public static void main(String[] args) {
		new Yahtzee().start(args);
	}

	public void run() {
		IODialog dialog = getDialog();
		nPlayers = dialog.readInt("Enter number of players");
		playerNames = new String[nPlayers];
		for (int i = 1; i <= nPlayers; i++) {
			playerNames[i - 1] = dialog.readLine("Enter name for player " + i);
		}
		display = new YahtzeeDisplay(getGCanvas(), playerNames);
		setUp();
		playGame();
	}
/*
 * 
 */
	private void playGame() {
		
		for (int i = 0; i < 3; i++) {
			for (int j = 1; j <= nPlayers; j++) {

				display.printMessage(playerNames[j - 1]
						+ "'s turn! Click \"Roll Dice\" button to roll the dice.");
				display.waitForPlayerToClickRoll(j);

				firstRoll();

				for (int k = 0; k < 2; k++) {

					display.printMessage("Select the dice you wish to re-roll and click \"Roll Again\"");
					display.waitForPlayerToSelectDice();
					rollAgain();
				}

				display.printMessage("Select a category for this roll.");
				category = display.waitForPlayerToSelectCategory();

				updateScore(dice, category, j);
			}
		}
		for (int i = 1; i <= nPlayers; i++) {

			display.updateScorecard(UPPER_SCORE, i, upperScore[i - 1]);
			display.updateScorecard(LOWER_SCORE, i, lowerScore[i - 1]);

			upperBonus[i - 1] = getBonus(upperScore[i - 1]);

			display.updateScorecard(UPPER_BONUS, i, upperBonus[i - 1]);

			totalScore[i - 1] = upperScore[i - 1] + upperBonus[i - 1]
					+ lowerScore[i - 1];

			display.updateScorecard(TOTAL, i, totalScore[i - 1]);

		}
		int winner = getWinner(totalScore);
		display.printMessage("Congratulations, " + playerNames[winner-1] + " ,you're the winner with a total score of " + totalScore[winner-1]);
		
	}
	
	

	private int getWinner(int[] totalScore) {
		int winner = 0;
		int topScore = 0;
		for(int i = 0; i < totalScore.length; i++){
			if(totalScore[i] > topScore){
				topScore = totalScore[i];
				winner = i + 1;
			}
		}
		return winner;
	}

	private void setUp() {
		upperScore = new int[nPlayers];
		lowerScore = new int[nPlayers];
		totalScore = new int[nPlayers];
		upperBonus = new int[nPlayers];

	}

	private int getBonus(int upperScore) {

		int upperScoreBonus;
		if (upperScore >= 63) {
			upperScoreBonus = 35;
		} else {
			upperScoreBonus = 0;
		}
		return upperScoreBonus;
	}

	private void updateScore(int[] dice, int category, int player) {

		if (checkCategory(dice, category)) {

			score = getScore(dice, category, player);
			display.updateScorecard(category, player, score);
			display.updateScorecard(TOTAL, player, totalScore[player - 1]);

		} else {
			display.updateScorecard(category, player, 0);
		}

	}

	private boolean checkCategory(int[] dice, int category) {

		int[] occDis = occDis(dice);
		int[] topTwo = getTopTwo(occDis);

		switch (category) {
		case ONES:
		case TWOS:
		case THREES:
		case FOURS:
		case FIVES:
		case SIXES:
		case CHANCE:
			return true;
		case THREE_OF_A_KIND:
			if (topTwo[0] >= 3) {
				return true;
			} 
			break;
		case FOUR_OF_A_KIND:
			if (topTwo[0] >= 4) {
				return true;
			} 
			break;
		case FULL_HOUSE:
			if (topTwo[0] == 3 && topTwo[1] == 2) {
				return true;
			}
			break;
		case SMALL_STRAIGHT:
			return (checkSmallStraight(occDis));
		case LARGE_STRAIGHT:
			if (topTwo[0] == 1 && (occDis[0] == 0 || occDis[5] == 0)) {
				return true;
			}
			break;
		case YAHTZEE:
			if (topTwo[0] == 5) {
				return true;
			}
			break;

		}
		return false;
	}

	private boolean checkSmallStraight(int[] occDis) {
		for (int i = 0; i < 3; i++) {
			if (occDis[i] >= 1 && occDis[i + 1] >= 1 && occDis[i + 2] >= 1
					&& occDis[i + 3] >= 1) {
				return true;
			}
		}
		return false;
	}

	private int[] occDis(int[] dice) {

		int[] occDis = new int[6];

		for (int i = 0; i < dice.length; i++) {
			switch (dice[i]) {
			case 1:
				occDis[0]++;
				break;
			case 2:
				occDis[1]++;
				break;
			case 3:
				occDis[2]++;
				break;
			case 4:
				occDis[3]++;
				break;
			case 5:
				occDis[4]++;
				break;
			case 6:
				occDis[5]++;
				break;
			}
		}
		return occDis;

	}

	private int[] getTopTwo(int occDis[]) {
		int[] topTwo = new int[2];

		int largest = 0;
		int secondLargest = 0;

		for (int i = 0; i < occDis.length; i++) {
			if (occDis[i] > largest) {
				largest = occDis[i];
			} else if (occDis[i] > secondLargest) {
				secondLargest = occDis[i];
			}
		}

		topTwo[0] = largest;
		topTwo[1] = secondLargest;

		return topTwo;
	}

	private void rollAgain() {

		for (int index = 0; index < dice.length; index++) {
			if (display.isDieSelected(index)) {
				dice[index] = rgen.nextInt(1, 6);
			}
		}
		display.displayDice(dice);

	}

	private void firstRoll() {

		for (int i = 0; i < dice.length; i++) {
			dice[i] = rgen.nextInt(1, 6);
		}
		display.displayDice(dice);
	}

	private int getScore(int[] dice, int category, int player) {
		int score = 0;
		switch (category) {
		case ONES:
		case TWOS:
		case THREES:
		case FOURS:
		case FIVES:
		case SIXES:
			for (int i = 0; i < dice.length; i++) {
				if (dice[i] == category) {
					score += dice[i];
				}
			}

			upperScore[player - 1] = score;
			break;
		case THREE_OF_A_KIND:
			score = sum(dice);
			lowerScore[player - 1] += score;
			break;
		case FOUR_OF_A_KIND:
			score = sum(dice);
			lowerScore[player - 1] += score;
			break;
		case FULL_HOUSE:
			score = 25;
			lowerScore[player - 1] += score;
			break;
		case SMALL_STRAIGHT:
			score = 30;
			lowerScore[player - 1] += score;
			break;
		case LARGE_STRAIGHT:
			score = 40;
			lowerScore[player - 1] += score;
			break;
		case YAHTZEE:
			score = 50;
			lowerScore[player - 1] += score;
			break;
		case CHANCE:
			score = sum(dice);
			lowerScore[player - 1] += score;
			break;
		}
		totalScore[player - 1] += score;
		return score;
	}

	private int sum(int[] dice) {
		int sum = 0;
		for (int i = 0; i < dice.length; i++) {
			sum += dice[i];
		}
		return sum;
	}

	/* Private instance variables */
	private int nPlayers;
	private String[] playerNames;
	private YahtzeeDisplay display;
	private RandomGenerator rgen = new RandomGenerator();
	private int[] dice = new int[N_DICE];
	private int score;
	private int category;
	private int[] upperScore;
	private int[] lowerScore;
	private int[] totalScore;
	private int[] upperBonus;

}
