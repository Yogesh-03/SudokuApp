package com.example.sudokugame.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [SudokuEntity::class],
    version = 1,
    exportSchema = false
)
abstract class SudokuRoomDatabase : RoomDatabase() {

    abstract fun getSudokuDao(): SudokuDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile  // made field recievalble to other thread immediately
        private var INSTANCE: SudokuRoomDatabase? = null

        fun getDatabase(context: Context): SudokuRoomDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        SudokuRoomDatabase::class.java,
                        "sudoku_data_database"
                    ).build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}