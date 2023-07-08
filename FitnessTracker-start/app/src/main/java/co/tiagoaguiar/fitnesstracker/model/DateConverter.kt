package co.tiagoaguiar.fitnesstracker.model

import androidx.room.TypeConverter
import java.util.*

object DateConverter {

    //irá informar o tipo que ele irá converter
    //para buscar o objeto
    @TypeConverter
    fun toDate(dateLong: Long?) : Date?{
        return if(dateLong != null) Date(dateLong) else null
    }

    //irá converter o date num LONG
    //gravar as informaçoes no BD
    @TypeConverter
    fun fromDate(date: Date?) : Long? {
        return date?.time
    }
}