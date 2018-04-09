package org.jceremony.io;

import java.io.StringReader;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.Map;
import java.util.Properties;

import org.jceremony.model.LifecycleModel;
import org.jceremony.model.LifecycleModelBuilder;
import org.jceremony.model.StateName;
import org.jceremony.model.LifecycleModelBuilder.StateModelBuilder;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.io.Attribute;
import org.jgrapht.io.ComponentUpdater;
import org.jgrapht.io.DOTImporter;
import org.jgrapht.io.EdgeProvider;
import org.jgrapht.io.GmlImporter;
import org.jgrapht.io.VertexProvider;

public class LifecycleModelGraphModelingLanguageImporter {
  LifecycleModelBuilder modelBuilder=new LifecycleModelBuilder();
  DefaultDirectedGraph<StateModelBuilder, DefaultEdge> graph;

  //FIXME The GML format allows for list of strings, but the GMLImporter in jgrapht does not support list of strings. See https://github.com/jgrapht/jgrapht/issues/543
  public LifecycleModel readFromGMLFormat() throws Exception {
    VertexProvider<String> vertexProvider=this::buildState;
    EdgeProvider<String, Properties> edgeProvider=this::buildTransition;
//    ComponentUpdater<StateModelBuilder> vertexUpdater=this::updateState;
//    ComponentUpdater<Graph<String, DefaultEdge>> graphUpdater=this::updateGraph;
//    DOTImporter<StateModelBuilder, DefaultEdge> importer=new DOTImporter<StateModelBuilder, DefaultEdge>(vertexProvider, edgeProvider, vertexUpdater, graphUpdater);
    GmlImporter<String, Properties> importer=new GmlImporter<String, Properties>(vertexProvider, edgeProvider);
    DefaultDirectedGraph<String, Properties> graph=new DefaultDirectedGraph<>(Properties.class);

    String content=new String(Files.readAllBytes(FileSystems.getDefault().getPath("src/test/java/org/jceremony/io/ab.gml")));
    importer.importGraph(graph, new StringReader(content));

    return modelBuilder.build();
  }
  
  protected String buildState(String stateName, Map<String, Attribute> arg1) {
    //FIXME The issue with the GML importer is that it does not support array of strings as property values.
    return stateName;
  }
  
  protected Properties buildTransition(String arg0, String arg1, String arg2, Map<String, Attribute> arg3) {
    return new Properties();
  }
  
  protected void updateState(StateModelBuilder arg0, Map<String, Attribute> arg1) {
    // TODO Auto-generated method stub
  }
  
  protected void updateGraph(Graph<StateModelBuilder, DefaultEdge> arg0, Map<String, Attribute> arg1) {
    // TODO Auto-generated method stub
  }
}
