package org.heigit.bigspatialdata.oshdb.grid;

import java.io.Serializable;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.heigit.bigspatialdata.oshdb.index.XYGrid;
import org.heigit.bigspatialdata.oshdb.osh.OSHEntity;
import org.heigit.bigspatialdata.oshdb.util.BoundingBox;
import org.heigit.bigspatialdata.oshdb.util.CellId;

@SuppressWarnings("rawtypes")
public abstract class GridOSHEntity<HOSM extends OSHEntity> implements Iterable<HOSM>, Serializable {

  private static final long serialVersionUID = 1L;
  protected final long id;
  protected final int level;

  protected final long baseTimestamp;

  protected final long baseLongitude;
  protected final long baseLatitude;

  protected final long baseId;

  protected final int[] index;
  protected final byte[] data;

  public GridOSHEntity(final long id,
          final int level,
          final long baseId,
          final long baseTimestamp, final long baseLongitude, final long baseLatitude, final int[] index, final byte[] data) {

    this.id = id;
    this.level = level;
    this.baseTimestamp = baseTimestamp;
    this.baseLongitude = baseLongitude;
    this.baseLatitude = baseLatitude;
    this.baseId = baseId;

    this.index = index;
    this.data = data;
  }

  public long getId() {
    return id;
  }

  public int getLevel() {
    return level;
  }

  @Override
  public String toString() {
    try {
      BoundingBox bbox = XYGrid.getBoundingBox(new CellId((int) id, level));
      return String.format(Locale.ENGLISH, "ID:%d Level:%d BBox:(%f,%f),(%f,%f)", id, level, bbox.minLat, bbox.minLon, bbox.maxLat, bbox.maxLon);
    } catch (CellId.cellIdExeption ex) {
      Logger.getLogger(GridOSHEntity.class.getName()).log(Level.WARNING, null, ex);
      return String.format(Locale.ENGLISH, "ID:%d Level:%d", id, level);
    }
  }
}
