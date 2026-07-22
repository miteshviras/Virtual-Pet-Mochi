package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PetDao {
  @Query("SELECT * FROM pet_stats WHERE id = 1")
  fun getPetStats(): Flow<PetEntity?>

  @Query("SELECT * FROM pet_stats WHERE id = 1")
  suspend fun getPetStatsSync(): PetEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun updatePetStats(pet: PetEntity)
}
