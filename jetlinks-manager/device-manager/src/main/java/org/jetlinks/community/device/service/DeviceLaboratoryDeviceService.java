package org.jetlinks.community.device.service;

import lombok.extern.slf4j.Slf4j;
import org.hswebframework.web.crud.service.GenericReactiveCrudService;
import org.jetlinks.community.device.entity.DeviceLaboratoryDeviceEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.LinkedHashSet;
import java.util.Set;

@Service
@Slf4j
public class DeviceLaboratoryDeviceService extends GenericReactiveCrudService<DeviceLaboratoryDeviceEntity, String> {

    /**
     * 根据实验室ID获取设备列表
     */
    public Flux<DeviceLaboratoryDeviceEntity> getDevicesByLaboratoryId(String laboratoryId) {
        return this
            .createQuery()
            .where(DeviceLaboratoryDeviceEntity::getLaboratoryId, laboratoryId)
            .where(DeviceLaboratoryDeviceEntity::getStatus, 1)
            .fetch();
    }

    /**
     * 分配设备到实验室（幂等：同一设备不会产生重复记录）
     */
    public Mono<Void> assignDevices(String laboratoryId, List<String> deviceIds) {
        if (deviceIds == null || deviceIds.isEmpty()) {
            return Mono.empty();
        }
        // 去重传入的设备ID，保持插入顺序
        Set<String> uniqueIds = new LinkedHashSet<>(deviceIds);

        return Flux.fromIterable(uniqueIds)
            .flatMap(deviceId ->
                // 先检查是否已存在映射
                this.createQuery()
                    .where(DeviceLaboratoryDeviceEntity::getLaboratoryId, laboratoryId)
                    .and(DeviceLaboratoryDeviceEntity::getDeviceId, deviceId)
                    .fetch()
                    .next()
                    .flatMap(existing -> {
                        // 若存在且被标记为移除（status!=1），则恢复为1；否则不做操作
                        if (existing.getStatus() != null && existing.getStatus() == 1) {
                            return Mono.empty();
                        }
                        return this.createUpdate()
                            .set(DeviceLaboratoryDeviceEntity::getStatus, 1)
                            .where(DeviceLaboratoryDeviceEntity::getLaboratoryId, laboratoryId)
                            .and(DeviceLaboratoryDeviceEntity::getDeviceId, deviceId)
                            .execute()
                            .then();
                    })
                    .switchIfEmpty(
                        // 不存在则新增
                        Mono.defer(() -> {
                            DeviceLaboratoryDeviceEntity entity = new DeviceLaboratoryDeviceEntity();
                            entity.setLaboratoryId(laboratoryId);
                            entity.setDeviceId(deviceId);
                            entity.setStatus(1);
                            return this.insert(entity).then();
                        })
                    )
            )
            .then();
    }

    /**
     * 从实验室移除设备（删除所有匹配记录）
     */
    public Mono<Void> removeDevices(String laboratoryId, List<String> deviceIds) {
        if (deviceIds == null || deviceIds.isEmpty()) {
            return Mono.empty();
        }
        return this
            .createDelete()
            .where(DeviceLaboratoryDeviceEntity::getLaboratoryId, laboratoryId)
            .in(DeviceLaboratoryDeviceEntity::getDeviceId, deviceIds)
            .execute()
            .then();
    }

    /**
     * 获取实验室下的设备数量（按设备去重统计）
     */
    public Mono<Long> getDeviceCount(String laboratoryId) {
        return this
            .createQuery()
            .where(DeviceLaboratoryDeviceEntity::getLaboratoryId, laboratoryId)
            .where(DeviceLaboratoryDeviceEntity::getStatus, 1)
            .fetch()
            .map(DeviceLaboratoryDeviceEntity::getDeviceId)
            .distinct()
            .count();
    }
}
