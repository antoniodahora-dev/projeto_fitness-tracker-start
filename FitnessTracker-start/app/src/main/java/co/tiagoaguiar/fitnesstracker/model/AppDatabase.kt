package co.tiagoaguiar.fitnesstracker.model

import android.content.Context
import androidx.room.*

//passamos a classe que contem as informações do BD mais a versão do BD
@Database(entities = [Calc::class], version = 1)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun calDao(): CalcDao

    //function singleton - criará um BD único
    companion object {

        //variavel de controle do BD - Sendo utilizamos a Variável em letras maisculas
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context) : AppDatabase {

            return if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "fitness_tracker"
                    ).build()
                }
                INSTANCE as AppDatabase
            } else {
                INSTANCE as AppDatabase
            }
        }
    }

}