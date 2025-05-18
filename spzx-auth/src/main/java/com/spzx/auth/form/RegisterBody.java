package com.spzx.auth.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户注册对象
 *
 * @author spzx
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterBody extends LoginBody
{
    @Schema(description = "昵称")
    private String nickName;

    @Schema(description = "验证码")
    private String code;
}
