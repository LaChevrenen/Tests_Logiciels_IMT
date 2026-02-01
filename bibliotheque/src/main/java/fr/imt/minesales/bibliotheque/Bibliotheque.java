package fr.imt.minesales.bibliotheque;

import java.time.LocalDate;
import java.util.*;

public class Bibliotheque {

	// Pas d'implémentation réelle - juste des stubs pour TDD
	
	public void identification(Abonne abonne) throws AbonneNonReconnuException {
		// À implémenter
	}

	public List<Livre> rechercher(String motCle) {
		// À implémenter
		return Collections.emptyList();
	}

	public boolean reservation(String isbn, Abonne abonne) throws LivreInexistantException {
		// À implémenter
		return false;
	}

	public Queue<Abonne> getFileAttente(String isbn) {
		// À implémenter
		return new LinkedList<>();
	}

	public List<Emprunt> getEmpruntsEnRetard(Abonne abonne) {
		// À implémenter
		return Collections.emptyList();
	}

	public void setDateCourante(LocalDate date) {
		// À implémenter
	}

	public boolean emprunt(String isbn, Abonne abonne) {
		// À implémenter
		return false;
	}

	public void retour(String isbn, Abonne abonne) {
		// À implémenter
	}

	public void notifierRetard(Abonne abonne, String isbn) {
		// À implémenter
	}
}
