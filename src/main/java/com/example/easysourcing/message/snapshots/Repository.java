package com.example.easysourcing.message.snapshots;

import com.example.easysourcing.message.snapshots.Snapshotable;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.stereotype.Component;

@Component
public class Repository {

  @Autowired
  private StreamsBuilderFactoryBean streamsBuilderFactoryBean;


  public <T extends Snapshotable> T get(String id, Class<T> tClass) {
    ReadOnlyKeyValueStore<String, T> keyValueStore = streamsBuilderFactoryBean.getKafkaStreams().store("snapshots", QueryableStoreTypes.keyValueStore());
    return keyValueStore.get(id);
  }

}