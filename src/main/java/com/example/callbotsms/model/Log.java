package com.example.callbotsms.model;

import com.im.sso.model.enums.ActionStatus;
import com.im.sso.model.enums.ActionType;
import com.im.sso.model.enums.EntityType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Log {
    @Id
    @GeneratedValue(generator = "uuid2", strategy = GenerationType.IDENTITY)
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private UUID id;
    private UUID tenantId;
    private UUID createdBy;
    private UUID entityId;
    @Column(length = 10485760)
    private String actionData;

    @Column(length = 10485760)
    private String actionFailureDetails;
    @CreationTimestamp
    @Column(updatable = false)
    private Date createdAt;
}
