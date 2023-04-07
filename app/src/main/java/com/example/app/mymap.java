package com.example.app;

import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.Row;

public class mymap implements MapFunction<Row, String> {

    @Override
    public String call(Row value) throws Exception {
        return value.getString(0);
    }
}
