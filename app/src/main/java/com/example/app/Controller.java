package com.example.app;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import scala.Tuple2;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/")
public class Controller {

    @Autowired
    private JavaSparkContext javaSparkContext;

    @Autowired
    private SparkSession sparkSession;

    @RequestMapping(path = "/health",method = RequestMethod.GET)
    public ResponseEntity<String> healthcheck() {
        return new ResponseEntity<>("{ \"status\": \"ok\"}", HttpStatus.OK);
    }

    @RequestMapping(path="/spark", method = RequestMethod.GET)
    public ResponseEntity<List<String>> getString(){
        List<String> s = new ArrayList<>();
        s.add("x");
        s.add("x");
        s.add("b");
        s.add("x");
        s.add("x");
        s.add("b");
        s.add("c");
        s.add("d");
        s.add("x");
        s.add("x");
        JavaRDD<String> rdd = javaSparkContext.parallelize(s);
        List<String> list =rdd.map((Function<String, Tuple2<String,Integer>>)x-> new Tuple2<>(x,1) )
                .keyBy((Function<Tuple2<String, Integer>, String>) k -> k._1 )
                .reduceByKey((Function2<Tuple2<String, Integer>, Tuple2<String, Integer>, Tuple2<String, Integer>>) (x,y)->{
                    return new Tuple2<>(x._1,x._2+y._2);
                })
                .filter((Function<Tuple2<String, Tuple2<String, Integer>>, Boolean>) x-> x._2._2>1)
                .map((Function<Tuple2<String, Tuple2<String, Integer>>, String>) k->k._1 ).collect();
        return new ResponseEntity<>(list,HttpStatus.OK);

    }

    @RequestMapping(path="/spark-s3", method = RequestMethod.GET)
    public ResponseEntity<List<String>> getS3(){
        String path = String.format("%s%s%s", "s3a://siesta/", "synthetic_pos", "/count.parquet/");


        List<String> s = sparkSession.read().parquet(path)
                .select("eventA")
                .distinct().toJavaRDD().mapPartitions((FlatMapFunction<Iterator<Row>, String>) row->{
                    List<String> list = new ArrayList<>();
                    while(row.hasNext()){
                        list.add(row.next().getString(0));
                    }
                    return list.iterator();
                }).collect();
//        System.out.println(df.count());

//        List<String> s = df
//                .map(new mymap(), Encoders.STRING())
//                .collectAsList().stream().map(x->x.getString(0)).collect(Collectors.toList()); //.toJavaRDD().collect();
                //.
                //.map((MapFunction<Row, String>) row -> row.getString(0) )
                //.collect();
        return new ResponseEntity<>(s,HttpStatus.OK);
    }


}

