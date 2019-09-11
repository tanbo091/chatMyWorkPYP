package com.work.fyc.MyWork.entity;

import com.google.gson.annotations.SerializedName;

/**
 * 服务器返回的 json 数据，可以被 gson 直接转换成 Class 类
 * 例如 ： 服务器返回的 { "result":"123" }
 * 会转换成:
 * ResultEntity result = new  ResultEntity()
 * result.Result = "123"
 * 你会得到 result
 */
public class ResultEntity {
    @SerializedName("result")
    public String Result;
}
