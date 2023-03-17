package com.tanhua.model.vo;

import com.tanhua.model.dto.YearDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardYearVo {


    /**
     * 今年
     */
    private YearDto thisYear;

    /**
     * 去年
     */
    private YearDto lastYear;
}
