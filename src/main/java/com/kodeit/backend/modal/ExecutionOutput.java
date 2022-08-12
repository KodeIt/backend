package com.kodeit.backend.modal;

import com.kodeit.backend.enums.ExecutionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExecutionOutput {

    private ExecutionStatus executionStatus;
    private String output;

}
