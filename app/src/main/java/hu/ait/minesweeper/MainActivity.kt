package hu.ait.minesweeper

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import hu.ait.minesweeper.databinding.ActivityMainBinding
import hu.ait.minesweeper.model.MinesweeperModel


class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val spinner: Spinner = binding.sizesSpinner
        spinner.onItemSelectedListener = this
        spinner.setSelection(1)
        ArrayAdapter.createFromResource(
            this,
            R.array.sizes_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears.
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner.
            spinner.adapter = adapter
        }

        binding.minesweeperView.resetGame()

        binding.btnNewGame.setOnClickListener() {
            binding.minesweeperView.resetGame()
            binding.tvGameResult.text = getString(R.string.empty_str)
            binding.btnNewGame.visibility = View.GONE
        }

    }
    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        // On selecting a spinner item
        MinesweeperModel.size = parent.getItemAtPosition(position).toString().toInt()
        binding.minesweeperView.resetGame()
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        MinesweeperModel.size = 4
    }

    fun isFlagModeOn() : Boolean {
        return binding.flagModeCheckBox.isChecked
    }
    fun setGameResult(msg: String) {
        binding.tvGameResult.text = msg
        binding.btnNewGame.visibility = View.VISIBLE
    }

    fun setFlagCount() {
        binding.tvFlagCount.text = MinesweeperModel.flags.toString()
    }
}