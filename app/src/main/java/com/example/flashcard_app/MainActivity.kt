package com.example.flashcard_app

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.flashcard_app.databinding.ActivityMainBinding
import com.example.flashcard_app.databinding.AlertDialogAddSetBinding
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // Declaration of variable for database handler/helper
    companion object {
        lateinit var flashcardDBHelper : FlashcardDBHelper
        var flashcardsetList = ArrayList<String>()
        var cardCountList = ArrayList<String>()
    }

    override fun onRestart() {
        super.onRestart()
        viewSets(binding)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        getSupportActionBar()?.hide()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initializing database
        flashcardDBHelper = FlashcardDBHelper(this)

        viewSets(binding)

        // Add button 2
        binding.b2Add.setOnClickListener() { createSet(binding) }

        // Change listener for vertical recycler view
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.rvSetsVertical.setOnScrollChangeListener { view, i, i2, i3, i4 ->
                // Toast.makeText(this, "vertical change", Toast.LENGTH_SHORT).show()
                binding.rvSetsHorizontal.layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL)
                binding.rvSetsHorizontal.adapter = RecyclerAdapterMain(this, flashcardsetList, cardCountList, R.layout.main_horizontal_set_layout, 3)
            }
        }
    }

    fun viewSets(binding: ActivityMainBinding) {
        binding.rvSetsVertical.visibility = View.GONE
        val setNames = ArrayList<String>()
        val setCounts = ArrayList<String>()
        var setNum: Int
        val sets = flashcardDBHelper.readAllSets()

        if(sets.isNotEmpty()) {
            try {
                sets.forEach {
                    setNames.add(it)
                }
                // Toast.makeText(this,"Fetched Question Sets", Toast.LENGTH_SHORT).show()
            } catch(e: Exception) {
                // Toast.makeText(this,"$e", Toast.LENGTH_SHORT).show()
            }
            flashcardsetList = setNames

            setNames.forEach() {
                setNum = flashcardDBHelper.getCount(it)
                setCounts.add("Num of Cards: $setNum")
            }
            cardCountList = setCounts
        }
        else {
            flashcardsetList = setNames
            cardCountList = setCounts
            Toast.makeText(this,"No Sets Added", Toast.LENGTH_SHORT).show()
        }

        // Load Vertical Recycler View
        binding.rvSetsVertical.layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        binding.rvSetsVertical.adapter = RecyclerAdapterMain(this, flashcardsetList, cardCountList, R.layout.main_vertical_set_layout, flashcardsetList.size)
        binding.rvSetsVertical.visibility = View.VISIBLE
        // Load Horizontal Recycler View
        binding.rvSetsHorizontal.layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL)
        binding.rvSetsHorizontal.adapter = RecyclerAdapterMain(this, flashcardsetList, cardCountList, R.layout.main_horizontal_set_layout, 3)
        
    }

    fun createSet(binding: ActivityMainBinding) {
        // Add set of flash cards things
        val mDialogView = AlertDialogAddSetBinding.inflate(layoutInflater)
        val mBuilder = androidx.appcompat.app.AlertDialog.Builder(this)
            .setView(mDialogView.root)
        val mAlertDialog = mBuilder.show()

        // Add Button in Alert Dialog is clicked
        mDialogView.btnSubmit.setOnClickListener {
            var input = mDialogView.etNewName.text.toString()
            if(input.trim().isNotEmpty() && input.trim().isNotBlank()) {
                if(checkInput(input)) {
                    input = normalizeInput(input)
                    val exists = flashcardDBHelper.readSet(input)
                    if(exists != 0) {
                        mDialogView.tilNewName.setError("Set Name Taken")
                        Toast.makeText(this, "Set Name is taken", Toast.LENGTH_SHORT).show()
                    }
                    else if(exists == 0) {
                        val result = flashcardDBHelper.insertSet(FlashcardSetModel(input))
                        if(result) {
                            Toast.makeText(this, "Successfully added set", Toast.LENGTH_SHORT).show()
                        }
                        else {
                            Toast.makeText(this, "Failed to add set", Toast.LENGTH_SHORT).show()
                        }

                        viewSets(binding)
                        mAlertDialog.dismiss()
                    }
                }
                else {
                    Toast.makeText(this, "Set Name not Acceptable", Toast.LENGTH_SHORT).show()
                }


            }
            else {
                mDialogView.tilNewName.error = "Empty field"
                // Toast.makeText(this, "ERROR: EMPTY FIELD", Toast.LENGTH_SHORT).show()
            }
        }
        // Cancel Button Clicked
        mDialogView.btnCancel.setOnClickListener {
            mAlertDialog.dismiss()
            // Toast.makeText(this, "Clicked CANCEL", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkInput(input: String): Boolean {
        var result = false
        val checkThis = input.trim().take(7).lowercase()
        if(checkThis == "sqlite_" || checkThis == "sqlite ")
            result = false
        else{
            val firstChar = input.trim().get(0)
            result = firstChar.isLetter()
        }
        return result
    }

    private fun normalizeInput(input: String): String {
        return input.trim().lowercase().replace("\\s".toRegex(),"_").replaceFirstChar {it.uppercase()}
    }
}

