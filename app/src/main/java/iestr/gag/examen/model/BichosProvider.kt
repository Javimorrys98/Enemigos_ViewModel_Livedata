package iestr.gag.examen.model

object BichosProvider {
    private const val CANTIDAD_INICIAL=4

    fun getUno()= Bicho(energia=(1..100).random())

    fun getLista()=MutableList(CANTIDAD_INICIAL){ getUno() }
    
}