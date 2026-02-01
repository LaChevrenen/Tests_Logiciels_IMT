package fr.imt.minesales.bibliotheque;

public class AbonneNonReconnuException extends RuntimeException {
    public AbonneNonReconnuException() {
        super();
    }

    public AbonneNonReconnuException(String message) {
        super(message);
    }
}
