package hu.ait.minesweeper.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import hu.ait.minesweeper.MainActivity
import hu.ait.minesweeper.R
import hu.ait.minesweeper.model.MinesweeperModel

class MinesweeperView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private val paintBackground  = Paint()
    private val paintLine = Paint()
    private val paintText = Paint()
    private var cellLength = 0f


    private var gameOver = false
    private var bombImg = BitmapFactory.decodeResource(resources, R.drawable.fickmine)
    private var flagImg = BitmapFactory.decodeResource(resources, R.drawable.newflag)
    private var greenImg = BitmapFactory.decodeResource(resources, R.drawable.green)

    init {
        paintBackground.color = Color.parseColor("#abd464")
        paintBackground.style = Paint.Style.FILL

        paintLine.color = Color.rgb(143, 174, 77)
        paintLine.style = Paint.Style.STROKE
        paintLine.strokeWidth = 5f

        paintText.textSize = 150F * 7 / MinesweeperModel.size
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paintBackground)
        cellLength = width / MinesweeperModel.size.toFloat()
        drawGameArea(canvas)

    }

    private fun drawGameArea(canvas: Canvas?) {
        drawBoard(canvas)
        for (i in 0..MinesweeperModel.size) {
            canvas?.drawLine(i * width.toFloat() / MinesweeperModel.size, 0f, i * width.toFloat() / MinesweeperModel.size, height.toFloat(), paintLine)
            canvas?.drawLine(0f, i * height.toFloat() / MinesweeperModel.size, width.toFloat(), i * height.toFloat() / MinesweeperModel.size, paintLine)
        }
    }

    private fun drawBoard(canvas: Canvas?) {
        (context as MainActivity).setFlagCount()
        for (i in 0 until MinesweeperModel.size) {
            for (j in 0 until MinesweeperModel.size) {
                val x = (i * cellLength).toFloat()
                val y = (j * cellLength).toFloat()
                if (MinesweeperModel.getFieldContent(i, j).isFlagged) {
                    val flagBitmap = Bitmap.createScaledBitmap(flagImg, cellLength.toInt(), cellLength.toInt(), false)
                    canvas?.drawBitmap(flagBitmap, x, y, null)
                } else if (gameOver && MinesweeperModel.getFieldContent(i, j).type == MinesweeperModel.MINE) {
                    val bombBitmap = Bitmap.createScaledBitmap(bombImg, cellLength.toInt(), cellLength.toInt(), false)
                    canvas?.drawBitmap(bombBitmap, x, y, null)
                } else if (MinesweeperModel.getFieldContent(i,j).wasClicked) {
                    drawWarnings(canvas, i, j)
                }
            }
        }
    }

    private fun drawWarnings( canvas: Canvas?, i:Int, j:Int): Unit {
        val mines = MinesweeperModel.getFieldContent(i, j).minesAround

        if (mines == 0) {
            val x = (i * cellLength)
            val y = (j * cellLength)
            val greenBitmap = Bitmap.createScaledBitmap(greenImg, cellLength.toInt()-2, cellLength.toInt()-2, false)
            canvas?.drawBitmap(greenBitmap, x, y, null)
        }
        if (mines > 0) {
            var rect = Rect()
            paintText.getTextBounds(mines.toString(), 0, 1, rect)
            paintText.color = mineColor(mines)
            val x = (i * width / MinesweeperModel.size + cellLength / 2 - rect.exactCenterX())
            val y = (j * height / MinesweeperModel.size + cellLength / 2 - rect.exactCenterY())
            canvas?.drawText(mines.toString(), x, y, paintText)
        }
    }

    private fun mineColor(mines: Int): Int {
        return when (mines) {
            1 -> Color.BLUE
            2 -> Color.BLACK
            3 -> Color.YELLOW
            else -> {
                Color.RED
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!gameOver) {
            if (event.action == MotionEvent.ACTION_DOWN) {
                val tX = event.x.toInt() / (width / MinesweeperModel.size)
                val tY = event.y.toInt() / (height / MinesweeperModel.size)
                gameOver = if ((context as MainActivity).isFlagModeOn())
                    MinesweeperModel.setFlag(tX, tY)
                else
                    MinesweeperModel.fieldClicked(tX, tY)
                invalidate()
            }

            if (gameOver) {
                (context as MainActivity).setGameResult(context.getString(R.string.msg_lose))
            } else if (MinesweeperModel.fullyClicked()) {
                (context as MainActivity).setGameResult(context.getString(R.string.msg_win))
            }
        }
        return true
    }

    fun resetGame() {
        MinesweeperModel.resetModel()
        gameOver = false
        paintText.textSize = 150F * 7 / MinesweeperModel.size
        invalidate()
    }
}