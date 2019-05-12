package it.polito.tdp.metroparis.model;

import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.event.ConnectedComponentTraversalEvent;
import org.jgrapht.event.EdgeTraversalEvent;
import org.jgrapht.event.TraversalListener;
import org.jgrapht.event.VertexTraversalEvent;
import org.jgrapht.graph.DefaultEdge;

public class EdgeTraverseGraphListener implements TraversalListener<Fermata, DefaultEdge> {

	Graph<Fermata, DefaultEdge> grafo;
	Map<Fermata,Fermata> back;
		
	public EdgeTraverseGraphListener(Map<Fermata, Fermata> back,Graph<Fermata, DefaultEdge> grafo) {
		super();
		this.grafo = grafo;
		this.back = back;
	}

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
		
		//il metodo è chiamato ogni volta che un arco è visitato
		//per ogni vertice tengo traccia del vertice precedente (padre)
		//estraggo i vertici a cui è collegato l'arco e
		//nella mappa ho un vertice e il vertice da cui è stato scoperto
		//back codifica relazioni del tipo child-parent
		//per un nuovo vertice child scoperto devo avere che:
		//child è ancora sconosciuto (non ancora trovato)
		//parent è già stato visitato
		
		//estraggo gli estremi dell'arco
		Fermata sourceVertex = grafo.getEdgeSource(evento.getEdge()); //padre
		Fermata targetVertex = grafo.getEdgeTarget(evento.getEdge()); //figlio
		
		//se il grafo è orientato, allora source sarà il parent e target sarà il child
		//se il grafo non è orientato, potrebbe essere al contrario
		
		//se il figlio non è contenuto nella mappa e il padre è contenuto(stato visitato)
		if(!back.containsKey(targetVertex) && back.containsKey(sourceVertex)) {
			back.put(targetVertex, sourceVertex);
		} else if(!back.containsKey(sourceVertex) && back.containsKey(targetVertex)) {
			back.put(sourceVertex, targetVertex);
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
