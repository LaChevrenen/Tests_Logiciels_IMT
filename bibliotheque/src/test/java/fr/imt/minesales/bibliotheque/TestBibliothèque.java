package fr.imt.minesales.bibliotheque;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestBibliothèque {

	private Bibliotheque bibliotheque;
	private Abonne abonne;

	@BeforeEach
	void setUp() {
		// Implémentation réelle de la bibliothèque
		bibliotheque = new Bibliotheque();
		abonne = new Abonne("123", "Jeanne", "Dupont");
		
		// Initialiser avec des livres
		bibliotheque.ajouterLivre(new Livre("978-111", "Polar 1", "Polar", 1));
		bibliotheque.ajouterLivre(new Livre("978-222", "Polar 2", "Polar", 1));
		bibliotheque.ajouterLivre(new Livre("978-333", "Roman Test", "Roman", 2));
		bibliotheque.ajouterLivre(new Livre("978-444", "Aventure", "Aventure", 1));
	}

	// S1 : Identification échoue → abonné non reconnu
	@Test
	void testS1_identificationEchoue() {
		// Scenario : Marie Dupont avec mauvais identifiants
		Abonne abonneInconnu = new Abonne("999", "Marie", "Dupont");

		// Vérification
		assertThrows(AbonneNonReconnuException.class, () -> {
			bibliotheque.identification(abonneInconnu);
		});
	}

	// S2 : Identification OK + recherche catégorie Polar
	@Test
	void testS2_recherchePolar() {
		// Scenario : Jeanne Dupont se connecte et recherche des Polars
		bibliotheque.identification(abonne);

		// Vérification
		List<Livre> resultats = bibliotheque.rechercher("Polar");
		assertEquals(2, resultats.size());
		assertEquals("Polar", resultats.get(0).getGenre());
	}

	// S3 : Recherche catégorie inexistante → liste vide
	@Test
	void testS3_rechercheVide() {
		// Scenario : recherche Voyage qui n'existe pas
		bibliotheque.identification(abonne);

		// Vérification
		List<Livre> resultats = bibliotheque.rechercher("Voyage");
		assertTrue(resultats.isEmpty());
	}

	// S4 : Réservation d'un livre existant mais indisponible
	@Test
	void testS4_reservationIndisponible() {
		// Scenario : réservation d'un livre indisponible
		bibliotheque.identification(abonne);

		// Ajouter un livre indisponible
		bibliotheque.ajouterLivre(new Livre("978-333-indispo", "Livre Indisponible", "Roman", 0));
		
		// Réservation retourne false (ajouté à la file)
		boolean resultat = bibliotheque.reservation("978-333-indispo", abonne);
		assertFalse(resultat);

		// Vérifier que l'abonné est dans la file
		Queue<Abonne> fileAttente = bibliotheque.getFileAttente("978-333-indispo");
		assertEquals(1, fileAttente.size());
		assertEquals(abonne, fileAttente.peek());
	}

	// S5 : Réservation d'un livre disponible → proposition d'emprunt
	@Test
	void testS5_reservationLivreDisponible() {
		// Scenario : réservation d'un livre disponible
		bibliotheque.identification(abonne);

		// Réservation retourne true (proposition d'emprunt)
		boolean proposition = bibliotheque.reservation("978-444", abonne);
		assertTrue(proposition);
	}

	// S6 : Réservation d'un livre inexistant → exception
	@Test
	void testS6_reservationLivreInexistant() {
		// Scenario : réservation d'un livre inexistant
		bibliotheque.identification(abonne);

		// Vérification
		assertThrows(LivreInexistantException.class, () -> {
			bibliotheque.reservation("978-555", abonne);
		});
	}

	// S7 : Identification → liste des emprunts en retard
	@Test
	void testS7_empruntsEnRetard() {
		// Scenario : abonné se connecte et voit ses retards
		bibliotheque.identification(abonne);

		// Ajouter un livre et faire un emprunt
		bibliotheque.ajouterLivre(new Livre("978-voyage", "Voyage Test", "Voyage", 1));
		bibliotheque.setDateCourante(LocalDate.of(2026, 1, 1));
		bibliotheque.emprunt("978-voyage", abonne);
		
		// Avancer la date courante pour créer un retard
		bibliotheque.setDateCourante(LocalDate.now().plusDays(50));
		
		// Vérification
		List<Emprunt> retards = bibliotheque.getEmpruntsEnRetard(abonne);
		assertTrue(retards.size() >= 1);
		assertTrue(retards.get(0).getDateRetourAttendue().isBefore(LocalDate.now().plusDays(50)));
	}

	// S8 : Emprunt le 30 janvier → le 1 mars = retard
	@Test
	void testS8_retardCalculDate() {
		// Scenario : emprunt le 30 janvier, on est le 1 mars
		bibliotheque.identification(abonne);
		
		bibliotheque.ajouterLivre(new Livre("978-test-retard", "Test Retard", "Roman", 1));
		bibliotheque.setDateCourante(LocalDate.of(2026, 1, 30));
		
		// Faire l'emprunt
		bibliotheque.emprunt("978-test-retard", abonne);
		
		// Avancer à mars 1
		bibliotheque.setDateCourante(LocalDate.of(2026, 3, 1));
		
		// Vérification
		List<Emprunt> retards = bibliotheque.getEmpruntsEnRetard(abonne);
		assertEquals(1, retards.size());
		assertTrue(retards.get(0).getDateRetourAttendue()
			.isBefore(LocalDate.of(2026, 3, 1)));
	}

	// S9 : Emprunt → stock mis à jour
	@Test
	void testS9_empruntLivre() {
		// Scenario : abonné emprunte un livre
		bibliotheque.identification(abonne);

		// Stock initial
		Livre livreInitial = bibliotheque.getLivre("978-333");
		int stockInitial = livreInitial.getNbExemplaires();
		assertEquals(2, stockInitial);

		// Emprunt réussit
		boolean resultat = bibliotheque.emprunt("978-333", abonne);
		assertTrue(resultat);
		
		// Vérifier que le stock a baissé
		Livre libreFinal = bibliotheque.getLivre("978-333");
		int stockFinal = libreFinal.getNbExemplaires();
		assertEquals(1, stockFinal);
		assertEquals(stockInitial - 1, stockFinal);
	}

	// S10 : Retour dans les temps → stock mis à jour
	@Test
	void testS10_retourDansLesTemps() {
		// Scenario : retour dans les temps
		bibliotheque.identification(abonne);

		// Emprunter un livre
		bibliotheque.emprunt("978-444", abonne);
		int stockApreEmprunt = bibliotheque.getLivre("978-444").getNbExemplaires();
		
		// Retourner dans les temps
		bibliotheque.retour("978-444", abonne);
		
		// Stock augmente
		int stockApresRetour = bibliotheque.getLivre("978-444").getNbExemplaires();
		assertEquals(stockApreEmprunt + 1, stockApresRetour);
	}

	// S11 : Retour en retard → stock + notification
	@Test
	void testS11_retourEnRetard() {
		// Scenario : retour en retard
		bibliotheque.identification(abonne);

		bibliotheque.ajouterLivre(new Livre("978-retard", "Livre Retard", "Roman", 1));
		
		// Emprunter
		bibliotheque.setDateCourante(LocalDate.of(2026, 1, 1));
		bibliotheque.emprunt("978-retard", abonne);
		
		// Retourner en retard (date actuelle > date retour attendue)
		bibliotheque.setDateCourante(LocalDate.of(2026, 3, 1));
		bibliotheque.retour("978-retard", abonne);
		
		// Stock augmente
		int stock = bibliotheque.getLivre("978-retard").getNbExemplaires();
		assertEquals(1, stock);
	}

	// S12 : Réservation → abonné premier → emprunt OK
	@Test
	void testS12_empruntApresReservationPremier() {
		// Scenario : abonné premier dans la file, emprunt réussit
		bibliotheque.identification(abonne);

		// Réserver un livre indisponible
		bibliotheque.ajouterLivre(new Livre("978-reserve", "Livre Reserve", "Roman", 0));
		bibliotheque.reservation("978-reserve", abonne);
		
		// Rendre disponible en ajoutant une copie
		Livre livre = new Livre("978-reserve", "Livre Reserve", "Roman", 1);
		bibliotheque.ajouterLivre(livre);
		
		// Emprunter - devrait réussir car en premier
		boolean ok = bibliotheque.emprunt("978-reserve", abonne);
		assertTrue(ok);
	}

	// S12 bis : Réservation → abonné pas premier → emprunt refusé
	@Test
	void testS12bis_empruntApresReservationPasPremier() {
		// Scenario : abonné pas premier, emprunt refusé
		bibliotheque.identification(abonne);

		Abonne autre = new Abonne("456", "Jean", "Martin");
		
		// Réserver pour l'autre
		bibliotheque.ajouterLivre(new Livre("978-reserve2", "Livre Reserve 2", "Roman", 0));
		bibliotheque.reservation("978-reserve2", autre);
		
		// Essayer d'emprunter avec notre abonné (pas premier)
		boolean ok = bibliotheque.emprunt("978-reserve2", abonne);
		assertFalse(ok);
	}
}
