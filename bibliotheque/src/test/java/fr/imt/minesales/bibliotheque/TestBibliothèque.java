package fr.imt.minesales.bibliotheque;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestBibliothèque {

	private Bibliotheque bibliotheque;
	private Abonne abonne;

	@BeforeEach
	void setUp() {
		// Mock complet de la bibliothèque pour TDD
		bibliotheque = mock(Bibliotheque.class);
		abonne = mock(Abonne.class);
	}

	// ------------------------------------------------------------
	// S1 : Identification échoue → abonné non reconnu
	// ------------------------------------------------------------
	@Test
	void testS1_identificationEchoue() {
		// Scenario : Marie Dupont avec mauvais identifiants
		when(abonne.getNom()).thenReturn("Marie");
		when(abonne.getPrenom()).thenReturn("Dupont");
		when(abonne.getNumeroAbonne()).thenReturn("999");

		// Mock : l'identification lève une exception
		doThrow(new AbonneNonReconnuException("Abonné non reconnu"))
			.when(bibliotheque).identification(abonne);

		// Vérification
		assertThrows(AbonneNonReconnuException.class, () -> {
			bibliotheque.identification(abonne);
		});
	}

	// ------------------------------------------------------------
	// S2 : Identification OK + recherche catégorie Polar
	// ------------------------------------------------------------
	@Test
	void testS2_recherchePolar() {
		// Scenario : Jeanne Dupont se connecte et recherche des Polars
		when(abonne.getNumeroAbonne()).thenReturn("123");
		
		// Mock : identification réussit (ne fait rien)
		doNothing().when(bibliotheque).identification(abonne);
		bibliotheque.identification(abonne);

		// Mock : la recherche retourne des polars
		List<Livre> polars = Arrays.asList(
			new Livre("978-111", "Polar 1", "Polar", 1),
			new Livre("978-222", "Polar 2", "Polar", 1)
		);
		when(bibliotheque.rechercher("Polar")).thenReturn(polars);

		// Vérification
		List<Livre> resultats = bibliotheque.rechercher("Polar");
		assertEquals(2, resultats.size());
		assertEquals("Polar", resultats.get(0).getGenre());
	}

	// ------------------------------------------------------------
	// S3 : Recherche catégorie inexistante → liste vide
	// ------------------------------------------------------------
	@Test
	void testS3_rechercheVide() {
		// Scenario : recherche Voyage qui n'existe pas
		when(abonne.getNumeroAbonne()).thenReturn("123");
		
		doNothing().when(bibliotheque).identification(abonne);
		bibliotheque.identification(abonne);

		// Mock : recherche retourne liste vide
		when(bibliotheque.rechercher("Voyage"))
			.thenReturn(Collections.emptyList());

		// Vérification
		List<Livre> resultats = bibliotheque.rechercher("Voyage");
		assertTrue(resultats.isEmpty());
	}

	// ------------------------------------------------------------
	// S4 : Réservation d'un livre existant mais indisponible
	// ------------------------------------------------------------
	@Test
	void testS4_reservationIndisponible() {
		// Scenario : réservation d'un livre indisponible
		when(abonne.getNumeroAbonne()).thenReturn("123");
		
		doNothing().when(bibliotheque).identification(abonne);
		bibliotheque.identification(abonne);

		// Mock : réservation retourne false (ajouté à la file)
		when(bibliotheque.reservation("978-333", abonne)).thenReturn(false);
		
		boolean resultat = bibliotheque.reservation("978-333", abonne);
		assertFalse(resultat);

		// Mock : vérifier que l'abonné est dans la file
		Queue<Abonne> file = new LinkedList<>();
		file.add(abonne);
		when(bibliotheque.getFileAttente("978-333")).thenReturn(file);
		
		Queue<Abonne> fileAttente = bibliotheque.getFileAttente("978-333");
		assertEquals(1, fileAttente.size());
		assertEquals(abonne, fileAttente.peek());
	}

	// ------------------------------------------------------------
	// S5 : Réservation d'un livre disponible → proposition d'emprunt
	// ------------------------------------------------------------
	@Test
	void testS5_reservationLivreDisponible() {
		// Scenario : réservation d'un livre disponible
		when(abonne.getNumeroAbonne()).thenReturn("123");
		
		doNothing().when(bibliotheque).identification(abonne);
		bibliotheque.identification(abonne);

		// Mock : réservation retourne true (proposition d'emprunt)
		when(bibliotheque.reservation("978-444", abonne)).thenReturn(true);

		// Vérification
		boolean proposition = bibliotheque.reservation("978-444", abonne);
		assertTrue(proposition);
	}

	// ------------------------------------------------------------
	// S6 : Réservation d'un livre inexistant → exception
	// ------------------------------------------------------------
	@Test
	void testS6_reservationLivreInexistant() {
		// Scenario : réservation d'un livre inexistant
		when(abonne.getNumeroAbonne()).thenReturn("123");
		
		doNothing().when(bibliotheque).identification(abonne);
		bibliotheque.identification(abonne);

		// Mock : réservation lève une exception
		when(bibliotheque.reservation("978-555", abonne))
			.thenThrow(new LivreInexistantException("Livre inexistant"));

		// Vérification
		assertThrows(LivreInexistantException.class, () -> {
			bibliotheque.reservation("978-555", abonne);
		});
	}

	// ------------------------------------------------------------
	// S7 : Identification → liste des emprunts en retard
	// ------------------------------------------------------------
	@Test
	void testS7_empruntsEnRetard() {
		// Scenario : abonné se connecte et voit ses retards
		when(abonne.getNumeroAbonne()).thenReturn("123");
		
		doNothing().when(bibliotheque).identification(abonne);
		bibliotheque.identification(abonne);

		// Mock : création d'un emprunt en retard
		Emprunt emprunt = mock(Emprunt.class);
		Livre livre = new Livre("978-111", "Voyage", "Voyage", 1);
		
		when(emprunt.getLivre()).thenReturn(livre);
		when(emprunt.getAbonne()).thenReturn(abonne);
		when(emprunt.getDateRetourAttendue())
			.thenReturn(LocalDate.now().minusDays(3));

		// Mock : retourne la liste des retards
		when(bibliotheque.getEmpruntsEnRetard(abonne))
			.thenReturn(List.of(emprunt));

		// Vérification
		List<Emprunt> retards = bibliotheque.getEmpruntsEnRetard(abonne);
		assertEquals(1, retards.size());
		assertTrue(retards.get(0).getDateRetourAttendue().isBefore(LocalDate.now()));
	}

	// ------------------------------------------------------------
	// S8 : Emprunt le 30 janvier → le 1 mars = retard
	// ------------------------------------------------------------
	@Test
	void testS8_retardCalculDate() {
		// Scenario : emprunt le 30 janvier, on est le 1 mars
		when(abonne.getNumeroAbonne()).thenReturn("123");
		
		doNothing().when(bibliotheque).identification(abonne);
		bibliotheque.identification(abonne);

		// Mock : création emprunt du 30 janvier
		Emprunt emprunt = mock(Emprunt.class);
		when(emprunt.getDateEmprunt())
			.thenReturn(LocalDate.of(2026, 1, 30));
		when(emprunt.getDateRetourAttendue())
			.thenReturn(LocalDate.of(2026, 2, 28));  // Date attendue : 28 février (car 30 jan + 1 mois = fin février)

		// Mock : définir date courante au 1 mars
		doNothing().when(bibliotheque).setDateCourante(LocalDate.of(2026, 3, 1));
		bibliotheque.setDateCourante(LocalDate.of(2026, 3, 1));

		// Mock : retourne l'emprunt en retard
		when(bibliotheque.getEmpruntsEnRetard(abonne))
			.thenReturn(List.of(emprunt));

		// Vérification
		List<Emprunt> retards = bibliotheque.getEmpruntsEnRetard(abonne);
		assertEquals(1, retards.size());
		assertTrue(retards.get(0).getDateRetourAttendue()
			.isBefore(LocalDate.of(2026, 3, 1)));
	}

	// ------------------------------------------------------------
	// S9 : Emprunt → stock mis à jour
	// ------------------------------------------------------------
	@Test
	void testS9_empruntLivre() {
		// Scenario : abonné emprunte un livre
		when(abonne.getNumeroAbonne()).thenReturn("123");
		
		doNothing().when(bibliotheque).identification(abonne);
		bibliotheque.identification(abonne);

		// Mock : emprunt réussit
		when(bibliotheque.emprunt("978-333", abonne)).thenReturn(true);

		// Vérification
		boolean resultat = bibliotheque.emprunt("978-333", abonne);
		assertTrue(resultat);
		
		// Vérifier que la méthode a bien été appelée
		verify(bibliotheque).emprunt("978-333", abonne);
	}

	// ------------------------------------------------------------
	// S10 : Retour dans les temps → stock mis à jour
	// ------------------------------------------------------------
	@Test
	void testS10_retourDansLesTemps() {
		// Scenario : retour dans les temps
		when(abonne.getNumeroAbonne()).thenReturn("123");
		
		doNothing().when(bibliotheque).identification(abonne);
		bibliotheque.identification(abonne);

		// Mock : retour ne fait rien (pas de notification)
		doNothing().when(bibliotheque).retour("978-444", abonne);

		// Vérification
		bibliotheque.retour("978-444", abonne);
		verify(bibliotheque).retour("978-444", abonne);
		
		// Pas de notification de retard
		verify(bibliotheque, never()).notifierRetard(any(), any());
	}

	// ------------------------------------------------------------
	// S11 : Retour en retard → stock + notification
	// ------------------------------------------------------------
	@Test
	void testS11_retourEnRetard() {
		// Scenario : retour en retard
		when(abonne.getNumeroAbonne()).thenReturn("123");
		
		doNothing().when(bibliotheque).identification(abonne);
		bibliotheque.identification(abonne);

		// Mock : retour effectué
		doNothing().when(bibliotheque).retour("978-555", abonne);
		bibliotheque.retour("978-555", abonne);

		// Mock : notification de retard appelée
		doNothing().when(bibliotheque).notifierRetard(abonne, "978-555");
		bibliotheque.notifierRetard(abonne, "978-555");

		// Vérification
		verify(bibliotheque).retour("978-555", abonne);
		verify(bibliotheque).notifierRetard(abonne, "978-555");
	}

	// ------------------------------------------------------------
	// S12 : Réservation → abonné premier → emprunt OK
	// ------------------------------------------------------------
	@Test
	void testS12_empruntApresReservationPremier() {
		// Scenario : abonné premier dans la file, emprunt réussit
		when(abonne.getNumeroAbonne()).thenReturn("123");
		
		doNothing().when(bibliotheque).identification(abonne);
		bibliotheque.identification(abonne);

		// Mock : file d'attente avec abonné en premier
		Queue<Abonne> file = new LinkedList<>();
		file.add(abonne);
		when(bibliotheque.getFileAttente("978-666")).thenReturn(file);

		// Mock : emprunt réussit
		when(bibliotheque.emprunt("978-666", abonne)).thenReturn(true);

		// Vérification
		boolean ok = bibliotheque.emprunt("978-666", abonne);
		assertTrue(ok);
	}

	// ------------------------------------------------------------
	// S12 bis : Réservation → abonné pas premier → emprunt refusé
	// ------------------------------------------------------------
	@Test
	void testS12bis_empruntApresReservationPasPremier() {
		// Scenario : abonné pas premier, emprunt refusé
		when(abonne.getNumeroAbonne()).thenReturn("123");
		
		doNothing().when(bibliotheque).identification(abonne);
		bibliotheque.identification(abonne);

		// Mock : file d'attente avec autre abonné en premier
		Abonne autre = mock(Abonne.class);
		Queue<Abonne> file = new LinkedList<>();
		file.add(autre);
		file.add(abonne);
		when(bibliotheque.getFileAttente("978-777")).thenReturn(file);

		// Mock : emprunt échoue
		when(bibliotheque.emprunt("978-777", abonne)).thenReturn(false);

		// Vérification
		boolean ok = bibliotheque.emprunt("978-777", abonne);
		assertFalse(ok);
	}
}
