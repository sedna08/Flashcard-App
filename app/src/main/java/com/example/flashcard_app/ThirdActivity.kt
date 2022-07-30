package com.example.flashcard_app

import android.app.Activity
import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.flashcard_app.databinding.ActivityThirdBinding
import com.example.flashcard_app.databinding.AlertDialogBinding
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip

class ThirdActivity : AppCompatActivity() {
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: RecyclerView.Adapter<RecyclerAdapter.ViewHolder>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        val binding = ActivityThirdBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // List for testing
        val questions = arrayOf("Question 1", "Question 2", "Question 3", "Question 4", "Question 5", "Question 6", "Question 7", "Question 8").toCollection(ArrayList<String>())
        val answers = arrayOf("Answer 1", "Answer 2", "Answer 3", "Answer 4", "Answer 5", "Answer 6", "Answer 7", "Answer 8").toCollection(ArrayList<String>())
        val durations = arrayOf(1, 2, 3, 4, 5, 6, 7, 8).toCollection(ArrayList<Int>())

        layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        binding.rvQuestions.layoutManager = layoutManager

        adapter = RecyclerAdapter(this, questions, answers, durations)
        binding.rvQuestions.adapter = adapter

        binding.imageButton3.setOnClickListener{
            val items: Array<String> = resources.getStringArray(R.array.alert_list)
            var checkItem: Int = 0
            var selectedMode: String
            var mode = 0
            AlertDialog.Builder(this)
                .setTitle("Timed Mode?")
                .setSingleChoiceItems(items, checkItem) { dialog, which ->
                    //action code

                    checkItem = which
                    selectedMode = items[checkItem]

                    if (selectedMode == "Yes") {
                        Toast.makeText(this, "Timed Mode", Toast.LENGTH_SHORT).show()
                        mode = 1
                    }
                    else if (selectedMode == "No") {
                        Toast.makeText(this, "Casual Mode", Toast.LENGTH_SHORT).show()
                        mode = 0
                    }

                }
                .setPositiveButton("OK") { dialog, which ->
                    //action code for positive response
                    Toast.makeText(this, "Starting Flashcards", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, SecondActivity::class.java)
                    intent.putExtra("user_mode", mode)
                    startActivity(intent)
                }
                .setNegativeButton("CANCEL") { dialog, which ->
                    //action code for negative response
                    Toast.makeText(this, "Canceled", Toast.LENGTH_SHORT).show()
                }
                .show()
        }

    }
}

class RecyclerAdapter(
    private val context: Activity,
    private val questions: ArrayList<String>,
    private val answers: ArrayList<String>,
    private val durations: ArrayList<Int>
) : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var tvQuestion: TextView
        var tvAnswer: TextView
        var tvDuration: TextView
        var cvFlashCard: MaterialCardView
        var cEdit: Chip
        var cDelete: Chip

        init {
            tvQuestion = itemView.findViewById(R.id.tvQuestion)
            tvAnswer = itemView.findViewById(R.id.tvAnswer)
            tvDuration = itemView.findViewById(R.id.tvDuration)
            cvFlashCard = itemView.findViewById<MaterialCardView>(R.id.cardView)
            cEdit = itemView.findViewById(R.id.cEdit)
            cDelete = itemView.findViewById(R.id.cDelete)

            itemView.setOnClickListener {
                Toast.makeText(itemView.context, "$adapterPosition", Toast.LENGTH_SHORT).show()
            }
            cDelete.setOnClickListener {
                Toast.makeText(itemView.context, "You removed card no $adapterPosition", Toast.LENGTH_SHORT).show()
                questions.removeAt(adapterPosition)
                answers.removeAt(adapterPosition)
                durations.removeAt(adapterPosition)
                notifyItemRemoved(adapterPosition)
            }
            cEdit.setOnClickListener {
                val mDialogView = AlertDialogBinding.inflate(context.layoutInflater)
                val mBuilder = AlertDialog.Builder(context).setView(mDialogView.root)
                val mAlertDialog = mBuilder.show()

                mDialogView.btnSubmit.setOnClickListener {
                    Toast.makeText(itemView.context, "Submit", Toast.LENGTH_SHORT).show()

                    // Code for submit button
                }
                mDialogView.btnCancel.setOnClickListener {
                    mAlertDialog.dismiss()
                    Toast.makeText(itemView.context, "Cancel", Toast.LENGTH_SHORT).show()

                    // Code for cancel button
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.flash_card_layout, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvQuestion.text = questions[position]
        holder.tvAnswer.text = answers[position]
        holder.tvDuration.text = "Duration: ${durations[position].toString()}s"
    }

    override fun getItemCount(): Int {
        return questions.size
    }



}
