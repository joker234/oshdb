package org.heigit.ohsome.filter;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import org.heigit.bigspatialdata.oshdb.impl.osh.OSHNodeImpl;
import org.heigit.bigspatialdata.oshdb.impl.osh.OSHRelationImpl;
import org.heigit.bigspatialdata.oshdb.impl.osh.OSHWayImpl;
import org.heigit.bigspatialdata.oshdb.osh.OSHNode;
import org.heigit.bigspatialdata.oshdb.osh.OSHRelation;
import org.heigit.bigspatialdata.oshdb.osh.OSHWay;
import org.heigit.bigspatialdata.oshdb.osm.OSMMember;
import org.heigit.bigspatialdata.oshdb.osm.OSMNode;
import org.heigit.bigspatialdata.oshdb.osm.OSMRelation;
import org.heigit.bigspatialdata.oshdb.osm.OSMType;
import org.heigit.bigspatialdata.oshdb.osm.OSMWay;
import org.heigit.bigspatialdata.oshdb.util.OSHDBTag;
import org.heigit.bigspatialdata.oshdb.util.OSHDBTimestamp;
import org.heigit.bigspatialdata.oshdb.util.exceptions.OSHDBKeytablesNotFoundException;
import org.heigit.bigspatialdata.oshdb.util.tagtranslator.TagTranslator;
import org.heigit.ohsome.oshdb.filter.FilterParser;
import org.junit.After;
import org.junit.Before;

/**
 * Test class for the ohsome-filter package.
 *
 * <p>Tests the parsing of filters and the application to OSM entities.</p>
 */
abstract class FilterTest {
  protected FilterParser parser;
  protected TagTranslator tagTranslator;

  @Before
  public void setup() throws SQLException, ClassNotFoundException, OSHDBKeytablesNotFoundException {
    Class.forName("org.h2.Driver");
    this.tagTranslator = new TagTranslator(DriverManager.getConnection(
        "jdbc:h2:./src/test/resources/keytables;ACCESS_MODE_DATA=r",
        "sa", ""
    ));
    this.parser = new FilterParser(this.tagTranslator);
  }

  @After
  public void teardown() throws SQLException {
    this.tagTranslator.getConnection().close();
  }

  protected int[] createTestTags(String... keyValues) {
    ArrayList<Integer> tags = new ArrayList<>(keyValues.length);
    for (int i = 0; i < keyValues.length; i += 2) {
      OSHDBTag t = tagTranslator.getOSHDBTagOf(keyValues[i], keyValues[i + 1]);
      tags.add(t.getKey());
      tags.add(t.getValue());
    }
    return tags.stream().mapToInt(x -> x).toArray();
  }

  protected OSMNode createTestOSMEntityNode(String... keyValues) {
    return new OSMNode(1, 1, new OSHDBTimestamp(0), 1, 1, createTestTags(keyValues), 0, 0);
  }

  protected OSMWay createTestOSMEntityWay(long[] nodeIds, String... keyValues) {
    OSMMember[] refs = new OSMMember[nodeIds.length];
    for (int i = 0; i < refs.length; i++) {
      refs[i] = new OSMMember(nodeIds[i], OSMType.NODE, 0);
    }
    return new OSMWay(1, 1, new OSHDBTimestamp(0), 1, 1, createTestTags(keyValues), refs);
  }

  protected OSMRelation createTestOSMEntityRelation(String... keyValues) {
    return new OSMRelation(1, 1, new OSHDBTimestamp(0), 1, 1, createTestTags(keyValues),
        new OSMMember[] {});
  }

  protected OSHNode createTestOSHEntityNode(OSMNode... versions) throws IOException {
    return OSHNodeImpl.build(Arrays.asList(versions));
  }

  protected OSHWay createTestOSHEntityWay(OSMWay...versions) throws IOException {
    return OSHWayImpl.build(Arrays.asList(versions), Collections.emptyList());
  }

  protected OSHRelation createTestOSHEntityRelation(OSMRelation... versions) throws IOException {
    return OSHRelationImpl.build(Arrays.asList(versions), Collections.emptyList(),
        Collections.emptyList());
  }
}
