package iestr.gag.examen.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import iestr.gag.examen.model.Arma
import iestr.gag.examen.model.Bicho
import iestr.gag.examen.model.BichosProvider
import kotlin.math.min

//El uso de AndroidViewModel en lugar de ViewModel te permite agregarle un contexto (application)
//al constructor que será útil a la hora de usar toasts.
//Application se la pasa automáticamente el viewmodel provider.
class PartidaViewModel(application: Application): AndroidViewModel(application) {

    //La lista de los bichos en el recyclerview y las variables con los datos del jugador.
    private var _listaBichos=MutableLiveData<MutableList<Bicho>>(BichosProvider.getLista())
    val listaBichos:LiveData<MutableList<Bicho>>
        get() = _listaBichos
    private var _energia=MutableLiveData<Int>(100)
    val energia:LiveData<Int>
        get() = _energia
    private var _puntos=MutableLiveData<Int>(0)
    val puntos:LiveData<Int>
        get() = _puntos
    private var _arma= MutableLiveData<Arma>(Arma.values()[(0..3).random()])
    val arma:LiveData<Arma>
        get() = _arma

    //A esta no le pongo la Livedata porque no se accede desde fuera del viewmodel.
    private var _ataques=MutableLiveData<Int>(0)//Para saber cuándo genero un enemigo extra.


    //Esta variable la usamos para actualizar la lista de enemigos. Cada vez que se finaliza un
    //ataque, se suma 1 por tanto cambia. El observer recoge el cambio y actúa actualizando la lista.
    private var _chivato=MutableLiveData<Int>(0)
    val chivato:LiveData<Int>
        get() = _chivato


    ////////////////////Funciones del juego/////////////////////////////////

    //No se trata de un bucle, sino más bien de acción->reacción:
    //  - Acción: el jugador hace algo (como respuesta a 'tocar' en alguna parte de la pantalla).
    //  - Reacción: los enemigos hacen algo.
    //Las acciones que puede realizar el jugador son: ataque individual, ataque melé, reponer fuerzas.
    //  - Individual. Al tocar un enemigo (línea del listado). Al terminar: ajustar puntos y energía, ajustar enemigos.
    //  - Melé. Al tocar el botón mele. Al terminar: ajustar puntos y energía, ajustar enemigos.
    //  - Reponer. Al tocar el botón reponer. Al terminar: ajustar la barra de energía.
    //Los enemigos hacen: uno al azar te ataca.
    //  - Tras el ataque del enemigo hay que: actualizar barra de energía.

    //Para reponer la energía del protagonista.
    fun repara(){
        Toast.makeText(getApplication(),"Te tomas una poción", Toast.LENGTH_SHORT).show()
        _energia.value=Math.min(100,_energia.value!!+50)
        //Si repongo energía, paso turno y le toca al enemigo atacarme.
        bichoAtacaProta()
    }

    //Reparto mi ataque entre todos los enemigos.
    fun mele(){
        var fuerzaGolpe=(_arma.value!!.ataque*_energia.value!!/_listaBichos.value!!.size).toInt()
        val listaBajas= mutableListOf<Bicho>()
        Toast.makeText(getApplication(),"¡A MELEEEEEE!",Toast.LENGTH_SHORT).show()
        for(b in _listaBichos.value!!){
            if(Math.random()>b.esquiveReal()){
                if(Math.random()<b.paradaReal()){
                    fuerzaGolpe=(fuerzaGolpe/10f).toInt()
                }
                val efecto= min(b.energia,fuerzaGolpe)
                b.energia-=efecto
                if(b.energia<=0) {
                    listaBajas.add(b)
                }
                _puntos.value=_puntos.value!!+efecto
            }
        }
        for(b in listaBajas){
            _listaBichos.value!!.remove(b)
            _energia.value = min(energia.value!! + 10, 100)
            nuevoBicho()
        }
        finalizaAtaque()
    }

    //Mi ataque a un bicho individual.
    fun protaAtacaBicho(pos:Int){
        val bicho=listaBichos.value!![pos]
        //Si me esquiva, no le hago nada.
        if(Math.random()<bicho.esquiveReal()){
            Toast.makeText(getApplication(),"¡El bicho te ha esquivado!", Toast.LENGTH_SHORT).show()
            //Si no me esquiva, le haré daño: más o menos, dependiendo de si para el golpe o no.
        }else{
            //La fuerza de mi golpe efectiva se ve afectada por la energía que tengo
            var fuerzaGolpe=(_arma.value!!.ataque*energia.value!!).toInt()
            //Si me para, le hago un 10% del daño que le haría si le doy de lleno
            if(Math.random()<bicho.paradaReal()){
                Toast.makeText(getApplication(),"¡El bicho te ha bloqueado!", Toast.LENGTH_SHORT).show()
                fuerzaGolpe=(fuerzaGolpe/10f).toInt()
            }else {
                Toast.makeText(getApplication(), "¡Le has partido la cara!", Toast.LENGTH_SHORT).show()
            }
            //La energía que quito al enemigo no puede ser más de la que tiene.
            val efecto=Math.min(bicho.energia,fuerzaGolpe)
            bicho.energia-=efecto
            if(bicho.energia<=0) {//Si lo mato, gano 10 (sin pasar de 100) de energía y genero otro bicho.
                _listaBichos.value!!.removeAt(pos)
                _energia.value = Math.min(energia.value!! + 10, 100)
                nuevoBicho()
            }
            //Sumo tantos puntos como energía le quito
            _puntos.value=_puntos.value!!+efecto
        }
        finalizaAtaque()
    }

    //Gestiono tanto ataque individual como melé.
    private fun finalizaAtaque(){
        //Cuando has acumulado un cierto número de ataques, aparece un enemigo adicional.
        _ataques.value=_ataques.value!!+1
        if(_ataques.value!!>=3){
            _ataques.value=0
            nuevoBicho()
        }
        //Tras ataque individual o melé, le toca al enmigo atacarme.
        bichoAtacaProta()
        //Hago saltar al chivato para actualizar la lista.
        _chivato.value=_chivato.value!!+1
    }

    //Añado un nuevo bicho a la lista.
    private fun nuevoBicho(){
        _listaBichos.value!!.add(BichosProvider.getUno())
    }

    //El ataque de un enemigo al prota.
    private fun bichoAtacaProta(){
        val bicho=_listaBichos.value!!.random()
        Toast.makeText(getApplication(),"¡${bicho.nombre} te está atacando!",Toast.LENGTH_SHORT).show()
        //Si esquivo el golpe, no me hace nada.
        val esquiveReal= _arma.value!!.esquive*_energia.value!!/100
        if(Math.random()<esquiveReal){
            Toast.makeText(getApplication(),"¡Has esquivado el golpe!",Toast.LENGTH_SHORT).show()
            //Si no lo esquivo, me hará más o menos daño, dependiendo de si paro o no.
        }else {
            var impactoReal=bicho.fuerzaReal()
            val paradaReal= _arma.value!!.parada*_energia.value!!/100
            //Si lo paro, me hace un 10% del daño que me haría si me da de lleno.
            if(Math.random()<paradaReal){
                Toast.makeText(getApplication(),"¡Has bloqueado el golpe!",Toast.LENGTH_SHORT).show()
                impactoReal=(impactoReal/10f).toInt()
            }else{
                Toast.makeText(getApplication(),"¡Te llevas un mamporro!",Toast.LENGTH_SHORT).show()
            }
            _energia.value=Math.max(0,_energia.value!!-impactoReal)
        }
    }
}