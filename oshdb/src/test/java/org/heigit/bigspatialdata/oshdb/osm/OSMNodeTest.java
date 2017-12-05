package org.heigit.bigspatialdata.oshdb.osm;

import java.sql.DriverManager;
import java.sql.SQLException;
import org.heigit.bigspatialdata.oshdb.util.TagTranslator;
import org.heigit.bigspatialdata.oshdb.util.tagInterpreter.TagInterpreter;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Rafael Troilo <rafael.troilo@uni-heidelberg.de>
 * @author Moritz Schott <m.schott@stud.uni-heidelberg.de>
 */
public class OSMNodeTest {

  public OSMNodeTest() {
  }

  @Test
  public void testGetLongitude() {
    OSMNode instance = new OSMNode(1L, 1, 1L, 1L, 1, new int[]{}, 1000000000L, 1L);
    double expResult = 100.0;
    double result = instance.getLongitude();
    assertEquals(expResult, result, 0.0);
  }

  @Test
  public void testGetLatitude() {
    OSMNode instance = new OSMNode(1L, 1, 1L, 1L, 1, new int[]{}, 1000000000L, 1000000000L);
    double expResult = 100.0;
    double result = instance.getLatitude();
    assertEquals(expResult, result, 0.0);
  }

  @Test
  public void testGetLon() {
    OSMNode instance = new OSMNode(1L, 1, 1L, 1L, 1, new int[]{}, 1000000000L, 1000000000L);
    long expResult = 1000000000L;
    long result = instance.getLon();
    assertEquals(expResult, result);
  }

  @Test
  public void testGetLat() {
    OSMNode instance = new OSMNode(1L, 1, 1L, 1L, 1, new int[]{}, 1000000000L, 1000000000L);
    long expResult = 1000000000L;
    long result = instance.getLat();
    assertEquals(expResult, result);
  }

  @Test
  public void testToString() {
    OSMNode instance = new OSMNode(1L, 1, 1L, 1L, 1, new int[]{}, 1000000000L, 1000000000L);
    String expResult = "NODE: ID:1 V:+1+ TS:1 CS:1 VIS:true UID:1 TAGS:[] 100.000000:100.000000";
    String result = instance.toString();
    assertEquals(expResult, result);
  }

  @Test
  public void testToString_TagTranslator() throws SQLException, ClassNotFoundException {
    int[] properties = {1, 2};
    OSMNode instance = new OSMNode(1L, 1, 1L, 1L, 1, properties, 1000000000L, 1000000000L);
    String expResult = "NODE: ID:1 V:+1+ TS:1 CS:1 VIS:true UID:1 UName:Alice TAGS:[(highway,track)] 100.000000:100.000000";
    String result = instance.toString(new TagTranslator(DriverManager.getConnection("jdbc:h2:./src/test/resources/keytables", "sa", "")));
    assertEquals(expResult, result);
  }

  @Test
  public void testEqualsTo() {
    OSMNode o = new OSMNode(1L, 1, 1L, 1L, 1, new int[]{1, 1, 2, 2, 3, 3}, 1000000000L, 1000000000L);
    OSMNode instance = new OSMNode(1L, 1, 1L, 1L, 1, new int[]{1, 1, 2, 2, 3, 3}, 1000000000L, 1000000000L);
    boolean expResult = true;
    boolean result = instance.equalsTo(o);
    assertEquals(expResult, result);
  }

  @Test
  public void testEqualsToII() {
    OSMNode o = new OSMNode(2L, 1, 1L, 1L, 1, new int[]{1, 1, 2, 2, 3, 3}, 1000000000L, 1000000000L);
    OSMNode instance = new OSMNode(1L, 1, 1L, 1L, 1, new int[]{1, 1, 2, 2, 3, 3}, 1000000000L, 1000000000L);
    boolean expResult = false;
    boolean result = instance.equalsTo(o);
    assertEquals(expResult, result);
  }

