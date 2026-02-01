package fr.imt.minesales.bibliotheque;

public class Livre {
	private final String isbn;
	private final String titre;
	private final String genre;
	private final int nbExemplaires;

	public Livre(String isbn, String titre, String genre, int nbExemplaires) {
		this.isbn = isbn;
		this.titre = titre;
		this.genre = genre;
		this.nbExemplaires = nbExemplaires;
	}

	public String getIsbn() {
		return isbn;
	}

	public String getTitre() {
		return titre;
	}

	public String getGenre() {
		return genre;
	}

	public int getNbExemplaires() {
		return nbExemplaires;
	}
}
