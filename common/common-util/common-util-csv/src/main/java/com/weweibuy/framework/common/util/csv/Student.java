package com.weweibuy.framework.common.util.csv;

import com.weweibuy.framework.common.util.csv.annotation.CsvProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : Knight
 * @date : 2021/1/14 11:55 上午
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Student {

    @CsvProperty(name = "年龄")
    private Integer age;

    @CsvProperty(name = "姓名", order = 10)
    private String name;

    @CsvProperty(name = "学校", order = 1)
    private String school;

}
