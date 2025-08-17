package org.jetlinks.community.device.service;

import lombok.extern.slf4j.Slf4j;
import org.hswebframework.web.api.crud.entity.TreeSupportEntity;
import org.hswebframework.web.crud.service.GenericReactiveTreeSupportCrudService;
import org.hswebframework.web.id.IDGenerator;
import org.jetlinks.community.device.entity.DeviceLaboratoryEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@Slf4j
public class DeviceLaboratoryService extends GenericReactiveTreeSupportCrudService<DeviceLaboratoryEntity, String> {

    @Override
    public IDGenerator<String> getIDGenerator() {
        return IDGenerator.MD5;
    }

    @Override
    public void setChildren(DeviceLaboratoryEntity entity, List<DeviceLaboratoryEntity> children) {
        entity.setChildren(children);
    }

    /**
     * 获取实验室树形结构
     */
    public Flux<DeviceLaboratoryEntity> getLaboratoryTree() {
        return this
            .createQuery()
            .fetch()
            .collectList()
            .flatMapMany(all -> Flux.fromIterable(TreeSupportEntity.list2tree(all, DeviceLaboratoryEntity::setChildren)));
    }

    /**
     * 根据查询条件获取实验室树形结构
     */
    public Flux<DeviceLaboratoryEntity> getLaboratoryTreeByQuery(Mono<org.hswebframework.web.api.crud.entity.QueryParamEntity> query) {
        return this
            .query(query)
            .collectList()
            .flatMapMany(all -> Flux.fromIterable(TreeSupportEntity.list2tree(all, DeviceLaboratoryEntity::setChildren)));
    }

    /**
     * 更新实验室设备数量
     */
    public Mono<Void> updateDeviceCount(String laboratoryId, int count) {
        return this
            .createUpdate()
            .set(DeviceLaboratoryEntity::getDeviceCount, count)
            .where(DeviceLaboratoryEntity::getId, laboratoryId)
            .execute()
            .then();
    }
}
