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
import com.example.flashcard_app.databinding.ActivityMainBinding
import com.example.flashcard_app.databinding.AlertDialogBinding
import com.example.flashcard_app.databinding.AlertdialogAddsetBinding
import com.example.flashcard_app.databinding.MainVerSetLayoutBinding
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip

class MainActivity : AppCompatActivity() {

    // declaration of variable for database handler/helper
    private lateinit var flashcardDBHelper : FlashcardDBHelper
    private  var flashcardsetList = ArrayList<String>()
    private  var cardCountList = ArrayList<String>()

    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: RecyclerView.Adapter<RecyclerAdapterMain.ViewHolder>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        getSupportActionBar()?.hide();
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // initializing database
        flashcardDBHelper = FlashcardDBHelper(this)
        /*
        // List for Sets
        val setList = arrayOf("Set 1", "Set 2", "Set 3", "Set 4", "Set 5", "Set 6", "Set 7", "Set 8").toCollection(ArrayList<String>())
        val numOfCardsList = arrayOf("Num of Cards: 1", "Num of Cards: 2", "Num of Cards: 3", "Num of Cards: 4", "Num of Cards: 5", "Num of Cards: 6", "Num of Cards: 7", "Num of Cards: 8").toCollection(ArrayList<String>())
        // Horizontal Panning Cards*/

        viewSets(binding)
        /*
        val setList = ArrayList<String>()
        val numOfCardsList = ArrayList<String>()
        */

        /*
        layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        binding.rvSets.layoutManager = layoutManager

        adapter = RecyclerAdapterMain(this, setList, numOfCardsList)
        binding.rvSets.adapter = adapter

        // check card view
        checkHCardView(binding, setList, numOfCardsList)
        */

        // Add button 1
        binding.b1Add.setOnClickListener() { createSet(binding) }

        // Add button 2
        binding.b2Add.setOnClickListener() { createSet(binding) }

