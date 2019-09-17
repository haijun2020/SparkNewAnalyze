package test01

import java.sql.DriverManager

import kafka.serializer.StringDecoder
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import kafka.serializer.StringDecoder
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

object StreamingKafka8 {


  def main(args: Array[String]): Unit = {

//    val spark  = SparkSession.builder()
//      .master("local[2]")
//      .appName("streaming").getOrCreate()
//
//    val sc =spark.sparkContext
//    val ssc = new StreamingContext(sc, Seconds(5))
//
//    // Create direct kafka stream with brokers and topics
//    val topicsSet =Set("weblogs")
//    val kafkaParams = Map[String, String]("metadata.broker.list" -> "DianXin101:9092")
//    val kafkaStream = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](
//      ssc, kafkaParams, topicsSet)



//    val lines: DStream[String] = kafkaStream.map(x => x._2)
//      lines.print()


//    val flag=0
//          for(  x <- lines)
//            {
//              if(x == ',')
//                {
//                  flag +=1
//
//                }
////            }
//    val words: DStream[String] = lines.flatMap(_.split(","(8)))
//  words.print(3)
 //   val wordCounts: Seq[DStream[String]] = List(words)
//
//    val it = Iterator(wordCounts)
//    while(it.hasNext)
//      {
//        println(it.next())
//      }
//val wordCountss: DStream[(String, Long)] = words.map(x => (x, 1L)).reduceByKey(_ + _)
//    wordCountss.print()
//  wordCounts.foreachRDD(rdd => rdd.foreachPartition(line => {
//    Class.forName("com.mysql.jdbc.Driver")
//    val conn = DriverManager
//      .getConnection("jdbc:mysql://node5:3306/test","root","1234")
//    try{
//      for(row <- line){
//        if(line ==3 or (line-3)%6==0)
//        {
//          val sql = "insert into webCount(titleName,count)values('"+row._1+"',"+row._2+")"
//          conn.prepareStatement(sql).executeUpdate()
//        }
//
//      }
//    }finally {
//      conn.close()
//    }
//  }))
  //wordCounts.print()
    val sparkConf =new SparkConf().setAppName("streaming").setMaster("local[2]")
    val ssc =new StreamingContext(sparkConf,Seconds(60))
    val Array(brokers,topics) =args
    val  kafkaParams =Map[String,String]("metadata.broker.list" -> "DianXin101:9092")
    val topicSet=topics.split(",").toSet
    val message =KafkaUtils.createDirectStream[String,String,StringDecoder,StringDecoder](ssc,kafkaParams,topicSet)
//    message.map(_._2).count().print()
    val cleanData =message.map(_._2).map{ x=>
      val strArr =x.split(",")
      strArr(2)

    }
    cleanData.print();

    ssc.start()
    ssc.awaitTermination()
  }
}
