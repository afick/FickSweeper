package hu.ait.minesweeper.model

import java.util.Random

object MinesweeperModel {

    val EMPTY: Short = 0
    val MINE: Short = 1
    private var clicked = 0

    var size = 5
    var flags = 0

    private lateinit var fieldMatrix: Array<Array<Field>>

    fun resetModel() {
        flags = 0
        generateBoard()
        clicked = 0
    }

    private fun randomMine() : Short {
        val ran = Random().nextInt(100)
        if (ran > 70) { flags++; return 1 }
        return 0
    }

    private fun generateBoard() {
        fieldMatrix= Array(size){ Array(size) {Field(0, 0, isFlagged = false, wasClicked = false)} }

        for (i in 0 .. size-1) {
            for (j in 0..size-1) {
                fieldMatrix[i][j].type = randomMine()
            }
        }
        for (i in 0 .. size - 1) {
            for (j in 0..size - 1) {
                if (fieldMatrix[i][j].type == EMPTY) {
                    fieldMatrix[i][j].minesAround = countMines(i, j)
                }
            }
        }
    }

    private fun countMines(i:Int, j:Int): Int {
        var mineCount = 0
        for (k in i-1..i+1) {
            for (l in j-1..j+1) {
                if (k in 0 .. size - 1 && l in 0 .. size-1) {
                    if (fieldMatrix[k][l].type == MINE) {
                        mineCount ++
                    }
                }
            }
        }
        return mineCount
    }

    fun getFieldContent(x: Int, y: Int) = fieldMatrix[x][y]

    fun setFlag(x: Int, y: Int) : Boolean {
        if (fieldMatrix[x][y].isFlagged) {
            fieldMatrix[x][y].isFlagged = false
            fieldMatrix[x][y].wasClicked = false
            clicked --
            flags ++
            return false
        } else if (fieldMatrix[x][y].wasClicked || flags == 0) {
            return false
        }
        else {
            fieldMatrix[x][y].isFlagged = true
            fieldMatrix[x][y].wasClicked = true
            clicked ++
            flags --
            return false
        }

    }

    fun fieldClicked(x: Int, y: Int): Boolean {
        if (fieldMatrix[x][y].wasClicked) {
            return false
        }
        return if (fieldMatrix[x][y].type == MINE) {
            true
        } else {
            if (fieldMatrix[x][y].minesAround == 0){
                floodFill(x,y)
            } else {
                fieldMatrix[x][y].wasClicked = true
                clicked ++
            }
            false
        }
    }

    private fun floodFill(x: Int, y: Int) {
        for (i in x-1..x+1) {
            for (j in y-1..y+1) {
                if (i in 0 .. size - 1 && j in 0 .. size-1) {
                    if (!fieldMatrix[i][j].wasClicked) {
                        fieldMatrix[i][j].wasClicked = true
                        clicked ++
                        if (fieldMatrix[i][j].minesAround == 0) {
                            floodFill(i, j)
                        }
                    }
                }
            }
        }
    }

    fun fullyClicked() : Boolean {
        return clicked == size*size
    }
}