  @Test
  public void testCompareTo() {
    OSMNode o = new OSMNode(1L, 1, 1L, 1L, 1, new int[]{1, 1, 2, 2, 3, 3}, 1000000000L, 1000000000L);
    OSMNode instance = new OSMNode(1L, 1, 1L, 1L, 1, new int[]{1, 1, 2, 2, 3, 3}, 1000000000L, 1000000000L);
    assertTrue(instance.compareTo(o) == 0);

    o = new OSMNode(1L, 3, 1L, 1L, 1, new int[]{1, 1, 2, 2, 3, 3}, 1000000000L, 1000000000L);
    instance = new OSMNode(1L, 1, 1L, 1L, 1, new int[]{1, 1, 2, 2, 3, 3}, 1000000000L, 1000000000L);
    assertTrue(instance.compareTo(o) < 0);

    o = new OSMNode(1L, 1, 1L, 1L, 1, new int[]{1, 1, 2, 2, 3, 3}, 1000000000L, 1000000000L);
    instance = new OSMNode(1L, 3, 1L, 1L, 1, new int[]{1, 1, 2, 2, 3, 3}, 1000000000L, 1000000000L);
    assertTrue(instance.compareTo(o) > 0);

    o = new OSMNode(1L, 1, 1L, 1L, 1, new int[]{1, 1, 2, 2, 3, 3}, 1000000000L, 1000000000L);
    instance = new OSMNode(1L, -6, 1L, 1L, 1, new int[]{1, 1, 2, 2, 3, 3}, 1000000000L, 1000000000L);
    assertTrue(instance.compareTo(o) > 0);
  }

  //-------------------

  @Test
  public void testGetId() {
    OSMNode instance = new OSMNode(1L, 1, 1L, 1L, 1, new int[]{}, 1000000000L, 1000000000L);
    long expResult = 1L;
    long result = instance.getId();
    assertEquals(expResult, result);
  }

  @Test
  public void testGetVersion() {
    OSMNode instance = new OSMNode(1L, 1, 1L, 1L, 1, new int[]{}, 1000000000L, 1000000000L);
    int expResult = 1;
    int result = instance.getVersion();
    assertEquals(expResult, result);
  }

  @Test
  public void testGetTimestamp() {
    OSMNode instance = new OSMNode(1L, 1, 1L, 1L, 1, new int[]{}, 1000000000L, 1000000000L);
    long expResult = 1L;
    long result = instance.getTimestamp();
    assertEquals(expResult, result);
  }

  @Test
  public void testGetChangeset() {
    OSMNode instance = new OSMNode(1L, 1, 1L, 1L, 1, new int[]{}, 1000000000L, 1000000000L);
    long expResult = 1L;
    long result = instance.getChangeset();
    assertEquals(expResult, result);
  }

  @Test
  public void testGetUserId() {
    OSMNode instance = new OSMNode(1L, 1, 1L, 1L, 1, new int[]{}, 1000000000L, 1000000000L);
    int expResult = 1;
    int result = instance.getUserId();
    assertEquals(expResult, result);
  }

  @Test
  public void testisVisible() {
    OSMNode instance = new OSMNode(1L, 1, 1L, 1L, 1, new int[]{}, 1000000000L, 1000000000L);
    boolean expResult = true;
    boolean result = instance.isVisible();
    assertEquals(expResult, result);
  }

  @Test
  public void testisVisibleII() {
    OSMNode instance = new OSMNode(1L, -1, 1L, 1L, 1, new int[]{}, 1000000000L, 1000000000L);
    boolean expResult = false;
    boolean result = instance.isVisible();
    assertEquals(expResult, result);
  }

  @Test
  public void testGetTags() {
    OSMNode instance = new OSMNode(1L, 1, 1L, 1L, 1, new int[]{}, 1000000000L, 1000000000L);
    int[] expResult = new int[]{};
    int[] result = instance.getTags();
    Assert.assertArrayEquals(expResult, result);
  }

