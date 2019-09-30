package com.example.rockpaperscissor

import android.content.res.Resources

enum class Gesture(val value: Int, val drawableId: Int) {
    ROCK(0, R.drawable.rock),
    PAPER(1, R.drawable.paper),
    SCISSOR(2, R.drawable.scissors);
}