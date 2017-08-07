package org.heigit.bigspatialdata.oshdb.api.objects;

import org.heigit.bigspatialdata.oshdb.api.utils.TimestampFormatter;
import java.util.Date;

public class OSHDBTimestamp implements Comparable<OSHDBTimestamp> {
  private long _tstamp;
  private static final TimestampFormatter _timeStampFormatter = TimestampFormatter.getInstance();

  public OSHDBTimestamp(long tstamp) {
    this._tstamp = tstamp;
  }

  @Override
  public int compareTo(OSHDBTimestamp other) {
    return Long.compare(this._tstamp, other._tstamp);
  }
  
  public Date toDate() {
    return new Date(this._tstamp * 1000);
  }

  public long toLong() {
    return this._tstamp;
  }
  
  public String formatDate() {
    return this._timeStampFormatter.date(this.toDate());
  }

  public String formatIsoDateTime() {
    return this._timeStampFormatter.isoDateTime(this.toDate());
  }

  public String toString() {
    return this.formatIsoDateTime();
  }
}