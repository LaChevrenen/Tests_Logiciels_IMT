package fr.imt.minesales.bibliotheque;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class TestBibliothèque {

	private Abonne mockAbonne;
	private Bibliotheque mockBibliotheque;
	private Catalogue mockCatalogue;
    private Emprunt mockEmprunt;
    private Livre mockLivre;
	
	@BeforeEach
	public void setUp() {
		mockAbonne = mock(Abonne.class);
		mockBibliotheque = mock(Bibliotheque.class);
		mockCatalogue = mock(Catalogue.class);
		mockEmprunt = mock(Emprunt.class);
        mockLivre = mock(Livre.class);
	}
	
	@Test
	void test() {
		fail("Not yet implemented");				
	}
}