  @Test
  public void testHasTagKey() {
    OSMNode instance = new OSMNode(1L, 1, 1L, 1L, 1, new int[]{}, 1000000000L, 1000000000L);
    boolean expResult = false;
    boolean result = instance.hasTagKey(1);
    assertEquals(expResult, result);

    instance = new OSMNode(1L, 1, 1L, 1L, 1, new int[]{1, 1, 2, 2, 3, 3}, 1000000000L, 1000000000L);
    expResult = true;
    result = instance.hasTagKey(1);
    assertEquals(expResult, result);

    instance = new OSMNode(1L, 1, 1L, 1L, 1, new int[]{1, 2, 2, 2, 3, 3}, 1000000000L, 1000000000L);
    expResult = false;
    result = instance.hasTagKey(1, new int[]{2, 3});
    assertEquals(expResult, result);

    instance = new OSMNode(1L, 1, 1L, 1L, 1, new int[]{1, 1, 2, 2, 3, 3}, 1000000000L, 1000000000L);
    expResult = true;
    result = instance.hasTagKey(1, new int[]{2, 3});
    assertEquals(expResult, result);

    instance = new OSMNode(1L, 1, 1L, 1L, 1, new int[]{2, 1, 3, 3}, 1000000000L, 1000000000L);
    expResult = false;
    result = instance.hasTagKey(1, new int[]{1, 3});
    assertEquals(expResult, result);
  }

  @Test
  public void testHasTagValue() {
    OSMNode instance = new OSMNode(1L, 1, 1L, 1L, 1, new int[]{1, 2, 2, 3}, 1000000000L, 1000000000L);
    boolean expResult = false;
    boolean result = instance.hasTagValue(1, 1);
    assertEquals(expResult, result);

    instance = new OSMNode(1L, 1, 1L, 1L, 1, new int[]{1, 1, 2, 3}, 1000000000L, 1000000000L);
    expResult = true;
    result = instance.hasTagValue(1, 1);
    assertEquals(expResult, result);
  }

  //--------------------

  @Test
  public void testEqualsToOSMNode() {
    long id = 123;
    int version = 1;
    long timestamp = 310172400000l;
    long changeset = 4444;
    int userId = 23;
    int[] tags = new int[]{1, 1, 2, 2, 3, 3};
    long longitude = 86809727l;
    long latitude = 494094984l;

    OSMNode a = new OSMNode(id, version, timestamp, changeset, userId, tags, longitude, latitude);
    OSMNode b = new OSMNode(id, version, timestamp, changeset, userId, tags, longitude, latitude);
    assertTrue(a.equalsTo(b));
  }

  @Test
  public void testCompareToV() {
    long id = 123;
    int version = 1;
    long timestamp = 310172400000l;
    long changeset = 4444;
    int userId = 23;
    int[] tags = new int[]{1, 1, 2, 2, 3, 3};
    long longitude = 86809727l;
    long latitude = 494094984l;

    OSMNode a = new OSMNode(id, version, timestamp, changeset, userId, tags, longitude, latitude);

    OSMNode b;

    b = new OSMNode(id, version + 2, timestamp, changeset, userId, tags, longitude, latitude);

    assertTrue(a.compareTo(b) < 0);
  }

  @Test
  public void testToGeoJSON_long_TagTranslator_TagInterpreter() throws SQLException, ClassNotFoundException {
    int[] properties = {1, 2};
    OSMNode instance = new OSMNode(1L, 1, 0L, 1L, 1, properties, 1000000000L, 1000000000L);
    TagTranslator tt = new TagTranslator(DriverManager.getConnection("jdbc:h2:./src/test/resources/keytables", "sa", ""));
    String expResult = "{\"type\":\"Feature\",\"id\":\"node/1@1970-01-01T00:00:01Z\",\"properties\":{\"@type\":\"node\",\"@id\":1,\"@visible\":true,\"@version\":1,\"@changeset\":1,\"@timestamp\":\"1970-01-01T00:00:00Z\",\"@geomtimestamp\":\"1970-01-01T00:00:01Z\",\"@user\":\"Alice\",\"@uid\":1,\"highway\":\"track\"},\"geometry\":{\"type\":\"Point\",\"coordinates\":[100.0,100.0]}}";
    String result = instance.toGeoJSON(1L, tt, new TagInterpreter(1, 1, null, null, null, 1, 1, 1));
    assertEquals(expResult, result);
  }
}