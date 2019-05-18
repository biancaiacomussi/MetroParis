package it.polito.tdp.metroparis.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.event.ConnectedComponentTraversalEvent;
import org.jgrapht.event.EdgeTraversalEvent;
import org.jgrapht.event.TraversalListener;
import org.jgrapht.event.VertexTraversalEvent;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.DepthFirstIterator;
import org.jgrapht.traverse.GraphIterator;

import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

import it.polito.tdp.metroparis.db.MetroDAO;

public class Model {
	
	private class EdgeTraverseGraphListener implements TraversalListener<Fermata, DefaultEdge>{

		@Override
		public void connectedComponentFinished(ConnectedComponentTraversalEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void connectedComponentStarted(ConnectedComponentTraversalEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void edgeTraversed(EdgeTraversalEvent<DefaultEdge> evento) {
			Fermata sourceVertex = grafo.getEdgeSource(evento.getEdge()); //padre
			Fermata targetVertex = grafo.getEdgeTarget(evento.getEdge()); //figlio
			
			//se il grafo è orientato, allora source sarà il parent e target sarà il child
			//se il grafo non è orientato, potrebbe essere al contrario
			
			//se il figlio non è contenuto nella mappa e il padre è contenuto(stato visitato)
			if(!backVisit.containsKey(targetVertex) && backVisit.containsKey(sourceVertex)) {
				backVisit.put(targetVertex, sourceVertex);
			} else if(!backVisit.containsKey(sourceVertex) && backVisit.containsKey(targetVertex)) {
				backVisit.put(sourceVertex, targetVertex);
			}
			
		}

		@Override
		public void vertexFinished(VertexTraversalEvent<Fermata> arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void vertexTraversed(VertexTraversalEvent<Fermata> arg0) {
			// TODO Auto-generated method stub
			
		}
	
	}
	
	//la classe Model internamente gestisce il grafo -> private
	//private Graph<Fermata, DefaultEdge> grafo;
	private Graph<Fermata, DefaultWeightedEdge> grafo;
	private List<Fermata> fermate;
	private Map<Integer, Fermata> fermateIdMap;
	private Map<Fermata,Fermata> backVisit;
	
	public void creaGrafo() {
		
		//Crea l'oggetto grafo
		//this.grafo = new SimpleDirectedGraph<>(DefaultEdge.class);
		this.grafo = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		
		//Aggiungi i vertici
		MetroDAO dao = new MetroDAO();
		this.fermate = dao.getAllFermate();
		Graphs.addAllVertices(this.grafo, this.fermate);
		
		//crea idMap
		this.fermateIdMap = new HashMap<>();
		for(Fermata f : this.fermate)
			fermateIdMap.put(f.getIdFermata(), f);
		
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
			List<Fermata> arrivi = dao.stazioniArrivo(partenza, fermateIdMap);
			
			for(Fermata arrivo : arrivi) { //aggiungo gli achi entranti nelle stazioni di arrivo
				this.grafo.addEdge(partenza, arrivo);
			}
		}
		
		//aggiungi i pesi agli archi
		
		List<ConnessioniVelocita> archipesati = dao.getConnessioneVelocita();
		for(ConnessioniVelocita cp : archipesati) {
			
			Fermata partenza = fermateIdMap.get(cp.getStazP());
			Fermata arrivo = fermateIdMap.get(cp.getStazA());
			double distanza = LatLngTool.distance(partenza.getCoords(), arrivo.getCoords(), LengthUnit.KILOMETER);
			double peso = distanza / cp.getVelocita() * 3600; //tempo in secondi
			
			grafo.setEdgeWeight(partenza, arrivo, peso);
			
			// oppure aggiungo archi e vertici insieme Graphs.addEdgeWithVertices(grafo, partenza, arrivo, peso);
		}
		
		
	}

	public List<Fermata> fermateRaggiungibili(Fermata source){
		
		List<Fermata> result = new ArrayList<>();
		backVisit = new HashMap<>();
		//GraphIterator<Fermata, DefaultEdge> it = new BreadthFirstIterator<>(this.grafo, source);
		GraphIterator<Fermata, DefaultEdge> it = new DepthFirstIterator<>(this.grafo, source);
		//l'iteratore parte dal primo vertice sorgente
		
		it.addTraversalListener(new EdgeTraverseGraphListener());
		
		/*it.addTraversalListener(new TraversalListener<Fermata, DefaultEdge>(){

			@Override
			public void connectedComponentFinished(ConnectedComponentTraversalEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void connectedComponentStarted(ConnectedComponentTraversalEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void edgeTraversed(EdgeTraversalEvent<DefaultEdge> arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void vertexFinished(VertexTraversalEvent<Fermata> arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void vertexTraversed(VertexTraversalEvent<Fermata> arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});*/
		backVisit.put(source, null); //il nodo radice non ha padre -> null
		
		while(it.hasNext()) {
			result.add(it.next());
		}
		
		//System.out.println(backVisit);
			
		return result;
	}
	
	public List<Fermata> percorsoFinoA(Fermata target){
		if(!backVisit.containsKey(target)) {
			//il target non è raggiungibile dalla source
			return null;
			}
		List<Fermata> percorso = new LinkedList<>();
	
		Fermata f = target;
		
		while(f!=null) {
		percorso.add(0,f);
		f = backVisit.get(f);
		}
		
		return percorso;
	}
	
	
	public List<Fermata> getFermate() {
		return fermate;
	}

	public void setFermate(List<Fermata> fermate) {
		this.fermate = fermate;
	}

	public Graph<Fermata, DefaultEdge> getGrafo() {
		return grafo;
	}

	public void setGrafo(Graph<Fermata, DefaultEdge> grafo) {
		this.grafo = grafo;
	}
	
	//se ho bisogno di calcolare tutti i cammini minimi usare l'algoritmo di floyd
	
	public List<Fermata>trovaCamminoMinimo(Fermata partenza, Fermata arrivo){
		DijkstraShortestPath<Fermata, DefaultWeightedEdge> dijkstra = new DijkstraShortestPath<>(this.grafo);
		GraphPath<Fermata, DefaultWeightedEdge> path = dijkstra.getPath(partenza, arrivo);
		return path.getVertexList();
		
	
	}

}
