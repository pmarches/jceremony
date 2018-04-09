package org.jceremony.io;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;

import org.jceremony.model.LifecycleModel;
import org.jceremony.model.WellKnownLifecycles;
import org.junit.Test;

public class LifecycleModelJSONExporterTest extends LifecycleModelJSONExporter {

  @Test
  public void testExportJSON() throws Exception {
    ByteArrayOutputStream baos=new ByteArrayOutputStream();
    LifecycleModelJSONExporter exporter=new LifecycleModelJSONExporter();
    LifecycleModel modelToExport=WellKnownLifecycles.abLifecycle().build();
    exporter.exportJSON(modelToExport, baos);
    String generated=new String(baos.toByteArray());
    String expected="{\n" + 
        "  \"nodes\" : [ {\n" + 
        "    \"id\" : \"a\",\n" + 
        "    \"ancestorStates\" : [ ],\n" + 
        "    \"checkCanDepart\" : [ \"checkCanDepartureA\" ],\n" + 
        "    \"uponDeparture\" : [ \"performAfterDepartureA\" ],\n" + 
        "    \"checkCanArrive\" : [ \"checkArrivalA1\" ],\n" + 
        "    \"uponArrived\" : [ \"performAfterArrivalA1\" ]\n" + 
        "  }, {\n" + 
        "    \"id\" : \"b\",\n" + 
        "    \"ancestorStates\" : [ \"a\" ],\n" + 
        "    \"checkCanDepart\" : [ ],\n" + 
        "    \"uponDeparture\" : [ ],\n" + 
        "    \"checkCanArrive\" : [ \"checkCanArrivalB1\" ],\n" + 
        "    \"uponArrived\" : [ \"performAfterArrivalB1\" ]\n" + 
        "  } ]\n" + 
        "}";
    assertEquals(expected, generated);
  }

}
