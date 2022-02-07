package com.syntifi.ori.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
