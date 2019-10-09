package com.example.rockpaperscissor

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class MainActivity : AppCompatActivity() {

    private val REQUEST_CODE = 1

    private lateinit var gameRepository: GameRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        gameRepository = GameRepository(this)
        initViews()
    }

    /**
     * Initialized all event listeners and updates game statistics
     *
     */
    private fun initViews() {
        initListeners()
        updateStats()
    }

    /**
     * Get wins, draws and losses with ROOM DB inside Coroutine
     *
     */
    private fun updateStats() {
        CoroutineScope(Dispatchers.Main).launch {
            val wins = withContext(Dispatchers.IO) {
                gameRepository.getWins()
            }
            val draws = withContext(Dispatchers.IO) {
                gameRepository.getDraws()
            }
            val losses = withContext(Dispatchers.IO) {
                gameRepository.getLosses()
            }
            tvStats.text = getString(
                R.string.win_draw_lose,
                wins.toString(),
                draws.toString(),
                losses.toString()
            )
        }
    }

    /**
     * Sets individual listeners on UI elements
     *
     */
    private fun initListeners() {
        ibtnHistory.setOnClickListener {
            startActivity()
        }
        ibtnRock.setOnClickListener { handleGame(Gesture.ROCK) }
        ibtnPaper.setOnClickListener { handleGame(Gesture.PAPER) }
        ibtnScissor.setOnClickListener { handleGame(Gesture.SCISSOR) }
    }

    /**
     * Method to start GameHistoryActivity
     *
     */
    private fun startActivity() {
        val intent = Intent(this, GameHistoryActivity::class.java)
        startActivityForResult(intent, REQUEST_CODE)
    }

    /**
     * Method to handle a single game,
     *
     * @param gesture Rock || Paper || Scissors chosen by user
     */
    private fun handleGame(gesture: Gesture) {
        val computerAction = (0..3).shuffled().first()
        val computerGesture = assignGesture(computerAction)
        var game = Game(
            null,
            "",
            Date(),
            gesture,
            computerGesture
        )

        game = calculateWinner(game)
        insertGameIntoDatabase(game)
        displayGameResults(game)
        updateStats()
    }

    /**
     * Method to insert game into the database
     *
     * @param game Game object to be inserted
     */
    private fun insertGameIntoDatabase(game: Game) {
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                gameRepository.insertGame(game)
            }
        }
    }

    /**
     * Method to assign gesture to values based on enums
     *
     * @param value Value to match against enums
     * @return Enum Gesture
     */
    private fun assignGesture(value: Int): Gesture {
        return when (value) {
            0 -> Gesture.ROCK
            1 -> Gesture.PAPER
            3 -> Gesture.SCISSOR
            else -> Gesture.ROCK
        }
    }

    /**
     * Method to calculate the winner of a game by comparing chose action/gesture
     *
     * @param game Game to calculate winner from
     * @return Returns game with game.result defined
     */
    private fun calculateWinner(game: Game): Game {
        game.result = when {
            game.computerAction == game.userAction -> getString(R.string.draw)
            game.computerAction == Gesture.ROCK && game.userAction == Gesture.SCISSOR -> getString(R.string.computer_win)
            game.computerAction == Gesture.SCISSOR && game.userAction == Gesture.PAPER -> getString(
                R.string.computer_win
            )
            game.computerAction == Gesture.PAPER && game.userAction == Gesture.ROCK -> getString(R.string.computer_win)
            else -> getString(R.string.you_win)
        }
        return game
    }

    /**
     * Displays the game results in the UI
     *
     * @param game Game to display results from
     */
    private fun displayGameResults(game: Game) {
        tvResult.text = game.result
        ivComputer.setImageDrawable(getDrawable(game.computerAction.drawableId))
        ivYou.setImageDrawable(getDrawable(game.userAction.drawableId))
    }

    /**
     * Method is triggered when returning from GameHistoryActivity
     *
     * @param requestCode RequestCode from activity
     * @param resultCode resultCode from activity
     * @param data Optional data from activity
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        updateStats()
        resetGameResults()
    }

    /**
     * Resets all game results on UI
     *
     */
    private fun resetGameResults() {
        tvResult.text = getString(R.string.initial_result)
        ivComputer.setImageDrawable(getDrawable(Gesture.PAPER.drawableId))
        ivYou.setImageDrawable(getDrawable(Gesture.PAPER.drawableId))
    }
}
