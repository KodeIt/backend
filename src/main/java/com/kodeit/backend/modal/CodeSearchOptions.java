package com.kodeit.backend.modal;

import com.kodeit.backend.enums.Language;
import com.kodeit.backend.enums.SortBy;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Sort;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CodeSearchOptions {

    private List<Language> languages;
    private Sort.Direction sortOrder;
    private SortBy sortBy;
    private Integer pageIndex;
    private Integer pageSize;
    private String title;

}
