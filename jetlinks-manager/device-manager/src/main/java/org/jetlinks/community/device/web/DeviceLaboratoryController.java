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
import java.util.Map;

@RestController
@RequestMapping("/device/laboratory")
@Slf4j
@Tag(name = "实验室管理")
@AllArgsConstructor
@Resource(id="device-laboratory",name = "实验室管理")
public class DeviceLaboratoryController implements ReactiveServiceCrudController<DeviceLaboratoryEntity,String> {

    // 请求DTO类
    public static class DeviceAssignmentRequest {
        private List<String> deviceIds;
        
        public List<String> getDeviceIds() {
            return deviceIds;
        }
        
        public void setDeviceIds(List<String> deviceIds) {
            this.deviceIds = deviceIds;
        }
    }

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
     * 重写接口默认方法，避免路径冲突
     */
    @Override
    @PostMapping
    @QueryNoPagingOperation(summary = "新增实验室(默认)")
    @Authorize(merge = false)
    public Mono<DeviceLaboratoryEntity> add(@RequestBody Mono<DeviceLaboratoryEntity> entity) {
        // 重定向到自定义方法
        return entity.flatMap(this::createLaboratory);
    }

    /**
     * 重写接口默认update方法，避免路径冲突
     */
    @Override
    @PutMapping("/{id}")
    @QueryNoPagingOperation(summary = "更新实验室(默认)")
    @Authorize(merge = false)
    public Mono<Boolean> update(@PathVariable String id, @RequestBody Mono<DeviceLaboratoryEntity> entity) {
        // 重定向到自定义方法
        return entity.flatMap(entityData -> this.updateLaboratory(id, entityData))
            .map(result -> result > 0);
    }

    /**
     * 重写接口默认delete方法，避免路径冲突
     */
    @Override
    @DeleteMapping("/{id}")
    @QueryNoPagingOperation(summary = "删除实验室(默认)")
    @Authorize(merge = false)
    public Mono<DeviceLaboratoryEntity> delete(@PathVariable String id) {
        // 重定向到自定义方法
        return this.deleteLaboratory(id)
            .flatMap(result -> result > 0 ? 
                laboratoryService.findById(id) : 
                Mono.empty());
    }

    /**
     * 重写接口默认getById方法，避免路径冲突
     */
    @Override
    @GetMapping("/{id}")
    @QueryNoPagingOperation(summary = "获取实验室详情(默认)")
    @Authorize(merge = false)
    public Mono<DeviceLaboratoryEntity> getById(@PathVariable String id) {
        // 重定向到自定义方法
        return this.getLaboratoryById(id);
    }

    /**
     * 新增实验室（自动生成编码）
     */
    @PostMapping("/create")
    @QueryNoPagingOperation(summary = "新增实验室")
    @Authorize(merge = false)
    public Mono<DeviceLaboratoryEntity> createLaboratory(@RequestBody DeviceLaboratoryEntity entity) {
        return laboratoryService.insertWithCode(entity);
    }

    /**
     * 更新实验室（不修改编码）
     */
    @PutMapping("/update/{id}")
    @QueryNoPagingOperation(summary = "更新实验室")
    @Authorize(merge = false)
    public Mono<Integer> updateLaboratory(@PathVariable String id, @RequestBody DeviceLaboratoryEntity entity) {
        return laboratoryService.updateByIdWithoutCode(id, entity);
    }

    /**
     * 删除实验室
     */
    @DeleteMapping("/delete/{id}")
    @QueryNoPagingOperation(summary = "删除实验室")
    @Authorize(merge = false)
    public Mono<Integer> deleteLaboratory(@PathVariable String id) {
        return laboratoryService.deleteById(id);
    }

    /**
     * 获取实验室详情
     */
    @GetMapping("/detail/{id}")
    @QueryNoPagingOperation(summary = "获取实验室详情")
    @Authorize(merge = false)
    public Mono<DeviceLaboratoryEntity> getLaboratoryById(@PathVariable String id) {
        return laboratoryService.findById(id);
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
    public Mono<Void> assignDevices(@PathVariable String laboratoryId, @RequestBody DeviceAssignmentRequest request) {
        if (request.getDeviceIds() == null || request.getDeviceIds().isEmpty()) {
            return Mono.error(new IllegalArgumentException("设备ID列表不能为空"));
        }
        return laboratoryDeviceService
            .assignDevices(laboratoryId, request.getDeviceIds())
            .then(
                laboratoryDeviceService
                    .getDeviceCount(laboratoryId)
                    .flatMap(count -> laboratoryService
                        .createUpdate()
                        .set(DeviceLaboratoryEntity::getDeviceCount, count.intValue())
                        .where(DeviceLaboratoryEntity::getId, laboratoryId)
                        .execute()
                        .then())
            );
    }

    /**
     * 从实验室移除设备
     */
    @DeleteMapping("/{laboratoryId}/devices")
    @QueryNoPagingOperation(summary = "从实验室移除设备")
    @Authorize(merge = false)
    public Mono<Void> removeDevices(@PathVariable String laboratoryId, @RequestBody DeviceAssignmentRequest request) {
        if (request.getDeviceIds() == null || request.getDeviceIds().isEmpty()) {
            return Mono.error(new IllegalArgumentException("设备ID列表不能为空"));
        }
        return laboratoryDeviceService
            .removeDevices(laboratoryId, request.getDeviceIds())
            .then(
                laboratoryDeviceService
                    .getDeviceCount(laboratoryId)
                    .flatMap(count -> laboratoryService
                        .createUpdate()
                        .set(DeviceLaboratoryEntity::getDeviceCount, count.intValue())
                        .where(DeviceLaboratoryEntity::getId, laboratoryId)
                        .execute()
                        .then())
            );
    }
}
