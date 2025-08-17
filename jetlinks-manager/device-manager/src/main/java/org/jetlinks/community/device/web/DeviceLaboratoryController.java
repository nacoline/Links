package org.jetlinks.community.device.web;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hswebframework.web.api.crud.entity.QueryNoPagingOperation;
import org.hswebframework.web.api.crud.entity.QueryParamEntity;
import org.hswebframework.web.api.crud.entity.TreeSupportEntity;
import org.hswebframework.web.authorization.annotation.Authorize;
import org.hswebframework.web.authorization.annotation.Resource;
import org.hswebframework.web.crud.service.ReactiveCrudService;
import org.hswebframework.web.crud.web.reactive.ReactiveServiceCrudController;
import org.jetlinks.community.device.entity.DeviceLaboratoryEntity;
import org.jetlinks.community.device.entity.DeviceLaboratoryDeviceEntity;
import org.jetlinks.community.device.service.DeviceLaboratoryService;
import org.jetlinks.community.device.service.DeviceLaboratoryDeviceService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/device/laboratory")
@Slf4j
@Tag(name = "实验室管理")
@AllArgsConstructor
@Resource(id="device-laboratory",name = "实验室管理")
public class DeviceLaboratoryController implements ReactiveServiceCrudController<DeviceLaboratoryEntity,String> {

    private final DeviceLaboratoryService laboratoryService;
    private final DeviceLaboratoryDeviceService laboratoryDeviceService;

    @GetMapping
    @QueryNoPagingOperation(summary = "获取全部实验室")
    @Authorize(merge = false)
    public Flux<DeviceLaboratoryEntity> getAllLaboratory(@Parameter(hidden = true) QueryParamEntity query) {
        return this
            .laboratoryService
            .createQuery()
            .setParam(query)
            .fetch();
    }

    @GetMapping("/_tree")
    @QueryNoPagingOperation(summary = "获取全部实验室(树结构)")
    @Authorize(merge = false)
    public Flux<DeviceLaboratoryEntity> getAllLaboratoryTree(@Parameter(hidden = true) QueryParamEntity query) {
        return this
            .laboratoryService
            .createQuery()
            .setParam(query)
            .fetch()
            .collectList()
            .flatMapMany(all-> Flux.fromIterable(TreeSupportEntity.list2tree(all, DeviceLaboratoryEntity::setChildren)));
    }

    @PostMapping("/_tree")
    @QueryNoPagingOperation(summary = "根据查询条件获取实验室(树结构)")
    @Authorize(merge = false)
    public Flux<DeviceLaboratoryEntity> getAllLaboratoryTreeByQueryParam(@RequestBody Mono<QueryParamEntity> query) {
        return this
            .laboratoryService
            .query(query)
            .collectList()
            .flatMapMany(all-> Flux.fromIterable(TreeSupportEntity.list2tree(all, DeviceLaboratoryEntity::setChildren)));
    }

    @Override
    public ReactiveCrudService<DeviceLaboratoryEntity, String> getService() {
        return laboratoryService;
    }

    /**
     * 获取实验室下的设备列表
     */
    @GetMapping("/{laboratoryId}/devices")
    @QueryNoPagingOperation(summary = "获取实验室下的设备列表")
    @Authorize(merge = false)
    public Flux<DeviceLaboratoryDeviceEntity> getDevicesByLaboratoryId(@PathVariable String laboratoryId) {
        return laboratoryDeviceService.getDevicesByLaboratoryId(laboratoryId);
    }

    /**
     * 分配设备到实验室
     */
    @PostMapping("/{laboratoryId}/devices")
    @QueryNoPagingOperation(summary = "分配设备到实验室")
    @Authorize(merge = false)
    public Mono<Void> assignDevices(@PathVariable String laboratoryId, @RequestBody List<String> deviceIds) {
        return laboratoryDeviceService.assignDevices(laboratoryId, deviceIds);
    }

    /**
     * 从实验室移除设备
     */
    @DeleteMapping("/{laboratoryId}/devices")
    @QueryNoPagingOperation(summary = "从实验室移除设备")
    @Authorize(merge = false)
    public Mono<Void> removeDevices(@PathVariable String laboratoryId, @RequestBody List<String> deviceIds) {
        return laboratoryDeviceService.removeDevices(laboratoryId, deviceIds);
    }
}
