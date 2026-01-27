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

	private Bibliotheque mockBibliotheque;
    private Abonne mockAbonne;
    private Livre mockLivre;
    private Catalogue mockCatalogue;
	
	@BeforeEach
	public void setUp() {
		bibliotheque = mock(Bibliotheque.class); 
        mockAbonne = mock(Abonne.class);
        mockLivre = mock(Livre.class);
	}
	
	@Test
	void test() {
		fail("Not yet implemented");				
	}
}