class RecyclerAdapterMain(
    private val context: Activity,
    private val setList: ArrayList<String>,
    private val mumOfCardsList: ArrayList<String>,
    private val layout: Int,
    private val displayLimit: Int
) : RecyclerView.Adapter<RecyclerAdapterMain.ViewHolder>() {

    private var currentColor = 0

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var tvSet: TextView
        var tvNumOfCards: TextView
        var cEditSet: Chip
        var cDeleteSet: Chip
        var cvSet: MaterialCardView

        init {
            tvSet = itemView.findViewById(R.id.tvSet)
            tvNumOfCards = itemView.findViewById(R.id.tvNumOfCards)
            cvSet = itemView.findViewById<MaterialCardView>(R.id.cardViewSet)
            cEditSet = itemView.findViewById(R.id.cEditSet)
            cDeleteSet = itemView.findViewById(R.id.cDeleteSet)

            //

            // background color changes
            if(layout == R.layout.main_horizontal_set_layout) {
                //cvSet.setCardBackgroundColor(ContextCompat.getColor(context, R.color.pastel1))
                //cvSet.set
                colorSet(cvSet)
            }

            itemView.setOnClickListener {
                // Go to third view
                val tableName = setList[adapterPosition]
                val numOfCards =  mumOfCardsList[adapterPosition]
                // Toast.makeText(itemView.context, "$adapterPosition", Toast.LENGTH_SHORT).show()
                val intent = Intent(itemView.context, ThirdActivity::class.java)
                intent.putExtra("tableName",tableName)
                intent.putExtra("numOfCards",numOfCards)
                itemView.context.startActivity(intent)
            }
            cDeleteSet.setOnClickListener {

                val tableName = setList[adapterPosition]
                setList.removeAt(adapterPosition)
                mumOfCardsList.removeAt(adapterPosition)
                notifyItemRemoved(adapterPosition)
                val result = MainActivity.flashcardDBHelper.deleteSet(tableName.lowercase())
                // Toast.makeText(itemView.context, "You removed card: " + tableName + " result: $result", Toast.LENGTH_SHORT).show()
            }
            cEditSet.setOnClickListener {
                /*
                // EDIT NAME OF SET
                val intent = Intent(itemView.context, SecondActivity::class.java)
                itemView.context.startActivity(intent)
                 */
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        // v.setBackgroundResource(colorSets())
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder:  RecyclerAdapterMain.ViewHolder, position: Int) {
        holder.tvSet.text = setList[position]
        holder.tvNumOfCards.text = mumOfCardsList[position]
        //holder.cvSet.setBackgroundColor(Color.parseColor(R.color.purple_200.toString()))
    }

    override fun getItemCount(): Int {
        return if (setList.size > displayLimit) displayLimit else setList.size
    }

    fun colorSet(card: MaterialCardView){
        val colorList = arrayListOf<Int>(R.color.pastel1, R.color.pastel2, R.color.pastel3, R.color.pastel4, R.color.pastel5)
        var nxtColor = 0

        if(currentColor == 4) {
            currentColor == 0
        } else {
            currentColor += 1
        }

        card.setCardBackgroundColor(ContextCompat.getColor(context, colorList[currentColor]))
    }
}