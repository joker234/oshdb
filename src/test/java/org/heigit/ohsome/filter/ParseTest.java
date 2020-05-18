package org.heigit.ohsome.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.heigit.bigspatialdata.oshdb.osm.OSMType;
import org.junit.Test;

/**
 * Test class for the ohsome-filter package.
 *
 * <p>Tests the parsing of filters and the application to OSM entities.</p>
 */
public class ParseTest extends FilterTest {
  @Test
  public void testTagFilterEquals() {
    FilterExpression expression = parser.parse("highway=residential");
    assertTrue(expression instanceof TagFilterEquals);
  }

  @Test
  public void testTagFilterStrings() {
    // tag key with colon; quoted string as value
    assertTrue(parser.parse("addr:street=\"Hauptstraße\"") instanceof TagFilter);
    // whitespace (in string; between key and value, single quotes
    assertTrue(parser.parse("name = \"Colorado River\"") instanceof TagFilter);

    // "Allowed characters are: the letters `a-z` and `A-Z`, digits, underscore, dashes and colons."
    assertTrue(parser.parse("name=a0_-:") instanceof TagFilter);
  }

  @Test
  public void testTagFilterEqualsAny() {
    FilterExpression expression = parser.parse("highway=*");
    assertTrue(expression instanceof TagFilterEqualsAny);
  }

  @Test
  public void testTagFilterNotEquals() {
    FilterExpression expression = parser.parse("highway!=residential");
    assertTrue(expression instanceof TagFilterNotEquals);
  }

  @Test
  public void testTagFilterNotEqualsAny() {
    FilterExpression expression = parser.parse("highway!=*");
    assertTrue(expression instanceof TagFilterNotEqualsAny);
  }

  @Test
  public void testTypeFilter() {
    FilterExpression expression = parser.parse("type:node");
    assertTrue(expression instanceof TypeFilter);
    assertEquals(OSMType.NODE, ((TypeFilter) expression).getType());
  }

  @Test
  public void testAndOperator() {
    FilterExpression expression = parser.parse("highway=residential and name=*");
    assertTrue(expression instanceof AndOperator);
    assertTrue(((AndOperator) expression).getLeftOperand() instanceof TagFilter);
    assertTrue(((AndOperator) expression).getRightOperand() instanceof TagFilter);
  }

  @Test
  public void testOrOperator() {
    FilterExpression expression = parser.parse("highway=residential or name=*");
    assertTrue(expression instanceof OrOperator);
    assertTrue(((OrOperator) expression).getLeftOperand() instanceof TagFilter);
    assertTrue(((OrOperator) expression).getRightOperand() instanceof TagFilter);
  }

  @Test
  public void testGeometryTypeFilterPoint() {
    FilterExpression expression = parser.parse("geometry:point");
    assertTrue(expression instanceof GeometryTypeFilter);
  }

  @Test
  public void testGeometryTypeFilterLine() {
    FilterExpression expression = parser.parse("geometry:line");
    assertTrue(expression instanceof GeometryTypeFilter);
  }

  @Test
  public void testGeometryTypeFilterPolygon() {
    FilterExpression expression = parser.parse("geometry:polygon");
    assertTrue(expression instanceof GeometryTypeFilter);
  }

  @Test
  public void testGeometryTypeFilterOther() {
    FilterExpression expression = parser.parse("geometry:other");
    assertTrue(expression instanceof GeometryTypeFilter);
  }
}
