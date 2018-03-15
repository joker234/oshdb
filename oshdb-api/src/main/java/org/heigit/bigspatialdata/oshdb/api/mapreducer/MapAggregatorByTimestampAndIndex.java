package org.heigit.bigspatialdata.oshdb.api.mapreducer;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.heigit.bigspatialdata.oshdb.api.generic.OSHDBTimestampAndIndex;
import org.heigit.bigspatialdata.oshdb.api.generic.function.*;
import org.heigit.bigspatialdata.oshdb.util.OSHDBTimestamp;
import org.heigit.bigspatialdata.oshdb.api.object.OSMContribution;
import org.jetbrains.annotations.Contract;

import java.util.*;

/**
 * A special variant of a MapAggregator, that does aggregation by two indexes: one timestamp index and another, arbitrary one.
 *
 * Like MapAggregateByTimestamp, this one can also automatically fill timestamps with no data with "zero" values (which for example results in actual 0's in the case os `sum()` or `count()` operations, or `NaN` when using `average()`).
 *
 * @param <X> the type that is returned by the currently set of mapper function. the next added mapper function will be called with a parameter of this type as input
 * @param <U> the type of the second index used to group results
 */
public class MapAggregatorByTimestampAndIndex<U, X> extends MapAggregator<OSHDBTimestampAndIndex<U>, X> {
  private boolean _zerofillTimestamps = true;
  private Collection<U> _zerofillKeys = Collections.emptyList();

  /**
   * constructor that takes an existing mapAggregatorByTimestamps object and adds a second index to it.
   *
   * @param timeMapAggregator already existing mapAggregatorByTimestamps object that should be
   *        aggregated by another index as well
   * @param indexer function that returns the index value by which to aggregate the results as well
   */
  MapAggregatorByTimestampAndIndex(
      MapAggregatorByTimestamps<X> timeMapAggregator,
      SerializableFunction<X, U> indexer
    ) {
    super();
    this._mapReducer = timeMapAggregator._mapReducer.map(data -> new MutablePair<OSHDBTimestampAndIndex<U>, X>(
        new OSHDBTimestampAndIndex<U>(
            data.getKey(),
            indexer.apply(data.getValue())
        ),
        data.getValue()
    ));
  }

  // "copy/transform" constructor
  private MapAggregatorByTimestampAndIndex(MapAggregatorByTimestampAndIndex<U ,?> obj, MapReducer<Pair<OSHDBTimestampAndIndex<U>, X>> mapReducer) {
    this._mapReducer = mapReducer;
    this._zerofillTimestamps = obj._zerofillTimestamps;
    this._zerofillKeys = obj._zerofillKeys;
  }

  @Override
  @Contract(pure = true)
  protected <R> MapAggregatorByTimestampAndIndex<U, R> copyTransform(MapReducer<Pair<OSHDBTimestampAndIndex<U>, R>> mapReducer) {
    return new MapAggregatorByTimestampAndIndex<>(this, mapReducer);
  }

  /**
   * Enables/Disables the zero-filling feature of otherwise empty timestamp entries in the result.
   *
   * This feature is enabled by default, and can be disabled by calling this function with a value of `false`.
   *
   * @param zerofill the enabled/disabled state of the zero-filling feature
   * @return a modified copy of this object (can be used to chain multiple commands together)
   */
  @Contract(pure = true)
  public MapAggregatorByTimestampAndIndex<U, X> zerofillTimestamps(boolean zerofill) {
    MapAggregatorByTimestampAndIndex<U, X> ret = this.copyTransform(this._mapReducer);
    ret._zerofillTimestamps = zerofill;
    return ret;
  }

  /**
   * Enables/Disables the zero-filling of otherwise empty entries in the result.
   *
   * @param zerofillKeys a collection of keys whose values should be filled with "zeros" if they
   *        would otherwise not be present in the result
   * @return a modified copy of this object (can be used to chain multiple commands together)
   */
  @Contract(pure = true)
  public MapAggregatorByTimestampAndIndex<U, X> zerofillIndices(Collection<U> zerofillKeys) {
    MapAggregatorByTimestampAndIndex<U, X> ret = this.copyTransform(this._mapReducer);
    ret._zerofillKeys = zerofillKeys;
    return ret;
  }

  /**
   * Set an arbitrary `map` transformation function.
   *
   * @return a modified copy of this MapAggregatorByTimestamps object operating on the transformed type (&lt;R&gt;)
   */
  @Override
  @Contract(pure = true)
  public <R> MapAggregatorByTimestampAndIndex<U, R> map(SerializableFunction<X, R> mapper) {
    return (MapAggregatorByTimestampAndIndex<U, R>)super.map(mapper);
  }

  /**
   * Set an arbitrary `flatMap` transformation function, which returns list with an arbitrary number of results per input data entry.
   * The results of this function will be "flattened", meaning that they can be for example transformed again by setting additional `map` functions.
   *
   * @return a modified copy of this MapAggregatorByTimestamps object operating on the transformed type (&lt;R&gt;)
   */
  @Override
  @Contract(pure = true)
  public <R> MapAggregatorByTimestampAndIndex<U, R> flatMap(SerializableFunction<X, List<R>> flatMapper) {
    return (MapAggregatorByTimestampAndIndex<U, R>)super.flatMap(flatMapper);
  }

