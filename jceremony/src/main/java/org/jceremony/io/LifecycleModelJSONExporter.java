package org.jceremony.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.jceremony.model.LifecycleModel;
import org.jceremony.model.LifecycleState;
import org.jceremony.model.StateName;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;

public class LifecycleModelJSONExporter {
  public void exportJSON(LifecycleModel modelToExport, OutputStream out) throws IOException {
    JsonFactory factory = new JsonFactory();
    // configure, if necessary:
    factory.enable(JsonParser.Feature.ALLOW_COMMENTS);
    JsonGenerator generator = factory.createGenerator(out);
    generator.useDefaultPrettyPrinter();
    generator.writeStartObject();
    generator.writeArrayFieldStart("nodes");
    for(Entry<StateName, LifecycleState> node: modelToExport.getStates().entrySet()) {
      generator.writeStartObject();
      LifecycleState state = node.getValue();
      generator.writeStringField("id", state.getName().toString());
      List<String> nameOfParentStates=modelToExport.getGraph().incomingEdgesOf(state).stream()
           .map(modelToExport.getGraph()::getEdgeSource)
           .map(parentState-> parentState.getName().toString())
           .collect(Collectors.toList());
      writeNamedArray(generator, "ancestorStates", nameOfParentStates);
      writeNamedArray(generator, "checkCanDepart", state.checkIfThisStateCanBeDeparted);
      writeNamedArray(generator, "uponDeparture", state.uponDepartedFromThisState);
      writeNamedArray(generator, "checkCanArrive", state.checkIfThisStateCanBeArrived);
      writeNamedArray(generator, "uponArrived", state.uponArrivedToThisState);
      generator.writeEndObject();
    }
    generator.writeEndArray();

    generator.writeEndObject();
    generator.close();
  }

  private void writeNamedArray(JsonGenerator generator, String fieldName, List<String> checkIfThisStateCanBeArrived) throws IOException {
    generator.writeArrayFieldStart(fieldName);
    for(int i=0; i<checkIfThisStateCanBeArrived.size(); i++) {
      generator.writeString(checkIfThisStateCanBeArrived.get(i));
    }
    generator.writeEndArray();
  }
}
