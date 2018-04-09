package org.jceremony.io;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;

import org.jceremony.io.LifecycleModelDotExporter;
import org.jceremony.model.LifecycleModel;
import org.jceremony.model.WellKnownLifecycles;
import org.junit.Test;

public class LifecycleModelExportTest {

//  @Test
//  public void testExport() throws Exception {
//    LifecycleModel straightLifecycle = WellKnownLifecycles.straightLineLifeCycle().build();
//    ByteArrayOutputStream baos=new ByteArrayOutputStream();
//    new LifecycleModelDotExporter().export(straightLifecycle.getGraph(), baos);
//    String dotContent=new String(baos.toByteArray());
//    String expectedDotExport=new String(Files.readAllBytes(FileSystems.getDefault().getPath("src/test/java/org/jceremony/straightLifecycle_export-dot.txt")));
//    assertEquals(expectedDotExport, dotContent);
//  }

  @Test
  public void testExportWithFunctions() throws Exception {
    LifecycleModel ABCLifecycle = WellKnownLifecycles.abcdLifecycle().build();
    ByteArrayOutputStream baos=new ByteArrayOutputStream();
    new LifecycleModelDotExporter().export(ABCLifecycle.getGraph(), baos);
    String dotContent=new String(baos.toByteArray());
    String expectedDotExport=new String(Files.readAllBytes(FileSystems.getDefault().getPath("src/test/java/org/jceremony/io/abc_export-dot.txt")));
    assertEquals(expectedDotExport, dotContent);
  }

}