  /**
   * Map-reduce routine with built-in aggregation by timestamp
   *
   * This can be used to perform an arbitrary map-reduce routine whose results should be aggregated separately according to timestamps.
   *
   * Timestamps with no results are filled with zero values (as provided by the `identitySupplier` function).
   *
   * The combination of the used types and identity/reducer functions must make "mathematical" sense:
   * <ul>
   *   <li>the accumulator and combiner functions need to be associative,</li>
   *   <li>values generated by the identitySupplier factory must be an identity for the combiner function: `combiner(identitySupplier(),x)` must be equal to `x`,</li>
   *   <li>the combiner function must be compatible with the accumulator function: `combiner(u, accumulator(identitySupplier(), t)) == accumulator.apply(u, t)`</li>
   * </ul>
   *
   * Functionally, this interface is similar to Java8 Stream's <a href="https://docs.oracle.com/javase/8/docs/api/java/util/stream/Stream.html#reduce-U-java.util.function.BiFunction-java.util.function.BinaryOperator-">reduce(identity,accumulator,combiner)</a> interface.
   *
   * @param identitySupplier a factory function that returns a new starting value to reduce results into (e.g. when summing values, one needs to start at zero)
   * @param accumulator a function that takes a result from the `mapper` function (type &lt;R&gt;) and an accumulation value (type &lt;S&gt;, e.g. the result of `identitySupplier()`) and returns the "sum" of the two; contrary to `combiner`, this function is allowed to alter (mutate) the state of the accumulation value (e.g. directly adding new values to an existing Set object)
   * @param combiner a function that calculates the "sum" of two &lt;S&gt; values; <b>this function must be pure (have no side effects), and is not allowed to alter the state of the two input objects it gets!</b>
   * @param <S> the data type used to contain the "reduced" (intermediate and final) results
   * @return the result of the map-reduce operation, the final result of the last call to the `combiner` function, after all `mapper` results have been aggregated (in the `accumulator` and `combiner` steps)
   * @throws Exception
   */
  @Override
  @Contract(pure = true)
  public <S> SortedMap<OSHDBTimestampAndIndex<U>, S> reduce(SerializableSupplier<S> identitySupplier, SerializableBiFunction<S, X, S> accumulator, SerializableBinaryOperator<S> combiner) throws Exception {
    SortedMap<OSHDBTimestampAndIndex<U>, S> result = super.reduce(identitySupplier, accumulator, combiner);
    if (!this._zerofillTimestamps && this._zerofillKeys.isEmpty()) return result;
    // fill nodata entries with "0"
    final SortedSet<OSHDBTimestamp> timestamps = this._mapReducer._tstamps.get();
    // pop last element from timestamps list if we're dealing with OSMContributions (where the timestamps list defines n-1 time intervals)
    if (this._mapReducer._forClass.equals(OSMContribution.class))
      timestamps.remove(timestamps.last());
    HashSet<U> seen = new HashSet<>();
    (new TreeSet<>(result.keySet())).forEach(index -> {
      if (!seen.contains(index.getOtherIndex())) {
        timestamps.forEach(ts -> {
          OSHDBTimestampAndIndex<U> potentiallyMissingIndex = new OSHDBTimestampAndIndex<>(ts, index.getOtherIndex());
          result.putIfAbsent(potentiallyMissingIndex, identitySupplier.get());
        });
        seen.add(index.getOtherIndex());
      }
    });
    TreeSet<U> zerofillKeys = new TreeSet<>(this._zerofillKeys);
    zerofillKeys.removeAll(seen);
    zerofillKeys.forEach(zerofillKey ->
      timestamps.forEach(ts ->
        result.put(new OSHDBTimestampAndIndex<>(ts, zerofillKey), identitySupplier.get())
      )
    );
    return result;
  }

  /**
   * Helper function that converts the dual-index data structure returned by aggregation operations on this object to a nested Map structure,
   * which can be easier to process further on.
   *
   * This version creates a map for each &lt;U&gt; index value, containing maps containing results by timestamps.
   *
   * See also {@link #nest_TimeThenIndex(Map)}.
   *
   * @param result the "flat" result data structure that should be converted to a nested structure
   * @param <A> an arbitrary data type, used for the data value items
   * @param <U> an arbitrary data type, used for the index'es key items
   * @return a nested data structure, where for each index part there is a separate level of nested maps
   */
  public static <A,U> SortedMap<U, SortedMap<OSHDBTimestamp, A>> nest_IndexThenTime(Map<OSHDBTimestampAndIndex<U>, A> result) {
    TreeMap<U, SortedMap<OSHDBTimestamp, A>> ret = new TreeMap<>();
    result.forEach((index, data) -> {
      if (!ret.containsKey(index.getOtherIndex()))
        ret.put(index.getOtherIndex(), new TreeMap<OSHDBTimestamp, A>());
      ret.get(index.getOtherIndex()).put(index.getTimeIndex(), data);
    });
    return ret;
  }

  /**
   * Helper function that converts the dual-index data structure returned by aggregation operations on this object to a nested Map structure,
   * which can be easier to process further on.
   *
   * This version creates a map for each timestamp, containing maps containing results by &lt;U&gt; index values.
   *
   * See also {@link #nest_IndexThenTime(Map)}.
   *
   * @param result the "flat" result data structure that should be converted to a nested structure
   * @param <A> an arbitrary data type, used for the data value items
   * @param <U> an arbitrary data type, used for the index'es key items
   * @return a nested data structure, where for each index part there is a separate level of nested maps
   */
  public static <A,U> SortedMap<OSHDBTimestamp, SortedMap<U, A>> nest_TimeThenIndex(Map<OSHDBTimestampAndIndex<U>, A> result) {
    TreeMap<OSHDBTimestamp, SortedMap<U, A>> ret = new TreeMap<>();
    result.forEach((index, data) -> {
      if (!ret.containsKey(index.getTimeIndex()))
        ret.put(index.getTimeIndex(), new TreeMap<U, A>());
      ret.get(index.getTimeIndex()).put(index.getOtherIndex(), data);
    });
    return ret;
  }
}