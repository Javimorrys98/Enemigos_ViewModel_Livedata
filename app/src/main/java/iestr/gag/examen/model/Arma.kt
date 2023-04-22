package iestr.gag.examen.model

enum class Arma(val ataque:Float,val parada:Float,val esquive:Float) {
    CUCHILLOS(0.5f,.25f,.75f),
    ESPADA_ESCUDO(1f,0.5f,0f),
    HACHA(0.75f,.25f,.5f),
    MAZA(.75f,.5f,.25f)
}