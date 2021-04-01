package hiutrun.example.notesapp.dao

import androidx.room.*
import hiutrun.example.notesapp.entities.Notes

@Dao
interface NoteDao {

    @get:Query("SELECT * FROM notes ORDER BY id DESC")
    val allNotes : List<Notes>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNotes(notes: Notes)

    @Delete
    fun deleteNotes(notes: Notes)

}