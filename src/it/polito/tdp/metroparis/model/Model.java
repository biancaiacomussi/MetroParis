package it.polito.tdp.metroparis.model;

import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;

import it.polito.tdp.metroparis.db.MetroDAO;

public class Model {
	
	//la classe Model internamente gestisce il grafo -> private
	private Graph<Fermata, DefaultEdge> grafo;
	private List<Fermata> fermate;
	
	public void creaGrafo() {
		
		//Crea l'oggetto grafo
		this.grafo = new SimpleDirectedGraph<>(DefaultEdge.class);
		
		//Aggiungi i vertici
		MetroDAO dao = new MetroDAO();
		this.fermate = dao.getAllFermate();
		Graphs.addAllVertices(this.grafo, this.fermate);
		
		//Aggiungi gli archi (3 modi diversi)
		
		//opzione 1
		//prendo tutte le coppie possibili di fermate e vedo se tra loro esiste una connessione
		/*for(Fermata partenza : this.grafo.vertexSet()) {
			for(Fermata arrivo : this.grafo.vertexSet()) {
				if(dao.esisteConnessione(partenza,arrivo)) {
					this.grafo.addEdge(partenza, arrivo);
				}
			}
		}*/
		
		//opzione 2
		for(Fermata partenza : this.grafo.vertexSet()) {
			List<Fermata> arrivi = dao.stazioniArrivo(partenza);
			
			for(Fermata arrivo : arrivi) { //aggiungo gli achi entranti nelle stazioni di arrivo
				this.grafo.addEdge(partenza, arrivo);
			}
		}
		
		//opzione 3
		
	}

	public Graph<Fermata, DefaultEdge> getGrafo() {
		return grafo;
	}

	public void setGrafo(Graph<Fermata, DefaultEdge> grafo) {
		this.grafo = grafo;
	}

}
