package org.jceremony.io;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.jceremony.model.LifecycleState;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.io.Attribute;
import org.jgrapht.io.ComponentAttributeProvider;
import org.jgrapht.io.ComponentNameProvider;
import org.jgrapht.io.DOTExporter;
import org.jgrapht.io.DefaultAttribute;
import org.jgrapht.io.ExportException;

public class LifecycleModelDotExporter {
  private DOTExporter<LifecycleState, DefaultEdge> graphExporter;
  
  public void export(Graph<LifecycleState, DefaultEdge> graph, OutputStream outStream) throws ExportException {
    ComponentNameProvider<LifecycleState> vertexIdProv=(s->s.getName().toString().replaceAll("__", ""));
    ComponentNameProvider<DefaultEdge> edgeLabelProv=null;
    ComponentNameProvider<LifecycleState> vertexLabelProv=new ComponentNameProvider<LifecycleState>(){
      @Override
      public String getName(LifecycleState stateModel) {
        String checkCanArrive=stateModel.checkIfThisStateCanBeArrived.stream().collect(Collectors.joining("\\n"));
        String uponArrival=stateModel.uponArrivedToThisState.stream().collect(Collectors.joining("\\n"));
        String checkCanDepart=stateModel.checkIfThisStateCanBeDeparted.stream().collect(Collectors.joining("\\n"));
        String afterDeparture=stateModel.uponDepartedFromThisState.stream().collect(Collectors.joining("\\n"));
        return String.format("{{%s|%s}|%s|{%s|%s}}", checkCanArrive, uponArrival, stateModel.getName(), checkCanDepart, afterDeparture);
      }
      
    };
    ComponentAttributeProvider<LifecycleState> vertexAttributeProv=LifecycleModelDotExporter::stateAttributes;
    ComponentAttributeProvider<DefaultEdge> edgeAttributeProv=null;
    this.graphExporter=new DOTExporter<>(vertexIdProv, vertexLabelProv, edgeLabelProv, vertexAttributeProv, edgeAttributeProv);

    graphExporter.exportGraph(graph, outStream);
  }

  static Map<String, Attribute> stateAttributes(LifecycleState state){
    HashMap<String, Attribute> x=new HashMap<>();
    x.put("shape", DefaultAttribute.createAttribute("record"));
    x.put("style", DefaultAttribute.createAttribute("filled"));
    return x;
  }

};
