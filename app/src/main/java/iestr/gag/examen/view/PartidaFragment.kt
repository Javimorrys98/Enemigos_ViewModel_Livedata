package iestr.gag.examen.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import iestr.gag.examen.model.Arma
import iestr.gag.examen.model.Bicho
import iestr.gag.examen.model.BichosProvider
import iestr.gag.examen.R
import iestr.gag.examen.databinding.FragmentPartidaBinding
import iestr.gag.examen.viewmodel.PartidaViewModel
import kotlin.math.min

class PartidaFragment : Fragment() {

    lateinit var vm:PartidaViewModel
    lateinit var enlace:FragmentPartidaBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        enlace= FragmentPartidaBinding.inflate(inflater,container,false)
        //Instancio/recupero el view model asociado a este fragment
        //No se usa un constructor 'clásico' porque no quiero un nuevo viewmodel cada vez que se cree el fragment
        //El 'provider' se encarga de crear un o o reconectar con el existente (al estilo singleton)
        // - La primera vez que pase por aquí, instanciará un JuegoViewModel y lo asociará a 'this'(owner)
        // - Las siguientes, simplemente, recuperará ese (sin instanciar más)
        vm= ViewModelProvider(this).get(PartidaViewModel::class.java)

        //Observadores del livedata para actualizar los elementos en pantalla de los puntos y la energía.
        vm.energia.observe(viewLifecycleOwner){
            enlace.fuerzaProta.progress=it
            if(it<=0){
                findNavController().navigate(PartidaFragmentDirections.actionPartidaFragmentToFinFragment(
                    vm.puntos.value!!))
            }
        }
        vm.puntos.observe(viewLifecycleOwner){
            enlace.puntosProta.text=it.toString()
        }

        return enlace.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Necesario para que funcione el RecyclerView.
        enlace.lista.layoutManager=LinearLayoutManager(activity)
        enlace.lista.adapter=BichosAdapter(this@PartidaFragment,vm)

        //Ajusto los elementos de la interfaz de usuario que no cambian durante la partida.
        enlace.armaProta.setImageResource(when(vm.arma.value!!.ordinal){
            0 -> R.drawable.cuchillos
            1 -> R.drawable.espadaescudo
            2 -> R.drawable.hacha
            else -> R.drawable.maza
        })
        with(enlace.botonReponer){
            setImageResource(R.drawable.icono_reponer)
            setOnClickListener{
                vm.repara()
            }
        }
        with(enlace.botonMele){
            setImageResource(R.drawable.icono_mele)
            setOnClickListener{
                vm.mele()
            }
        }
    }
}