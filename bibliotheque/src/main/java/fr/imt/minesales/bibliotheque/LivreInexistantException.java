package fr.imt.minesales.bibliotheque;

public class LivreInexistantException extends RuntimeException {
    public LivreInexistantException() {
        super();
    }

    public LivreInexistantException(String message) {
        super(message);
    }
}
