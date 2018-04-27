package org.heigit.bigspatialdata.oshdb.util.celliterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.heigit.bigspatialdata.oshdb.grid.GridOSHNodes;
import org.heigit.bigspatialdata.oshdb.osh.OSHNode;
import org.heigit.bigspatialdata.oshdb.osm.OSMEntity;
import org.heigit.bigspatialdata.oshdb.osm.OSMNode;
import org.heigit.bigspatialdata.oshdb.util.OSHDBBoundingBox;
import org.heigit.bigspatialdata.oshdb.util.OSHDBTimestamp;
import org.heigit.bigspatialdata.oshdb.util.celliterator.CellIterator.IterateAllEntry;
import org.heigit.bigspatialdata.oshdb.util.celliterator.CellIterator.IterateByTimestampEntry;
import org.heigit.bigspatialdata.oshdb.util.geometry.OSHDBGeometryBuilder;
import org.heigit.bigspatialdata.oshdb.util.geometry.helpers.OSMXmlReaderTagInterpreter;
import org.heigit.bigspatialdata.oshdb.util.geometry.helpers.TimestampParser;
import org.heigit.bigspatialdata.oshdb.util.tagInterpreter.TagInterpreter;
import org.heigit.bigspatialdata.oshdb.util.test.OSMXmlReader;
import org.heigit.bigspatialdata.oshdb.util.time.OSHDBTimestamps;
import org.junit.Test;

public class NewIterateByTimestampsNodesTest {
  private GridOSHNodes oshdbDataGridCell;
  private final OSMXmlReader osmXmlTestData = new OSMXmlReader();
  TagInterpreter areaDecider;

  public NewIterateByTimestampsNodesTest() throws IOException {
    osmXmlTestData.add("./src/test/resources/different-timestamps/node.osm");
    areaDecider = new OSMXmlReaderTagInterpreter(osmXmlTestData);
    List<OSHNode> oshNodes = new ArrayList<>();
    for (Entry<Long, Collection<OSMNode>> entry : osmXmlTestData.nodes().asMap().entrySet()) {
      oshNodes.add(OSHNode.build(new ArrayList<>(entry.getValue())));
    }
    oshdbDataGridCell = GridOSHNodes.rebase(-1, -1, 0, 0, 0, 0,
        oshNodes
    );
  }

  @Test
  public void testGeometryChange() {
    // node 1: creation and two geometry changes, but no tag changes

    List<IterateByTimestampEntry> result = (new CellIterator(
        new OSHDBTimestamps(
            "2000-01-01T00:00:00Z",
            "2018-01-01T00:00:00Z",
            "P1Y"
        ).get(),
        new OSHDBBoundingBox(-180,-90, 180, 90),
        areaDecider,
        oshEntity -> oshEntity.getId() == 1,
        osmEntity -> true,
        false
    )).iterateByTimestamps(
        oshdbDataGridCell
    ).collect(Collectors.toList());

    assertEquals(11, result.size());
    assertNotEquals(result.get(1).geometry.get().getCoordinates(), result.get(0).geometry.get().getCoordinates());
    assertNotEquals(result.get(2).geometry.get().getCoordinates(), result.get(1).geometry.get().getCoordinates());
  }

