# tao
Spark新闻实时分析
这里总结一下完成项目时遇到的几个问题：
提的个建议：
如果自己尝试了网上很多种办法都没有部署成功，这时一定要去看官方文档。仔细查看是否兼容其他框架，
开源的项目，博客写的也比较详细，一定严格的按照给的软件版本安装部署。否则就可能和我一样（之前没注意用的Kafka0.8.2.1结果集成Flume1.7的时候一直报错）
之后我严格按照给的版本部署
HUE3.9.0又出问题，一直没有装成功，恼火！！！
最后我去官网查了一下是因为我装的系统镜像centos6.8版本太高与HUE3.9.0冲突。



所以以后再做项目的时候一定要自习去官网查看清楚框架的适配情况
 # SparkStreaming+Kafka0.8.2.1
## DStream有状态转换操作

```
package test01

import java.sql.DriverManager

import org.apache.spark.sql.{ForeachWriter, Row}
import kafka.serializer.StringDecoder
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.streaming.ProcessingTime
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

import scala.collection.mutable.ListBuffer

/**
  * Created by Administrator on 2019/09/18.
  */
object StreamingKafka {

  def main(args: Array[String]): Unit = {

    val updateFunc = (values:Seq[Int],state:Option[Int]) =>{
      val currentCount = values.foldLeft(0)(_+_)
      val previousCount = state.getOrElse(0)
      Some(currentCount + previousCount)
    }

    val spark  = SparkSession.builder()
      .master("local[2]")
      .appName("streaming").getOrCreate()
    val sc =spark.sparkContext
    val ssc = new StreamingContext(sc, Seconds(5))
        ssc.checkpoint("file:///E://test01//stateful")

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
    val wordCounts: DStream[(String, Int)] = words.map(x =>(x,1)).updateStateByKey[Int](updateFunc)
    wordCounts.print()
       ssc.start()
        ssc.awaitTermination()



  }
}

```

## DStream无状态转换
  ```
 val spark  = SparkSession.builder()
    .master("local[2]")
    .appName("streaming").getOrCreate()
  val sc =spark.sparkContext
  val ssc = new StreamingContext(sc, Seconds(5))
  ssc.checkpoint("file:///E://test01//stateful")

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

```
  
  
 

