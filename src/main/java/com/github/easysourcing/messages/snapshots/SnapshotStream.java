package com.github.easysourcing.messages.snapshots;


import com.github.easysourcing.messages.aggregates.Aggregate;
import com.github.easysourcing.serdes.CustomJsonSerde;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;

import java.util.Set;
import java.util.concurrent.ConcurrentMap;

@Slf4j
public class SnapshotStream {

  private final Set<String> topics;
  private final ConcurrentMap<Class<?>, SnapshotHandler> snapshotHandlers;
  private final boolean frequentCommits;

  public SnapshotStream(Set<String> topics, ConcurrentMap<Class<?>, SnapshotHandler> snapshotHandlers, boolean frequentCommits) {
    this.topics = topics;
    this.snapshotHandlers = snapshotHandlers;
    this.frequentCommits = frequentCommits;
  }

  public void buildStream(StreamsBuilder builder) {
    // --> Snapshots
    KStream<String, Aggregate> snapshotKStream = builder.stream(topics,
        Consumed.with(Serdes.String(), new CustomJsonSerde<>(Aggregate.class).noTypeInfo()))
        .filter((key, aggregate) -> key != null)
        .filter((key, aggregate) -> aggregate != null)
        .filter((key, aggregate) -> aggregate.getPayload() != null)
        .filter((key, aggregate) -> aggregate.getTopicInfo() != null)
        .filter((key, aggregate) -> aggregate.getAggregateId() != null);

    // Snapshots --> Void
    snapshotKStream
        .transformValues(() -> new SnapshotTransformer(snapshotHandlers, frequentCommits));
  }

}
