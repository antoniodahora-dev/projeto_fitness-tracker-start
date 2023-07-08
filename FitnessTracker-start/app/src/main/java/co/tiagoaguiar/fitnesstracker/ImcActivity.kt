package co.tiagoaguiar.fitnesstracker

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import co.tiagoaguiar.fitnesstracker.model.Calc

class ImcActivity : AppCompatActivity() {

    private lateinit var editWeight: EditText
    private lateinit var editHeight: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_imc)

        // irá receber as informações do EDITTEXT da ACTIVITY IMC
        editWeight = findViewById(R.id.edit_icm_weigth)
        editHeight = findViewById(R.id.edit_icm_heigth)

        val btnSend: Button = findViewById(R.id.btn_imc_send)
        btnSend.setOnClickListener {
            if (!validate()) {
                Toast.makeText(this, R.string.fields_message,
                    Toast.LENGTH_SHORT).show()
                return@setOnClickListener //return irá matar a bloco do if
            }

            val weight = editWeight.text.toString().toInt()
            val height = editHeight.text.toString().toInt()

            val result = calculateImc(weight, height)

            Log.d("Teste", "resultado: $result")

            val imcResponseId = imcResponse(result)

            AlertDialog.Builder(this)
                .setTitle(getString(R.string.imc_response, result))
                .setMessage(imcResponseId)
                .setPositiveButton(
                    android.R.string.ok
                ) { dialog, which -> } // function lambda
                .setNegativeButton(R.string.save) { dialog, which ->

                    //btn para salvar as informações no BD
                    //referencia do BD
                    Thread {
                        val app = application as App
                        val dao = app.db.calDao()
                       // dao.insert(Calc(type = "imc", res = result)),
                        val updateId = intent.extras?.getInt("updateId")
                        if (updateId != null) {
                            dao.update(Calc(id = updateId, type = "imc", res = result))
                            Log.d("Teste", "atualizar as informações")
                        } else {
                            dao.insert(Calc(type = "imc", res = result))
                        }

                        //para que o toast funcione dentro da thread é necessário ter o runOnUiThread
                        runOnUiThread {
                            openListActivity()
                        }
                    }.start()

                }
                .create()
                .show()

            //metodo para esconder o teclado apos clicar as informações
            val service = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            service.hideSoftInputFromWindow(currentFocus?.windowToken, 0)


        }
    }

    //onCreate para o menus
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true // o menu ficar visível
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //menu de listagem dos icms
        if (item.itemId == R.id.menu_search) {
            finish() // destrui activity atual
            openListActivity()
        }
        return super.onOptionsItemSelected(item)
    }

    // funcao para ser reutilizada para chamar as telas
    //tanto para abrir tanto do menu quando salvar as informações
    private fun openListActivity() {
        val intent = Intent(this@ImcActivity, ListCalcActivity::class.java)
        intent.putExtra("type", "imc")
        startActivity(intent)
    }


    @StringRes // anotação de recurso - forca o desenvolver a passar um arquivo de recurso
    private fun imcResponse(imc: Double): Int {

//        if (imc <15.0) {
//            return R.string.imc_severely_low_weight
//        } else if (imc < 16.0) {
//            return R.string.imc_very_low_weight
//        } else if (imc < 18.5) {
//            return R.string.imc_low_weight
//        } else if (imc < 25.0) {
//            return R.string.normal
//        } else if (imc < 30.0) {
//            return R.string.imc_high_weight
//        } else if (imc < 35.0) {
//            return R.string.imc_so_high_weight
//        } else if (imc < 40.0) {
//            return R.string.imc_severely_high_weight
//        } else {
//            return R.string.imc_extreme_weight
//        }

        //forma otimizada de fazer um código mais limpo
        return when {
            imc < 15.0 -> R.string.imc_severely_low_weight
            imc < 16.0 -> R.string.imc_very_low_weight
            imc < 18.5 -> R.string.imc_low_weight
            imc < 25.0 -> R.string.normal
            imc < 30.0 -> R.string.imc_high_weight
            imc < 35.0 -> R.string.imc_so_high_weight
            imc < 40.0 -> R.string.imc_severely_high_weight

            else -> R.string.imc_extreme_weight
        }
    }


    //calculadora do peso x altura
    private fun calculateImc(weight: Int, height: Int): Double {
        //regra de negocio - para os campos peso e altura

        return weight / ((height / 100.0) * (height / 100.0)) //(height/100.0) - calculando altura ao quadrado
    }

    private fun validate(): Boolean {
        //não pode inserir valores nulos / vazio
//        //1º opcao if e else
//        if (editWeight.text.toString().isNotEmpty()
//            && editHeight.text.toString().isNotEmpty()
//            && !editWeight.text.toString().startsWith("0")
//            && !editHeight.text.toString().startsWith("0")
//        ) {
//            return true
//        } else {
//            return false
//        }

        //2º forma otimizada de fazer um código mais limpo
        return (editWeight.text.toString().isNotEmpty()
                && editHeight.text.toString().isNotEmpty()
                && !editWeight.text.toString().startsWith("0")
                && !editHeight.text.toString().startsWith("0"))
    }
}