package iestr.gag.examen.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import iestr.gag.examen.R
import iestr.gag.examen.model.Bicho
import iestr.gag.examen.viewmodel.PartidaViewModel

class BichosAdapter(padre: Fragment, private val vm:PartidaViewModel): RecyclerView.Adapter<BichosAdapter.Holder>() {

    private lateinit var listaBichos:MutableList<Bicho>

    init {
        //Cuando el valor del chivato cambia (En este caso al fianlizar un ataque), se actualiza la lista.
        vm.chivato.observe(padre.viewLifecycleOwner){
            listaBichos=vm.listaBichos.value!!
            notifyDataSetChanged()
        }
    }

    //Ocurre internamente al crear los contenedores del RecyclerView.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        //Como no estoy en un contexto, tengo que coger el layoutinflater de algún contexto.
        val vista= LayoutInflater
            .from(parent.context)
            .inflate(R.layout.linea,parent,false)
        //El false significa: "no añadas lo que has inflado al padre todavía, ya lo hare yo".
        //En realidad no lo hago yo, sino que lo hará el recyclerview cuando corresponda.
        return Holder(vista)
    }

    //Ocurre internamente cada vez que hacemos scroll en el RecyclerView por lo que se
    //descarga el que sale de la pantalla y se carga el que entra.
    override fun onBindViewHolder(holder: Holder, position: Int) {
        val bicho=listaBichos[position]
        holder.rellena(bicho)
    }

    //Devuelve la longitud de la lista.
    override fun getItemCount(): Int {
        return listaBichos.size
    }

    inner class Holder(vista: View):RecyclerView.ViewHolder(vista){
        //Almaceno en variables todos los elementos del layout linea con el que relleno
        //el contenedor para poder asignarles valores.
        val calaveraBicho=vista.findViewById<ImageView>(R.id.calavera_bicho)
        val caraBicho=vista.findViewById<ImageView>(R.id.cara_bicho)
        val rasgosBicho=vista.findViewById<ImageView>(R.id.rasgos_bicho)
        val nombreBicho=vista.findViewById<TextView>(R.id.nombre_bicho)
        val armaBicho=vista.findViewById<ImageView>(R.id.arma_bicho)

        init{
            //Gestiono lo que pasa al tocar uno de los contenedores del RecyclerView.
            vista.setOnClickListener{
                vm.protaAtacaBicho(absoluteAdapterPosition)
            }
        }
        //Relleno los elementos en cada línea dentro de cada contenedor.
        fun rellena(bicho: Bicho){
            //Dependiendo del tipo del bicho tendrá unas imágenes.
            when(bicho.tipo){
                0 ->{
                    caraBicho.setImageResource(R.drawable.vampiro_cara)
                    calaveraBicho.setImageResource(R.drawable.craneo_cuernos)
                    rasgosBicho.setImageResource(R.drawable.vampiro_normal)
                }
                1 ->{
                    caraBicho.setImageResource(R.drawable.demonio_cara)
                    calaveraBicho.setImageResource(R.drawable.craneo_cuernos)
                    rasgosBicho.setImageResource(R.drawable.demonio_normal)
                }
                2 ->{
                    caraBicho.setImageResource(R.drawable.orco_cara)
                    calaveraBicho.setImageResource(R.drawable.craneo)
                    rasgosBicho.setImageResource(R.drawable.orco_normal)
                }
                else ->{
                    caraBicho.setImageResource(R.drawable.troll_cara)
                    calaveraBicho.setImageResource(R.drawable.craneo)
                    rasgosBicho.setImageResource(R.drawable.troll_normal)
                }
            }
            //Se ponen las 3 capas una encima de otra y a menor energía tengan mas se ve la
            //calavera y menos la cara usando transparencias.
            caraBicho.alpha= kotlin.math.min(
                1f,
                (bicho.energia / 100.0f) * 1.1f
            )//Le añado un poco para que no se note hasta bajar de 90
            rasgosBicho.alpha=caraBicho.alpha
            calaveraBicho.alpha=(1-caraBicho.alpha)*.75f//Dos tercios para que no aparezca muy rápido
            //Pongo en el texto el nombre y la energía del bicho.
            nombreBicho.text=bicho.nombre+": "+bicho.energia.toString()+"%"
            //Dependiendo del arma que tenga el bicho cambia la imagen.
            armaBicho.setImageResource(when(bicho.arma){
                0-> R.drawable.cuchillos_orco
                1 -> R.drawable.espadaescudo_orco
                2 -> R.drawable.hacha_orco
                else -> R.drawable.maza_orco
            })
        }
    }
}