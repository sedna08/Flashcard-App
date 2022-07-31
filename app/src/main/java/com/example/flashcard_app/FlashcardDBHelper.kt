package com.example.flashcard_app

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.content.contentValuesOf
import java.util.ArrayList

class FlashcardDBHelper(context: Context) : SQLiteOpenHelper(context,DATABASE_NAME,null,DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(SQL_CREATE_ENTRY)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(SQL_DELETE_ENTRY)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "flashcardapp.db"

        private const val SQL_CREATE_ENTRY =
            "CREATE TABLE " + FlashcardAppDBSchema.FlashcardSetEntity.TABLE_NAME + " (" +
                    FlashcardAppDBSchema.FlashcardSetEntity.COLUMN_NAME + " TEXT PRIMARY KEY)"

        private const val SQL_DELETE_ENTRY = "DROP TABLE IF EXISTS " + FlashcardAppDBSchema.FlashcardSetEntity.TABLE_NAME

    }

    private fun createFlashcardSetTable(name: String): Boolean {
        val db = writableDatabase
        var result = true
        try {
            val query = "CREATE TABLE IF NOT EXISTS " + name + "(" + FlashcardAppDBSchema.FlashcardEntity.COLUMN_QUESTION + " TEXT PRIMARY KEY, " + FlashcardAppDBSchema.FlashcardEntity.COLUMN_ANSWER + " TEXT);"
            db.execSQL(query)
        } catch(e: SQLiteException) {
            result = false
        }
        db.close()
        return result
    }

    @Throws(SQLiteConstraintException::class)
    fun insertSet(setModel: FlashcardSetModel): Boolean {
        // Create a new map of values, where column names are the keys
        val values = ContentValues()
        values.put(FlashcardAppDBSchema.FlashcardSetEntity.COLUMN_NAME, setModel.topic)

        val createResult = createFlashcardSetTable(setModel.topic)

        if(createResult){
            // Gets the data repository in write mode
            val db = writableDatabase
            // Insert the new row, returning the primary key value of the new row
            val success = db.insert(FlashcardAppDBSchema.FlashcardSetEntity.TABLE_NAME, null, values)
            db.close()
            return (Integer.parseInt("$success") != -1)
        }
        else {
            return false
        }
    }

    @Throws(SQLiteConstraintException::class)
    fun deleteSet(setName: String): Boolean {
        // Gets the data repository in write mode
        val db = writableDatabase

        db.execSQL("DROP TABLE IF EXISTS $setName")

        // Define 'where' part of query.
        val selection = FlashcardAppDBSchema.FlashcardSetEntity.COLUMN_NAME + " LIKE ?"
        // Specify arguments in placeholder order.
        val selectionArgs = arrayOf(setName)


        // Issue SQL statement.
        val success = db.delete(FlashcardAppDBSchema.FlashcardSetEntity.TABLE_NAME, selection, selectionArgs)

        db.close()
        return (Integer.parseInt("$success")!= 0)
    }

    @SuppressLint("Range")
    fun readSet(setName: String): Int {
        val exists = 0
        val db = readableDatabase
        val cursor: Cursor?
        try {
            cursor = db.rawQuery("select count(*) from " + FlashcardAppDBSchema.FlashcardSetEntity.TABLE_NAME + " WHERE " + FlashcardAppDBSchema.FlashcardSetEntity.COLUMN_NAME + " = ?", arrayOf(setName))
            if (cursor.moveToFirst()) {
                return cursor.getInt(0)
            }
            return 0
        } catch (e: SQLiteException) {
            // if table not yet present, create it
            db.execSQL(SQL_CREATE_ENTRY)
            db.close()
            return exists
        }
        cursor.close()
        db.close()
        return exists
    }

    fun updateSet(newName: String, oldName: String): Boolean {
        val db = writableDatabase
        val values = ContentValues()
        values.put(FlashcardAppDBSchema.FlashcardSetEntity.COLUMN_NAME, newName)
        val success = db.update(FlashcardAppDBSchema.FlashcardSetEntity.TABLE_NAME, values, "" + FlashcardAppDBSchema.FlashcardSetEntity.COLUMN_NAME +  "=?", arrayOf(oldName))
        db.execSQL("ALTER TABLE " + oldName + " RENAME TO " + newName + "")
        db.close()
        return (Integer.parseInt("$success")!= 0)
    }

    @SuppressLint("Range")
    fun readAllSets(): ArrayList<String>{
        val questionSets = ArrayList<String>()
        val db = writableDatabase
        val cursor: Cursor?
        try {
            cursor = db.rawQuery("select * from " + FlashcardAppDBSchema.FlashcardSetEntity.TABLE_NAME, null)
        } catch (e: SQLiteException) {
            db.execSQL(SQL_CREATE_ENTRY)
            return ArrayList()
        }

        var name: String
        if (cursor!!.moveToFirst()) {
            while (!cursor.isAfterLast) {
                name = cursor.getString(cursor.getColumnIndex(FlashcardAppDBSchema.FlashcardSetEntity.COLUMN_NAME))
                questionSets.add(name)
                cursor.moveToNext()
            }
        }
        cursor.close()
        db.close()
        return questionSets
    }

    fun updateQuestion(newQuestion: String, newAnswer: String, oldQuestion: String): Boolean {
        val db = writableDatabase
        val values = ContentValues()
        values.put(FlashcardAppDBSchema.FlashcardEntity.COLUMN_QUESTION, newQuestion)
        values.put(FlashcardAppDBSchema.FlashcardEntity.COLUMN_ANSWER, newAnswer)
        val success = db.update(FlashcardAppDBSchema.FlashcardSetEntity.TABLE_NAME, values, "" + FlashcardAppDBSchema.FlashcardEntity.COLUMN_QUESTION +  " = ?", arrayOf(oldQuestion))
        db.close()
        return (Integer.parseInt("$success")!= 0)
    }

    @SuppressLint("Range")
    fun readAllQuestions(name: String): ArrayList<FlashcardModel>{
        val flashcardList = ArrayList<FlashcardModel>()
        val db = writableDatabase
        val cursor: Cursor?
        try {
            cursor = db.rawQuery("select * from $name", null)
        } catch (e: SQLiteException) {
            return ArrayList()
        }
        var question: String
        var answer: String
        if (cursor!!.moveToFirst()) {
            while (!cursor.isAfterLast) {
                question = cursor.getString(cursor.getColumnIndex(FlashcardAppDBSchema.FlashcardEntity.COLUMN_QUESTION))
                answer = cursor.getString(cursor.getColumnIndex(FlashcardAppDBSchema.FlashcardEntity.COLUMN_ANSWER))
                flashcardList.add(FlashcardModel(question,answer))
                cursor.moveToNext()
            }
        }
        cursor.close()
        db.close()
        return flashcardList
    }

    @Throws(SQLiteConstraintException::class)
    fun insertQuestion(setModel: FlashcardModel, name: String): Boolean {
        // Gets the data repository in write mode
        val db = writableDatabase
        // Create a new map of values, where column names are the keys
        val values = ContentValues()
        values.put(FlashcardAppDBSchema.FlashcardEntity.COLUMN_QUESTION, setModel.question)
        values.put(FlashcardAppDBSchema.FlashcardEntity.COLUMN_ANSWER, setModel.answer)

        // Insert the new row, returning the primary key value of the new row
        val success = db.insert(name, null, values)

        db.close()
        return (Integer.parseInt("$success")!=-1)
    }

    @Throws(SQLiteConstraintException::class)
    fun deleteQuestion(question:String, name: String): Boolean {
        // Gets the data repository in write mode
        val db = writableDatabase

        // Define 'where' part of query.
        val selection = FlashcardAppDBSchema.FlashcardEntity.COLUMN_QUESTION + " LIKE ?"
        // Specify arguments in placeholder order.
        val selectionArgs = arrayOf(question)


        // Issue SQL statement.
        val success = db.delete(name, selection, selectionArgs)

        db.close()
        return (Integer.parseInt("$success")!= 0)
    }

    @SuppressLint("Range")
    fun readQuestion(setQuestion: String, tableName: String): Int {
        val exists = 0
        val db = readableDatabase
        val cursor: Cursor?
        try {
            cursor = db.rawQuery("select count(*) from " + tableName + " WHERE " + FlashcardAppDBSchema.FlashcardEntity.COLUMN_QUESTION + " = ?", arrayOf(setQuestion))
            if (cursor.moveToFirst()) {
                return cursor.getInt(0)
            }
            return 0
        } catch (e: SQLiteException) {
            // if table not yet present, create it
            db.execSQL(SQL_CREATE_ENTRY)
            db.close()
            return exists
        }
        cursor.close()
        db.close()
        return exists
    }

    fun getCount(tableName: String): Int {
        val db = readableDatabase
        val count = DatabaseUtils.queryNumEntries(db,tableName)
        db.close()
        return count.toInt()
    }
}