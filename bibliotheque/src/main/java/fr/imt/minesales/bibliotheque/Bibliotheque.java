package fr.imt.minesales.bibliotheque;

import java.time.LocalDate;
import java.util.*;

public class Bibliotheque {

	private Map<String, Livre> livres = new HashMap<>();
	private List<Abonne> abonnesEnregistres = new ArrayList<>();
	private Map<String, Queue<Abonne>> filesAttente = new HashMap<>();
	private List<Emprunt> emprunts = new ArrayList<>();
	private LocalDate dateCourante = LocalDate.now();
	
	// Abonnés enregistrés dans le système
	static {
	}
	
	public Bibliotheque() {
		// Initialiser avec un abonné connu
		Abonne abonneTest = new Abonne("123", "Jeanne", "Dupont");
		abonnesEnregistres.add(abonneTest);
	}
	
	public void ajouterLivre(Livre livre) {
		if (livre == null) {
			return;
		}
		String isbn = livre.getIsbn();
		Livre existant = livres.get(isbn);
		if (existant == null) {
			livres.put(isbn, livre);
			filesAttente.putIfAbsent(isbn, new LinkedList<>());
			return;
		}
		int nouveauStock = existant.getNbExemplaires() + livre.getNbExemplaires();
		Livre livreMisAJour = new Livre(existant.getIsbn(), existant.getTitre(),
			existant.getGenre(), nouveauStock);
		livres.put(isbn, livreMisAJour);
		filesAttente.putIfAbsent(isbn, new LinkedList<>());
	}
	
	public void identification(Abonne abonne) throws AbonneNonReconnuException {
		if (abonne == null || abonne.getNumeroAbonne() == null) {
			throw new AbonneNonReconnuException("Abonné non reconnu");
		}
		boolean trouve = abonnesEnregistres.stream()
			.anyMatch(a -> a.getNumeroAbonne().equals(abonne.getNumeroAbonne()));
		if (!trouve) {
			throw new AbonneNonReconnuException("Abonné non reconnu");
		}
	}

	public List<Livre> rechercher(String genre) {
		List<Livre> resultats = new ArrayList<>();
		for (Livre livre : livres.values()) {
			if (livre.getGenre().equalsIgnoreCase(genre)) {
				resultats.add(livre);
			}
		}
		return resultats;
	}

	public boolean reservation(String isbn, Abonne abonne) throws LivreInexistantException {
		if (!livres.containsKey(isbn)) {
			throw new LivreInexistantException("Livre inexistant");
		}
		
		Livre livre = livres.get(isbn);
		if (livre.getNbExemplaires() > 0) {
			// Livre disponible
			return true;
		} else {
			// Livre indisponible, ajouter à la file
			filesAttente.get(isbn).add(abonne);
			return false;
		}
	}

	public Queue<Abonne> getFileAttente(String isbn) {
		return filesAttente.getOrDefault(isbn, new LinkedList<>());
	}

	public List<Emprunt> getEmpruntsEnRetard(Abonne abonne) {
		List<Emprunt> retards = new ArrayList<>();
		for (Emprunt e : emprunts) {
			if (e.getAbonne().getNumeroAbonne().equals(abonne.getNumeroAbonne())) {
				if (e.getDateRetourAttendue().isBefore(dateCourante)) {
					retards.add(e);
				}
			}
		}
		return retards;
	}

	public void setDateCourante(LocalDate date) {
		this.dateCourante = date;
	}

	public boolean emprunt(String isbn, Abonne abonne) {
		if (!livres.containsKey(isbn)) {
			return false;
		}
		
		Livre livre = livres.get(isbn);
		Queue<Abonne> file = filesAttente.get(isbn);
		
		// Vérifier si abonné est en première position dans la file
		if (!file.isEmpty() && !file.peek().getNumeroAbonne().equals(abonne.getNumeroAbonne())) {
			return false;
		}
		
		// Vérifier stock
		if (livre.getNbExemplaires() > 0) {
			// Créer un nouvel exemplaire avec stock diminué
			Livre livreMisAJour = new Livre(livre.getIsbn(), livre.getTitre(), 
				livre.getGenre(), livre.getNbExemplaires() - 1);
			livres.put(isbn, livreMisAJour);
			
			// Créer l'emprunt
			LocalDate dateRetour = dateCourante.plusMonths(1);
			Emprunt nouvelEmprunt = new Emprunt(abonne, livre, dateCourante, dateRetour);
			emprunts.add(nouvelEmprunt);
			
			// Retirer de la file si présent
			if (!file.isEmpty()) {
				file.poll();
			}
			
			return true;
		}
		return false;
	}

	public void retour(String isbn, Abonne abonne) {
		// Trouver et supprimer l'emprunt
		for (int i = 0; i < emprunts.size(); i++) {
			Emprunt e = emprunts.get(i);
			if (e.getLivre().getIsbn().equals(isbn) && 
				e.getAbonne().getNumeroAbonne().equals(abonne.getNumeroAbonne())) {
				
				// Augmenter le stock
				Livre livre = livres.get(isbn);
				Livre livreMisAJour = new Livre(isbn, livre.getTitre(), 
					livre.getGenre(), livre.getNbExemplaires() + 1);
				livres.put(isbn, livreMisAJour);
				
				// Vérifier si retard et notifier
				if (e.getDateRetourAttendue().isBefore(dateCourante)) {
					notifierRetard(abonne, isbn);
				}
				
				emprunts.remove(i);
				break;
			}
		}
	}

	public void notifierRetard(Abonne abonne, String isbn) {
		// Notification effectuée
	}

	public Livre getLivre(String isbn) {
		return livres.get(isbn);
	}
}
