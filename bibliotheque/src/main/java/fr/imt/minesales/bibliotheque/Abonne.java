package fr.imt.minesales.bibliotheque;

/**
 * Classe Abonné - TDD
 */
public class Abonne {
	
	private String nom;
	private String prenom;
	private String numeroAbonne;
	
	public Abonne() {
	}
	
	public Abonne(String numeroAbonne, String prenom, String nom) {
		this.numeroAbonne = numeroAbonne;
		this.prenom = prenom;
		this.nom = nom;
	}
	
	public String getNom() {
		return this.nom;
	}

	public String getPrenom() {
		return this.prenom;
	}

	public String getNumeroAbonne() {
		return this.numeroAbonne;
	}
}
