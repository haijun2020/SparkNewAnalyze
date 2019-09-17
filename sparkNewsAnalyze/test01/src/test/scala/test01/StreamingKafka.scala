package test01

import java.sql.DriverManager


import org.apache.spark.sql.{ForeachWriter, Row}
import kafka.serializer.StringDecoder
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.streaming.ProcessingTime
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

import scala.collection.mutable.ListBuffer

/**
  * Created by Administrator on 2017/10/15.
  */
object StreamingKafka {

  def main(args: Array[String]): Unit = {

    val spark  = SparkSession.builder()
      .master("local[2]")
      .appName("streaming").getOrCreate()
    val sc =spark.sparkContext
    val ssc = new StreamingContext(sc, Seconds(5))

    // Create direct kafka stream with brokers and topics
    val topicsSet =Set("weblogs")
    val kafkaParams = Map[String, String]("metadata.broker.list" -> "DianXin103:9092")
    val kafkaStream = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](
      ssc, kafkaParams, topicsSet)
    val lines = kafkaStream.map(x => x._2).map{x=>
      val strArr =x.split(",")
      strArr(2)

    }
    val words = lines.flatMap(_.split(" "))
   val wordCounts = words.map(x => (x, 1L)).reduceByKey(_ + _)
//   if(words.->()!=0)
//     {
//       words.->()=words.->()+1
//     }
    wordCounts.print()
    wordCounts.foreachRDD(rdd => rdd.foreachPartition(line => {
      Class.forName("com.mysql.jdbc.Driver")
      val conn = DriverManager
          .getConnection("jdbc:mysql://DianXin103:3306/test?user=root&useUnicode=true&characterEncoding=utf8&password=123456")

////      val con =DriverManager
////        .getConnection("jdbc:mysql://DianXin103:3306/test","root","123456")

      var statement=conn.createStatement()

     try{
       for(row <- line){
          val querySql="select 1 from webCount where titleName ='"+row._1+"'"
          val insertSql = "insert into webCount(titleName,count)values('"+row._1+"',"+row._2+")"

          val updateSql = "update webCount set count=count+"+row._2+" where titleName = '"+row._1+"'"
         // val s ="select titleName, sum(count) from webCount group by titleName;"
          var resultSet=statement.executeQuery(querySql)
         if(resultSet.next())
           {
             conn.prepareStatement(updateSql).executeUpdate()
           }else{
           conn.prepareStatement(insertSql).execute()
         }



     //  con.prepareStatement(s).executeUpdate()




       }
      }finally {
        conn.close()
       statement.close()
      }
    }))




   ssc.start()
    ssc.awaitTermination()



  }
}
