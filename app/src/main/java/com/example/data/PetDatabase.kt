package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [PetEntity::class], version = 7, exportSchema = false)
abstract class PetDatabase : RoomDatabase() {
  abstract fun petDao(): PetDao

  companion object {
    @Volatile
    private var INSTANCE: PetDatabase? = null

    fun getDatabase(context: Context): PetDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          PetDatabase::class.java,
          "mochi_pet_db"
        )
        .fallbackToDestructiveMigration()
        .build()
        INSTANCE = instance
        instance
      }
    }
  }
}
