package br.com.s4.s4extraction.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing an access log record as returned by the remote API.
 * Field names are mapped from the snake_case JSON keys using @JsonProperty.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccessLogs {

    private Long id;
    private Long time;
    private Integer event;

    @JsonProperty("device_id")
    private Long deviceId;

    @JsonProperty("identifier_id")
    private Long identifierId;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("portal_id")
    private Long portalId;

    @JsonProperty("identification_rule_id")
    private Long identificationRuleId;

    @JsonProperty("card_value")
    private Long cardValue;

    @JsonProperty("qrcode_value")
    private String qrcodeValue;

    @JsonProperty("pin_value")
    private String pinValue;

    @JsonProperty("log_type_id")
    private Integer logTypeId;

    @JsonProperty("uhf_tag")
    private String uhfTag;
}
