/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.heigit.bigspatialdata.oshdb.api.tests;

import org.heigit.bigspatialdata.oshdb.OSHDB;
import org.heigit.bigspatialdata.oshdb.api.mapreducer.MapReducer;
import org.heigit.bigspatialdata.oshdb.api.mapreducer.OSMContributionView;
import org.heigit.bigspatialdata.oshdb.api.mapreducer.OSMEntitySnapshotView;
import org.heigit.bigspatialdata.oshdb.api.objects.OSHDBTimestamps;
import org.heigit.bigspatialdata.oshdb.api.objects.OSMContribution;
import org.heigit.bigspatialdata.oshdb.api.objects.OSMEntitySnapshot;
import org.heigit.bigspatialdata.oshdb.osm.OSMType;
import org.heigit.bigspatialdata.oshdb.util.BoundingBox;
import org.heigit.bigspatialdata.oshdb.util.ContributionType;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 *
 */
abstract class FlatMapReduceGroupedById {
  private final OSHDB oshdb;

  private final BoundingBox bbox = new BoundingBox(8, 9, 49, 50);
  private final OSHDBTimestamps timestamps6 = new OSHDBTimestamps(2010, 2015, 1, 1);
  private final OSHDBTimestamps timestamps72 = new OSHDBTimestamps(2010, 2015, 1, 12);

  FlatMapReduceGroupedById(OSHDB oshdb) throws Exception {
    this.oshdb = oshdb;
  }

  private MapReducer<OSMContribution> createMapReducerOSMContribution() throws Exception {
    return OSMContributionView.on(oshdb).osmTypes(OSMType.NODE).filterByTag("highway").areaOfInterest(bbox);
  }

  private MapReducer<OSMEntitySnapshot> createMapReducerOSMEntitySnapshot() throws Exception {
    return OSMEntitySnapshotView.on(oshdb).osmTypes(OSMType.NODE).filterByTag("highway").areaOfInterest(bbox);
  }

  @Test
  public void testOSMContributionView() throws Exception {
    Number result = createMapReducerOSMContribution()
        .timestamps(timestamps72)
        .groupById()
        .flatMap(contributions -> {
          if (contributions.get(0).getEntityAfter().getId() != 617308093)
            return new ArrayList<>();
          List<Integer> ret = new ArrayList<>();
          contributions.stream().filter(c -> c.getContributionTypes().contains(ContributionType.GEOMETRY_CHANGE)).map(ignored -> 1).forEach(ret::add);
          ret.add(2); // just add another "2" for good measure ;-)
          return ret;
        })
        .reduce(
            () -> 0,
            (x,y) -> x + y,
            (x,y) -> x + y
        );

    assertEquals(5+2, result.intValue());
  }

  @Test
  public void testOSMEntitySnapshotView() throws Exception {
    Number result = createMapReducerOSMEntitySnapshot()
        .timestamps(timestamps6)
        .groupById()
        .flatMap(snapshots -> {
          if (snapshots.get(0).getEntity().getId() != 617308093)
            return new ArrayList<>();
          List<Integer> ret = new ArrayList<>();
          for (int i=1; i<snapshots.size(); i++)
            if (!snapshots.get(i-1).getGeometry().equals(snapshots.get(i).getGeometry()))
              ret.add(1);
          ret.add(2); // just add another "2" for good measure ;-)
          return ret;
        })
        .sum();

    assertEquals(2+2, result.intValue());
  }
}
