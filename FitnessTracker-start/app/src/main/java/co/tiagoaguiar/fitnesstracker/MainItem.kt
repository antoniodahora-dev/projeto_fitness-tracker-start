package co.tiagoaguiar.fitnesstracker

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

//class de Dados
data class MainItem(
    val id: Int,
    @DrawableRes val drawableId: Int,
    @StringRes val textStringId: Int,
    //val color: Int
)