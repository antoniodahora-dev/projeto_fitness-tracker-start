package co.tiagoaguiar.fitnesstracker

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import co.tiagoaguiar.fitnesstracker.model.Calc

class TmbActivity : AppCompatActivity() {

    private lateinit var lifestyle: AutoCompleteTextView

    private lateinit var editWeight: EditText
    private lateinit var editHeight: EditText
    private lateinit var editAge: EditText

    private lateinit var result: MutableList<Calc>
    private lateinit var adapter: ListCalcActivity.ListCalcAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tmb)

        editWeight = findViewById(R.id.edit_tbm_weigth)
        editHeight = findViewById(R.id.edit_tbm_heigth)
        editAge = findViewById(R.id.edit_tmb_age)

        //preenchimento do dropbox com Material Design
        lifestyle = findViewById(R.id.auto_lifestyle)
        val items = resources.getStringArray(R.array.tmb_lifestyle)
        lifestyle.setText(items.first())
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, items)
        lifestyle.setAdapter(adapter)

        val btnSend: Button = findViewById(R.id.btn_tmb_send)
        btnSend.setOnClickListener {
            if (!validate()) {
                Toast.makeText(this, R.string.fields_message,
                    Toast.LENGTH_SHORT).show()
                return@setOnClickListener //return irá matar a bloco do if
            }

            val weight = editWeight.text.toString().toInt()
            val height = editHeight.text.toString().toInt()
            val age = editAge.text.toString().toInt()

            val result = calculateTmb(weight, height, age)

            Log.d("Teste", "resultado: $result")
            val response = tmbRequest(result)


            AlertDialog.Builder(this)
                .setMessage(getString(R.string.tmb_response, result))
                .setPositiveButton(android.R.string.ok) { dialog, which -> } // function lambda
                .setNegativeButton(R.string.save) { dialog, which ->
                    //btn para salvar as informações no BD
                    //referencia do BD
                    Thread {
                        val app = application as App
                        val dao = app.db.calDao()

                        //atualizar as informações do TMB
                        // FIXME: checa se tem um updateId, se tiver, significa
                        // FIXME: que viemos da tela da lista de itens e devemos
                        // FIXME: editar ao inves de inserir
                        val updateId = intent.extras?.getInt("updateId")
                            if (updateId != null) {
                                dao.update(Calc(id = updateId, type = "tmb", res = response))
                                Log.d("Teste", "atualizar as informações")
                            } else {
                                dao.insert(Calc(type = "tmb", res = response))
                            }

                        //dao.insert(Calc(type = "tmb", res = response))
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
        val intent = Intent(this, ListCalcActivity::class.java)
        intent.putExtra("type", "tmb")
        startActivity(intent)
    }

    private fun tmbRequest(tmb: Double) : Double {
        val items = resources.getStringArray(R.array.tmb_lifestyle)
        return when {
            lifestyle.text.toString() == items[0] -> tmb * 1.2
            lifestyle.text.toString() == items[1] -> tmb * 1.375
            lifestyle.text.toString() == items[2] -> tmb * 1.55
            lifestyle.text.toString() == items[3] -> tmb * 1.725
            lifestyle.text.toString() == items[4] -> tmb * 1.9
            else ->  return 0.0
        }
    }

    private  fun calculateTmb(weight: Int, height: Int, age: Int): Double {
        return 66 + (13.8 * weight) + (5 * height ) - (6.8 * age)
    }

    private fun validate(): Boolean {
        return (editWeight.text.toString().isNotEmpty()
                && editHeight.text.toString().isNotEmpty()
                && editAge.text.toString().isNotEmpty()
                && !editWeight.text.toString().startsWith("0")
                && !editHeight.text.toString().startsWith("0")
                && !editAge.text.toString().startsWith("0"))
    }
}