package com.example.flashcard_app

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.flashcard_app.databinding.ActivityMainBinding
import com.example.flashcard_app.databinding.ActivityThirdBinding
import com.example.flashcard_app.databinding.AlertDialogAddCardBinding
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import kotlin.random.Random

class ThirdActivity : AppCompatActivity() {

    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: RecyclerView.Adapter<RecyclerAdapter.ViewHolder>? = null

    // Declaration of variable for database handler/helper
    companion object {
        lateinit var flashcardDBHelper : FlashcardDBHelper
        var questionList = ArrayList<String>()
        var answerList = ArrayList<String>()
        lateinit var numOfCards: String
        lateinit var tableName: String
        lateinit var binding: ActivityThirdBinding
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        binding = ActivityThirdBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initializing database variable
        flashcardDBHelper = FlashcardDBHelper(this)

        val bundle: Bundle? = intent.extras
        val num = bundle!!.getString("numOfCards")
        numOfCards = num.toString()
        val setName = bundle!!.getString("tableName")
        tableName = setName.toString()

        binding.tvTitle.text = tableName
        binding.tvNumberOfCards.text = "Number of Cards : $numOfCards"


        viewContents(binding)

        binding.btnSubmit.setOnClickListener {
            Toast.makeText(this, "Add", Toast.LENGTH_SHORT).show()

            val mDialogView = AlertDialogAddCardBinding.inflate(layoutInflater)
            val mBuilder = AlertDialog.Builder(this).setView(mDialogView.root)
            val mAlertDialog = mBuilder.show()
            val back = ColorDrawable(Color.TRANSPARENT)
            val inset = InsetDrawable(back, 20)
            mAlertDialog.window?.setBackgroundDrawable(inset)

            mDialogView.btnSubmit.setOnClickListener {
                Toast.makeText(this, "Submit", Toast.LENGTH_SHORT).show()
                // Code for submit button
                var inputQ = mDialogView.editTextQuestion.text.toString()
                var inputA = mDialogView.editTextAnswer.text.toString()
                if(inputQ.trim().isNotEmpty() && inputQ.trim().isNotBlank() && inputA.trim().isNotEmpty() &&  inputA.trim().isNotBlank()) {
                    inputQ = normalizeInput(inputQ)
                    val exists = flashcardDBHelper.readQuestion(inputQ,tableName)
                    if(exists != 0) {
                        Toast.makeText(this, "Question already exists", Toast.LENGTH_SHORT).show()
                    }
                    else if(exists == 0) {
                        val result = flashcardDBHelper.insertQuestion(FlashcardModel(inputQ,inputA),tableName)
                        Toast.makeText(this, "Added Question: $result", Toast.LENGTH_SHORT).show()
                        viewContents(binding)
                        mAlertDialog.dismiss()
                    }
                }
                else {
                    Toast.makeText(this, "ERROR: EMPTY FIELD", Toast.LENGTH_SHORT).show()
                }

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

    fun viewContents(binding: ActivityThirdBinding) {
        binding.rvQuestions.visibility = View.GONE
        var setQuestions = ArrayList<String>()
        var setAnswers = ArrayList<String>()
        var setNum: Int
        val flashcards = flashcardDBHelper.readAllQuestions(tableName)

        if(flashcards.isNotEmpty()) {
            try {
                flashcards.forEach {
                    setQuestions.add(it.question)
                    setAnswers.add(it.answer)
                }
                Toast.makeText(this,"Fetched Flashcards", Toast.LENGTH_SHORT).show()
            } catch(e: Exception) {
                Toast.makeText(this,"$e", Toast.LENGTH_SHORT).show()
            }
            questionList = setQuestions
            answerList = setAnswers

            setNum = flashcardDBHelper.getCount(tableName)
        }
        else {
            questionList = setQuestions
            answerList = setAnswers
            setNum = 0
            Toast.makeText(this,"No Questions Added", Toast.LENGTH_SHORT).show()
        }
        binding.tvNumberOfCards.text = "Number of Cards : " + setNum.toString()

        // Load Recycler View
        layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.rvQuestions.layoutManager = layoutManager
        adapter = RecyclerAdapter(this, questionList, answerList)
        binding.rvQuestions.adapter = adapter
        binding.rvQuestions.visibility = View.VISIBLE

    }

    private fun normalizeInput(input: String): String {
        return input.trim().lowercase().replace("\\s".toRegex(),"_").replaceFirstChar {it.uppercase()}
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

            ProcessClickCard(itemView)
            ProcessDeleteCard(itemView)
            ProcessEditCard(itemView)
        }
    }

    private fun ViewHolder.ProcessClickCard(itemView: View) {
        itemView.setOnClickListener {
            Toast.makeText(itemView.context, "$adapterPosition", Toast.LENGTH_SHORT).show()

        }
    }

    private fun ViewHolder.ProcessDeleteCard(itemView: View) {
        cDelete.setOnClickListener {
            val result = ThirdActivity.flashcardDBHelper.deleteQuestion(
                questions[adapterPosition],
                ThirdActivity.tableName.lowercase()
            )
            questions.removeAt(adapterPosition)
            answers.removeAt(adapterPosition)
            notifyItemRemoved(adapterPosition)
            val numOfCards =
                ThirdActivity.flashcardDBHelper.getCount(ThirdActivity.tableName.lowercase())
            ThirdActivity.binding.tvNumberOfCards.text =
                "Number of Cards : " + numOfCards.toString()
            Toast.makeText(
                itemView.context,
                "Removed question, result: $result",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun ViewHolder.ProcessEditCard(itemView: View) {
        cEdit.setOnClickListener {
            val mDialogView = AlertDialogAddCardBinding.inflate(context.layoutInflater)
            val mBuilder = AlertDialog.Builder(context).setView(mDialogView.root)
            val mAlertDialog = mBuilder.show()
            val back = ColorDrawable(Color.TRANSPARENT)
            val inset = InsetDrawable(back, 20)
            mAlertDialog.window?.setBackgroundDrawable(inset)

            // Set edit text field values
            mDialogView.tvCardTitle.setText("Edit this card")
            mDialogView.editTextAnswer.setText(answers[adapterPosition])
            mDialogView.editTextQuestion.setText(questions[adapterPosition])

            // Click listeners
            mDialogView.btnSubmit.setOnClickListener {
                Toast.makeText(itemView.context, "Submit", Toast.LENGTH_SHORT).show()
                var inputQ = mDialogView.editTextQuestion.text.toString()
                var inputA = mDialogView.editTextAnswer.text.toString()
                if(inputQ.trim().isNotEmpty() && inputQ.trim().isNotBlank() && inputA.trim().isNotEmpty() &&  inputA.trim().isNotBlank()) {
                    inputQ = normalizeInput(inputQ)
                    val exists = ThirdActivity.flashcardDBHelper.readQuestion(inputQ,ThirdActivity.tableName)
                    if(exists != 0) {
                        Toast.makeText(itemView.context, "Question already exists", Toast.LENGTH_SHORT).show()
                    }
                    else if(exists == 0) {
                        val result = ThirdActivity.flashcardDBHelper.updateQuestion(inputQ,inputA,questions[adapterPosition],ThirdActivity.tableName)
                        if(result) {
                            Toast.makeText(itemView.context, "Successfully Updated Flashcard", Toast.LENGTH_SHORT).show()
                            questions.set(adapterPosition, inputQ)
                            answers.set(adapterPosition, inputA)
                            mAlertDialog.dismiss()
                        }
                        else{
                            Toast.makeText(itemView.context, "Failed to Update Flashcard", Toast.LENGTH_SHORT).show()
                            mAlertDialog.dismiss()
                        }
                    }

                }
                else {
                    Toast.makeText(itemView.context, "ERROR: EMPTY FIELD", Toast.LENGTH_SHORT).show()
                }

                // Code for submit button
                notifyDataSetChanged()
            }
            mDialogView.btnCancel.setOnClickListener {
                mAlertDialog.dismiss()
                Toast.makeText(itemView.context, "Cancel", Toast.LENGTH_SHORT).show()

                // Code for cancel button
            }
        }
    }

    private fun normalizeInput(input: String): String {
        return input.trim().lowercase().replace("\\s".toRegex(),"_").replaceFirstChar {it.uppercase()}
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
