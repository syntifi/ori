package com.syntifi.ori.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Ori DTO for AML rules data
 * 
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * 
 * @since 0.1.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AMLRulesDTO {
    private double structuringOverTimeScore;

    private double unusualOutgoingVolumeScore;

    private double unusualBehaviorScore;

    private double flowThroughScore;
}