  @Test
  public void testTagChange() {
    // node 2: creation and two tag changes, but no geometry changes

    List<IterateByTimestampEntry> result = (new CellIterator(
        new OSHDBTimestamps(
            "2000-01-01T00:00:00Z",
            "2018-01-01T00:00:00Z",
            "P1Y"
        ).get(),
        new OSHDBBoundingBox(-180,-90, 180, 90),
        areaDecider,
        oshEntity -> oshEntity.getId() == 2,
        osmEntity -> true,
        false
    )).iterateByTimestamps(
        oshdbDataGridCell
    ).collect(Collectors.toList());
    assertEquals(12, result.size());
    assertNotEquals(result.get(1).osmEntity.getRawTags(), result.get(0).osmEntity.getRawTags());
    assertEquals(result.get(2).osmEntity.getRawTags(), result.get(1).osmEntity.getRawTags());
    assertEquals(result.get(3).osmEntity.getRawTags(), result.get(2).osmEntity.getRawTags());
    assertEquals(result.get(4).osmEntity.getRawTags(), result.get(3).osmEntity.getRawTags());
    assertEquals(result.get(5).osmEntity.getRawTags(), result.get(4).osmEntity.getRawTags());
    assertEquals(result.get(6).osmEntity.getRawTags(), result.get(5).osmEntity.getRawTags());
    assertNotEquals(result.get(7).osmEntity.getRawTags(), result.get(6).osmEntity.getRawTags());
    assertEquals(result.get(8).osmEntity.getRawTags(), result.get(7).osmEntity.getRawTags());
    assertEquals(result.get(9).osmEntity.getRawTags(), result.get(8).osmEntity.getRawTags());
    assertEquals(result.get(10).osmEntity.getRawTags(), result.get(9).osmEntity.getRawTags());
    assertEquals(result.get(11).osmEntity.getRawTags(), result.get(10).osmEntity.getRawTags());
  }

  @Test
  public void testVisibleChange() {
    // node 3: creation and 4 visible changes, but no geometry and no tag changes

    List<IterateByTimestampEntry> result = (new CellIterator(
        new OSHDBTimestamps(
            "2000-01-01T00:00:00Z",
            "2018-01-01T00:00:00Z",
            "P1Y"
        ).get(),
        new OSHDBBoundingBox(-180,-90, 180, 90),
        areaDecider,
        oshEntity -> oshEntity.getId() == 3,
        osmEntity -> true,
        false
    )).iterateByTimestamps(
        oshdbDataGridCell
    ).collect(Collectors.toList());
    assertEquals(5, result.size());
  }

  @Test
  public void testMultipleChanges() {
    // node 4: creation and 5 changes:
    // tag and geometry,
    // visible = false,
    // visible = true and tag and geometry
    // geometry
    // tag

    List<IterateByTimestampEntry> result = (new CellIterator(
        new OSHDBTimestamps(
            "2000-01-01T00:00:00Z",
            "2018-01-01T00:00:00Z",
            "P1Y"
        ).get(),
        new OSHDBBoundingBox(-180,-90, 180, 90),
        areaDecider,
        oshEntity -> oshEntity.getId() == 4,
        osmEntity -> true,
        false
    )).iterateByTimestamps(
        oshdbDataGridCell
    ).collect(Collectors.toList());

    assertEquals(11, result.size());
    assertNotEquals(result.get(1).geometry.get().getCoordinates(), result.get(0).geometry.get().getCoordinates());
    assertEquals(result.get(2).geometry.get().getCoordinates(), result.get(1).geometry.get().getCoordinates());
    assertNotEquals(result.get(3).geometry.get().getCoordinates(), result.get(2).geometry.get().getCoordinates());
    assertEquals(result.get(5).geometry.get().getCoordinates(), result.get(3).geometry.get().getCoordinates());
    assertNotEquals(result.get(6).geometry.get().getCoordinates(), result.get(3).geometry.get().getCoordinates());
    assertEquals(result.get(9).geometry.get().getCoordinates(), result.get(6).geometry.get().getCoordinates());
    assertNotEquals(result.get(1).osmEntity.getRawTags(), result.get(0).osmEntity.getRawTags());
    assertEquals(result.get(2).osmEntity.getRawTags(), result.get(1).osmEntity.getRawTags());
    assertNotEquals(result.get(3).osmEntity.getRawTags(), result.get(2).osmEntity.getRawTags());
    assertEquals(result.get(5).osmEntity.getRawTags(), result.get(4).osmEntity.getRawTags());
    assertNotEquals(result.get(9).osmEntity.getRawTags(), result.get(6).osmEntity.getRawTags());
  }
}
