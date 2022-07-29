package com.example.flashcard_app

import android.provider.BaseColumns

object FlashcardAppDBSchema {
    // Inner class that defines the table contents
    class FlashcardSetEntity:BaseColumns {
        companion object {
            const val TABLE_NAME = "flashcardsets"
            const val COLUMN_NAME = "name"
        }
    }
    class FlashcardEntity:BaseColumns {
        companion object {
            const val COLUMN_QUESTION = "question"
            const val COLUMN_ANSWER = "answer"
        }
    }
}