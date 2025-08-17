package org.jetlinks.community.device.relation;

import lombok.AllArgsConstructor;
import org.jetlinks.core.things.relation.ObjectType;
import org.jetlinks.core.things.relation.PropertyOperation;
import org.jetlinks.community.device.entity.DeviceGroupEntity;
import org.jetlinks.community.device.service.DeviceGroupService;
import org.jetlinks.community.relation.RelationObjectProvider;
import org.jetlinks.community.relation.impl.SimpleObjectType;
import org.jetlinks.community.relation.impl.property.PropertyOperationStrategy;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class GroupObjectProvider implements RelationObjectProvider {

    public static final String TYPE_GROUP = "group";

    private final DeviceGroupService groupService;

    @Override
    public String getTypeId() {
        return TYPE_GROUP;
    }

    @Override
    public Mono<ObjectType> getType() {
        return Mono.just(new SimpleObjectType(getTypeId(), "分组", "设备分组"));
    }

    @Override
    public PropertyOperation properties(String id) {
        return PropertyOperationStrategy.simple(
            groupService.findById(id),
            strategy -> strategy
                .addMapper("id", DeviceGroupEntity::getId)
                .addMapper("name", DeviceGroupEntity::getName)
        );
    }
} 