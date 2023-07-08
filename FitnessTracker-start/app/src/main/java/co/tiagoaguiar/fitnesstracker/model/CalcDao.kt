package co.tiagoaguiar.fitnesstracker.model

import androidx.room.*

@Dao
interface CalcDao {

    //inserir informações no BD
    @Insert
    fun insert(calc: Calc)

    //busca informações no BD e joga em uma LISTA
    @Query("SELECT * FROM Calc WHERE type = :type")
    fun getRegisterByType(type: String) : List<Calc>

    //delete o registro
    @Delete
    fun delete(calc: Calc): Int

    @Update
    fun update(calc: Calc)

}