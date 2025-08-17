package org.jetlinks.community.device.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.hswebframework.web.api.crud.entity.QueryNoPagingOperation;
import org.hswebframework.web.api.crud.entity.QueryParamEntity;
import org.hswebframework.web.api.crud.entity.TreeSupportEntity;
import org.hswebframework.web.authorization.annotation.Authorize;
import org.hswebframework.web.authorization.annotation.QueryAction;
import org.hswebframework.web.authorization.annotation.Resource;
import org.hswebframework.web.crud.web.reactive.ReactiveServiceCrudController;
import org.jetlinks.community.device.entity.DeviceGroupEntity;
import org.jetlinks.community.device.entity.DeviceInstanceEntity;
import org.jetlinks.community.device.relation.GroupObjectProvider;
import org.jetlinks.community.device.service.DeviceGroupService;
import org.jetlinks.community.device.service.LocalDeviceInstanceService;
import org.jetlinks.community.relation.entity.RelatedEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@AllArgsConstructor
@RestController
@RequestMapping("/device/group")
@Resource(id = "device-group", name = "设备分组")
@Authorize
@Tag(name = "设备分组管理")
public class DeviceGroupController implements ReactiveServiceCrudController<DeviceGroupEntity, String> {

    private final DeviceGroupService service;

    private final LocalDeviceInstanceService instanceService;

    private final org.hswebframework.ezorm.rdb.mapping.ReactiveRepository<RelatedEntity, String> relatedRepository;

    @Override
    public DeviceGroupService getService() {
        return service;
    }

    @GetMapping("/_tree")
    @QueryNoPagingOperation(summary = "获取分组(树结构)")
    @Authorize(merge = false)
    public Flux<DeviceGroupEntity> getTree(@Parameter(hidden = true) QueryParamEntity query) {
        return this
            .service
            .createQuery()
            .setParam(query)
            .fetch()
            .collectList()
            .flatMapMany(list -> Flux.fromIterable(TreeSupportEntity.list2tree(list, DeviceGroupEntity::setChildren)));
    }

    @GetMapping("/{groupId}/devices")
    @QueryAction
    @Operation(summary = "按分组查询设备列表")
    public Flux<DeviceInstanceEntity> getDevicesByGroup(@PathVariable String groupId,
                                                        @Parameter(hidden = true) QueryParamEntity query) {
        return relatedRepository
            .createQuery()
            .where(RelatedEntity::getRelatedType, GroupObjectProvider.TYPE_GROUP)
            .and(RelatedEntity::getRelation, GroupObjectProvider.TYPE_GROUP)
            .and(RelatedEntity::getRelatedId, groupId)
            .select(RelatedEntity::getObjectId)
            .fetch()
            .collectList()
            .filter(ids -> !ids.isEmpty())
            .flatMapMany(ids -> instanceService
                .createQuery()
                .setParam(query)
                .in(DeviceInstanceEntity::getId, ids)
                .fetch());
    }
} 