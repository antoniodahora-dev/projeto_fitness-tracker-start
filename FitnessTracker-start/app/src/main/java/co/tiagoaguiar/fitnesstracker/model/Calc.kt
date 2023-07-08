package co.tiagoaguiar.fitnesstracker.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity // Classe de BD
data class Calc(
    //chave primaria de identificação
    @PrimaryKey(autoGenerate =  true) val id: Int = 0,

    // colunas de tipo do BD
    @ColumnInfo(name = "type") val type: String,
    @ColumnInfo(name = "res") val res: Double,

    // coluna que irá grava a data de inserção da informação
    @ColumnInfo(name = "created_date") val createdDate: Date = Date(),
)
