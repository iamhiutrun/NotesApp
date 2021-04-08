package hiutrun.example.notesapp.dao

import androidx.room.*
import hiutrun.example.notesapp.entities.Notes

@Dao
interface NoteDao {

    @Query("SELECT * FROM notes ORDER BY id DESC")
    suspend fun getAllNotes() : List<Notes>

    @Query("SELECT * FROM notes WHERE id=:id")
    suspend fun getSpecificNote(id:Int) : Notes

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotes(notes: Notes)

    @Delete
    suspend fun deleteNotes(notes: Notes)

}