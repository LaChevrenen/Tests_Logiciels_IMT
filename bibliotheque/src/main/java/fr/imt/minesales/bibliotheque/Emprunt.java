package fr.imt.minesales.bibliotheque;

import java.time.LocalDate;

public class Emprunt {
	private Abonne abonne;
	private Livre livre;
	private LocalDate dateEmprunt = LocalDate.now();
	private LocalDate dateRetourAttendue = LocalDate.now();

	public Emprunt() {
	}

	public Emprunt(Abonne abonne, Livre livre, LocalDate dateEmprunt, LocalDate dateRetourAttendue) {
		this.abonne = abonne;
		this.livre = livre;
		this.dateEmprunt = dateEmprunt;
		this.dateRetourAttendue = dateRetourAttendue;
	}

	public Emprunt(LocalDate dateRetourAttendue) {
		this.dateRetourAttendue = dateRetourAttendue;
	}

	public Abonne getAbonne() {
		return abonne;
	}

	public Livre getLivre() {
		return livre;
	}

	public LocalDate getDateEmprunt() {
		return dateEmprunt;
	}

	public LocalDate getDateRetourAttendue() {
		return dateRetourAttendue;
	}
}
