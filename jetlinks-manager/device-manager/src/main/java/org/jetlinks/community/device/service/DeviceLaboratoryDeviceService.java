package org.jetlinks.community.device.service;

import lombok.extern.slf4j.Slf4j;
import org.hswebframework.web.crud.service.GenericReactiveCrudService;
import org.jetlinks.community.device.entity.DeviceLaboratoryDeviceEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

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
     * 分配设备到实验室
     */
    public Mono<Void> assignDevices(String laboratoryId, List<String> deviceIds) {
        return Flux.fromIterable(deviceIds)
            .flatMap(deviceId -> {
                DeviceLaboratoryDeviceEntity entity = new DeviceLaboratoryDeviceEntity();
                entity.setLaboratoryId(laboratoryId);
                entity.setDeviceId(deviceId);
                entity.setStatus(1);
                return this.insert(entity);
            })
            .then();
    }

    /**
     * 从实验室移除设备
     */
    public Mono<Void> removeDevices(String laboratoryId, List<String> deviceIds) {
        return this
            .createDelete()
            .where(DeviceLaboratoryDeviceEntity::getLaboratoryId, laboratoryId)
            .where(DeviceLaboratoryDeviceEntity::getDeviceId, deviceIds)
            .execute()
            .then();
    }

    /**
     * 获取实验室下的设备数量
     */
    public Mono<Long> getDeviceCount(String laboratoryId) {
        return this
            .createQuery()
            .where(DeviceLaboratoryDeviceEntity::getLaboratoryId, laboratoryId)
            .where(DeviceLaboratoryDeviceEntity::getStatus, 1)
            .count()
            .map(Long::valueOf);
    }
}
