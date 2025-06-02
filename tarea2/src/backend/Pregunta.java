    package backend;

    public class Pregunta {

        private String enunciado;
        private String bloom;
        private int tiempoEstimado;

        public Pregunta(String enunciado, String bloom, int tiempoEstimado) {
            this.enunciado = enunciado;
            this.bloom = bloom;
            this.tiempoEstimado = tiempoEstimado;
        }

        public String getEnunciado() {
            return enunciado;
        }

        public String getBloom() {
            return bloom;
        }

        public int getTiempoEstimado() {
            return tiempoEstimado;
        }
    }