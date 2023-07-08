package co.tiagoaguiar.fitnesstracker


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.tiagoaguiar.fitnesstracker.model.Calc
import java.lang.IllegalStateException
import java.text.SimpleDateFormat
import java.util.*

class ListCalcActivity : AppCompatActivity(), OnListClickListener {

    // FIXME: adapter e result precisa estar no escopo para podermos usá-lo na hora de excluir o item
    private lateinit var adapter: ListCalcAdapter
    private lateinit var result: MutableList<Calc>

    private lateinit var rvList : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_calc)

        result = mutableListOf<Calc>()

        // FIXME: passando o this que é Activity que implementa o OnListClickListener
        adapter = ListCalcAdapter(result, this)

        rvList = findViewById(R.id.rv_list)
        rvList.layoutManager = LinearLayoutManager(this)
        rvList.adapter = adapter


        val type =
            intent?.extras?.getString("type") ?: throw IllegalStateException("type not found")

        //buscar no banco esse tipo
        Thread {
            val app = application as App
            val dao = app.db.calDao()
            val response = dao.getRegisterByType(type)

            //para que o toast funcione dentro da thread é necessário ter o runOnUiThread
            runOnUiThread {
               result.addAll(response)

                //irá notificar que os dados foram modificados
               adapter.notifyDataSetChanged()
            }
        }.start()


    }

    // FIXME: função de callback que irá disparar o evento de click no item da lista do Adapter
    override fun onClick(id: Int, type: String) {
        when(type) {
            "imc" -> {
                val intent = Intent(this, ImcActivity::class.java)
                // FIXME: passando o ID do item que precisa ser atualizado, ou seja, na outra tela
                // FIXME: vamos buscar o item e suas propriedades com esse ID
                intent.putExtra("updateId", id)
                startActivity(intent)
            }
            "tmb" -> {
                val intent = Intent(this, TmbActivity::class.java)
                intent.putExtra("updateId", id)
                startActivity(intent)
            }
        }
        finish()
    }

    // FIXME: função de callback que irá disparar o evento de long-click no item da lista do Adapter
    override fun onLongClick(position: Int, calc: Calc) {
        // FIXME: pergunta se realmente quer excluir
        AlertDialog.Builder(this)
            .setMessage(getString(R.string.delete_message))
            .setNegativeButton(android.R.string.cancel) { dialog, which ->
            }
            .setPositiveButton(android.R.string.ok) { dialog, which ->
                Thread {
                    val app = application as App
                    val dao = app.db.calDao()

                    // FIXME: exclui o item que foi clicado com long-click
                    val response = dao.delete(calc)
                    if (response > 0) {
                        runOnUiThread {
                            // FIXME: remove da lista e do adapter o item
                            result.removeAt(position)
                            adapter.notifyItemRemoved(position)
                        }
                    }
                }.start()
            }
            .create()
            .show()
    }

    inner class ListCalcAdapter(
      private val listCalc: List<Calc>,
      private val listener: OnListClickListener
    ) : RecyclerView.Adapter<ListCalcAdapter.ListCalcViewHolder>() {


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListCalcViewHolder {
            val view = layoutInflater.inflate(android.R.layout.simple_expandable_list_item_1, parent, false)
            return ListCalcViewHolder(view)
        }


        override fun onBindViewHolder(holder: ListCalcViewHolder, position: Int) {

            val itemCurrent = listCalc[position]
            holder.bind(itemCurrent)
        }


        override fun getItemCount(): Int {
            //tamanhop da lista
            return listCalc.size
        }

        //class que ira buscar a referencias da main_Item.xml (Activity)
        inner class ListCalcViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bind(item: Calc) {
                //text simple_expandable_list_item_1
                val tv = itemView as TextView

                //formatação data padrão PT-BR
                val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt", "BR"))
                val data = sdf.format(item.createdDate)
                val res = item.res

                tv.text = getString(R.string.list_response, res, data)

                // FIXME: usado para delegar a quem estiver implementando a interface (Activity) o evento para
                // FIXME: excluir o item da lista
                tv.setOnLongClickListener {

                    // FIXME: precisamos da posição corrente (adapterPosition) para saber qual item da lista
                    // FIXME: deve ser removido da recyclerview usando o notify do Adapter
                    listener.onLongClick(adapterPosition, item)
                    true
                }

                // FIXME: usado para delegar a quem estiver implementando a interface (Activity) o evento para
                // FIXME: editar um item da lista
                tv.setOnClickListener {

                    AlertDialog.Builder(this@ListCalcActivity)
                        .setMessage("Deseja alterar o Registro ${item.id} do - ${item.type} ?")
                        .setNegativeButton(android.R.string.cancel) { dialog, which -> }
                        .setPositiveButton(android.R.string.ok) { dialog, which ->
                            Thread {
                                listener.onClick(item.id, item.type)
                                Log.d("Teste", "clicou na lista")
                            }.start()
                        }
                        .create()
                        .show()
                }
            }
        }
    }
}