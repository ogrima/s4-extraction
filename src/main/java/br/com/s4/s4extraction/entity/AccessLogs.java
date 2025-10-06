package br.com.s4.s4extraction.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing an access log record as returned by the remote API.
 * Maps to a local table t_access_logs (if/when created).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccessLogs {

    private Long id;
    private Long time;
    private Integer event;
    private Long deviceId;
    private Long identifierId;
    private Long userId;
    private Long portalId;
    private Long identificationRuleId;
    private Long cardValue;
    private String qrcodeValue;
    private String pinValue;
    private Integer logTypeId;
    private String uhfTag;

}
