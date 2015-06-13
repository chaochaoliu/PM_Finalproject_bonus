/*
 * File: Yahtzee.java
 * ------------------
 * This program will eventually play the Yahtzee game.
 * Each round the game can have 1~4 players.After each turn, the player
 * can choose one of the 13 categories. If the upper score is higher than 63,
 * the player can get 35 bonus. The player with the higher total score will win
 * in the game.
 * 
 * Author:Chao Liu
 * Contact: liuchao@200240@gmail.com
 * 
 * Total grade: 100
 * How to grade:
 * 1. Must run correct (60%): 60
 * 2. Proper Comments: file comment and function comment (10%): 10
 * 3. Follow the style guideline (10%): 10
 * 4. A right "top-down" Decomposition (20%): 20
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
 *  Method: playGame()
 * ------------------
 * This method enable the players to play the game.
 */
	private void playGame() {

		for (int i = 0; i < 13; i++) {
			for (int j = 1; j <= nPlayers; j++) {
               playOneTurn(j);
			}
		}
		
		calculateFinalScore();
		
		printFinalScore();
		
		printResult(getWinner(scorecard));

	}
/*
 * Method: playOneTurn(int player)
 * ------------------
 * This method enable one player to play one turn of game.
 */
private void playOneTurn(int player) {
	
	display.printMessage(playerNames[player - 1]
			+ "'s turn! Click \"Roll Dice\" button to roll the dice.");
	display.waitForPlayerToClickRoll(player);

	firstRoll();
	
	rollAgainTwice();
	
	display.printMessage("Select a category for this roll.");
	category = display.waitForPlayerToSelectCategory();
	
	updatePlayerScore(player);
}
/*
 * Method:rollAgainTwice();
 *  ------------------
 * This method enable the player to reroll the dice twice after the first roll.
 */
private void rollAgainTwice() {
	for (int k = 0; k < 2; k++) {
		display.printMessage("Select the dice you wish to re-roll and click \"Roll Again\"");
		display.waitForPlayerToSelectDice();
		rollAgain();
	}
}
/*
 *  Method:printResult(int winner);
 * ------------------------------------
 * This method will print the result of the game.
 */
	private void printResult(int winner) {
		display.printMessage("Congratulations, " + playerNames[winner - 1]
				+ " ,you're the winner with a total score of "
				+ scorecard[winner - 1][TOTAL - 1]);

	}
/*
 *  Method:calculateFinalScore()
 * ------------------------------------
 * This method will calculate the bonus score and the final total score of each player.
 */
	private void calculateFinalScore() {
		for (int i = 1; i <= nPlayers; i++) {

			scorecard[i - 1][UPPER_BONUS - 1] = getBonus(scorecard[i - 1][UPPER_SCORE - 1]);

			scorecard[i - 1][TOTAL - 1] = scorecard[i - 1][UPPER_SCORE - 1]
					+ scorecard[i - 1][UPPER_BONUS - 1]
					+ scorecard[i - 1][LOWER_SCORE - 1];
		}
	}
/*
 * Method:printFinalScore()
 * ----------------------------
 * This method will display the upper score and lower score and the total score.
 */
	private void printFinalScore() {
		for (int i = 1; i <= nPlayers; i++) {
			display.updateScorecard(UPPER_SCORE, i,
					scorecard[i - 1][UPPER_SCORE - 1]);
			display.updateScorecard(LOWER_SCORE, i,
					scorecard[i - 1][LOWER_SCORE - 1]);
			display.updateScorecard(UPPER_BONUS, i,
					scorecard[i - 1][UPPER_BONUS - 1]);
			display.updateScorecard(TOTAL, i, scorecard[i - 1][TOTAL - 1]);
		}
	}
/*
 * Method:updatePlayerScore(int player)
 * -------------------------------------
 * This method will update the score card after each round.
 * 
 */
	private void updatePlayerScore(int player) {
		if (!selected[player - 1][category - 1]) {
			updateScore(dice, category, player);
		} else {
			while (selected[player - 1][category - 1]) {
				display.printMessage("This category has been selected. Please select another category!");
				category = display.waitForPlayerToSelectCategory();
			}
			updateScore(dice, category, player);
		}
	}
/*
 *  Method:getWinner(int[][] scorecard)
 * -------------------------------------
 * This method will get the index of the winner. 
 */
	private int getWinner(int[][] scorecard) {
		int winner = 0;
		int topScore = 0;
		for (int i = 0; i < nPlayers; i++) {
			if (scorecard[i][TOTAL - 1] > topScore) {
				topScore = scorecard[i][TOTAL - 1];
				winner = i + 1;
			}
		}
		return winner;
	}
/*
 *  Method:setUp()
 * -------------------
 * This method will initialize the score card and selected array. 
 */
	private void setUp() {
		scorecard = new int[nPlayers][17];
		selected = new boolean[nPlayers][17];
	}
/*
 *  Method:getBonus(int upperScore)
 * ------------------------------------
 * This method will get the bonus according to the upper score after each round.
 */
	private int getBonus(int upperScore) {

		int upperScoreBonus;
		if (upperScore >= 63) {
			upperScoreBonus = 35;
		} else {
			upperScoreBonus = 0;
		}
		return upperScoreBonus;
	}
