package org.jetlinks.community.device.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.hswebframework.ezorm.rdb.mapping.annotation.ColumnType;
import org.hswebframework.ezorm.rdb.mapping.annotation.Comment;
import org.hswebframework.ezorm.rdb.mapping.annotation.DefaultValue;
import org.hswebframework.web.api.crud.entity.GenericTreeSortSupportEntity;
import org.hswebframework.web.api.crud.entity.RecordCreationEntity;
import org.hswebframework.web.crud.annotation.EnableEntityEvent;
import org.hswebframework.web.crud.generator.Generators;
import org.hswebframework.web.validator.CreateGroup;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.sql.JDBCType;
import java.util.List;

@Getter
@Setter
@Table(name = "dev_laboratory")
@Comment("实验室信息表")
@EnableEntityEvent
public class DeviceLaboratoryEntity extends GenericTreeSortSupportEntity<String> implements RecordCreationEntity {

    @Override
    @Id
    @Column(length = 64, updatable = false)
    @GeneratedValue(generator = Generators.SNOW_FLAKE)
    @NotBlank(message = "ID不能为空", groups = CreateGroup.class)
    @Pattern(regexp = "^[0-9a-zA-Z_\\-|]+$", message = "ID只能由数字,字母,下划线和中划线组成", groups = CreateGroup.class)
    public String getId() {
        return super.getId();
    }

    @Schema(description = "实验室编码")
    @Column(nullable = false, length = 64)
    @DefaultValue(generator = Generators.CURRENT_TIME)
    private String code;

    @Schema(description = "实验室名称")
    @Column(nullable = false)
    @NotBlank(message = "实验室名称不能为空", groups = CreateGroup.class)
    private String name;

    @Schema(description = "实验室描述")
    @Column
    private String describe;

    @Schema(description = "实验室地址")
    @Column
    private String address;

    @Schema(description = "负责人")
    @Column
    private String manager;

    @Schema(description = "联系电话")
    @Column
    private String phone;

    @Schema(description = "邮箱")
    @Column
    private String email;

    @Schema(description = "状态")
    @Column
    @DefaultValue("1")
    private Integer status;

    @Schema(description = "子节点")
    private List<DeviceLaboratoryEntity> children;

    @Schema(description = "设备数量")
    @Column
    @DefaultValue("0")
    private Integer deviceCount;

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
