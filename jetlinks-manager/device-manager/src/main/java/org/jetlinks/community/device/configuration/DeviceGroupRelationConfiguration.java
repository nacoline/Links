package org.jetlinks.community.device.configuration;

import lombok.AllArgsConstructor;
import org.hswebframework.ezorm.rdb.mapping.ReactiveRepository;
import org.hswebframework.ezorm.rdb.mapping.ReactiveUpdate;
import org.jetlinks.community.relation.configuration.RelationProperties;
import org.jetlinks.community.relation.entity.RelationEntity;
import org.jetlinks.community.relation.RelationObjectProvider;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.HashMap;

@Configuration
@AllArgsConstructor
public class DeviceGroupRelationConfiguration implements ApplicationListener<ApplicationReadyEvent> {

    private final ReactiveRepository<RelationEntity, String> relationRepository;

    private final RelationProperties relationProperties;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        // 定义: 设备(device) --(group)--> 分组(group)
        RelationEntity entity = new RelationEntity();
        entity.setObjectType(RelationObjectProvider.TYPE_DEVICE);
        entity.setObjectTypeName("设备");
        entity.setRelation("group");
        entity.setName("所属分组");
        entity.setTargetType("group");
        entity.setTargetTypeName("分组");
        entity.generateId();

        Mono<Void> initRelation = relationRepository
            .findById(entity.getId())
            .switchIfEmpty(relationRepository.insert(Mono.just(entity)).then(Mono.empty()))
            .then();

        // 配置可关联类型（追加 device->group）
        relationProperties.getRelatable().compute(RelationObjectProvider.TYPE_DEVICE, (k, v) -> {
            if (v == null) return new java.util.ArrayList<>(Collections.singletonList("group"));
            if (!v.contains("group")) v.add("group");
            return v;
        });

        initRelation.subscribe();
    }
} 