        // delete buttons are in the recyclerview.

    }

    fun viewSets(binding: ActivityMainBinding) {
        binding.rvSets.visibility = View.GONE
        binding.hScrollView.visibility = View.GONE
        val setNames = ArrayList<String>()
        val setCounts = ArrayList<String>()
        var setNum: Int
        val sets = flashcardDBHelper.readAllSets()
        if(sets.isNotEmpty()) {
            try{
                sets.forEach {
                    setNames.add(it)
                }
                Toast.makeText(this,"Fetched Question Sets", Toast.LENGTH_SHORT).show()
            } catch(e: Exception) {
                Toast.makeText(this,"$e", Toast.LENGTH_SHORT).show()
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

        layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        binding.rvSets.layoutManager = layoutManager

        adapter = RecyclerAdapterMain(this, flashcardsetList, cardCountList)
        binding.rvSets.adapter = adapter

        checkHCardView(binding, flashcardsetList, cardCountList)

        binding.rvSets.visibility = View.VISIBLE
        binding.hScrollView.visibility = View.VISIBLE
    }

    fun createSet(binding: ActivityMainBinding) {
        // add set of flash cards things
        val mDialogView = AlertdialogAddsetBinding.inflate(layoutInflater)
        val mBuilder = androidx.appcompat.app.AlertDialog.Builder(this)
            .setView(mDialogView.root)
            .setTitle("Add Flashcard Set")
        val mAlertDialog = mBuilder.show()

        // Add Button in Alert Dialog is clicked
        mDialogView.btnAdd.setOnClickListener {
            val input = mDialogView.etNewName.text.toString()
            if(input.trim().isNotEmpty() && input.trim().isNotBlank()) {
                val exists = flashcardDBHelper.readSet(input.lowercase())
                if(exists != 0) {
                    mDialogView.etNewName.setError("Set Name Taken")
                    Toast.makeText(this, "Set Name is taken", Toast.LENGTH_SHORT).show()
                }
                else if(exists == 0) {
                    mAlertDialog.dismiss()
                    val result = flashcardDBHelper.insertSet(FlashcardSetModel(input.lowercase()))
                    Toast.makeText(this, "Added Set: $result", Toast.LENGTH_SHORT).show()
                }
                /* // original code
                mAlertDialog.dismiss()
                //get text from EditText of Custom Layout
                val newName = mDialogView.etNewName.text.toString()
                val result = questionSetDBHelper.updateSet(newName,tableName)
                if(result) {
                    binding.tvSetName.text = newName
                    tableName = newName
                    binding.lvQuestionsList.visibility = View.GONE
                }*/
            }
            else {
                mDialogView.etNewName.setError("Empty Field")
                Toast.makeText(this, "ERROR: EMPTY FIELD", Toast.LENGTH_SHORT).show()
            }
        }
        //Cancel Button Clicked
        mDialogView.btnCancel.setOnClickListener {
            mAlertDialog.dismiss()
            Toast.makeText(this, "Clicked CANCEL", Toast.LENGTH_SHORT).show()
        }

        viewSets(binding)

    }

    fun checkHCardView(binding: ActivityMainBinding, setList: ArrayList<String>, numOfCardsList: ArrayList<String>) {
        var numOfSets = setList.count()
        when (numOfSets) {
            0 -> {
                //binding.hScrollView.visibility = View.GONE
                binding.cv2.visibility = View.GONE
                binding.cv3.visibility = View.GONE
                binding.cv1Name.text = "No Sets Added Yet"
                binding.cv1Num.text = ""
            }
            1 -> {
                binding.cv2.visibility = View.GONE
                binding.cv3.visibility = View.GONE
                binding.cv1Name.text = setList[0]
                binding.cv1Num.text = numOfCardsList[0]
            }
            2 -> {
                binding.cv3.visibility = View.GONE
                binding.cv1Name.text = setList[0]
                binding.cv1Num.text = numOfCardsList[0]
                binding.cv2Name.text = setList[1]
                binding.cv2Num.text = numOfCardsList[1]
            }
            else -> {
                binding.cv1Name.text = setList[0]
                binding.cv1Num.text = numOfCardsList[0]
                binding.cv2Name.text = setList[1]
                binding.cv2Num.text = numOfCardsList[1]
                binding.cv3Name.text = setList[2]
                binding.cv3Num.text = numOfCardsList[2]
            }
        }
    }

}

class RecyclerAdapterMain(
    private val context: Activity,
    private val setList: ArrayList<String>,
    private val mumOfCardsList: ArrayList<String>
) : RecyclerView.Adapter<RecyclerAdapterMain.ViewHolder>() {

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var tvSet: TextView
        var tvNumOfCards: TextView
        var cPlaySet: Chip
        var cDeleteSet: Chip
        var cvSet: MaterialCardView

        init {
            tvSet = itemView.findViewById(R.id.tvSet)
            tvNumOfCards = itemView.findViewById(R.id.tvNumOfCards)
            cvSet = itemView.findViewById<MaterialCardView>(R.id.cardViewSet)
            cPlaySet = itemView.findViewById(R.id.cPlaySet)
            cDeleteSet = itemView.findViewById(R.id.cDeleteSet)

            //cvSet.setBackgroundResource(colorSets())

            itemView.setOnClickListener {
                Toast.makeText(itemView.context, "$adapterPosition", Toast.LENGTH_SHORT).show()
                // GO TO 3RD VIEW
                // val intent = Intent(this, ThirdActivity::class.java)
                // startActivity(intent)
                //
            }
            cDeleteSet.setOnClickListener {
                Toast.makeText(itemView.context, "You removed card no $adapterPosition", Toast.LENGTH_SHORT).show()
                setList.removeAt(adapterPosition)
                mumOfCardsList.removeAt(adapterPosition)
                notifyItemRemoved(adapterPosition)
            }
            cPlaySet.setOnClickListener {

                // GO TO 3RD VIEW
                // val intent = Intent(this, ThirdActivity::class.java)
                // startActivity(intent)
                // can be deleted.

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.main_ver_set_layout, parent, false)
        // v.setBackgroundResource(colorSets())
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder:  RecyclerAdapterMain.ViewHolder, position: Int) {
        holder.tvSet.text = setList[position]
        holder.tvNumOfCards.text = mumOfCardsList[position]
    }


    override fun getItemCount(): Int {
        return setList.size
    }


}