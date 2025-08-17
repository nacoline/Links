package org.jetlinks.community.device.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.hswebframework.ezorm.rdb.mapping.annotation.Comment;
import org.hswebframework.ezorm.rdb.mapping.annotation.DefaultValue;
import org.hswebframework.web.api.crud.entity.GenericEntity;
import org.hswebframework.web.api.crud.entity.RecordCreationEntity;
import org.hswebframework.web.crud.annotation.EnableEntityEvent;
import org.hswebframework.web.crud.generator.Generators;
import org.hswebframework.web.validator.CreateGroup;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Table(name = "dev_laboratory_device")
@Comment("实验室设备关联表")
@EnableEntityEvent
public class DeviceLaboratoryDeviceEntity extends GenericEntity<String> implements RecordCreationEntity {

    @Override
    @Id
    @Column(length = 64, updatable = false)
    @GeneratedValue(generator = Generators.SNOW_FLAKE)
    @NotBlank(message = "ID不能为空", groups = CreateGroup.class)
    public String getId() {
        return super.getId();
    }

    @Schema(description = "实验室ID")
    @Column(nullable = false, length = 64)
    @NotBlank(message = "实验室ID不能为空", groups = CreateGroup.class)
    private String laboratoryId;

    @Schema(description = "设备ID")
    @Column(nullable = false, length = 64)
    @NotBlank(message = "设备ID不能为空", groups = CreateGroup.class)
    private String deviceId;

    @Schema(description = "分配时间")
    @Column
    @DefaultValue(generator = Generators.CURRENT_TIME)
    private Long assignTime;

    @Schema(description = "状态")
    @Column
    @DefaultValue("1")
    private Integer status;

    @Column(updatable = false)
    @Schema(
        description = "创建者ID(只读)"
        , accessMode = Schema.AccessMode.READ_ONLY
    )
    private String creatorId;

    @Column(updatable = false)
    @DefaultValue(generator = Generators.CURRENT_TIME)
    @Schema(
        description = "创建时间(只读)"
        , accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long createTime;
}
