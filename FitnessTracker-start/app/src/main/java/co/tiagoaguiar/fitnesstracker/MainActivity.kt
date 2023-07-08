package co.tiagoaguiar.fitnesstracker

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    //private lateinit var btnImc: LinearLayout
    private lateinit var rvMain: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mainItems = mutableListOf<MainItem>()
        mainItems.add(
            MainItem(
                id = 1,
                drawableId = R.drawable.ic_baseline_directions_run_24,
                textStringId = R.string.label_imc,
            )
        )

        mainItems.add(
            MainItem(
                id = 2,
                drawableId = R.drawable.ic_baseline_directions_bike_24,
                textStringId = R.string.label_tmb
                )
        )

        mainItems.add(
            MainItem(
                id = 3,
                drawableId = R.drawable.ic_baseline_article_24,
                textStringId = R.string.list_imc
                )
        )

        mainItems.add(
            MainItem(
                id = 4,
                drawableId = R.drawable.ic_baseline_assessment_24,
                textStringId = R.string.list_tmb
                )
        )

        //chamando a recycleView
        //val adapter = MainAdapter(mainItems, object : OnItemClickListener {
           //evento de click #forma 2
//            override fun onClick(id: Int) {
//                when(id) {
//                    1 -> {
//                        val intent = Intent(this@MainActivity, ImcActivity::class.java)
//                        startActivity(intent)
//                    }
//                }
//                Log.i("Teste", "Clicou no imageButton $id")
//            }
//
//        })

        //# metodo 3 sem interface e sem objeto anomino
        val adapter = MainAdapter(mainItems) { id ->
            when (id) {
                1 -> {
                    val intent = Intent(this@MainActivity, ImcActivity::class.java)
                    startActivity(intent)
                }

                2 -> {
                    val intent = Intent(this@MainActivity, TmbActivity::class.java)
                    startActivity(intent)
                }

                3 -> {
                    openListActivity(id)
                }

                4 -> {
                    openListActivity(id)
                   }
            }
            Log.i("Teste", "Clicou no imageButton $id")
        }


        rvMain = findViewById(R.id.rv_main)
        rvMain.adapter = adapter

        //geraciador do Layout
        rvMain.layoutManager = GridLayoutManager(this, 1)

        //classe para administrar o RecycleView e suas celulas
       // Class Adapter

//        btnImc = findViewById(R.id.btn_imc)
//
//        btnImc.setOnClickListener {
//            val i = Intent(this, ImcActivity::class.java)
//            startActivity(i)
//        }
    }




    //onCreate para o menus
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return false // o menu ficar visível
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //menu de listagem dos icms
        if (item.itemId == R.id.menu_search) {
            finish() // destrui activity atual
            openListActivity(item.itemId)
        }
        return super.onOptionsItemSelected(item)
    }

    // funcao para ser reutilizada para chamar as telas
    //tanto para abrir tanto do menu quando salvar as informações
    private fun openListActivity(id: Int) {
       if (id == 3 ) {
           val intent = Intent(this, ListCalcActivity::class.java)
           intent.putExtra("type", "imc")
           startActivity(intent)
       } else {
           val intent = Intent(this, ListCalcActivity::class.java)
           intent.putExtra("type", "tmb")
           startActivity(intent)
       }
    }

    //forma # 1
//    override fun onClick(id: Int) {
//        when(id) {
//            1 -> {
//                val i = Intent(this, ImcActivity::class.java)
//                startActivity(i)
//            }
//        }
//        Log.i("Teste", "Clicou no imageButton $id")
//    }

    //somente a tela prinicipal irá ver essa class
    //class administra
    private inner class MainAdapter(
        private val mainItems: List<MainItem>,
//        private val onItemClickListener: OnItemClickListener
        private val onItemClickListener: (Int) -> Unit,
            ) : RecyclerView.Adapter<MainAdapter.MainViewHolder>() {

        // 1 - QUal é o layout XML da celula especifica (item)
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
            val view = layoutInflater.inflate(R.layout.main_item, parent, false)
            return MainViewHolder(view)
        }

        // 2 - Disparado toda vez que houver uma rolagem na tela e for necessário troca o conteudo
        override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
            // buscar a posição do item da lista
            val itemCurrent = mainItems[position]

            // METODO PARA PEGAR DINAMICAMENTE A POSICAO DO ITEM,
            holder.bind(itemCurrent)
        }

        // 3 -informar quantas celulas essa listagem terá
        override fun getItemCount(): Int {
            return mainItems.size
        }

        //class que ira buscar a referencias da main_Item.xml (Activity)
        private inner class MainViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            //ADMINISTRADOR DA CELULA
            fun bind(item: MainItem) {

                //referencia do item
                val img: ImageView = itemView.findViewById(R.id.item_img_icon)
                val name: TextView = itemView.findViewById(R.id.item_txt_name)
                val  container: LinearLayout = itemView.findViewById(R.id.item_container_imc)

                img.setImageResource(item.drawableId)
                name.setText(item.textStringId)
               // container.setBackgroundColor(item.color)

                container.setOnClickListener {
                    //pegando a informação de um function
                    onItemClickListener.invoke(item.id)

                    //pegando a informação de uma interface
                    //onItemClickListener.onClick(item.id)
                }
            }
        }
    }

}