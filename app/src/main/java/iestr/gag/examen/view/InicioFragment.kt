package iestr.gag.examen.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import iestr.gag.examen.databinding.FragmentInicioBinding


class InicioFragment : Fragment() {
    lateinit var enlace: FragmentInicioBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        enlace= FragmentInicioBinding.inflate(inflater,container,false)
        return enlace.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Botón NUEVA PARTIDA.
        enlace.botonNueva.setOnClickListener {
            findNavController().navigate(InicioFragmentDirections.actionInicioFragmentToPartidaFragment())
        }

    }
}