/*
 * Method:updateScore(int[] dice, int category, int player)
 * ---------------------------------------------------------
 * This method will update the score card after each round.
 */
	private void updateScore(int[] dice, int category, int player) {
		if (checkCategory(dice, category, player)) {

			score = getScore(dice, category, player);
			display.updateScorecard(category, player, score);
			display.updateScorecard(TOTAL, player,
					scorecard[player - 1][TOTAL - 1]);

		} else {
			display.updateScorecard(category, player, 0);
		}

	}
/*
 *  Method:checkCategory(int[] dice, int category, int player)
 * ---------------------------------------------------------
 * This method will check whether the dice suits the chosen category.
 */
	private boolean checkCategory(int[] dice, int category, int player) {

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
			selected[player - 1][category - 1] = true;
			return true;
		case THREE_OF_A_KIND:
			if (topTwo[0] >= 3) {
				selected[player - 1][category - 1] = true;
				return true;
			}
			break;
		case FOUR_OF_A_KIND:
			if (topTwo[0] >= 4) {
				selected[player - 1][category - 1] = true;
				return true;
			}
			break;
		case FULL_HOUSE:
			if (topTwo[0] == 3 && topTwo[1] == 2) {
				selected[player - 1][category - 1] = true;
				return true;
			}
			break;
		case SMALL_STRAIGHT:
			selected[player - 1][category - 1] = true;
			return (checkSmallStraight(occDis));
		case LARGE_STRAIGHT:
			if (topTwo[0] == 1 && (occDis[0] == 0 || occDis[5] == 0)) {
				selected[player - 1][category - 1] = true;
				return true;
			}
			break;
		case YAHTZEE:
			if (topTwo[0] == 5) {
				selected[player - 1][category - 1] = true;
				return true;
			}
			break;

		}
		return false;
	}
/*
 * Method:checkSmallStraight(int[] occDis)
 * -------------------------------------------------
 * This method will check whether the occur distribution suits the 
 * small straight category.
 */
	private boolean checkSmallStraight(int[] occDis) {
		for (int i = 0; i < 3; i++) {
			if (occDis[i] >= 1 && occDis[i + 1] >= 1 && occDis[i + 2] >= 1
					&& occDis[i + 3] >= 1) {
				return true;
			}
		}
		return false;
	}
/*
 *  Method:occDis(int[] dice)
 * -------------------------------------------------
 * This method will get the occur distribution of each number from 1 to 6 
 * according to a dice.
 */
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
/*
 * Method:getTopTwo(int occDis[])
 * -------------------------------------------------
 * This method will get the top two occurrence number of 
 * a occurrence distribution.
 */
	private int[] getTopTwo(int occDis[]) {
		int[] topTwo = new int[2];

		int largest = 0;
		int secondLargest = 0;

		for (int i = 0; i < occDis.length; i++) {
			if (occDis[i] > largest) {
				secondLargest = largest;
				largest = occDis[i];
			} else if (occDis[i] > secondLargest) {
				secondLargest = occDis[i];
			}
		}

		topTwo[0] = largest;
		topTwo[1] = secondLargest;

		return topTwo;
	}
/*
 * Method:rollAgain() 
 * --------------------------------------
 * This method will update the chosen dice.
 */
	private void rollAgain() {

		for (int index = 0; index < dice.length; index++) {
			if (display.isDieSelected(index)) {
				dice[index] = rgen.nextInt(1, 6);
			}
		}
		display.displayDice(dice);

	}
/*
 * Method:firstRoll() 
 * --------------------------------------
 * This method will update all the dices.
 */
	private void firstRoll() {

		for (int i = 0; i < dice.length; i++) {
			dice[i] = rgen.nextInt(1, 6);
		}
		display.displayDice(dice);
	}
/*
 * Method:getScore(int[] dice, int category, int player) 
 * ------------------------------------------------------
 * This method will get the score according to the dice and category. 
 */
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
			scorecard[player - 1][UPPER_SCORE - 1] += score;
			break;
		case THREE_OF_A_KIND:
			score = sum(dice);
			scorecard[player - 1][LOWER_SCORE - 1] += score;
			break;
		case FOUR_OF_A_KIND:
			score = sum(dice);
			scorecard[player - 1][LOWER_SCORE - 1] += score;
			break;
		case FULL_HOUSE:
			score = 25;
			scorecard[player - 1][LOWER_SCORE - 1] += score;
			break;
		case SMALL_STRAIGHT:
			score = 30;
			scorecard[player - 1][LOWER_SCORE - 1] += score;
			break;
		case LARGE_STRAIGHT:
			score = 40;
			scorecard[player - 1][LOWER_SCORE - 1] += score;
			break;
		case YAHTZEE:
			score = 50;
			scorecard[player - 1][LOWER_SCORE - 1] += score;
			break;
		case CHANCE:
			score = sum(dice);
			scorecard[player - 1][LOWER_SCORE - 1] += score;
			break;
		}
		scorecard[player - 1][TOTAL - 1] += score;
		return score;
	}
/*
 * Method:sum(int[] dice)
 * ------------------------------------------------------
 * This method will calculate the sum of the numbers of the dices.
 */
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
	private int[][] scorecard;
	private boolean[][] selected;

}
