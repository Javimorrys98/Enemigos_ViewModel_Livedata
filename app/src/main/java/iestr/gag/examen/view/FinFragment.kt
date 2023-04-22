package iestr.gag.examen.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import iestr.gag.examen.databinding.FragmentFinBinding

class FinFragment : Fragment() {
    lateinit var enlace:FragmentFinBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        enlace= FragmentFinBinding.inflate(inflater,container,false)
        return enlace.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Recupero información procedente del fragmento anterior, e informo la pantalla.
        arguments?.let {
            val datos = FinFragmentArgs.fromBundle(it)
            enlace.puntos.text = datos.puntos.toString()
        }

    }
}