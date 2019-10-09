package com.example.rockpaperscissor

import android.os.Bundle
import android.view.MenuItem
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

import kotlinx.android.synthetic.main.activity_game_history.*
import kotlinx.android.synthetic.main.content_game_history.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GameHistoryActivity : AppCompatActivity() {

    private var games = arrayListOf<Game>()
    private val gameAdapter = GameAdapter(games)
    private lateinit var gameRepository: GameRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_history)
        setSupportActionBar(toolbar)
        gameRepository = GameRepository(this)
        initViews()
    }

    /**
     * sets up adapter, gets games from database and sets event listeners
     *
     */
    private fun initViews() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        rvGames.adapter = gameAdapter
        rvGames.layoutManager =
            LinearLayoutManager(this@GameHistoryActivity, RecyclerView.VERTICAL, false)
        rvGames.addItemDecoration(
            DividerItemDecoration(
                this@GameHistoryActivity,
                DividerItemDecoration.VERTICAL
            )
        )
        getGamesFromDatabase()
        ibtnDelete.setOnClickListener { onDeleteClick() }
    }

    /**
     * Method triggered when clicking on delete button
     * All games are deleted from database within Coroutine
     *
     */
    private fun onDeleteClick() {
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                gameRepository.deleteAllGames()
            }
            getGamesFromDatabase()
            gameAdapter.notifyDataSetChanged()
        }
    }

    /**
     * Gets all games from the database and updates gameAdapter
     *
     */
    private fun getGamesFromDatabase() {
        CoroutineScope(Dispatchers.Main).launch {
            val games = withContext(Dispatchers.IO) {
                gameRepository.getAllGames()
            }
            this@GameHistoryActivity.games.clear()
            this@GameHistoryActivity.games.addAll(games)
            gameAdapter.notifyDataSetChanged()
        }
    }

    /**
     * Closes current activity
     *
     */
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return onBackButtonClick()
    }

    /**
     * Closes current activity
     *
     */
    private fun onBackButtonClick(): Boolean {
        finish()
        return true
    }


}
