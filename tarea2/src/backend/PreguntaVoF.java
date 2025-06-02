package backend;

public class PreguntaVoF extends Pregunta{
    private String respuestaVF;

    private String respuestaCorrectaVF;

    public PreguntaVoF(String enunciado, String bloom, int tiempoEstimado, String respuestaCorrectaVF) {
        super(enunciado, bloom, tiempoEstimado);
        this.respuestaCorrectaVF = respuestaCorrectaVF;
    }

    //usar para almacenar respuestas ingresadas por usuario
    public String getRespuestaVF() {
        return respuestaVF;
    }

    public void setRespuestaVF(String respuestaVF) {
        this.respuestaVF = respuestaVF;
    }

    public String getRespuestaCorrecta() {
        return respuestaCorrectaVF;
    }


}

