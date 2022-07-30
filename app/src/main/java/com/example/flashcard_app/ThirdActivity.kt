package com.example.flashcard_app

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.flashcard_app.databinding.ActivityThirdBinding
import com.example.flashcard_app.databinding.AlertDialogAddCardBinding
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
        val questions = arrayOf("What would you do If I kissed you right now?", "What is your biggest turn on?", "What is your biggest turn off?", "Do you prefer cuddling or kissing?", "What are your favourite pet names? Babe, Cutie etc.", "Want to know a secret?", "Would you ever have a sugar daddy?", "Who was your teacher crush?").toCollection(ArrayList<String>())
        val answers = arrayOf("Answer 1", "Answer 2", "Answer 3", "Answer 4", "Answer 5", "Answer 6", "Answer 7", "Answer 8").toCollection(ArrayList<String>())

        // Load Recycler View
        layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.rvQuestions.layoutManager = layoutManager
        adapter = RecyclerAdapter(this, questions, answers)
        binding.rvQuestions.adapter = adapter

        binding.btnSubmit.setOnClickListener {
            Toast.makeText(this, "Add", Toast.LENGTH_SHORT).show()

            val mDialogView = AlertDialogAddCardBinding.inflate(layoutInflater)
            val mBuilder = AlertDialog.Builder(this).setView(mDialogView.root)
            val mAlertDialog = mBuilder.show()

            mDialogView.btnSubmit.setOnClickListener {
                Toast.makeText(this, "Submit", Toast.LENGTH_SHORT).show()

                // Code for submit button
            }
            mDialogView.btnCancel.setOnClickListener {
                mAlertDialog.dismiss()
                Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show()

                // Code for cancel button
            }
        }
        binding.btnPlay.setOnClickListener {
            Toast.makeText(this, "Play", Toast.LENGTH_SHORT).show()

            // Code for play button
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
) : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var tvQuestion: TextView
        var tvAnswer: TextView
        var cvFlashCard: MaterialCardView
        var cEdit: Chip
        var cDelete: Chip

        init {
            tvQuestion = itemView.findViewById(R.id.tvQuestion)
            tvAnswer = itemView.findViewById(R.id.tvAnswer)
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
                notifyItemRemoved(adapterPosition)
            }
            cEdit.setOnClickListener {
                val mDialogView = AlertDialogAddCardBinding.inflate(context.layoutInflater)
                val mBuilder = AlertDialog.Builder(context).setView(mDialogView.root)
                val mAlertDialog = mBuilder.show()

                // Set edit text field values
                mDialogView.editTextAnswer.setText(answers[adapterPosition])
                mDialogView.editTextQuestion.setText(questions[adapterPosition])

                // Click listeners
                mDialogView.btnSubmit.setOnClickListener {
                    Toast.makeText(itemView.context, "Submit", Toast.LENGTH_SHORT).show()

                    // Code for submit button
                    questions.set(adapterPosition, "String")
                    notifyDataSetChanged()
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
        val v = LayoutInflater.from(parent.context).inflate(R.layout.third_vertical_card_layout, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvQuestion.text = questions[position]
        holder.tvAnswer.text = answers[position]
    }

    override fun getItemCount(): Int {
        return questions.size
    }

}
